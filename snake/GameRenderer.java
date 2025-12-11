package snake;

public class GameRenderer {
    private static final char WALL_CHAR = 'X';
    private static final char SNAKE_CHAR = '#';
    private static final char FOOD_CHAR = '*';
    private static final char EMPTY_CHAR = ' ';

    private final int screenHeight;
    private final int screenWidth;

    public GameRenderer(int screenHeight, int screenWidth) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
    }

    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public String render(Snake snake, Food food, int score) {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < screenHeight; row++) {
            for (int col = 0; col < screenWidth; col++) {
                Position currentPos = new Position(row, col);
                char charToDraw = EMPTY_CHAR;

                // Check walls
                if (row == 0 || row == screenHeight - 1 || col == 0 || col == screenWidth - 1) {
                    charToDraw = WALL_CHAR;
                }

                // Check food
                if (food.isAt(currentPos)) {
                    charToDraw = FOOD_CHAR;
                }

                if (snake.collidesWith(currentPos)) {
                    charToDraw = SNAKE_CHAR;
                }

                sb.append(charToDraw);
            }
            sb.append('\n');
        }

        sb.append("Score: ").append(score);
        return sb.toString();
    }
}