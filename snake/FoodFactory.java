package snake;

import java.util.List;
import java.util.Random;

public class FoodFactory {
    private final int maxRow;
    private final int maxCol;
    private final Random random;

    public FoodFactory(int maxRow, int maxCol) {
        this.maxRow = maxRow;
        this.maxCol = maxCol;
        this.random = new Random();
    }

    public Food createFood() {
        int row = random.nextInt(maxRow - 2) + 1;
        int col = random.nextInt(maxCol - 2) + 1;
        return new Food(new Position(row, col));
    }

    public Food createFoodExcluding(List<Position> excludedPositions) {
        Food food;
        do {
            food = createFood();
        } while (excludedPositions.contains(food.getPosition()));
        return food;
    }
}