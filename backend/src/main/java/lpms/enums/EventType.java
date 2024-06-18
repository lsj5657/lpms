package lpms.enums;

public enum EventType {
    NA(0),
    FIXED(1),
    FLOATING(2),
    DUAL(3);

    private final int value;

    EventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static EventType fromValue(int value) {
        for (EventType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid EventType value: " + value);
    }
}