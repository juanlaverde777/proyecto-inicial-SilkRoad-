package silkRoad;

/**
 * Enumera las variantes de tienda soportadas.
 */
public enum StoreType {
    NORMAL(0),
    AUTONOMOUS(1),
    FIGHTER(2);

    private final int code;

    StoreType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static StoreType fromCode(int code) {
        for (StoreType value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return NORMAL;
    }
}
