package silkRoad;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tienda que ignora la ubicación solicitada y elige su propio espacio.
 */
public class AutonomousStore extends Store {

    public AutonomousStore(int requestedLocation, int tenges, int roadStartX, int roadStartY, boolean visible, int roadLength, List<Store> existingStores) {
        super(StoreType.AUTONOMOUS, resolveLocation(requestedLocation, roadLength, existingStores), tenges, roadStartX, roadStartY, visible, "blue");
    }

    private static int resolveLocation(int requestedLocation, int roadLength, List<Store> existingStores) {
        int clampedLength = Math.max(0, roadLength);
        Set<Integer> occupied = new HashSet<Integer>();
        if (existingStores != null) {
            for (Store store : existingStores) {
                if (store != null) {
                    occupied.add(store.getLocation());
                }
            }
        }

        int center = clampedLength / 2;
        if (!occupied.contains(center)) {
            return center;
        }

        for (int offset = 10; offset <= clampedLength; offset += 10) {
            int right = center + offset;
            if (right <= clampedLength && !occupied.contains(right)) {
                return right;
            }
            int left = center - offset;
            if (left >= 0 && !occupied.contains(left)) {
                return left;
            }
        }

        int fallback = Math.max(0, Math.min(clampedLength, requestedLocation));
        if (!occupied.contains(fallback)) {
            return fallback;
        }

        for (int probe = 0; probe <= clampedLength; probe += 1) {
            if (!occupied.contains(probe)) {
                return probe;
            }
        }
        return fallback;
    }
}
