package shapes;

import java.awt.geom.Ellipse2D;

public class Circle extends AbstractShape {

    public static final double PI = 3.1416;

    private int diameter;

    public Circle() {
        super(20, 15, "blue");
        diameter = 30;
    }

    public void changeSize(int newDiameter) {
        erase();
        diameter = newDiameter;
        draw();
    }

    @Override
    protected java.awt.Shape awtShape() {
        return new Ellipse2D.Double(xPosition, yPosition, diameter, diameter);
    }
}
