package shapes;

import java.awt.Shape;

public abstract class AbstractShape {

    protected int xPosition;
    protected int yPosition;
    protected String color;
    protected boolean isVisible;

    protected AbstractShape(int xPosition, int yPosition, String color) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.color = color;
        this.isVisible = false;
    }

    public void makeVisible() {
        isVisible = true;
        draw();
    }

    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    public void moveRight() {
        moveHorizontal(20);
    }

    public void moveLeft() {
        moveHorizontal(-20);
    }

    public void moveUp() {
        moveVertical(-20);
    }

    public void moveDown() {
        moveVertical(20);
    }

    public void moveHorizontal(int distance) {
        erase();
        xPosition += distance;
        draw();
    }

    public void moveVertical(int distance) {
        erase();
        yPosition += distance;
        draw();
    }

    public void slowMoveHorizontal(int distance) {
        int delta = distance < 0 ? -1 : 1;
        int steps = Math.abs(distance);
        for (int i = 0; i < steps; i++) {
            xPosition += delta;
            draw();
        }
    }

    public void slowMoveVertical(int distance) {
        int delta = distance < 0 ? -1 : 1;
        int steps = Math.abs(distance);
        for (int i = 0; i < steps; i++) {
            yPosition += delta;
            draw();
        }
    }

    public void changeColor(String newColor) {
        color = newColor;
        draw();
    }

    protected void draw() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.draw(this, color, awtShape());
            canvas.wait(10);
        }
    }

    protected void erase() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }

    protected abstract Shape awtShape();
}
