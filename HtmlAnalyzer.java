import java.util.List;
import java.util.Optional;

final public class HtmlAnalyzer {
    public static void main(String[] args) {

    }
}

class Tag {
    private String value;
    private TagType type;

    public String getValue() {
        return this.value;
    }

    public TagType getType() {
        return this.type;
    }
}

    }
}

class DomTracker {
    private List<Tag> tagStack;

    public void track(Tag tag) {
        Tag lastTag = this.tagStack.getLast();

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
