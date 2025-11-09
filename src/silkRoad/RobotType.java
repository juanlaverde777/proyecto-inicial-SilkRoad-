package silkRoad;

/**
 * Enumera las variantes de robot soportadas.
 */
public enum RobotType {
    NORMAL(0),
    NEVERBACK(1),
    TENDER(2);

    private final int code;

    RobotType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static RobotType fromCode(int code) {
        for (RobotType value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return NORMAL;
    }
}
