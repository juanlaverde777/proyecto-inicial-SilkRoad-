package shapes;

public class Rectangle extends AbstractShape {

    public static final int EDGES = 4;

    private int height;
    private int width;

    public Rectangle() {
        super(0, 0, "magenta");
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
        return new java.awt.Rectangle(xPosition, yPosition, width, height);
    }
}
