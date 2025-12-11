package snake;

public class Food {
    private Position position;

    public Food(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void respawn(Position newPosition) {
        this.position = newPosition;
    }

    public boolean isAt(Position position) {
        return this.position.equals(position);
    }
}