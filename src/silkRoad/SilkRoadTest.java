package silkRoad;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

/**
 * Suite unificada de pruebas separada por ciclos del proyecto.
 */
public class SilkRoadTest {

    private SilkRoad road;

    @After
    public void tearDown() {
        if (road != null) {
            road.finish();
            road = null;
        }
    }

    // Ciclo 1

    @Test
    public void ciclo1ConstructorInicializaGananciaCero() {
        road = new SilkRoad(500);
        assertNotNull(road);
        assertTrue(road.ok());
        assertEquals(0, road.totalProfit());
    }

    @Test
    public void ciclo1PermiteAgregarTienda() {
        road = new SilkRoad(500);
        road.placeStore(100, 50);
        assertTrue(road.ok());

        int[][] stores = road.stores();
        assertEquals(1, stores.length);
        assertEquals(100, stores[0][0]);
        assertEquals(50, stores[0][1]);
    }

    @Test
    public void ciclo1PermiteAgregarRobot() {
        road = new SilkRoad(500);
        road.placeRobot(0);
        assertTrue(road.ok());

        int[][] robots = road.robots();
        assertEquals(1, robots.length);
        assertEquals(0, robots[0][0]);
        assertEquals(0, robots[0][1]);
    }

    @Test
    public void ciclo1MovimientoRobotGeneraGanancia() {
        road = new SilkRoad(500);
        road.placeStore(100, 80);
        road.placeRobot(0);

        int inicial = road.totalProfit();
        road.moveRobot(0, 100);

        assertTrue(road.ok());
        assertNotEquals(inicial, road.totalProfit());

        int[][] robots = road.robots();
        assertEquals(100, robots[0][0]);
    }

    @Test
    public void ciclo1ReportaTiendasVaciadas() {
        road = new SilkRoad(500);
        road.placeStore(100, 50);
        road.placeStore(200, 75);
        road.placeRobot(0);

        assertEquals(0, road.emptiedStores().length);

        road.moveRobot(0, 100);

        int[][] emptied = road.emptiedStores();
        assertEquals(1, emptied.length);
        assertEquals(100, emptied[0][0]);
        assertTrue(emptied[0][1] > 0);
    }

    @Test
    public void ciclo1MovimientoAutomaticoNoFalla() {
        road = new SilkRoad(500);
        road.placeStore(100, 50);
        road.placeStore(200, 75);
        road.placeRobot(0);
        road.placeRobot(50);

        int[][] antes = road.robots();
        road.moveRobots();
        assertTrue(road.ok());

        int[][] despues = road.robots();
        assertEquals(antes.length, despues.length);
    }

    @Test
    public void ciclo1ReportaGananciasPorMovimiento() {
        road = new SilkRoad(500);
        road.placeStore(100, 150);
        road.placeRobot(0);
        road.moveRobot(0, 100);

        int[][] info = road.profitPerMove();
        assertNotNull(info);
        assertTrue(info.length > 0);
        assertEquals(0, info[0][0]);
        assertEquals(3, info[0].length);
        assertEquals(50, info[0][1]);
        assertEquals(0, info[0][2]);
    }

    @Test
    public void ciclo1ReinicioRestableceEstado() {
        road = new SilkRoad(500);
        road.placeStore(100, 50);
        road.placeRobot(0);

        road.moveRobot(0, 100);
        int previo = road.totalProfit();

        road.reboot();
        assertTrue(road.ok());
        assertEquals(0, road.totalProfit());
        assertNotEquals(previo, road.totalProfit());
    }

    @Test
    public void ciclo1AceptaValoresNegativosComoCero() {
        road = new SilkRoad(500);
        road.placeStore(100, -50);
        assertTrue(road.ok());

        int[][] stores = road.stores();
        if (stores.length > 0) {
            assertEquals(0, stores[0][1]);
        }
    }

    // Ciclo 2

    @Test
    public void ciclo2CuentaVaciasPorAbastecimiento() {
        road = new SilkRoad(500);
        road.placeStore(100, 200);
        road.placeRobot(0);

        road.moveRobot(0, 100);
        road.resupplyStores();
        road.returnRobots();
        road.moveRobot(0, 100);

        int[][] emptied = road.emptiedStores();
        assertEquals(1, emptied.length);
        assertEquals(100, emptied[0][0]);
        assertEquals(2, emptied[0][1]);
    }

    @Test
    public void ciclo2SimulaDiasConEventos() {
        int[][] days = new int[][]{
            {0, 100, 150},
            {1, 0},
            {0, 300, 400},
            {1, 250}
        };

        road = new SilkRoad(600, days);
        int[][] summary = road.dayProfits();
        assertEquals(4, summary.length);
        assertArrayEquals(new int[]{0, 0}, summary[0]);
        assertArrayEquals(new int[]{1, 50}, summary[1]);
        assertArrayEquals(new int[]{2, 100}, summary[2]);
        assertArrayEquals(new int[]{3, 400}, summary[3]);
        assertEquals(550, road.totalProfit());
    }

    @Test
    public void ciclo2ReportaGananciasPorRobot() {
        road = new SilkRoad(600);
        road.placeStore(100, 200);
        road.placeStore(300, 300);
        road.placeRobot(0);
        road.placeRobot(200);

        road.moveRobots();

        int[][] profits = road.profitPerMove();
        assertEquals(2, profits.length);
        assertEquals(0, profits[0][0]);
        assertEquals(100, profits[0][1]);
        assertEquals(200, profits[1][0]);
        assertEquals(200, profits[1][1]);

        int[][] emptied = road.emptiedStores();
        assertEquals(2, emptied.length);
        assertEquals(100, emptied[0][0]);
        assertEquals(1, emptied[0][1]);
        assertEquals(300, emptied[1][0]);
        assertEquals(1, emptied[1][1]);
    }

    // Ciclo 4

    @Test
    public void ciclo4TiendaAutonomaCentraPosicion() {
        road = new SilkRoad(300);
        road.placeStore(12, 100, StoreType.AUTONOMOUS);

        int[][] stores = road.stores();
        assertEquals(1, stores.length);
        assertEquals(150, stores[0][0]);
        assertEquals(100, stores[0][1]);
    }

    @Test
    public void ciclo4TiendaAutonomaBuscaSiguienteEspacio() {
        road = new SilkRoad(300);
        road.placeStore(150, 80, StoreType.NORMAL);
        road.placeStore(10, 120, StoreType.AUTONOMOUS);

        int[][] stores = road.stores();
        assertEquals(2, stores.length);
        assertArrayEquals(new int[]{150, 80}, stores[0]);
        assertArrayEquals(new int[]{160, 120}, stores[1]);
    }

    @Test
    public void ciclo4TiendaFighterExigeRobotsRicos() {
        road = new SilkRoad(500);
        road.placeStore(100, 50, StoreType.FIGHTER);
        road.placeRobot(0, RobotType.NORMAL);

        road.moveRobot(0, 100);
        assertEquals(-100, road.totalProfit());
        assertEquals(50, buscarEntrada(road.stores(), 100)[1]);
        assertEquals(0, road.emptiedStores().length);

        road.placeStore(400, 600, StoreType.NORMAL);
        road.moveRobot(0, 300);
        assertEquals(200, road.totalProfit());
        assertEquals(0, buscarEntrada(road.stores(), 400)[1]);

        road.moveRobot(0, -300);
        assertEquals(-50, road.totalProfit());
        assertEquals(0, buscarEntrada(road.stores(), 100)[1]);

        int[][] emptied = road.emptiedStores();
        assertEquals(2, emptied.length);
        assertArrayEquals(new int[]{100, 1}, emptied[0]);
        assertArrayEquals(new int[]{400, 1}, emptied[1]);
    }

    @Test
    public void ciclo4RobotTenderTomaMitad() {
        road = new SilkRoad(200);
        road.placeStore(40, 99, StoreType.NORMAL);
        road.placeRobot(0, RobotType.TENDER);

        road.moveRobot(0, 40);

        assertEquals(10, road.totalProfit());
        assertEquals(49, buscarEntrada(road.stores(), 40)[1]);

        int[][] robots = road.robots();
        assertEquals(1, robots.length);
        assertArrayEquals(new int[]{40, 10}, robots[0]);
    }

    @Test
    public void ciclo4RobotNeverbackIgnoraRetorno() {
        road = new SilkRoad(200);
        road.placeRobot(20, RobotType.NEVERBACK);

        road.moveRobot(20, 40);
        road.returnRobots();

        int[][] robots = road.robots();
        assertEquals(1, robots.length);
        assertArrayEquals(new int[]{60, -40}, robots[0]);

        road.reboot();
        robots = road.robots();
        assertEquals(1, robots.length);
        assertArrayEquals(new int[]{20, 0}, robots[0]);
    }

    @Test
    public void ciclo4SimulacionConTiposEnDias() {
        int[][] days = new int[][]{
            {0, 40, 140},
            {1, 0, RobotType.TENDER.getCode()},
            {0, 120, 80, StoreType.FIGHTER.getCode()},
            {1, 120, RobotType.NORMAL.getCode()}
        };

        road = new SilkRoad(300, days);
        int[][] summary = road.dayProfits();
        assertEquals(4, summary.length);
        assertArrayEquals(new int[]{0, 0}, summary[0]);
        assertArrayEquals(new int[]{1, 100}, summary[1]);
        assertArrayEquals(new int[]{2, 100}, summary[2]);
        assertArrayEquals(new int[]{3, 60}, summary[3]);
        assertEquals(260, road.totalProfit());

        int[][] stores = road.stores();
        assertEquals(2, stores.length);
        assertArrayEquals(new int[]{40, 0}, stores[0]);
        assertArrayEquals(new int[]{120, 80}, stores[1]);

        int[][] robots = road.robots();
        assertEquals(2, robots.length);
        assertArrayEquals(new int[]{0, 200}, robots[0]);
        assertArrayEquals(new int[]{40, 60}, robots[1]);
    }

    @Test
    public void ciclo4ConcursoResuelveConEntidadesTipadas() {
        int[][] days = new int[][]{
            {0, 40, 140},
            {1, 0, RobotType.TENDER.getCode()},
            {0, 120, 80, StoreType.FIGHTER.getCode()},
            {1, 120, RobotType.NORMAL.getCode()}
        };

        SilkRoadContest contest = new SilkRoadContest();
        int[] profits = contest.solve(days);
        assertArrayEquals(new int[]{0, 100, 100, 60}, profits);
    }

    // Concurso (ciclo adicional)

    @Test
    public void concursoManejaMultiplesTiendasMismoRobot() {
        int[][] days = new int[][]{
            {1, 0},
            {0, 20, 100},
            {0, 50, 120}
        };

        SilkRoadContest contest = new SilkRoadContest();
        int[] profits = contest.solve(days);
        assertArrayEquals(new int[]{0, 80, 170}, profits);
    }

    @Test
    public void concursoPermiteEliminarEntidades() {
        int[][] days = new int[][]{
            {1, 0},
            {0, 20, 100},
            {2, 20},
            {0, 40, 80}
        };

        SilkRoadContest contest = new SilkRoadContest();
        int[] profits = contest.solve(days);
        assertArrayEquals(new int[]{0, 80, 0, 40}, profits);
    }

    @Test
    public void concursoResuelveGananciasDiarias() {
        int[][] days = new int[][]{
            {0, 100, 150},
            {1, 0},
            {0, 300, 400},
            {1, 250}
        };

        SilkRoadContest contest = new SilkRoadContest();
        int[] profits = contest.solve(days);
        assertArrayEquals(new int[]{0, 50, 100, 400}, profits);
    }

    @Test
    public void concursoIgnoraTiendaNoRentable() {
        int[][] days = new int[][]{
            {1, 0},
            {0, 10, 5}
        };

        SilkRoadContest contest = new SilkRoadContest();
        int[] profits = contest.solve(days);
        assertArrayEquals(new int[]{0, 0}, profits);
    }

    @Test
    public void concursoSimulacionSinErrores() {
        int[][] days = new int[][]{
            {1, 0},
            {0, 50, 200},
            {0, 120, 100}
        };

        SilkRoadContest contest = new SilkRoadContest();
        contest.simulate(days, false);
    }

    private int[] buscarEntrada(int[][] entries, int location) {
        for (int[] entry : entries) {
            if (entry != null && entry.length >= 2 && entry[0] == location) {
                return entry;
            }
        }
        fail("No se encontró información para la localización " + location);
        return new int[0];
    }
}
