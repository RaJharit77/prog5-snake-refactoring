package snake;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Snake {
    private LinkedList<Position> body;
    private Direction currentDirection;
    private Direction nextDirection;

    public Snake(Position startPosition) {
        this.body = new LinkedList<>();
        this.body.add(startPosition);
        this.body.add(new Position(startPosition.getRow(), startPosition.getCol() - 1));
        this.body.add(new Position(startPosition.getRow(), startPosition.getCol() - 2));
        this.currentDirection = Direction.RIGHT;
        this.nextDirection = Direction.RIGHT;
    }

    public void updateDirection(Direction newDirection) {
        if (!newDirection.isOpposite(currentDirection)) {
            nextDirection = newDirection;
        }
    }

    public Position move(boolean shouldGrow) {
        currentDirection = nextDirection;
        Position head = body.getFirst();
        Position newHead = movePosition(head, currentDirection);

        if (!shouldGrow) {
            body.removeLast();
        }
        body.addFirst(newHead);

        return newHead;
    }

    private Position movePosition(Position position, Direction direction) {
        switch (direction) {
            case UP: return new Position(position.getRow() - 1, position.getCol());
            case DOWN: return new Position(position.getRow() + 1, position.getCol());
            case LEFT: return new Position(position.getRow(), position.getCol() - 1);
            case RIGHT: return new Position(position.getRow(), position.getCol() + 1);
            default: return position;
        }
    }

    public boolean collidesWith(Position position) {
        return body.contains(position);
    }

    public boolean collidesWithItself() {
        Position head = body.getFirst();
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }

    public List<Position> getBody() {
        return Collections.unmodifiableList(body);
    }

    public Position getHead() {
        return body.getFirst();
    }

    public int getLength() {
        return body.size();
    }
}