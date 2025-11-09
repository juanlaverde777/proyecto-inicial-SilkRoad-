package silkRoad;

/**
 * Robot por defecto que conserva el comportamiento del ciclo 1.
 */
public class NormalRobot extends Robot {

    public NormalRobot(int initialLocation, int roadStartX, int roadStartY, boolean visible) {
        super(RobotType.NORMAL, initialLocation, roadStartX, roadStartY, visible, "red");
    }
}
