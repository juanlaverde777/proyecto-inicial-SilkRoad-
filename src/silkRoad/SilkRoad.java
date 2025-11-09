package silkRoad;

import java.util.*;

import shapes.Rectangle;

/**
 * Simulador simple de la Ruta de la Seda implementación mínima para el primer ciclo.
 * Usa Rectangle para la carretera y las tiendas, Triangle para los robots y el Canvas provisto para dibujar.
 * Refactorizado para usar una arquitectura orientada a objetos con clases Store y Robot separadas.
 */
public class SilkRoad {

    private int length;
    private Rectangle roadShape;
    private int roadStartX;
    private int roadStartY;

    private List<Store> stores = new ArrayList<Store>();
    private List<Robot> robots = new ArrayList<Robot>();
    private List<String> moveLog = new ArrayList<String>();

    private boolean visible = false;
    private boolean lastOk = true;

    private int totalProfit = 0;
    private int[][] _days; // valores [dayIndex, profit]
    private int[][] dayEvents;

    /**
     * Constructor crea una carretera de la longitud dada en píxeles Aaltura de la carretera fija a 20
     */
    // Constructor de SilkRoad calcula posiciones para centrar la carretera, crea y configura roadShape
    public SilkRoad(int length){
        this.length = Math.max(0, length);

        // centrar la carretera en el canvas 600x600 usado por Canvas.getCanvas()
        this.roadStartX = (600 - this.length) / 2;
        this.roadStartY = (600 - 20) / 2;

        roadShape = new Rectangle();
        // ajustar el tamaño de la carretera alto 20, ancho = length
        roadShape.changeSize(20, this.length);
        // mover a la posición inicial
        roadShape.moveHorizontal(this.roadStartX);
        roadShape.moveVertical(this.roadStartY);
        roadShape.changeColor("yellow");
        if(visible) roadShape.makeVisible();
        
        // Inicializar array de días (empezar con tamaño pequeño, se expandirá según necesidad)
        _days = new int[0][0];
        dayEvents = new int[0][0];
    }

    public SilkRoad(int length, int[][] days){
        this(length);
        loadDays(days);
    }

    public void loadDays(int[][] days){
        if(days == null){
            dayEvents = new int[0][0];
            _days = new int[0][0];
            clearAll();
            return;
        }

        dayEvents = new int[days.length][];
        for(int i = 0; i < days.length; i++){
            int[] row = days[i];
            if(row == null){
                dayEvents[i] = new int[0];
            } else {
                dayEvents[i] = Arrays.copyOf(row, row.length);
            }
        }
        simulateDays();
    }

    public int[][] dayProfits(){
        int[][] copy = new int[_days.length][];
        for(int i = 0; i < _days.length; i++){
            copy[i] = Arrays.copyOf(_days[i], _days[i].length);
        }
        return copy;
    }

    private void simulateDays(){
        clearAll();
        if(dayEvents == null){
            _days = new int[0][0];
            return;
        }

        List<int[]> summary = new ArrayList<int[]>();
        for(int i = 0; i < dayEvents.length; i++){
            resupplyStores();
            returnRobots();

            int before = totalProfit;
            int[] event = dayEvents[i];
            if(event != null && event.length >= 2){
                int type = event[0];
                int location = event[1];
                if(type == 0){
                    int tenges = event.length >= 3 ? event[2] : 0;
                    StoreType storeType = event.length >= 4 ? StoreType.fromCode(event[3]) : StoreType.NORMAL;
                    placeStore(location, tenges, storeType);
                } else if(type == 1){
                    RobotType robotType = event.length >= 3 ? RobotType.fromCode(event[2]) : RobotType.NORMAL;
                    placeRobot(location, robotType);
                } else if(type == 2){
                    removeStore(location);
                } else if(type == 3){
                    removeRobot(location);
                }
            }

            moveRobots();
            consumeMoveLog();
            int profit = totalProfit - before;
            summary.add(new int[]{i, profit});
        }

        _days = new int[summary.size()][];
        for(int i = 0; i < summary.size(); i++){
            _days[i] = summary.get(i);
        }
        lastOk = true;
    }

    private void clearAll(){
        for(Store s: stores){
            s.destroy();
        }
        stores.clear();
        for(Robot r: robots){
            r.destroy();
        }
        robots.clear();
        totalProfit = 0;
        moveLog.clear();
        lastOk = true;
    }

    /**
     * Colocar una tienda en la posición location distancia en píxeles desde la izquierda de la carretera con tenges
     */
    // placeStore valida bounds y ocupación, crea una Store y la añade a la lista stores
    public void placeStore(int location, int tenges){
        placeStore(location, tenges, StoreType.NORMAL);
    }

    public void placeStore(int location, int tenges, StoreType type){
        lastOk = false;
        StoreType resolvedType = type == null ? StoreType.NORMAL : type;
        if(resolvedType != StoreType.AUTONOMOUS && (location < 0 || location > length)){
            return;
        }

        Store candidate = buildStore(resolvedType, location, tenges);
        if(candidate == null){
            return;
        }

        for(Store existing : stores){
            if(existing.getLocation() == candidate.getLocation()){
                candidate.destroy();
                return;
            }
        }

        stores.add(candidate);
        if(visible){
            candidate.makeVisible();
        }
        lastOk = true;
    }

    /**
     * Eliminar la tienda en la localización dada si existe
     recorremos la lista
     * desde el final hacia el principio y usamos remove i para evitar
     * problemas al modificar la lista mientras la recorremos
     */
    // removeStore busca desde el final la tienda en location, la destruye y la elimina de la lista
    public void removeStore(int location){
        lastOk = false;
        for (int i = stores.size() - 1; i >= 0; i--) {
            Store s = stores.get(i);
            if (s.getLocation() == location) {
                s.destroy();
                stores.remove(i);
                lastOk = true;
                return;
            }
        }
    }

    /**
     * Colocar un robot en la posición dada distancia desde la izquierda de la carretera Los robots deben empezar en localizaciones distintas
     */
    // placeRobot valida bounds y unicidad de initialLocation, crea un Robot y lo añade a robots
    public void placeRobot(int location){
        placeRobot(location, RobotType.NORMAL);
    }

    public void placeRobot(int location, RobotType type){
        lastOk = false;
        if(location < 0 || location > length){
            return;
        }

        for(Robot existing : robots){
            if(existing.getInitialLocation() == location){
                return;
            }
        }

        Robot candidate = buildRobot(type == null ? RobotType.NORMAL : type, location);
        if(candidate == null){
            return;
        }

        robots.add(candidate);
        if(visible){
            candidate.makeVisible();
        }
        lastOk = true;
    }

    /**
     * Eliminar el robot cuyo lugar inicial coincide con la localización dada
     * por índice para evitar ConcurrentModificationException
     */
    // removeRobot busca desde el final un robot con initialLocation == location, lo destruye y lo elimina.
    public void removeRobot(int location){
        lastOk = false;
        for (int i = robots.size() - 1; i >= 0; i--) {
            Robot r = robots.get(i);
            if (r.getInitialLocation() == location) {
                r.destroy();
                robots.remove(i);
                lastOk = true;
                return;
            }
        }
    }

    /**
     * Mover el robot identificado por su localización inicial en meters positivo = derecha, negativo = izquierda
     * Si después del movimiento queda exactamente en la localización de una tienda y la tienda tiene tenges,
     * el robot los recoge
     * Ganancia añadida = store.tenges - metrosMovidos valor absoluto de metros
     */
    // moveRobot localiza al robot por initialLocation, lo mueve meters
    // si termina en una tienda con tenges>0 calcula gain, lo añade a collected y totalProfit y vacía la tienda.
    public void moveRobot(int location, int meters){
        lastOk = false;
        for(Robot r: robots){
            if(r.getInitialLocation() == location){

                int startPosition = r.getCurrentLocation();
                int moved = r.move(meters);
                int loot = 0;

                for(Store s: stores){
                    if(s.getLocation() == r.getCurrentLocation()){
                        loot = s.loot(r);
                        break;
                    }
                }

                int gain = loot - moved;
                r.addTenges(gain);
                r.recordMove(gain);
                totalProfit += gain;
                moveLog.add("robot " + r.getInitialLocation() + " from " + startPosition + " to " + r.getCurrentLocation() + " profit=" + gain);
                lastOk = true;
                return;
            }
        }
    }

    /**
     * Reabastecer todas las tiendas a su cantidad inicial de tenges
     */
    // resupplyStores recorre todas las tiendas y las reabastece a su cantidad inicial.
    public void resupplyStores(){
        for(Store s: stores){
            s.resupply();
        }
        lastOk = true;
    }

    /**
     * Devolver todos los robots a sus posiciones iniciales
     */
    // returnRobots devuelve cada robot a su posición inicial.
    public void returnRobots(){
        for(Robot r: robots){
            r.returnToStart();
        }
        lastOk = true;
    }

    /**
     * Reiniciar la Ruta de la Seda restaurar tiendas y robots tal como fueron añadidos posiciones y dinero inicial y reiniciar la ganancia
     */
    // reboot restaura tiendas, reinicia robots y pone totalProfit a 0
    public void reboot(){
        for(Store s: stores){
            s.resupply();
            s.resetStats();
        }

        for(Robot r: robots){
            r.reset();
        }
        totalProfit = 0;
        moveLog.clear();
        lastOk = true;
    }


    /**
     * Consultar información de tiendas como int[][] ordenado por localización location, tenges
     */
    // stores() devuelve un int[][] con pares [location, tenges] ordenado por location crea copia, ordena y construye el array
    public int[][] stores(){
        List<Store> copy = new ArrayList<Store>(stores);
        Collections.sort(copy, new Comparator<Store>(){
            public int compare(Store a, Store b){ return Integer.compare(a.getLocation(), b.getLocation()); }
        });
        int[][] out = new int[copy.size()][];
        for(int i=0;i<copy.size();i++){
            out[i] = copy.get(i).getInfo();
        }
        return out;
    }

    /**
     * Consultar información de robots como int[][] ordenado por localización location, collected
     */
    // robots() devuelve un int[][] con pares currentLocation, collected ordenado por initialLocation crea copia y ordena
    public int[][] robots(){
        List<Robot> copy = new ArrayList<Robot>(robots);
        Collections.sort(copy, new Comparator<Robot>(){
            public int compare(Robot a, Robot b){ return Integer.compare(a.getInitialLocation(), b.getInitialLocation()); }
        });
        int[][] out = new int[copy.size()][];
        for(int i=0;i<copy.size();i++){
            out[i] = copy.get(i).getInfo();
        }
        return out;
    }

    /**
     * Hacer visibles todas las formas carretera tiendas, robots
     */
    // makeVisible marca visible=true y hace visibles roadShape todas las tiendas y robots
    public void makeVisible(){
        visible = true;
        if(roadShape != null) roadShape.makeVisible();
        for(Store s: stores) s.makeVisible();
        for(Robot r: robots) r.makeVisible();
        lastOk = true;
    }

    /**
     * Hacer invisibles todas las formas del simulador
     */
    // makeInvisible marca visible=false y hace invisibles roadShape, tiendas y robots
    public void makeInvisible(){
        visible = false;
        if(roadShape != null) roadShape.makeInvisible();
        for(Store s: stores) s.makeInvisible();
        for(Robot r: robots) r.makeInvisible();
        lastOk = true;
    }

    /**
     * Finalizar el simulador hacer invisible y marcar como finalizado
     */
    // finish finaliza el simulador llama makeInvisible y lastOk=true
    public void finish(){
        makeInvisible();
        lastOk = true;
    }

    /**
     * ok indica si la última operación se realizó con éxito.
     */
    // ok devuelve true si la última operación fue exitosa lastOk
    public boolean ok(){ return lastOk; }

    /**
     * Devuelve la ganancia total acumulada por todos los robots
     */
    // totalProfit devuelve el valor actual de la ganancia total acumulada
    public int totalProfit(){ return totalProfit; }

    public int profit(){
        return totalProfit;
    }

    public String[] consumeMoveLog(){
        String[] lines = moveLog.toArray(new String[moveLog.size()]);
        moveLog.clear();
        return lines;
    }

    /**
     * Devuelve las tiendas que están vacías (sin tenges) ordenadas por localización de menor a mayor
     * @return array bidimensional con [location, times] donde times indica cuántas veces se ha vaciado
     */
    public int[][] emptiedStores(){
        List<Store> emptied = new ArrayList<Store>();
        for(Store s: stores){
            if(s.getEmptiedCount() > 0){
                emptied.add(s);
            }
        }

        Collections.sort(emptied, new Comparator<Store>(){
            public int compare(Store a, Store b){
                return Integer.compare(a.getLocation(), b.getLocation());
            }
        });

        int[][] result = new int[emptied.size()][2];
        for(int i = 0; i < emptied.size(); i++){
            Store s = emptied.get(i);
            result[i][0] = s.getLocation();
            result[i][1] = s.getEmptiedCount();
        }
        return result;
    }

    private Store buildStore(StoreType type, int location, int tenges){
        StoreType resolved = type == null ? StoreType.NORMAL : type;
        if(resolved == StoreType.AUTONOMOUS){
            return new AutonomousStore(location, tenges, this.roadStartX, this.roadStartY, visible, this.length, stores);
        }
        if(location < 0 || location > length){
            return null;
        }
        int clamped = Math.max(0, Math.min(length, location));
        if(resolved == StoreType.FIGHTER){
            return new FighterStore(clamped, tenges, this.roadStartX, this.roadStartY, visible);
        }
        return new NormalStore(clamped, tenges, this.roadStartX, this.roadStartY, visible);
    }

    private Robot buildRobot(RobotType type, int location){
        RobotType resolved = type == null ? RobotType.NORMAL : type;
        int clamped = Math.max(0, Math.min(length, location));
        if(resolved == RobotType.NEVERBACK){
            return new NeverbackRobot(clamped, this.roadStartX, this.roadStartY, visible);
        }
        if(resolved == RobotType.TENDER){
            return new TenderRobot(clamped, this.roadStartX, this.roadStartY, visible);
        }
        return new NormalRobot(clamped, this.roadStartX, this.roadStartY, visible);
    }

    /**
     * Mueve todos los robots automáticamente hacia la siguiente tienda disponible
     */
    public void moveRobots(){
        lastOk = true;
        while(true){
            Robot bestRobot = null;
            Store bestStore = null;
            int bestProfit = Integer.MIN_VALUE;

            for(Robot r: robots){
                for(Store s: stores){
                    if(!s.hasTenges()){
                        continue;
                    }
                    if(!s.canBeLootedByRobot(r)){
                        continue;
                    }
                    int potentialLoot = s.previewLoot(r);
                    if(potentialLoot <= 0){
                        continue;
                    }
                    int distance = Math.abs(s.getLocation() - r.getCurrentLocation());
                    int profit = potentialLoot - distance;
                    if(profit > bestProfit){
                        bestProfit = profit;
                        bestRobot = r;
                        bestStore = s;
                    }
                }
            }

            if(bestRobot == null || bestStore == null || bestProfit <= 0){
                break;
            }

            int meters = bestStore.getLocation() - bestRobot.getCurrentLocation();
            moveRobot(bestRobot.getInitialLocation(), meters);
        }
    }

    /**
     * Devuelve información de profit por movimiento ordenada por localización
     * @return array con [location, profit_move_1, profit_move_2] para cada posición
     */
    public int[][] profitPerMove(){
        List<Robot> orderedRobots = new ArrayList<Robot>(robots);
        Collections.sort(orderedRobots, new Comparator<Robot>(){
            public int compare(Robot a, Robot b){
                return Integer.compare(a.getInitialLocation(), b.getInitialLocation());
            }
        });

        int[][] result = new int[orderedRobots.size()][3];
        for(int i = 0; i < orderedRobots.size(); i++){
            Robot r = orderedRobots.get(i);
            int[] profits = r.getProfitPerMove();
            int first = profits.length > 0 ? profits[0] : 0;
            int second = profits.length > 1 ? profits[1] : 0;
            result[i][0] = r.getInitialLocation();
            result[i][1] = first;
            result[i][2] = second;
        }
        return result;
    }

}


