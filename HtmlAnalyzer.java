import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

final public class HtmlAnalyzer {
    public static void main(String[] args) {

    private static String handle(String url) {
        Fetcher fetcher = new Fetcher(url);
        fetcher.fetch();

        Optional<String> body = fetcher.getValue();

        if (body.isEmpty()) return ErrorResponse.HttpError.getMessage();

        String html = body.get();
        List<String> lines = html.lines().toList();

        DomTracker tracker = new DomTracker();

        int maxDepth = 0;
        String deepestText = "";

        for (String line : lines) {
            String lineContent = line.strip();

            if (Tag.isTag(lineContent)) {
                Tag tag = Tag.parse(lineContent);
                tracker.track(tag);
                continue;
            }

            int depth = tracker.stackedTags();

            if (depth > maxDepth) {
                deepestText = lineContent.strip();
                maxDepth = depth;
            }
        }

        if (!tracker.domIsWellFormed()) return ErrorResponse.HTMLError.getMessage();

        return deepestText;
    }
}

enum TagType {
    Closing,
    Opening,
}

enum ErrorResponse {
    HttpError("URL connection error"),
    HTMLError("malformed HTML");

    private final String message;

    ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}

class Tag {
    private final String value;
    private final TagType type;

    private Tag(String value, TagType type) {
        this.value = value;
        this.type = type;
    }

    public static Tag parse(String tagString) {
        if (tagString.startsWith("</")) {
            String tagValue = tagString.substring(2, tagString.length() -1);
            return new Tag(tagValue, TagType.Closing);
        }

        String tagValue = tagString.substring(1, tagString.length() -1);
        return new Tag(tagValue, TagType.Opening);
    }

    public static boolean isTag(String maybeTag) {
        return maybeTag.startsWith("<");
    }

    public String getValue() {
        return this.value;
    }

    public TagType getType() {
        return this.type;
    }

    public String toString() {
        return switch (this.type) {
            case Closing -> String.format("</%s>", this.value);
            case Opening -> String.format("<%s>", this.value);
        };
    }
}

class DomTracker {
    private final Deque<Tag> tagStack;
    private boolean noAttemptsToCloseUndefinedOpening = true;

    public DomTracker() {
        this.tagStack = new ArrayDeque<>();
    }

    public void track(Tag tag) {
        if (tag.getType().equals(TagType.Opening)) {
            this.tagStack.add(tag);
            return;
        }

        Tag lastTag = this.tagStack.peekLast();

        // dom stack cannot proceed without opened tags in the stack...
        // what would it even be closing?
        if (lastTag == null || !lastTag.getValue().equals(tag.getValue())) {
            this.noAttemptsToCloseUndefinedOpening = false;
            return;
        }

        this.tagStack.removeLast();
    }

    public int stackedTags() {
        return this.tagStack.size();
    }

    public boolean domIsWellFormed() {
        return this.tagStack.isEmpty() && this.noAttemptsToCloseUndefinedOpening;
    }
}

class Fetcher {
    private final String url;
    private String value;

    public Fetcher(String url) {
        this.url = url;
    }

    public void fetch() {
        HttpClient client = HttpClient.newBuilder().build();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();

            if (response.statusCode() != 200) {
                return;
            }

            this.value = response.body();
        } catch (Exception exception) {
            return;
        }
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(this.value);
    }
}
