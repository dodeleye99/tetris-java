package com.yahoo.dodeleye.tetris.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the a ghost piece a tetromino on the playfield.
 *
 * @since 1.1.0
 */
public class GhostStructure{

    /**
     * The positions of each square unit of the ghost piece.
     */
    private List<int[]> positions = new ArrayList<>();
    /**
     * The colour of the ghost piece.
     */
    private Color colour;
    /**
     * The tetromino the ghost piece 'shadows'.
     */
    private Tetromino tetromino;
    /**
     * The transparency value of the ghost piece to determine its opacity.
     */
    private static final int ALPHA = 128;

    /**
     * Gets the positions of each unit of the ghost piece.
     * @return the positions of each unit of the ghost piece.
     */
    public List<int[]> getPositions() {
        return positions;
    }

    /**
     * Gets the colour of the ghost piece.
     * @return the colour of the ghost piece.
     */
    public Color getColour() {
        return colour;
    }

    GhostStructure(Color colour, Tetromino tetromino){

        // Initialise the colour field, using the parameter colour value and the alpha value.
        this.colour = new Color(
                colour.getRed(),
                colour.getGreen(),
                colour.getBlue(),
                ALPHA);


        this.tetromino = tetromino;

    }

    /**
     * Updates the position of each block of the ghost piece.
     */
    void updatePositions(){

        // Vertical distance from the tetromino - start at 0.
        int dy = 0;

        // Empty the ghost positions list.
        positions.clear();

        // Repeatedly go vertically down the grid until the tetromino is grounded.
        while (!tetromino.wouldCollide(0, dy + 1)) {
            dy += 1;
        }

        // Loop through each square unit of the tetromion
        for(Block block : tetromino.getBlocks()){

            // Get the block's x-position.
            int x = block.getX();

            // Calculate the grounded position of the block.
            int y = block.getY() + dy;

            // Form an array holding the coordinates.
            int[] pos = new int[]{x, y};

            // Add the position to the ghost positions list.
            positions.add(pos);
        }
    }
}
