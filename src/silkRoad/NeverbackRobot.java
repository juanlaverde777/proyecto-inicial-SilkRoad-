package silkRoad;

/**
 * Robot que se niega a volver a su punto inicial cuando se le ordena.
 */
public class NeverbackRobot extends Robot {

    public NeverbackRobot(int initialLocation, int roadStartX, int roadStartY, boolean visible) {
        super(RobotType.NEVERBACK, initialLocation, roadStartX, roadStartY, visible, "cyan");
    }

    @Override
    public void returnToStart() {
    // se ignora intencionalmente
    }

    @Override
    public void reset() {
        moveTo(getInitialLocation());
        setCollected(0);
        clearProfitHistory();
    }
}
