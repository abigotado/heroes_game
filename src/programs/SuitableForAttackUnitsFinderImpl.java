package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    /**
     * Алгоритмическая сложность метода getSuitableUnits:

     * 1. Внешний цикл по рядам (unitsByRow):
     *    - Выполняется один раз для каждого ряда.
     *    - Сложность: O(m), где m — количество рядов.
     *    - Поскольку m фиксировано (равно 3), это константная сложность: O(1).

     * 2. Внутренний цикл по юнитам в ряду:
     *    - Для каждого рда метод обходит всех юнитов.
     *    - Сложность: O(n), где n — количество юнитов в строке.

     * 3. Проверка условий доступности юнита для атаки:
     *    - Выполняется за O(1) для каждого юнита.

     * 4. Добавление подходящих юнитов в список:
     *    - Выполняется за O(1) для каждого подходящего юнита.

     * Итоговая сложность:
     *    - Внешний цикл: O(m).
     *    - Внутренний цикл: O(n).
     *    - Общая сложность: O(m * n).

     * Упрощение сложности:
     *    - Поскольку количество рядов (m) фиксировано (3), фактическая сложность:
     *      O(m * n) = O(3 * n) = O(n).
     */
    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        for (List<Unit> row : unitsByRow) {
            for (int i = 0; i < row.size(); i++) {
                Unit currentUnit = row.get(i);

                // Если юнит уже мёртв, пропускаем его
                if (!currentUnit.isAlive()) {
                    continue;
                }

                // Проверяем условия доступности для атаки
                if (isLeftArmyTarget) {
                    // Атакуется армия компьютера: юнит не закрыт справа
                    if (i == row.size() - 1 || !row.get(i + 1).isAlive()) {
                        suitableUnits.add(currentUnit);
                    }
                } else {
                    // Атакуется армия игрока: юнит не закрыт слева
                    if (i == 0 || !row.get(i - 1).isAlive()) {
                        suitableUnits.add(currentUnit);
                    }
                }
            }
        }

        return suitableUnits;
    }
}
