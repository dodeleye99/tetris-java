package com.yahoo.dodeleye.tetris.view;

import com.yahoo.dodeleye.tetris.Tetris1;
import com.yahoo.dodeleye.tetris.model.GameLogic;
import com.yahoo.dodeleye.tetris.model.Playfield;
import com.yahoo.dodeleye.tetris.model.ShapeStructure;
import com.yahoo.dodeleye.tetris.model.GhostStructure;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Represents the container holding all components of the game.
 *
 * @version 1.0.2
 * @since 1.0.0
 */
public class GamePanel extends JPanel {

    /**
     * The background colour of the panel.
     */
    private static final Color BGC = Color.RED;

    /**
     * The text colour for all text displayed on the panel.
     */
    private static final Color TEXT_COLOUR = Color.WHITE;

    /**
     * The colour for the outlines of each component on the panel.
     */
    private static final Color OUTLINE_COLOUR = Color.WHITE;

    /**
     * The background colour of all panels held by the game panel.
     */
    private static final Color PANEL_COLOUR = Color.BLACK;

    /**
     * The name of the font used for the text.
     */
    private static String fontName = "Impact";

    /**
     * The font used for all text displayed on the panel.
     */
    private static final Font textFont = new Font(fontName, Font.PLAIN, 20);

    /**
     * The panel for displaying the current level.
     */
    private static JPanel levelPanel;

    /**
     * The panel for displaying the next tetromino piece.
     */
    private static JPanel nextPanel;

    /**
     * The panel for displaying the high score and the player's current score.
     */
    private static JPanel scorePanel;

    /**
     * The panel for displaying the number of lines cleared.
     */
    private static JPanel linesPanel;

    /**
     * The panel for displaying the playfield matrix.
     */
    private static JPanel playfield;

    /**
     * Inner container for holding the next, level and score panels.
     */
    private JPanel subPanel;

    public GamePanel() {

        // go through JPanel constructor.
        super();

        // use the GridBagLayout layout manager for this panel.
        this.setLayout(new GridBagLayout());

        // Set the size to the same size as the main frame.
        this.setSize(Tetris1.WIDTH, Tetris1.HEIGHT);


        // Set its background colour.
        this.setBackground(BGC);
    }

    /**
     * Places given component onto a given panel.
     *
     * @param p       the panel that the component will be added to.
     * @param c       the component to be added to the panel.
     * @param x       the column position to add the component to.
     * @param y       the row position to add the component to.
     * @param w       the number of columns the component will take up.
     * @param h       the number of rows the component will take up.
     * @param itop    the amount of space to leave for the component from the top.
     * @param ibottom the amount of space to leave for the component from the left.
     * @param ileft   the amount of space to leave for the component from the bottom.
     * @param iright  the amount of space to leave for the component from the right.
     * @param weightx the amount of space to distribute among columns when the panel is resized.
     *                <p>
     *                How much it should 'stretch' if the panel is resized. Passing 0 means it will keep its width
     *                regardless of the panel's resizing behaviour.
     *                </p>
     * @param weighty the amount of space to distribute among rows when the panel is resized.
     *                <p>
     *                How much it should 'stretch' if the panel is resized. Passing 0 means it will keep its height
     *                regardless of the panel's resizing behaviour.
     *                </p>
     * @param allign  determines where the component will be placed if it is smaller than its display area.
     */
    private static void addItem(JPanel p, JComponent c, int x, int y, int w, int h,
                                int itop, int ibottom, int ileft, int iright,
                                float weightx, float weighty, int allign) {


        // Create new gridbag contraints.
        GridBagConstraints gc = new GridBagConstraints();

        // Set its fields using the parameters passed.
        gc.gridx = x;
        gc.gridy = y;
        gc.gridwidth = w;
        gc.gridheight = h;
        gc.insets = new Insets(itop, ibottom, ileft, iright);
        gc.weightx = weightx;
        gc.weighty = weighty;
        gc.anchor = allign;
        gc.fill = GridBagConstraints.BOTH;

        // Set the background colour of the component.
        c.setBackground(PANEL_COLOUR);

        // Add it to the panel.
        p.add(c, gc);
    }

    /**
     * Places given component onto a given panel.
     *
     * @param p       the panel that the component will be added to.
     * @param c       the component to be added to the panel.
     * @param x       the column position to add the component to.
     * @param y       the row position to add the component to.
     * @param w       the number of columns the component will take up.
     * @param h       the number of rows the component will take up.
     * @param weightx the amount of space to distribute among columns when the panel is resized.
     *                <p>
     *                How much it should 'stretch' if the panel is resized. Passing 0 means it will keep its width
     *                regardless of the panel's resizing behaviour.
     *                </p>
     * @param weighty the amount of space to distribute among rows when the panel is resized.
     *                <p>
     *                How much it should 'stretch' if the panel is resized. Passing 0 means it will keep its height
     *                regardless of the panel's resizing behaviour.
     *                </p>
     * @param allign  determines where the component will be placed if it is smaller than its display area.
     */
    private static void addItem
            (JPanel p, JComponent c, int x, int y, int w, int h, float weightx, float weighty, int allign) {

        addItem(p, c, x, y, w, h, 5, 5, 5, 5, weightx, weighty, allign);
    }

    /**
     * Places given component onto a given panel.
     *
     * @param p       the panel that the component will be added to.
     * @param c       the component to be added to the panel.
     * @param x       the column position to add the component to.
     * @param y       the row position to add the component to.
     * @param w       the number of columns the component will take up.
     * @param h       the number of rows the component will take up.
     * @param allign  determines where the component will be placed if it is smaller than its display area.
     */
    private static void addItem(JPanel p, JComponent c, int x, int y, int w, int h, int allign) {

        addItem(p, c, x, y, w, h, 100, 100, allign);
    }

    /**
     * Creates new label using the game panel's defined font and text colour.
     * @param text the text the label should display.
     * @return a new label in the style defined by the game panel.
     */
    static JLabel createLabel(String text) {

        // Create new label.
        JLabel l = new JLabel(text);
        // Set its text colour to the game panel's defined colour.
        l.setForeground(TEXT_COLOUR);
        // Set its font to the game panel's defined font.
        l.setFont(textFont);
        // Return the label.
        return l;
    }

    /**
     * Sets up the game panel.
     */
    public void init() {

        // JButton pauseButton = new JButton("Pause");

        // Initialise the lines panel.
        linesPanel = new LinesPanel();

        // Add lines panel to the game panel
        addItem(this, linesPanel, 0, 0, 10, 1, 0, 0, GridBagConstraints.CENTER);

        // Initialise the grid surface.
        playfield = new GridSurface();

        // Add lines panel to the game panel
        addItem(this, playfield, 0, 1, 10, 20, 0, 0, GridBagConstraints.CENTER);

        // Initialise the score panel.
        scorePanel = new ScorePanel();

        // Initialise the next panel.
        nextPanel = new NextPanel();

        // Initialise the level label.
        levelPanel = new LevelPanel();

        // Initialise the sub-panel.
        subPanel = new JPanel();
        // Set its layout to gridbag.
        subPanel.setLayout(new GridBagLayout());

        // Add the score, next and level panels to the sub-panel.
        addItem(subPanel, scorePanel, 0, 0, 1, 5, 100, 100, GridBagConstraints.NORTH);
        addItem(subPanel, nextPanel, 0, 5, 1, 4, 100, 400, GridBagConstraints.EAST);
        addItem(subPanel, levelPanel, 0, 9, 1, 3, 100, 50, GridBagConstraints.EAST);

        //addItem(subPanel, pauseButton, 0, 12, 1, 2, 100, 200, GridBagConstraints.SOUTH);

        // Add the sub-panel to the game panel.
        addItem(this, subPanel, 11, 0, 1, 21, 0, 0, 0, 0,
                0, 0, GridBagConstraints.EAST);

        // The sub-panel should use to background since it only works as a container.
        subPanel.setBackground(null);

        // Set the game panel to visible.
        this.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        // Configure appropriate settings for the graphics object.
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Set colour to the outline colour.
        g2.setColor(OUTLINE_COLOUR);
        // Set thickness to 2.
        g2.setStroke(new BasicStroke(2));

        // Draw outline of the playfield.
        Rectangle rect1 = playfield.getBounds();
        g2.draw(rect1);

        // Draw outline of the lines panel.
        Rectangle rect2 = linesPanel.getBounds();
        g2.draw(rect2);

        // Draw outline of the next panel.
        Rectangle rect3 = nextPanel.getBounds();
        rect3.x += subPanel.getX();
        rect3.y += subPanel.getY();
        g2.draw(rect3);

        // Draw outline of the score panel.
        Rectangle rect4 = scorePanel.getBounds();
        rect4.x += subPanel.getX();
        rect4.y += subPanel.getY();
        g2.draw(rect4);

        // Draw outline of the level panel.
        Rectangle rect5 = levelPanel.getBounds();
        rect5.x += subPanel.getX();
        rect5.y += subPanel.getY();
        g2.draw(rect5);

    }

}

/**
 * Used as a visual representation of the grid the game is played on.
 */
class GridSurface extends JPanel {

    /**
     * The pixel square size each cell on the grid should have.
     */
    private static int squareSize = 20;

    private JLabel gameOverLabel;

    GridSurface() {

        super();
        Dimension gridSize = Playfield.getVisibleGridSize();
        gridSize.width *= squareSize;
        gridSize.height *= squareSize;

        this.setPreferredSize(gridSize);

        gameOverLabel = GamePanel.createLabel("GAME OVER");

        add(gameOverLabel);

        gameOverLabel.setVisible(false);


    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Configure appropriate settings for the graphics object.
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Get the game model.
        GameLogic model = Tetris1.getGameModel();

        gameOverLabel.setVisible(model.checkGameOver());

        if (model.checkGameOver()) {
            return;
        }

        drawGhostPiece(g2, model.getGhostStructure());

        // Draw the blocks.
        drawBlocks(g2, model.getColourMatrix());

        // Draw the grid-lines.
        drawGridlines(g2);
    }

    /**
     * Draws gridlines on the grid surface
     *
     * @param g the Graphics object for drawing on the panel.
     */
    private void drawGridlines(Graphics2D g) {
        // Variables used for the lower-bounds of the nested for loop iteration for each row and column of the grid.
        int x1 = 1;
        int y1 = 1;

        // Get the width and height of the surface.
        int width = this.getWidth();
        int height = this.getHeight();

        // Variables used for the upper-bounds of the nested for loop iteration for each row and column of the grid.
        // Using floor division just to ensure that x2 and y2 are integers, otherwise an error would be raised.
        int x2 = width / squareSize;
        int y2 = height / squareSize;

        // Set drawing colour to white.
        g.setColor(Color.WHITE);
        // Use thickness of 1.
        g.setStroke(new BasicStroke(1));

        /*
        Draw all the horizontal gridlines.
            Loop through the all the rows of the playfield not including the boundaries
        */
        for (int y = y1; y < y2; y++) {
            int yCoord = y * squareSize;
            g.drawLine(0, yCoord, width, yCoord);
        }

        /*
        Draw all the vertical gridlines.
            Loop through the all the columns of the playfield not including the boundaries
        */
        for (int x = x1; x < x2; x++) {
            int xCoord = x * squareSize;
            g.drawLine(xCoord, 0, xCoord, height);
        }
    }

    /**
     * Draws the blocks currently held by the playfield.
     *
     * @param g         the Graphics object for drawing on the panel.
     * @param colorGrid The matrix containing the colours each cell should be drawn with.
     */
    private void drawBlocks(Graphics g, Color[][] colorGrid) {

        int gridHeight = colorGrid.length;
        int gridWidth = colorGrid[0].length;

        // Loop through each visible row of the colour grid - the hidden rows will be skipped
        for (int row = Playfield.HIDDEN_ROWS; row < gridHeight; row++) {

            // The actual y pixel coordinate of where the top left of the block will be drawn from.
            int yCoord = (row - Playfield.HIDDEN_ROWS) * squareSize;

            // Loop through each column of the colour grid
            for (int column = 0; column < gridWidth; column++) {

                // The actual x pixel coordinate of where the top left of the block will be drawn from.
                int xCoord = column * squareSize;

                // Get the block at the current position
                Color color = colorGrid[row][column];

                // Ensure that the color is not a null type. Skip it otherwise.
                if (color != null) {

                    // Draw the block on the surface by filling the area position with its display colour
                    g.setColor(color);
                    g.fillRect(xCoord, yCoord, squareSize, squareSize);


                }

            }
        }
    }

    private void drawGhostPiece(Graphics g, GhostStructure ghostStructure){

        // If no structure is passed, do nothhing.
        if(ghostStructure == null)
            return;

        // Get the positions of each unit of the structure.
        List<int[]> positions = ghostStructure.getPositions();


        // Set the colour of the graphics object to the structure colour.
        g.setColor(ghostStructure.getColour());


        // Loop through each position coordinate of the structure.
        for(int i = 0; i < positions.size(); i++){

            int[] pos = positions.get(i);

            // The actual x pixel coordinate of where the top left of the unit will be drawn from.
            int x = pos[0] * squareSize;

            // The actual y pixel coordinate of where the top left of the unit will be drawn from.
            int y = (pos[1] - Playfield.HIDDEN_ROWS) * squareSize;

            // Draw the unit on the surface by filling the area position
            g.fillRect(x, y, squareSize, squareSize);
        }





    }

    /**
     * Creates an image from the structure of a tetromino.
     *
     * @param structure the structure of the tetromimo to create an image from.
     *
     * @return a new image representing the structure.
     */
    static BufferedImage createImage(ShapeStructure structure) {

        // Get data about the structure - width, height, positions of each unit, and colour.
        int widthUnits = structure.getWidthUnits();
        int heightUnits = structure.getHeightUnits();
        List<int[]> unitPositions = structure.getUnitPositions();
        Color color = structure.getColor();

        // Create a blank image matching the size of the structure.
        BufferedImage image = new BufferedImage(
                widthUnits * squareSize, heightUnits * squareSize, BufferedImage.TYPE_INT_ARGB);

        // Get the graphics from the new imaage.
        Graphics g = image.getGraphics();

        // Set the colour to the colour of the structure.
        g.setColor(color);

        // Loop through each unit position.
        for (int[] pos : unitPositions) {

            // Get the x and y position of the unit.
            int x = pos[0];
            int y = pos[1];

            // Fill the image in the region of the unit's position.
            g.fillRect(x * squareSize, y * squareSize, squareSize, squareSize);

        }

        // Finally return the image.
        return image;
    }

}

/**
 * Used to display the current number of lines cleared by the player.
 */
class LinesPanel extends JPanel {

    /**
     * The label to display the number of lines cleared.
     */
    private JLabel label;

    LinesPanel() {

        super();

        // Setup the layout manager
        setLayout(new FlowLayout(FlowLayout.LEFT));
        // Create label text to add to the panel.
        label = GamePanel.createLabel("LINES CLEARED -- " + 0);
        add(label);
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        // Get the game model.
        GameLogic model = Tetris1.getGameModel();

        // Form a string from the current number of lines cleared, with a minimum of 3 digits.
        String lines = String.format("%03d", model.getNumOfLines());

        // Update the label.
        label.setText("LINES CLEARED -- " + lines);
    }
}

/**
 * Used to display the next piece to spawn on the playfield.
 */
class NextPanel extends JPanel {

    /**
     * The structure for the tetromino held on the queue.
     */
    private ShapeStructure currentStruct;
    /**
     * The image of the next tetromino
     */
    private BufferedImage image;

    NextPanel() {

        // Setup the layout manager
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(GamePanel.createLabel("NEXT PIECE"));

    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        // Get the structure from the game model.
        ShapeStructure nextStruct = Tetris1.getGameModel().getNextStructure();

        // Get width and height of the panel.
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int imageWidth;
        int imageHeight;

        int centerX;
        int centreY;

        // If the structure received does not match the one already held.
        if (nextStruct != currentStruct) {

            // Set the current structure to the new one.
            currentStruct = nextStruct;
            // Create an image from the new structure.
            image = GridSurface.createImage(currentStruct);
        }

        // Get the width and height of the image.
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();

        // Caclulate the centre position of the panel to add the image to.
        centerX = (panelWidth - imageWidth) / 2;
        centreY = (panelHeight - imageHeight) / 2;
        // centreY = 2*(panelHeight - imageHeight) / 3;


        // Draw the image onto the panel.
        g.drawImage(image, centerX, centreY, this);

    }
}

/**
 * Used to display the high score and the player's current score.
 */
class ScorePanel extends JPanel {

    /**
     * The label to display the player's current score.
     */
    private JLabel currentScoreLabel;

    /**
     * The label to display the high score.
     */
    private JLabel highScoreLabel;

    ScorePanel() {

        super();

        // Create a new box to add labels on top of one another.
        Box box1 = Box.createVerticalBox();

        // Set layout of the panel.
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // Iniitalise the labels.
        currentScoreLabel = GamePanel.createLabel("000000");
        highScoreLabel = GamePanel.createLabel("000000");

        // Create label text to add to the panel.
        box1.add(GamePanel.createLabel("HIGH SCORE"));
        box1.add(Box.createVerticalStrut(10));
        box1.add(highScoreLabel);
        box1.add(Box.createVerticalStrut(10));
        box1.add(GamePanel.createLabel("SCORE"));
        box1.add(Box.createVerticalStrut(10));
        box1.add(currentScoreLabel);

        // Add the box to the panel.
        add(box1);
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        // Get the game model.
        GameLogic model = Tetris1.getGameModel();

        // Form a string from each score, with a minimum of 6 digits.
        String currentScore = String.format("%06d", model.getCurrentScore());
        String highScore = String.format("%06d", model.getHighScore());

        // Update the labels.
        currentScoreLabel.setText(currentScore);
        highScoreLabel.setText(highScore);
    }
}

/**
 * Used to display the current level of the game.
 */
class LevelPanel extends JPanel {

    /**
     * The label to display the current level.
     */
    private JLabel levelLabel;

    LevelPanel() {

        // Create a new box to add labels on top of one another.
        Box box = Box.createVerticalBox();

        // Setup the layout manager
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // Create label text to add to the panel.
        box.add(GamePanel.createLabel("LEVEL"));

        box.add(Box.createVerticalStrut(10));

        // Initialise the level label.
        levelLabel = GamePanel.createLabel("00");
        // Add it to the box.
        box.add(levelLabel);

        // Add the box to the panel.
        add(box);

    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        // Get the game model.
        GameLogic model = Tetris1.getGameModel();

        // Form a string from the level value, with a minimum of 2 digits.
        String level = String.format("%02d", model.getLevel());

        // Update the level label.
        levelLabel.setText(level);

    }


}