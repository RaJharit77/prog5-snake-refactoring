package snake;

public class SnakeGame {
    public static void main(String[] args) {
        try {
            Game game = new Game();
            System.out.println("Snake Game Started!");
            System.out.println("Controls: W (Up), S (Down), A (Left), D (Right)");
            System.out.println("Press any key to start...");

            System.in.read();

            game.update();

            if (game.getState() == GameState.GAME_OVER) {
                System.out.println("\nGame Over! Final Score: " + game.getScore());
                System.out.println("Would you like to play again? (y/n)");

                char choice = (char) System.in.read();
                if (Character.toLowerCase(choice) == 'y') {
                    game.restart();
                    game.update();
                }
            }

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}