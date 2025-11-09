package silkRoad;

import java.util.Scanner;

/**
 * Pruebas de aceptación interactivas para demostrar las capacidades clave del proyecto.
 */
public class SilkRoadAtest {

    private static final long WAIT_SHORT = 900L;
    private static final long WAIT_MEDIUM = 1300L;

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        try {
            boolean first = escenarioAutonomousTender(scanner);
            boolean second = escenarioFighterNeverback(scanner);
            if (first && second) {
                System.out.println("Resultado final: ambas pruebas fueron aceptadas por el usuario.");
            } else if (first || second) {
                System.out.println("Resultado final: solo una de las pruebas fue aceptada.");
            } else {
                System.out.println("Resultado final: ninguna de las pruebas fue aceptada.");
            }
        } finally {
            scanner.close();
        }
    }

    private static boolean escenarioAutonomousTender(Scanner scanner) throws InterruptedException {
        System.out.println("Escenario 1: tienda autónoma y robot tender.");
        SilkRoad road = new SilkRoad(200);
        try {
            road.makeVisible();
            road.placeStore(15, 120, StoreType.AUTONOMOUS);
            Thread.sleep(WAIT_MEDIUM);

            int autonomousLocation = road.stores()[0][0];
            System.out.println("La tienda autónoma eligió la posición " + autonomousLocation + ".");

            road.placeRobot(0, RobotType.TENDER);
            Thread.sleep(WAIT_SHORT);

            int displacement = autonomousLocation;
            System.out.println("El robot tender se desplazará " + displacement + " unidades para abastecerse.");
            road.moveRobot(0, displacement);
            Thread.sleep(WAIT_MEDIUM);

            int remaining = road.stores()[0][1];
            System.out.println("Tras la visita, la tienda conserva " + remaining + " tenges (mitad de lo inicial).");
            System.out.println("Ganancia acumulada: " + road.totalProfit());
            Thread.sleep(WAIT_SHORT);

            return preguntarAceptacion(scanner, "¿Acepta el resultado del escenario 1? (s/n): ");
        } finally {
            road.finish();
            Thread.sleep(WAIT_SHORT);
        }
    }

    private static boolean escenarioFighterNeverback(Scanner scanner) throws InterruptedException {
        System.out.println("Escenario 2: tienda fighter y robot neverback.");
        SilkRoad road = new SilkRoad(260);
        try {
            road.makeVisible();
            road.placeStore(90, 120, StoreType.FIGHTER);
            road.placeStore(200, 400, StoreType.NORMAL);
            road.placeRobot(0, RobotType.NORMAL);
            Thread.sleep(WAIT_SHORT);

            System.out.println("Intento 1: robot con poco capital visita la tienda fighter.");
            road.moveRobot(0, 90);
            Thread.sleep(WAIT_MEDIUM);
            System.out.println("Ganancia acumulada tras intento 1: " + road.totalProfit());
            System.out.println("La tienda fighter mantiene " + obtenerTenges(road, 90) + " tenges.");
            Thread.sleep(WAIT_SHORT);

            System.out.println("El robot buscará capital en la tienda normal para poder competir.");
            road.moveRobot(0, 110);
            Thread.sleep(WAIT_MEDIUM);
            System.out.println("Ganancia acumulada tras abastecerse: " + road.totalProfit());
            Thread.sleep(WAIT_SHORT);

            System.out.println("Intento 2: con más capital regresa a la tienda fighter.");
            road.moveRobot(0, -110);
            Thread.sleep(WAIT_MEDIUM);
            System.out.println("Ganancia acumulada tras intento 2: " + road.totalProfit());
            System.out.println("La tienda fighter ahora tiene " + obtenerTenges(road, 90) + " tenges.");
            Thread.sleep(WAIT_SHORT);

            System.out.println("Se incorpora un robot neverback para mostrar que no obedece la devolución.");
            road.placeRobot(60, RobotType.NEVERBACK);
            Thread.sleep(WAIT_SHORT);
            road.moveRobot(60, 80);
            Thread.sleep(WAIT_MEDIUM);
            road.returnRobots();
            Thread.sleep(WAIT_SHORT);

            for (int[] robotInfo : road.robots()) {
                System.out.println("Robot en " + robotInfo[0] + " con ganancia " + robotInfo[1]);
            }

            return preguntarAceptacion(scanner, "¿Acepta el resultado del escenario 2? (s/n): ");
        } finally {
            road.finish();
            Thread.sleep(WAIT_SHORT);
        }
    }

    private static boolean preguntarAceptacion(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = scanner.nextLine().trim().toLowerCase();
            if ("s".equals(answer) || "si".equals(answer)) {
                return true;
            }
            if ("n".equals(answer) || "no".equals(answer)) {
                return false;
            }
            System.out.println("Respuesta inválida. Por favor ingrese 's' o 'n'.");
        }
    }

    private static int obtenerTenges(SilkRoad road, int location) {
        int[][] stores = road.stores();
        for (int[] store : stores) {
            if (store != null && store.length >= 2 && store[0] == location) {
                return store[1];
            }
        }
        return -1;
    }
}
