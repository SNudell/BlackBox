import java.awt.*;
import java.util.LinkedList;

import static java.awt.Color.black;

public class Game {

    private static int NUM_OF_MOLECULES = 5;

    private LinkedList<Point> molecules;
    private int size;
    private Color[][] board;


    public Game(int size) {
        this.size = size;
        this.molecules = new LinkedList<Point>();
        for(int i = 0; i < NUM_OF_MOLECULES; i++) {
            Point coordinates;
            do {
                coordinates = getRandomCoordinates();
            } while (isMolecule(coordinates));
            molecules.add(coordinates);
        }
        initBoard();
    }

    private void initBoard() {
        board = new Color[size+2][size+2];
        for(int y = 0; y < board.length; y++) {
            for (int x = 0; x < board.length; x++) {
                Point p = new Point(x, y);
                board[x][y] = (isMolecule(p)) ? black : Color.white;
            }
        }
        computeEdges();
    }

    public ComputationResult computeEdge(Point start, Direction direction) {
        return computeEdge(start, direction,0);
    }

    private void computeEdges() {
        for(int y = 0; y < board.length; y++) {
            // TODO initiate all the computations
        }
    }

    public boolean isEdgePoint(Point p) {
        if (p.x == 0 || p.x == size+1) {
            return true;
        }
        if (p.y == 0 || p.y == size+1) {
            return true;
        }
        return false;
    }

    private boolean isOutOfBounds(Point p) {
        if(p.x < size+2 && p.x >= 0 && p.y < size+2 && p.y >= 0) {
            return false;
        }
        return true;
    }

    private ComputationResult computeEdge(Point start,Direction direction, int step) {
        printPosition(start);
        if (isOutOfBounds(start)) {
            return null;
        }
        if (step > 0 && isEdgePoint(start)) {
            return new ComputationResult(start, step);
        }
        LinkedList<Point> adjacentMolecules = getAdjacentMolecules(start);
        if (adjacentMolecules.isEmpty()) {
            return computeEdge(nextPointInDirection(start, direction), direction, step+1);
        }

        if (adjacentMolecules.size() == 1) {
            Point molecule = adjacentMolecules.getFirst();
            //running directly into molecule -> gets absorbed
            if (molecule.equals(nextPointInDirection(start,direction))) {
                return new ComputationResult(molecule, step);
            }
            //otherwise the molecule has to be at one of the edges so change direction accordingly
            return changingDirection(start, direction, molecule, step+1);
        }

        if (adjacentMolecules.size() == 2) {
            // if one of the two molecules is the one directly in front end
            if (adjacentMolecules.contains(nextPointInDirection(start, direction))) {
                return new ComputationResult(start, step);
            } else {
                // both are edge molecules and therefor the direction needs to be inverted
                Direction newDirection = invertDirection(direction);
                Point newStart = nextPointInDirection(start, newDirection);
                if(isOutOfBounds(newStart)) {
                    return new ComputationResult(start, step+1);
                }
                return computeEdge(nextPointInDirection(start, newDirection), newDirection, step+1);
            }
        }

        // now all 3 next pieces are molecules so we need abort
        return new ComputationResult(start, step);
    }

    private Direction invertDirection(Direction direction) {
        switch (direction) {
            case up:
                return Direction.down;
            case down:
                return Direction.up;
            case left:
                return Direction.right;
            case right:
                return Direction.left;
        }
        return null;
    }

    private ComputationResult changingDirection(Point start, Direction direction, Point molecule, int step) {
        Direction newDirection = Direction.up;
        Point newStart = null;
        switch (direction) {
            case down:
                if (molecule.y == start.y - 1) {
                    newStart = nextPointInDirection(start, direction);
                    newDirection = direction;
                    break;
                }
                if (molecule.x == start.x - 1) {
                    newDirection = Direction.right;
                    newStart = new Point(start.x + 1, start.y);
                    break;
                } else {
                    newDirection = Direction.left;
                    newStart = new Point(start.x - 1, start.y);
                    break;
                }
            case up:
                if (molecule.y == start.y + 1) {
                    newStart = nextPointInDirection(start, direction);
                    newDirection = direction;
                    break;
                }
                if (molecule.x == start.x - 1) {
                    newDirection = Direction.right;
                    newStart = new Point(start.x + 1, start.y);
                    break;
                } else {
                    newDirection = Direction.left;
                    newStart = new Point(start.x - 1, start.y);
                    break;
                }
            case left:
                if (molecule.x == start.x + 1) {
                    newStart = nextPointInDirection(start, direction);
                    newDirection = direction;
                    break;
                }
                if (molecule.y == start.y - 1) {
                    newDirection = Direction.down;
                    newStart = new Point(start.x, start.y + 1);
                    break;
                } else {
                    newDirection = Direction.up;
                    newStart = new Point(start.x, start.y - 1);
                    break;
                }
            case right:
                if (molecule.x == start.x - 1) {
                    newStart = nextPointInDirection(start, direction);
                    newDirection = direction;
                    break;
                }
                if (molecule.y == start.y - 1) {
                    newDirection = Direction.down;
                    newStart = new Point(start.x, start.y + 1);
                    break;
                } else {
                    newDirection = Direction.up;
                    newStart = new Point(start.x, start.y - 1);
                    break;
                }

        }
        return computeEdge(newStart, newDirection, step+1);
    }

    private Point nextPointInDirection(Point start, Direction direction) {
        switch (direction) {
            case up:
                return new Point(start.x, start.y-1);
            case down:
                return new Point(start.x, start.y+1);
            case left:
                return new Point(start.x-1, start.y);
            case right:
                return new Point(start.x+1, start.y);
        }
        return null;
    }

    public Point[] getAdjacentPoints(Point p) {
        Point[] points = new Point[8];
        points[0] = new Point(p.x-1, p.y-1);
        points[1] = new Point(p.x, p.y-1);
        points[2] = new Point(p.x+1, p.y-1);
        points[3] = new Point(p.x+1, p.y);
        points[4] = new Point(p.x+1, p.y+1);
        points[5] = new Point(p.x, p.y+1);
        points[6] = new Point(p.x-1, p.y+1);
        points[7] = new Point(p.x-1, p.y);
        return points;
    }

    private LinkedList<Point> getAdjacentMolecules(Point p) {
        Point[] adjacentPoints = getAdjacentPoints(p);
        LinkedList<Point> adjacentMolecules = new LinkedList<Point>();
        for (Point adjacentPoint: adjacentPoints) {
            if (isMolecule(adjacentPoint))
                adjacentMolecules.add(adjacentPoint);
        }
        return adjacentMolecules;
    }

    private Point getRandomCoordinates() {
        //so they are in the inner circle
        int x = (int) Math.floor((Math.random() * size))+1;
        int y = (int) Math.floor((Math.random() * size))+1;
        return new Point(x, y);
    }

    public LinkedList<Point> getMolecules() {
        return molecules;
    }

    public void printPosition(Point p) {
        for(int y = 0; y < board.length; y++) {
            for (int x = 0; x < board.length; x++) {
                Point current = new Point(x, y);
                if (isMolecule(current)) {
                    System.out.print("M ");
                } else if (current.equals(p)) {
                    System.out.print("X ");
                }else {
                    System.out.print("O ");
                }
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("");
    }

    private boolean isMolecule(Point p) {
        return molecules.stream().anyMatch(point -> (point.x == p.x && point.y == p.y));
    }
}
