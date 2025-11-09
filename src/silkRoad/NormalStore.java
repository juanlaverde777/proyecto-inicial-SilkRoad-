package silkRoad;

/**
 * Tienda por defecto que se comporta igual que la implementación original.
 */
public class NormalStore extends Store {

    public NormalStore(int location, int tenges, int roadStartX, int roadStartY, boolean visible) {
        super(StoreType.NORMAL, location, tenges, roadStartX, roadStartY, visible, "green");
    }
}
