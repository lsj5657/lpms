package lpms.backend.enums;

/**
 * Enum representing different types of events.
 */
public enum EventType {
    NA(0),        // Not Applicable
    FIXED(1),     // Fixed event type
    FLOATING(2),  // Floating event type
    DUAL(3);      // Dual event type


    private final int value;

    EventType(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value associated with the event type.
     *
     * @return the integer value of the event type
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets the EventType corresponding to a given integer value.
     *
     * @param value the integer value of the event type
     * @return the EventType corresponding to the given value
     * @throws IllegalArgumentException if the value does not correspond to any EventType
     */
    public static EventType fromValue(int value) {
        for (EventType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid EventType value: " + value);
    }
}