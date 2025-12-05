package test.technique.SMA.entity;

public enum StudentLevel {
    FIRST_YEAR("First Year"),
    SECOND_YEAR("Second Year"),
    THIRD_YEAR("Third Year"),
    FOURTH_YEAR("Fourth Year"),
    FIFTH_YEAR("Fifth Year");

    private final String displayName;

    StudentLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
