package silkRoad;

/**
 * Robot que solo toma la mitad del dinero de la tienda en cada visita.
 */
public class TenderRobot extends Robot {

    public TenderRobot(int initialLocation, int roadStartX, int roadStartY, boolean visible) {
        super(RobotType.TENDER, initialLocation, roadStartX, roadStartY, visible, "magenta");
    }

    @Override
    public int desiredLootAmount(Store store, int available) {
        if (available <= 0) {
            return 0;
        }
        return Math.max(1, (available + 1) / 2);
    }
}
