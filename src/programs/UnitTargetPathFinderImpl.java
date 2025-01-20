package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    private static final int FIELD_WIDTH = 27;
    private static final int FIELD_HEIGHT = 21;
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Возможные направления движения

    /**
     * Алгоритмическая сложность метода getTargetPath:

     * 1. Инициализация:
     *    - Инициализация массива расстояний: O(WIDTH × HEIGHT).
     *    - Создание списка занятых клеток (occupiedCells): O(k), где k — количество юнитов в existingUnitList.
     *    - Инициализация PriorityQueue и стартовой точки: O(1).
     *    - Суммарная сложность этой части: O(WIDTH × HEIGHT + k).

     * 2. Построение графа:
     *    - Каждый узел графа проверяется и добавляется в очередь: O(WIDTH × HEIGHT).
     *    - Для каждой точки (узла) рассматриваются 4 соседние клетки:
     *      - Проверка валидности координат и обновление: O(1).
     *      - Сложность на все узлы: O(WIDTH × HEIGHT × DIRECTIONS).
     *    - Вставка и удаление из PriorityQueue: O(log(WIDTH × HEIGHT)).
     *    - Итоговая сложность этой части: O((WIDTH × HEIGHT) × log(WIDTH × HEIGHT)).

     * 3. Восстановление пути:
     *    - Проход по узлам пути от целевой клетки до начальной: O(d), где d — длина пути.
     *    - Сложность: O(d), где d ≤ WIDTH × HEIGHT.

     * Общая сложность:
     *    - Инициализация: O(WIDTH × HEIGHT + k).
     *    - Построение графа (поиск пути): O((WIDTH × HEIGHT) × log(WIDTH × HEIGHT)).
     *    - Восстановление пути: O(WIDTH × HEIGHT) в худшем случае.
     *    - Итоговая сложность: O((WIDTH × HEIGHT) × log(WIDTH × HEIGHT)), так как поиск пути доминирует.
     */
    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        // Построение графа
        boolean[][] occupiedCells = markOccupiedCells(existingUnitList, attackUnit, targetUnit);

        // Используем PriorityQueue для алгоритма Дейкстры
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(Node::getCost));
        Map<String, Node> graph = new HashMap<>();
        initializeGraph(graph, occupiedCells);

        // Начальная и конечная точки
        String startKey = attackUnit.getxCoordinate() + "," + attackUnit.getyCoordinate();
        String targetKey = targetUnit.getxCoordinate() + "," + targetUnit.getyCoordinate();

        // Если начальная или конечная точка занята, возвращаем пустой путь
        if (!graph.containsKey(startKey) || !graph.containsKey(targetKey)) {
            return Collections.emptyList();
        }

        // Инициализация начального узла
        Node startNode = graph.get(startKey);
        startNode.setCost(0);
        queue.add(startNode);

        // Алгоритм поиска пути
        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.getKey().equals(targetKey)) {
                return reconstructPath(current);
            }

            for (Node neighbor : current.getNeighbors()) {
                int newCost = current.getCost() + 1; // Все ребра имеют вес 1
                if (newCost < neighbor.getCost()) {
                    neighbor.setCost(newCost);
                    neighbor.setPrevious(current);
                    queue.add(neighbor);
                }
            }
        }

        // Если путь не найден
        return Collections.emptyList();
    }

    // Построение графа из клеток игрового поля.
    private void initializeGraph(Map<String, Node> graph, boolean[][] occupiedCells) {
        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_HEIGHT; y++) {
                if (!occupiedCells[x][y]) {
                    String key = x + "," + y;
                    int finalX = x;
                    int finalY = y;
                    Node node = graph.computeIfAbsent(key, k -> new Node(finalX, finalY));
                    for (int[] direction : DIRECTIONS) {
                        int nx = x + direction[0];
                        int ny = y + direction[1];
                        if (isWithinBounds(nx, ny) && !occupiedCells[nx][ny]) {
                            String neighborKey = nx + "," + ny;
                            Node neighbor = graph.computeIfAbsent(neighborKey, k -> new Node(nx, ny));
                            node.addNeighbor(neighbor);
                        }
                    }
                }
            }
        }
    }

    // Определяет занятые клетки.
    private boolean[][] markOccupiedCells(List<Unit> existingUnitList, Unit attackUnit, Unit targetUnit) {
        boolean[][] occupied = new boolean[FIELD_WIDTH][FIELD_HEIGHT];
        for (Unit unit : existingUnitList) {
            if (unit.isAlive() && unit != attackUnit && unit != targetUnit) {
                occupied[unit.getxCoordinate()][unit.getyCoordinate()] = true;
            }
        }
        return occupied;
    }

    // Восстановление пути, начиная с конечного узла.
    private List<Edge> reconstructPath(Node targetNode) {
        List<Edge> path = new ArrayList<>();
        Node current = targetNode;

        while (current != null) {
            path.add(new Edge(current.getX(), current.getY()));
            current = current.getPrevious();
        }

        Collections.reverse(path);
        return path;
    }

    // Проверка валидности координат.
    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < FIELD_WIDTH && y >= 0 && y < FIELD_HEIGHT;
    }

    // Вспомогательный класс для представления узла графа.
    private static class Node {
        private final int x;
        private final int y;
        private int cost;
        private Node previous;
        private final List<Node> neighbors;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
            this.cost = Integer.MAX_VALUE; // Изначально недостижимо
            this.neighbors = new ArrayList<>();
        }

        public void addNeighbor(Node neighbor) {
            neighbors.add(neighbor);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public Node getPrevious() {
            return previous;
        }

        public void setPrevious(Node previous) {
            this.previous = previous;
        }

        public List<Node> getNeighbors() {
            return neighbors;
        }

        public String getKey() {
            return x + "," + y;
        }
    }
}