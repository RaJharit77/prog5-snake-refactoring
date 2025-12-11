package snake;

import java.io.IOException;

public class Game {
    public static final int DEFAULT_SCREEN_HEIGHT = 20;
    public static final int DEFAULT_SCREEN_WIDTH = 40;
    public static final int TICK_DELAY_MS = 120;

    private final int screenHeight;
    private final int screenWidth;

    private Snake snake;
    private Food food;
    private final FoodFactory foodFactory;
    private final GameRenderer renderer;
    private final InputHandler inputHandler;

    private int score;
    private GameState state;

    public Game() {
        this(DEFAULT_SCREEN_HEIGHT, DEFAULT_SCREEN_WIDTH);
    }

    public Game(int screenHeight, int screenWidth) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.renderer = new GameRenderer(screenHeight, screenWidth);
        this.inputHandler = new InputHandler();
        this.foodFactory = new FoodFactory(screenHeight, screenWidth);
        initializeGame();
    }

    private void initializeGame() {
        Position startPosition = new Position(screenHeight / 2, screenWidth / 2);
        this.snake = new Snake(startPosition);
        this.food = foodFactory.createFoodExcluding(snake.getBody());
        this.score = 0;
        this.state = GameState.RUNNING;
    }

    public void update() throws InterruptedException, IOException {
        while (state == GameState.RUNNING) {
            // Handle input
            if (System.in.available() > 0) {
                char input = (char) System.in.read();
                Direction newDirection = inputHandler.handleInput(input);
                if (newDirection != null) {
                    snake.updateDirection(newDirection);
                }
            }

            // Move snake
            Position newHead = snake.move(false);

            // Check collisions
            if (isWallCollision(newHead) || snake.collidesWithItself()) {
                gameOver();
                return;
            }

            // Check food collision
            if (newHead.equals(food.getPosition())) {
                score++;
                snake.move(true); // Grow the snake
                food = foodFactory.createFoodExcluding(snake.getBody());
            }

            // Render
            renderer.clearScreen();
            System.out.println(renderer.render(snake, food, score));

            Thread.sleep(TICK_DELAY_MS);
        }
    }

    private boolean isWallCollision(Position position) {
        return position.getRow() <= 0 || position.getRow() >= screenHeight - 1 ||
                position.getCol() <= 0 || position.getCol() >= screenWidth - 1;
    }

    private void gameOver() {
        state = GameState.GAME_OVER;
        System.out.println("GAME OVER - SCORE = " + score);
    }

    public void restart() {
        initializeGame();
    }

    public GameState getState() {
        return state;
    }

    public int getScore() {
        return score;
    }

    public Snake getSnake() {
        return snake;
    }

    public Food getFood() {
        return food;
    }
}