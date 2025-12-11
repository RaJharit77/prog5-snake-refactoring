package snake;

import java.util.*;
import java.io.IOException;

/**
 * 
 * SPECS :
 * Characters :
 * - Snake
 * - Food
 * - Score
 * - Screen
 * - Direction
 * 
 * Rules :
 * - Tant que le jeu lancé :
 *  Si => utilisateur clic sur "a" + entré et direction non "RIGHT" va gauche
 *  Si => utilisateur clic sur "d" + entré et direction non "LEFT" va droite
 *  Si => utilisateur clic sur "w" + entré et direction non "DOWN" va haut
 *  Si => utilisateur clic sur "s" + entré et direction non "UP" va bas
 *  Si => Sakafo azo, mihalava Score++
 *  Si => bibilave midona amin'ny vatany/"sisinà screen" à une lapse de temps 
 *          => mort
 *          => Game over + show score
 *          => Reset score/bibilave/position
 * 
 * Pattern :
 * - Observer => Observer direction snake
 * - Factory => Conrétiser les objets
 * - State => Snake (isMaty), Sakafo (isLany), Jeu (isTapitra)
 * - Command => execute (start, change direction)
 * 
 * EXERCICE DE REFACTORING
 *
 * Ce code fonctionne, mais viole de nombreux principes de bonne pratique.
 * Votre mission : le refactorer pour en faire un code propre et maintenable.
 *
 * PROBLÈMES À CORRIGER :
 * 1. Noms de variables cryptiques (sh, sw, s, f, d, mv, hd, etc.)
 * 2. Présence de « magic numbers » partout (20, 40, 120, etc.)
 * 3. Méthode main() monolithique – aucune séparation des responsabilités
 * 4. Pas de classes/objets – tout repose sur des primitives et des tableaux
 * 5. Gestion des directions basée sur des chaînes de caractères (fragile)
 * 6. Affichage mélangé avec la logique du jeu
 * 7. Aucune constante pour les caractères spéciaux
 * 8. Détection de collisions inefficace (boucles imbriquées)
 * 9. Aucune validation des entrées utilisateur
 * 10. Séquences d’échappement du terminal codées en dur
 *
 * AMÉLIORATIONS SUGGÉRÉES :
 * - Créer des classes : Game, Snake, Food, Position, Direction (enum)
 * - Extraire des constantes : SCREEN_HEIGHT, SCREEN_WIDTH, TICK_DELAY
 * - Séparer en méthodes : update(), render(), handleInput(), checkCollisions()
 * - Utiliser des noms explicites : screenHeight au lieu de sh, snake au lieu de s
 * - Envisager une grille 2D pour une détection de collision plus efficace
 * - Ajouter des commentaires
 */
public class BadSnake {
    // ENUMS
    public enum Direction {
        UP, DOWN, LEFT, RIGHT;

        public boolean isOpposite(snake.BadSnake.Direction other) {
            return (this == UP && other == DOWN) ||
                    (this == DOWN && other == UP) ||
                    (this == LEFT && other == RIGHT) ||
                    (this == RIGHT && other == LEFT);
        }
    }

    public enum GameState {
        RUNNING, GAME_OVER, PAUSED
    }

    // CLASSES

    public static class Position {
        public final int row;
        public final int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            snake.BadSnake.Position position = (snake.BadSnake.Position) obj;
            return row == position.row && col == position.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        public snake.BadSnake.Position move(snake.BadSnake.Direction direction) {
            return switch (direction) {
                case UP -> new snake.BadSnake.Position(row - 1, col);
                case DOWN -> new snake.BadSnake.Position(row + 1, col);
                case LEFT -> new snake.BadSnake.Position(row, col - 1);
                case RIGHT -> new snake.BadSnake.Position(row, col + 1);
                default -> this;
            };
        }
    }

    public static class Snake {
        private LinkedList<snake.BadSnake.Position> body;
        private snake.BadSnake.Direction currentDirection;
        private snake.BadSnake.Direction nextDirection;

        public Snake(snake.BadSnake.Position startPosition) {
            this.body = new LinkedList<>();
            this.body.add(startPosition);
            this.body.add(new snake.BadSnake.Position(startPosition.row, startPosition.col - 1));
            this.body.add(new snake.BadSnake.Position(startPosition.row, startPosition.col - 2));
            this.currentDirection = snake.BadSnake.Direction.RIGHT;
            this.nextDirection = snake.BadSnake.Direction.RIGHT;
        }

        public void updateDirection(snake.BadSnake.Direction newDirection) {
            if (!newDirection.isOpposite(currentDirection)) {
                nextDirection = newDirection;
            }
        }

        public snake.BadSnake.Position move(boolean shouldGrow) {
            currentDirection = nextDirection;
            snake.BadSnake.Position head = body.getFirst();
            snake.BadSnake.Position newHead = head.move(currentDirection);

            if (!shouldGrow) {
                body.removeLast();
            }
            body.addFirst(newHead);

            return newHead;
        }

        public boolean collidesWith(snake.BadSnake.Position position) {
            return body.contains(position);
        }

        public boolean collidesWithItself() {
            snake.BadSnake.Position head = body.getFirst();
            for (int i = 1; i < body.size(); i++) {
                if (head.equals(body.get(i))) {
                    return true;
                }
            }
            return false;
        }

        public List<snake.BadSnake.Position> getBody() {
            return Collections.unmodifiableList(body);
        }

        public snake.BadSnake.Position getHead() {
            return body.getFirst();
        }
    }

    public static class Food {
        private snake.BadSnake.Position position;

        public Food(snake.BadSnake.Position position) {
            this.position = position;
        }

        public snake.BadSnake.Position getPosition() {
            return position;
        }

        public void respawn(snake.BadSnake.Position newPosition) {
            this.position = newPosition;
        }
    }

    public static class FoodFactory {
        private final int maxRow;
        private final int maxCol;
        private final Random random;

        public FoodFactory(int maxRow, int maxCol) {
            this.maxRow = maxRow;
            this.maxCol = maxCol;
            this.random = new Random();
        }

        public snake.BadSnake.Food createFood() {
            int row = random.nextInt(maxRow - 2) + 1;
            int col = random.nextInt(maxCol - 2) + 1;
            return new snake.BadSnake.Food(new snake.BadSnake.Position(row, col));
        }

        public snake.BadSnake.Food createFoodExcluding(List<snake.BadSnake.Position> excludedPositions) {
            snake.BadSnake.Food food;
            do {
                food = createFood();
            } while (excludedPositions.contains(food.getPosition()));
            return food;
        }
    }

    public static class GameRenderer {
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

        public String render(snake.BadSnake.Snake snake, snake.BadSnake.Food food, int score) {
            StringBuilder sb = new StringBuilder();

            for (int row = 0; row < screenHeight; row++) {
                for (int col = 0; col < screenWidth; col++) {
                    snake.BadSnake.Position currentPos = new snake.BadSnake.Position(row, col);
                    char charToDraw = EMPTY_CHAR;

                    if (row == 0 || row == screenHeight - 1 || col == 0 || col == screenWidth - 1) {
                        charToDraw = WALL_CHAR;
                    }

                    if (food.getPosition().equals(currentPos)) {
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

    public static class InputHandler {
        public snake.BadSnake.Direction handleInput(char input, snake.BadSnake.Direction currentDirection) {
            return switch (Character.toLowerCase(input)) {
                case 'w' -> snake.BadSnake.Direction.UP;
                case 's' -> snake.BadSnake.Direction.DOWN;
                case 'a' -> snake.BadSnake.Direction.LEFT;
                case 'd' -> snake.BadSnake.Direction.RIGHT;
                default -> currentDirection;
            };
        }
    }

    public static class Game {
        private static final int DEFAULT_SCREEN_HEIGHT = 20;
        private static final int DEFAULT_SCREEN_WIDTH = 40;
        private static final int TICK_DELAY_MS = 120;

        private final int screenHeight;
        private final int screenWidth;

        private snake.BadSnake.Snake snake;
        private snake.BadSnake.Food food;
        private snake.BadSnake.FoodFactory foodFactory;
        private snake.BadSnake.GameRenderer renderer;
        private snake.BadSnake.InputHandler inputHandler;

        private int score;
        private GameState state;

        public Game() {
            this(DEFAULT_SCREEN_HEIGHT, DEFAULT_SCREEN_WIDTH);
        }

        public Game(int screenHeight, int screenWidth) {
            this.screenHeight = screenHeight;
            this.screenWidth = screenWidth;
            this.renderer = new snake.BadSnake.GameRenderer(screenHeight, screenWidth);
            this.inputHandler = new snake.BadSnake.InputHandler();
            this.foodFactory = new snake.BadSnake.FoodFactory(screenHeight, screenWidth);
            initializeGame();
        }

        private void initializeGame() {
            snake.BadSnake.Position startPosition = new snake.BadSnake.Position(screenHeight / 2, screenWidth / 2);
            this.snake = new snake.BadSnake.Snake(startPosition);
            this.food = foodFactory.createFoodExcluding(snake.getBody());
            this.score = 0;
            this.state = GameState.RUNNING;
        }

        public void update() throws InterruptedException, IOException {
            while (state == GameState.RUNNING) {
                if (System.in.available() > 0) {
                    char input = (char) System.in.read();
                    snake.BadSnake.Direction newDirection = inputHandler.handleInput(input, null);
                    snake.updateDirection(newDirection);
                }

                snake.BadSnake.Position newHead = snake.move(false);

                if (isWallCollision(newHead) || snake.collidesWithItself()) {
                    gameOver();
                    return;
                }

                if (newHead.equals(food.getPosition())) {
                    score++;
                    snake.move(true);
                    food = foodFactory.createFoodExcluding(snake.getBody());
                }

                renderer.clearScreen();
                System.out.println(renderer.render(snake, food, score));

                Thread.sleep(TICK_DELAY_MS);
            }
        }

        private boolean isWallCollision(snake.BadSnake.Position position) {
            return position.row <= 0 || position.row >= screenHeight - 1 ||
                    position.col <= 0 || position.col >= screenWidth - 1;
        }

        private void gameOver() {
            state = GameState.GAME_OVER;
            System.out.println("GAME OVER - SCORE = " + score);
            System.out.println("Press any key to restart...");
        }

        public void restart() {
            initializeGame();
        }

        public snake.BadSnake.GameState getState() {
            return state;
        }

        public int getScore() {
            return score;
        }
    }


    public static void main(String[] args) throws Exception {
        snake.BadSnake.Game snakeGame = new snake.BadSnake.Game();
        snakeGame.update();
    }
}