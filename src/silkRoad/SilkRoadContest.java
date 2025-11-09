package silkRoad;

public class SilkRoadContest {

    private static final int MIN_LENGTH = 100;
    private static final int EXTRA_MARGIN = 10;

    public int[] solve(int[][] days) {
        SilkRoad road = new SilkRoad(estimateLength(days), days);
        try {
            int[][] summary = road.dayProfits();
            int[] result = new int[summary.length];
            for (int[] row : summary) {
                if (row != null && row.length >= 2) {
                    int day = row[0];
                    if (day >= 0 && day < result.length) {
                        result[day] = row[1];
                    }
                }
            }
            return result;
        } finally {
            road.finish();
        }
    }

    public void simulate(int[][] days, boolean slow) {
        SilkRoad road = new SilkRoad(estimateLength(days));
        try {
            if (slow) {
                road.makeVisible();
            }
            int totalDays = days == null ? 0 : days.length;
            for (int day = 0; day < totalDays; day++) {
                road.resupplyStores();
                road.returnRobots();

                int[] event = days[day];
                if (event != null && event.length >= 2) {
                    int type = event[0];
                    int location = event[1];
                    int tenges = event.length >= 3 ? event[2] : 0;
                    if (type == 0) {
                        road.placeStore(location, tenges);
                    } else if (type == 1) {
                        road.placeRobot(location);
                    } else if (type == 2) {
                        road.removeStore(location);
                    } else if (type == 3) {
                        road.removeRobot(location);
                    }
                }

                road.moveRobots();
                System.out.println("Dia " + (day + 1));
                String[] moves = road.consumeMoveLog();
                if (moves.length == 0) {
                    System.out.println("  Sin movimientos");
                }
                for (String move : moves) {
                    System.out.println("  " + move);
                    pauseIfNeeded(slow);
                }
                System.out.println("  Ganancia acumulada: " + road.profit());
                pauseIfNeeded(slow);
            }
        } finally {
            road.finish();
        }
    }

    private int estimateLength(int[][] days) {
        int max = 0;
        if (days != null) {
            for (int[] day : days) {
                if (day != null && day.length >= 2) {
                    max = Math.max(max, day[1]);
                }
            }
        }
        return Math.max(MIN_LENGTH, max + EXTRA_MARGIN);
    }

    private void pauseIfNeeded(boolean slow) {
        if (!slow) {
            return;
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
