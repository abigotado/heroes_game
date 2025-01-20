package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog;

    /**
     * Алгоритмическая сложность метода simulate:
     * 1. Инициализация:
     *    - Сортировка юнитов в методе initializeUnits: O(n log n), где n — общее количество юнитов.
     * 2. Основной цикл битвы:
     *    - Для каждого атакующего юнита проверяется и обрабатывается каждая цель: O(n).
     *    - Удаление мертвых юнитов с использованием итератора: O(1) для каждого юнита.
     * 3. Обновление списков защищающихся:
     *    - Удаление мертвых юнитов выполняется за O(k), где k — количество защищающихся.
     * 4. Максимальное количество раундов — n (в худшем случае).
     * Итоговая сложность: O(n^2), так как основной доминирующий фактор — проверки и атаки каждого юнита.
     */
    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        List<Unit> playerUnits = initializeUnits(playerArmy);
        List<Unit> computerUnits = initializeUnits(computerArmy);

        while (!playerUnits.isEmpty() && !computerUnits.isEmpty()) {
            // Ход игрока
            executeAttacks(playerUnits, computerUnits);

            // Если все юниты компьютера уничтожены, завершаем бой
            if (computerUnits.isEmpty()) break;

            // Ход компьютера
            executeAttacks(computerUnits, playerUnits);

            // Если все юниты игрока уничтожены, завершаем бой
            if (playerUnits.isEmpty()) break;
        }
    }

    private List<Unit> initializeUnits(Army army) {
        List<Unit> units = new ArrayList<>(army.getUnits());
        units.removeIf(unit -> !unit.isAlive()); // Удаляем мертвые юниты
        units.sort((u1, u2) -> Integer.compare(u2.getBaseAttack(), u1.getBaseAttack())); // Сортировка по атаке
        return units;
    }

    private void executeAttacks(List<Unit> attackingUnits, List<Unit> defendingUnits) throws InterruptedException {
        Iterator<Unit> iterator = defendingUnits.iterator();

        for (Unit attacker : attackingUnits) {
            if (!attacker.isAlive()) continue; // Пропускаем мертвых атакующих

            while (iterator.hasNext()) {
                Unit target = iterator.next();
                if (!target.isAlive()) {
                    iterator.remove(); // Удаляем мертвых из защищающихся
                    continue;
                }

                // Выполняем атаку
                attacker.getProgram().attack();
                printBattleLog.printBattleLog(attacker, target);

                // Если цель погибла, удаляем её
                if (!target.isAlive()) {
                    iterator.remove();
                }
                break; // Атакующий завершил атаку
            }
        }
    }
}
