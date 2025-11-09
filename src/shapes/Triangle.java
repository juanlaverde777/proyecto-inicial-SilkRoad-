package shapes;

import java.awt.Polygon;

public class Triangle extends AbstractShape {

    public static final int VERTICES = 3;

    private int height;
    private int width;

    public Triangle() {
        super(0, 0, "green");
        height = 30;
        width = 40;
    }

    public void changeSize(int newHeight, int newWidth) {
        erase();
        height = newHeight;
        width = newWidth;
        draw();
    }

    @Override
    protected java.awt.Shape awtShape() {
        int halfWidth = width / 2;
        int[] xPoints = {xPosition, xPosition + halfWidth, xPosition - halfWidth};
        int[] yPoints = {yPosition, yPosition + height, yPosition + height};
        return new Polygon(xPoints, yPoints, VERTICES);
    }
}
