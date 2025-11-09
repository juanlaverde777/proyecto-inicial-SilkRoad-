package silkRoad;

/**
 * Tienda que solo entrega dinero a robots más ricos que ella.
 */
public class FighterStore extends Store {

    public FighterStore(int location, int tenges, int roadStartX, int roadStartY, boolean visible) {
        super(StoreType.FIGHTER, location, tenges, roadStartX, roadStartY, visible, "orange");
    }

    @Override
    protected boolean canBeLootedBy(Robot robot) {
        if (robot == null) {
            return false;
        }
        return getCurrentTenges() > 0 && robot.getCollected() > getCurrentTenges();
    }
}
