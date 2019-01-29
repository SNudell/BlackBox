import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;

public class Display extends JFrame implements ActionListener  {
    private int BUTTON_WIDTH = 200;
    private int NUMBER_OF_INPUTS_PER_SIDE = 8;
    private int NUMBER_OF_BOXES_PER_SIDE = NUMBER_OF_INPUTS_PER_SIDE+2;
    private Color STANDARD_EDGE_COLOR = Color.lightGray;
    private Color STANDARD_MIDDLE_COLOR = Color.darkGray;
    private Color SELECTED_COLOR = Color.cyan;

    private JButton[][] buttons;
    private boolean[][] tagged;
    private Game game;
    private LinkedList<Point> molecules;
    private LinkedList<Color> unusedColors;

    public static void main(String[] args) {
        JFrame display = new Display();
    }

    public Display() {
        super();
        this.game = new Game(NUMBER_OF_INPUTS_PER_SIDE);
        this.molecules = game.getMolecules();
        //                                                   + 1 is for padding
        int width = BUTTON_WIDTH * (NUMBER_OF_BOXES_PER_SIDE + 1);
        setSize(width, width);
        setTitle("Black Box");
        setBackground(Color.gray);
        GridLayout grid = new GridLayout(NUMBER_OF_BOXES_PER_SIDE, NUMBER_OF_BOXES_PER_SIDE);
        setLayout(grid);
        tagged = new boolean[NUMBER_OF_BOXES_PER_SIDE][NUMBER_OF_BOXES_PER_SIDE];
        buttons = new JButton[NUMBER_OF_BOXES_PER_SIDE][NUMBER_OF_BOXES_PER_SIDE];
        for(int y = 0; y < NUMBER_OF_BOXES_PER_SIDE; y++) {
            for(int x = 0; x < NUMBER_OF_BOXES_PER_SIDE; x++) {
                JButton current = new JButton();
                current.addActionListener(this);
                current.setBorderPainted(true);
                int currentX  = x*BUTTON_WIDTH;
                int currentY = y*BUTTON_WIDTH;
                current.setBounds(currentX, currentY, BUTTON_WIDTH, BUTTON_WIDTH);
                add(current);
                buttons[x][y] = current;
            }
        }
        applyBeginningColoring();
        // reveal molecules
        //for (Point molecule: molecules) {
            //buttons[molecule.x][molecule.y].setBackground(Color.MAGENTA);
        //}
        initColors();
        this.setVisible(true);
    }

    private void applyBeginningColoring() {
        for(int y = 0; y < NUMBER_OF_BOXES_PER_SIDE; y++) {
            for(int x = 0; x < NUMBER_OF_BOXES_PER_SIDE; x++) {
                JButton current = buttons[x][y];
                // if button is on the Outside color it differently
                if (x == 0 || y == 0 || x == NUMBER_OF_BOXES_PER_SIDE-1 || y == NUMBER_OF_BOXES_PER_SIDE -1)
                    current.setBackground(STANDARD_EDGE_COLOR);
                else
                    current.setBackground(STANDARD_MIDDLE_COLOR);
            }
        }
        labelButtons();
    }

    private void labelButtons() {
        buttons[0][0].setText("Check");
        buttons[0][0].setBackground(Color.green);
        buttons[0][NUMBER_OF_BOXES_PER_SIDE-1].setText("Reset");
        buttons[0][NUMBER_OF_BOXES_PER_SIDE-1].setBackground(Color.red);
    }

    private void initColors() {
        this.unusedColors = new LinkedList<Color>();
        for(int r = 0; r < 255; r += 50) {
            for(int g = 0; g < 255; g += 50) {
                for(int b = 0; b < 255; b+=50) {
                    if( r == b && b == g) {
                        continue;
                    }
                    unusedColors.add(new Color(r,g,b));
                }
            }
        }
    }

    private boolean isCornerButton(Point p) {
        if (p.x == 0 && p.y == 0) {
            return true;
        }
        if (p.x == 0 && p.y == NUMBER_OF_BOXES_PER_SIDE-1) {
            return true;
        }
        if (p.x == NUMBER_OF_BOXES_PER_SIDE-1 && p.y == 0) {
            return true;
        }
        if (p.x == NUMBER_OF_BOXES_PER_SIDE-1 && p.y == NUMBER_OF_BOXES_PER_SIDE-1) {
            return true;
        }
        return false;
    }

    private boolean isEdgeButton(Point p) {
        if (p.x == 0 || p.x == NUMBER_OF_BOXES_PER_SIDE-1 || p.y == 0 || p.y == NUMBER_OF_BOXES_PER_SIDE-1) {
            return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent e) {
        Point coordinates = getCoordinatesOfClick(e);

        // if its the Check button
        if (coordinates.x == 0 && coordinates.y == 0) {
            for (Point molecule: molecules) {
                if (!tagged[molecule.x][molecule.y]) {
                    System.out.println("Fail!");
                    gameFinishedColorUpdate();
                    return;
                }
            }
            System.out.println("Win!!!");
            gameFinishedColorUpdate();
            return;
        }

        // if its the resetButton
        if (coordinates.x == 0 && coordinates.y == NUMBER_OF_BOXES_PER_SIDE-1) {
            resetGame();
            return;
        }

        if (isCornerButton(coordinates)) {
            return;
        }

        //if its any of the boxButtons
        if(isEdgeButton(coordinates)) {
            ComputationResult result = null;
            if (coordinates.x == 0) {
                result = game.computeEdge(coordinates, Direction.right);
            } else if (coordinates.x == NUMBER_OF_BOXES_PER_SIDE-1) {
                result = game.computeEdge(coordinates, Direction.left);
            } else if (coordinates.y == 0) {
                result = game.computeEdge(coordinates, Direction.down);
            } else if (coordinates.y == NUMBER_OF_BOXES_PER_SIDE -1){
                result = game.computeEdge(coordinates, Direction.up);
            }
            colorBasedOnResult(coordinates, result);
            return;
        }
        tagged[coordinates.x][coordinates.y] = !tagged[coordinates.x][coordinates.y];
        buttons[coordinates.x][coordinates.y].setBackground((tagged[coordinates.x][coordinates.y]) ? SELECTED_COLOR : STANDARD_MIDDLE_COLOR);
    }

    public void disableButtonAt(Point p) {
        buttons[p.x][p.y].setEnabled(false);
    }

    private void colorBasedOnResult(Point start, ComputationResult result) {
        Point end = result.end;
        int steps = result.steps;
        if (!game.isEdgePoint(end)) {
            colorPoint(start, Color.black);
            disableButtonAt(start);
            return;
        }
        if (steps == 0) {
            colorPoint(start, Color.black);
            disableButtonAt(start);
            return;
        }
        if (end.equals(start)) {
            colorPoint(start, Color.white);
            disableButtonAt(start);
            return;
        }
        Point[] adjacentPoints = game.getAdjacentPoints(start);
        if (Arrays.stream(adjacentPoints).anyMatch( point -> point.equals(end))) {
            if (steps == 2) {
                colorPoint(start, Color.white);
                disableButtonAt(start);
                return;
            }
        }
        int index = (int) Math.floor(Math.random()*unusedColors.size());
        Color c = unusedColors.get(index);
        unusedColors.remove(index);
        colorPoint(start,c);
        disableButtonAt(start);
        colorPoint(end,c);
        disableButtonAt(end);
    }

    public void colorPoint(Point p, Color c) {
        buttons[p.x][p.y].setBackground(c);
    }

    private Point getCoordinatesOfClick(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        for(int y = 0; y < NUMBER_OF_BOXES_PER_SIDE; y++) {
            for (int x = 0; x < NUMBER_OF_BOXES_PER_SIDE; x++) {
                if (buttons[x][y] == b)
                    return new Point(x,y);
            }
        }
        return null;
    }

    private void gameFinishedColorUpdate() {
        for (Point molecule :molecules) {
            colorPoint(molecule, (tagged[molecule.x][molecule.y]) ? Color.green : Color.red);
        }
    }

    private void resetGame() {
        game = new Game(NUMBER_OF_INPUTS_PER_SIDE);
        molecules = game.getMolecules();
        tagged = new boolean[NUMBER_OF_BOXES_PER_SIDE][NUMBER_OF_BOXES_PER_SIDE];
        applyBeginningColoring();
        enableButtons();
    }

    private void enableButtons() {
        Arrays.stream(buttons).forEach(buttonArray -> Arrays.stream(buttonArray).forEach(button -> button.setEnabled(true)));
    }
}
