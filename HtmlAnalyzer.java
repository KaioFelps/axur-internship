import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

final public class HtmlAnalyzer {
    public static void main(String[] args) {

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

    public DomTracker() {
        this.tagStack = new ArrayDeque<>();
    }

    public void track(Tag tag) {
        if (tag.getType().equals(TagType.Opening)) {
            this.tagStack.add(tag);
            return;
        }

        if (!lastTag.getValue().equals(tag.getValue())) return;

        tagStack.removeLast();
    }

    public boolean domIsWellFormed() {
        return !this.tagStack.isEmpty();
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
