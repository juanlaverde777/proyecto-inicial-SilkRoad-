package silkRoad;

import shapes.Rectangle;

/**
 * Clase base de tienda que admite comportamientos personalizados según el tipo.
 */
public abstract class Store {

    private final StoreType type;
    private final int initialTenges;
    private final int roadStartX;
    private final int roadStartY;
    private final String color;

    private int location;
    private int currentTenges;
    private int emptiedCount;
    private Rectangle shape;

    protected Store(StoreType type, int location, int tenges, int roadStartX, int roadStartY, boolean visible, String color) {
        this.type = type == null ? StoreType.NORMAL : type;
        this.location = Math.max(0, location);
        this.initialTenges = Math.max(0, tenges);
        this.currentTenges = this.initialTenges;
        this.roadStartX = roadStartX;
        this.roadStartY = roadStartY;
        this.color = color == null ? "green" : color;
        this.shape = createShape();
        placeShape();
        if (visible) {
            shape.makeVisible();
        }
    }

    public final StoreType getType() {
        return type;
    }

    public final int getLocation() {
        return location;
    }

    protected final void setLocation(int newLocation) {
        int adjusted = Math.max(0, newLocation);
        if (adjusted == location) {
            return;
        }
        if (shape != null) {
            int delta = adjusted - location;
            shape.moveHorizontal(delta);
        }
        location = adjusted;
    }

    public final int getInitialTenges() {
        return initialTenges;
    }

    public final int getCurrentTenges() {
        return currentTenges;
    }

    public final void setCurrentTenges(int tenges) {
        currentTenges = Math.max(0, tenges);
    }

    public final boolean hasTenges() {
        return currentTenges > 0;
    }

    public final void resupply() {
        currentTenges = initialTenges;
    }

    public final int getEmptiedCount() {
        return emptiedCount;
    }

    public final void resetStats() {
        emptiedCount = 0;
    }

    public final void makeVisible() {
        if (shape != null) {
            shape.makeVisible();
        }
    }

    public final void makeInvisible() {
        if (shape != null) {
            shape.makeInvisible();
        }
    }

    public final void destroy() {
        if (shape != null) {
            shape.makeInvisible();
            shape = null;
        }
    }

    public final int[] getInfo() {
        return new int[]{location, currentTenges};
    }

    public final int loot(Robot robot) {
        int granted = previewLoot(robot);
        if (granted <= 0) {
            return 0;
        }
        currentTenges -= granted;
        if (currentTenges == 0) {
            emptiedCount++;
        }
        afterLoot(robot, granted);
        return granted;
    }

    public final int previewLoot(Robot robot) {
        if (robot == null || !canBeLootedBy(robot) || currentTenges <= 0) {
            return 0;
        }
        int requested = robot.desiredLootAmount(this, currentTenges);
        int granted = Math.min(currentTenges, Math.max(0, requested));
        granted = adjustLootAmount(robot, granted);
        if (granted <= 0) {
            return 0;
        }
        return Math.min(currentTenges, granted);
    }

    public final boolean canBeLootedByRobot(Robot robot) {
        return canBeLootedBy(robot);
    }

    protected boolean canBeLootedBy(Robot robot) {
        return currentTenges > 0;
    }

    protected int adjustLootAmount(Robot robot, int tentative) {
        return tentative;
    }

    protected void afterLoot(Robot robot, int amount) {
        // gancho para subclases
    }

    private Rectangle createShape() {
        Rectangle figure = new Rectangle();
        figure.changeSize(20, 20);
        figure.changeColor(color);
        return figure;
    }

    private void placeShape() {
        if (shape == null) {
            return;
        }
        shape.moveHorizontal(roadStartX + location);
        shape.moveVertical(roadStartY);
    }

    @Override
    public String toString() {
        return "Store{" + type + "@" + location + ", tenges=" + currentTenges + "/" + initialTenges + "}";
    }
}