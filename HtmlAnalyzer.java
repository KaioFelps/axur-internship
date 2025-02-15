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

enum TagType {
    Closing,
    Opening,
}

class Fetcher<T> {
    private final String url;
    private Optional<T> value;

    public Fetcher(String url) {
        this.url = url;
        this.value = Optional.empty();
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
