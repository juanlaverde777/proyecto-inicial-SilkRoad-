package silkRoad;

import java.util.ArrayList;
import java.util.List;

import shapes.Triangle;

/**
 * Clase base de robot que admite múltiples comportamientos.
 */
public abstract class Robot {

    private final RobotType type;
    private final int roadStartX;
    private final int roadStartY;
    private final String color;

    private final List<Integer> profitHistory;

    private Triangle shape;
    private int initialLocation;
    private int currentLocation;
    private int collected;

    protected Robot(RobotType type, int initialLocation, int roadStartX, int roadStartY, boolean visible, String color) {
        this.type = type == null ? RobotType.NORMAL : type;
        this.initialLocation = initialLocation;
        this.currentLocation = initialLocation;
        this.collected = 0;
        this.roadStartX = roadStartX;
        this.roadStartY = roadStartY;
        this.color = color == null ? "red" : color;
        this.profitHistory = new ArrayList<Integer>();
        this.shape = createShape();
        placeShape();
        if (visible) {
            shape.makeVisible();
        }
    }

    public final RobotType getType() {
        return type;
    }

    public final int getInitialLocation() {
        return initialLocation;
    }

    protected final void setInitialLocation(int location) {
        initialLocation = location;
    }

    public final int getCurrentLocation() {
        return currentLocation;
    }

    protected final void setCurrentLocation(int location) {
        int target = location;
        int delta = target - currentLocation;
        currentLocation = target;
        if (shape != null) {
            shape.moveHorizontal(delta);
        }
    }

    public final int getCollected() {
        return collected;
    }

    protected final void setCollected(int value) {
        collected = value;
    }

    public final int move(int meters) {
        int steps = meters;
        currentLocation += steps;
        if (shape != null) {
            shape.moveHorizontal(steps);
        }
        onMove(meters);
        return Math.abs(meters);
    }

    protected void onMove(int meters) {
        // gancho para robots especializados
    }

    public void addTenges(int tenges) {
        collected += tenges;
    }

    public void recordMove(int profit) {
        profitHistory.add(Integer.valueOf(profit));
    }

    public void returnToStart() {
        moveTo(initialLocation);
    }

    protected final void moveTo(int location) {
        int delta = location - currentLocation;
        currentLocation = location;
        if (shape != null) {
            shape.moveHorizontal(delta);
        }
    }

    public void reset() {
        moveTo(initialLocation);
        collected = 0;
        profitHistory.clear();
    }

    public boolean isAt(int location) {
        return currentLocation == location;
    }

    public int calculateNetGain(int tengesFromStore, int movementCost) {
        return tengesFromStore - movementCost;
    }

    public void makeVisible() {
        if (shape != null) {
            shape.makeVisible();
        }
    }

    public void makeInvisible() {
        if (shape != null) {
            shape.makeInvisible();
        }
    }

    public void destroy() {
        if (shape != null) {
            shape.makeInvisible();
            shape = null;
        }
    }

    public int[] getInfo() {
        return new int[]{currentLocation, collected};
    }

    public int[] getProfitPerMove() {
        int[] data = new int[profitHistory.size()];
        for (int i = 0; i < profitHistory.size(); i++) {
            data[i] = profitHistory.get(i).intValue();
        }
        return data;
    }

    public int desiredLootAmount(Store store, int available) {
        return available;
    }

    protected final void clearProfitHistory() {
        profitHistory.clear();
    }

    private Triangle createShape() {
        Triangle figure = new Triangle();
        figure.changeSize(20, 20);
        figure.changeColor(color);
        return figure;
    }

    private void placeShape() {
        if (shape == null) {
            return;
        }
        shape.moveHorizontal(roadStartX + initialLocation);
        shape.moveVertical(roadStartY - 30);
    }

    @Override
    public String toString() {
        return "Robot{" + type + " initial=" + initialLocation + ", current=" + currentLocation + ", collected=" + collected + "}";
    }
}