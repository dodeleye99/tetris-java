package com.yahoo.dodeleye.tetris.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the structure (or shape) of a tetromino piece.
 */
public class ShapeStructure {

    // The colour of the tetromino piece.
    private Color color;

    // The number of columns the piece would take up if placed on the grid.
    private int widthUnits;
    // The number of rows the piece would take up if placed on the grid.
    private int heightUnits;

    // The relative positions of each of its blocks from a top-left origin.
    private java.util.List<int[]> unitPositions = new ArrayList<>();

    ShapeStructure(int widthUnits, int heightUnits, int[][] unitPositions, Color color){

        this.color = color;
        this.widthUnits = widthUnits;
        this.heightUnits = heightUnits;
        this.unitPositions.addAll(Arrays.asList(unitPositions));
    }

    /**
     * Gets the colour of the tetromino piece.
     * @return the colour of the tetromino piece.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the unit height of the tetromino piece.
     * @return the unit height of the tetromino piece.
     */
    public int getHeightUnits() {
        return heightUnits;
    }

    /**
     * Gets the unit width of the tetromino piece.
     * @return the unit width of the tetromino piece.
     */
    public int getWidthUnits() {
        return widthUnits;
    }

    /**
     * Gets the relative positions of each of its blocks.
     * @return the relative positions of each of its blocks.
     */
    public List<int[]> getUnitPositions() {
        return unitPositions;
    }
}
