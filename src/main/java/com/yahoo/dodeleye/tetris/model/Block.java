package com.yahoo.dodeleye.tetris.model;

import java.awt.*;

/**
 * Represents a single square unit of a tetromino piece, taking one cell of the playfield.
 *
 * @version 1.0.0
 * @since 1.0.0
 */
class Block {

    // The column index that the block is stored at on the playfield.
    private int x;

    // The row index that the block is stored at on the playfield.
    private int y;

    // The colour that the block will be displayed with on the playfield.
    private Color colour;

    Block(int x, int y, Color colour) {

        this.x = x;
        this.y = y;
        this.colour = colour;
    }

    /**
     * Gets the x coordinate the block is set at on the grid.
     * @return the x coordinate the block is set at on the grid.
     */
    int getX() {

        return x;
    }

    /**
     * Gets the y coordinate the block is set at on the grid.
     * @return the y coordinate the block is set at on the grid.
     */
    int getY() {

        return y;
    }

    /**
     * Sets the row and column position of the block.
     * @param x the column position to set the block to.
     * @param y the row position to set the block to.
     */
    void setCoords(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * Moves the block along the playfield, horizontally or vertically.
     *
     * @param dx the change in the x coordinate of the block.
     *           <p>
     *              +ve = right, -ve = left
     *           </p>
     * @param dy the change in the y coordinate of the block.
     *           <p>
     *              +ve = down, -ve = up.
     *           </p>
     */
    private void translate(int dx, int dy) {

        x += dx;
        y += dy;
    }

    /**
     * Moves the block horizontally along the playfield, one space to the left.
     */
    void shiftLeft() {

        translate(-1, 0);
    }

    /**
     * Moves the block horizontally along the playfield, one space to the right.
     */
    void shiftRight() {

        translate(1, 0);
    }

    /**
     * Moves the block vertically along the playfield, one space downwards.
     */
    void shiftDown() {

        translate(0, 1);
    }

    /**
     * Gets the colour of the block.
     * @return the colour of the block.
     */
    Color getColour() {

        return colour;
    }

    @Deprecated
    void setX(int x) {

        this.x = x;
    }

    @Deprecated
    void setY(int y) {

        this.y = y;
    }

    @Deprecated
    int[] getXY() {

        return new int[]{x, y};
    }


}
