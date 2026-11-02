package texteditor;

public final class DocumentCharacter {
    private final char value;
    private final CharacterProperties properties;

    public DocumentCharacter(char value, CharacterProperties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("A character needs properties");
        }
        this.value = value;
        this.properties = properties;
    }

    public char getValue() {
        return value;
    }

    public CharacterProperties getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "'" + value + "' (" + properties + ")";
    }
}
