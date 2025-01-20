package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    /**
     * Алгоритмическая сложность метода generate:

     * 1. Сортировка списка юнитов (sortUnitsByEffectiveness):
     *    - Выполняется один раз для списка из n юнитов.
     *    - Сложность сортировки: O(n log n).

     * 2. Генерация армии:
     *    - Проход по каждому юниту из списка: O(n).
     *    - Для каждого юнита добавление в армию выполняется за O(1).
     *    - Суммарная сложность этой части: O(n).

     * 3. Распределение координат (assignCoordinates):
     *    - Проход по каждому юниту (размер списка — n): O(n).
     *    - Генерация случайных координат и проверка уникальности:
     *        - Использование HashSet для проверки и добавления координат:
     *          средняя сложность одной операции — O(1).
     *    - Итоговая сложность распределения координат: O(n).

     * Общая сложность:
     *    - Сортировка: O(n log n).
     *    - Генерация армии: O(n).
     *    - Распределение координат: O(n).
     *    - Итоговая сложность: O(n log n), так как сортировка доминирует.
     */
    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        Army computerArmy = new Army();
        List<Unit> selectedUnits = new ArrayList<>();

        // Сортируем юниты по их эффективности
        sortUnitsByEffectiveness(unitList);

        int currentPoints = 0;
        Map<String, Integer> unitTypeCount = new HashMap<>();

        // Проходим по списку юнитов и добавляем их в армию
        for (Unit unit : unitList) {
            int unitCount = unitTypeCount.getOrDefault(unit.getUnitType(), 0);

            // Рассчитываем максимальное количество юнитов, которые можно добавить
            int unitsToAdd = Math.min(11 - unitCount, (maxPoints - currentPoints) / unit.getCost());

            for (int i = 0; i < unitsToAdd; i++) {
                Unit newUnit = createNewUnit(unit, unitCount + 1);
                selectedUnits.add(newUnit);
                currentPoints += unit.getCost();
                unitTypeCount.put(unit.getUnitType(), unitCount + 1);
                unitCount++;
            }
        }

        // Распределяем координаты с учетом их силы
        assignCoordinates(selectedUnits);

        computerArmy.setUnits(selectedUnits);
        computerArmy.setPoints(currentPoints);

        return computerArmy;
    }

    // Сортировка юнитов по их эффективности (атака и здоровье учитываются одновременно)
    private void sortUnitsByEffectiveness(List<Unit> units) {
        units.sort((unit1, unit2) -> {
            double effectiveness1 = ((double) unit1.getBaseAttack() / unit1.getCost()) + ((double) unit1.getHealth() / unit1.getCost());
            double effectiveness2 = ((double) unit2.getBaseAttack() / unit2.getCost()) + ((double) unit2.getHealth() / unit2.getCost());
            return Double.compare(effectiveness2, effectiveness1); // Сортируем по убыванию эффективности
        });
    }

    // Создаем новый юнит без указания координат (они будут назначены позже)
    private Unit createNewUnit(Unit unit, int index) {
        return new Unit(
                unit.getUnitType() + " " + index, // Уникальное имя юнита
                unit.getUnitType(),
                unit.getHealth(),
                unit.getBaseAttack(),
                unit.getCost(),
                unit.getAttackType(),
                unit.getAttackBonuses(),
                unit.getDefenceBonuses(),
                0, 0 // Не задаем координаты (будут переписаны в assignCoordinates)
        );
    }

    // Распределяем координаты юнитов
    private void assignCoordinates(List<Unit> units) {
        Set<String> occupiedCoordinates = new HashSet<>();
        Random random = new Random();

        for (Unit unit : units) {
            int x, y;
            do {
                x = random.nextInt(3); // Ширина поля: 3
                y = random.nextInt(21); // Высота поля: 21
            } while (!occupiedCoordinates.add(x + "," + y)); // Генерируем до тех пор, пока координата не станет уникальной

            unit.setxCoordinate(x);
            unit.setyCoordinate(y);
        }
    }
}