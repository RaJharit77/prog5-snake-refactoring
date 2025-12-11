package snake;

public class InputHandler {
    public Direction handleInput(char input) {
        return switch (Character.toLowerCase(input)) {
            case 'w' -> Direction.UP;
            case 's' -> Direction.DOWN;
            case 'a' -> Direction.LEFT;
            case 'd' -> Direction.RIGHT;
            default -> null;
        };
    }
}