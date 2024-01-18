package com.yahoo.dodeleye.tetris.model;

import com.yahoo.dodeleye.GameAudio;
import com.yahoo.dodeleye.tetris.BlockAlreadyAtPositonException;
import com.yahoo.dodeleye.tetris.BlockNotFoundException;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Used as the basic template for all the variants of the tetromino shapes used in the game.
 *
 */
public abstract class Tetromino {

    /**
     * Stores the playfield column position of the tetromino's origin.
     */
    private int x;

    /**
     * Stores the playfield row position of the tetromino's origin.
     */
    private int y;

    /**
     * The colour of the tetromino.
     */
    private Color color;

    /**
     * Identifiers for each type of tetromino piece.
     */
    public enum Type {

        /**
         * ShapeI
         */
        I,
        /**
         * ShapeJ
         */
        J,
        /**
         * ShapeL
         */
        L,
        /**
         * ShapeO
         */
        O,
        /**
         * ShapeS
         */
        S,
        /**
         * ShapeT
         */
        T,
        /**
         * ShapeZ
         */
        Z
    }

    /**
     * Represents one of the two directions of rotation.
     */
    public enum Rotation{

        /**
         * Repesents clockwise direction.
         */
        CLOCKWISE(1),
        /**
         * Represents anticlockwise direction.
         */
        ANTICLOCKWISE(-1);

        private final int value;

        Rotation(int value){

            this.value = value;
        }

        public int getValue(){
            return value;
        }
    }

    /**
     * Holds the four square units that make up the tetromino piece"""
     */
    private Block[] squareUnits = new Block[4];

    /**
     * A list of 'vectors' stored for each square unit for each orientation.
     * <p>
     *     Holds the relative coordinate positions (from the tetromino's origin position) of each square unit
     *     of the tetromino piece, for each orientation.
     * </p>
     */
    private List<int[][]> blockPositions = new ArrayList<>();

    /**
     * Stores the current index used from the block_positions 3D list.
     * <p>
     *     Used to keep track of the tetromino's current orientation.
     * </p>
     */
    private int rotationIndex = 0;

    /**
     * A reference to the playfield the tetromino is placed on.
     */
    private Playfield grid;

    /**
     * The structure of the piece while in its default orientation - its size and relative positions.
     */
    private ShapeStructure structure;

    /**
     * The ghost piece of the tetromino, concerning its position and colour.
     */
    private GhostStructure ghostPiece;

    /**
     * Creates a new tetromino instance.
     * @param type the type of tetromino piece to be created.
     * @return A new tetromino piece of the type corresponding to the Type passed.
     */
    static Tetromino create(Type type){

        switch (type){

            case I:
                return new ShapeI();
            case J:
                return new ShapeJ();
            case L:
                return new ShapeL();

            case O:
                return new ShapeO();

            case S:
                return new ShapeS();

            case T:
                return new ShapeT();

            case Z:
                return new ShapeZ();

            default:
                return null;
        }

    }

    /**
     * Initialise the Tetromino instance.
     * @param color The colour to be assigned to the tetromino.
     * @param structure The structure of the piece - its dimensions and relative block positions.
     */
    @Deprecated
    public Tetromino(Color color, ShapeStructure structure) {

        this.color = color;
        this.structure = structure;
    }

    /**
     * Initialise the Tetromino instance.
     * @param color The colour to be assigned to the tetromino.
     * @param widthUnits The number of columns it takes up on the grid.
     * @param heightUnits The number of rows it takes up on the grid.
     * @param unitPositions The relative positions of each of its blocks from a top-left origin.
     */
    Tetromino(Color color, int widthUnits, int heightUnits, int[][] unitPositions) {

        this.color = color;
        structure = new ShapeStructure(widthUnits, heightUnits, unitPositions, color);

        ghostPiece = new GhostStructure(color, this);
    }

    /**
     * Gets the square units that make up the tetromino piece.
     * @return all four square units that make up the tetromino piece.
     */
    Block[] getBlocks(){

        return squareUnits;
    }

    /**
     * It sets up the square blocks of the tetromino piece, and their initial positions on the playfield.
     * <p>
     *     Will be called before spawning the tetromino piece onto the playfield.
     * </p>
     *
     * @param xPos the initial x-position to place the tetromino piece.
     * @param yPos the initial x-position to place the tetromino piece.
     * @param grid the grid the tetromino is to be placed on.
     */
    void setupBlocks(int xPos, int yPos, Playfield grid) {

        // Keep a reference of the playfield.
        this.grid = grid;

        // Set the origin coordinates of the tetromino to the parameter values passed.
        this.x = xPos;
        this.y = yPos;

        // Extract the relative positions of the blocks for the current orientation.
        int[][] blockVectors = blockPositions.get(rotationIndex);

        // Loop through each index of the blockVectors 2D array.
        for(int i=0; i < blockVectors.length; i++){

            // Get the relative position the block will be set at
            int[] vectors = blockVectors[i];

            // Calculate the actual position to place the block at on the grid
            int x = xPos + vectors[0];
            int y = yPos + vectors[1];

            // Instantiate the block, passing the initial position and colour.
            Block block = new Block(x, y, color);

            // Add the block to the squareUnits array at the position of the index
            squareUnits[i] = block;
        }

        // Calculate the position of the ghost piece.
        calculateGhostPositions();
    }

    /**
     * Sets the value of blockPositions from a 3D array.
     * @param pos The 3D array to be stored by blockPositions.
     */
    void setBlockPositions(int[][][] pos) {

        blockPositions.addAll(Arrays.asList(pos));
    }

    /**
     * Gets the structure of the piece while in its default orientation
     * @return the structure of the tetromino piece.
     */
    ShapeStructure getStructure() {

        return structure;
    }

    /**
     * Gets the ghost piece of the tetromino.
     * @return the ghost piece of the tetromino.
     * @since 1.1.0
     */
    GhostStructure getGhostStructure(){
        return ghostPiece;
    }

    /**
     * Moves the block horizontally along the grid, one space to the left.
     */
    void shiftLeft() {

        // Do not shift the tetromino if it would collide with the playfield or another block.
        if(!wouldCollide(-1, 0)){

            // Tetromino will be shifted left, therefore decrease x-origin by 1.
            x -= 1;

            // First remove the square units from the grid.
            removeBlocks();

            // Loop through each square unit of the tetromino piece.
            for (Block b: squareUnits) {

                // Each block will be shifted along the grid to the left by one place.
                b.shiftLeft();
            }

            // Finally place all blocks back onto the grid.
            addToGrid();

            // Calculate the new position of the ghost piece.
            calculateGhostPositions();

            //GameAudio.playSound(GameAudio.MOVE_SFX);
            GameAudio.MOVE_CLIP.play();
        }
    }

    /**
     * Moves the block horizontally along the grid, one space to the right.
     */
    void shiftRight() {

        // Do not shift the tetromino if it would collide with the playfield or another block.
        if(!wouldCollide(1, 0)){

            // Tetromino will be shifted right, therefore inccrease x-origin by 1.
            x += 1;

            // First remove the square units from the grid.
            removeBlocks();

            // Loop through each square unit of the tetromino piece.
            for (Block b: squareUnits) {

                // Each block will be shifted along the grid to the right by one place.
                b.shiftRight();
            }

            // Finally place all blocks back onto the grid.
            addToGrid();

            // Calculate the new position of the ghost piece.
            calculateGhostPositions();

            //GameAudio.playSound(GameAudio.MOVE_SFX);
            GameAudio.MOVE_CLIP.play();

        }
    }

    /**
     * Moves the block vertically on the playfield, one space downwards.
     */
    void shiftDown() {

        // Tetromino will be shifted downwards, therefore inccrease y-origin by 1 (as down is +ve)
        y += 1;

        // First remove the square units from the grid.
        removeBlocks();

        // Loop through each square unit of the tetromino piece.
        for(Block b : squareUnits){

            // Each block will be shifted downwards b one place.
            b.shiftDown();
        }

        // Finally place all blocks back onto the grid.
        addToGrid();
    }

    /**
     * Removes all the square units of the tetromino from the playfield.
     *
     * <p>
     *     Will throw a BlockNotFoundError if a block being removed is not at its stated positon.
     * </p>
     */
    private void removeBlocks() {

        // Loop through each square unit of the tetromino from the playfield.
        for(Block b : squareUnits){

            // Attempt to remove it from the playfield. Throw a BlockNotFoundError if the block is not there.
            try {
                grid.removeBlock(b);
            } catch (BlockNotFoundException e) {

                throw new RuntimeException(e);
            }


        }

    }

    /**
     * Checks whether shifting the tetromino will cause it to collide.
     * <p>
     *     For a given shift vector, the tetromino will be checked whether or not it will hit the grid boundaries or
     *     overlap with another block at the position.
     * </p>
     * @param dx the change in x position of the tetromino to check.
     * @param dy the change in y position of the tetromino to check.
     * @return true if there would be a collision, false otherwise.
     */
    boolean wouldCollide(int dx, int dy) {

        // Loop through each square units of the tetromino
        for(Block block : squareUnits){

            // Define the x and y coordinates of where to check for collision
            int x = block.getX() + dx;
            int y = block.getY() + dy;

            // It would collide if the position is outside of the grid boundaries
            if(!grid.isInbounds(x ,y)){

                return true;
            }

            // A collision would occur if the position is currently occupied
            if(!grid.checkCellEmpty(x, y)){

                // Get the block at the position
                Block blockFound = grid.get(x, y);

                // The collision would only count if the block is not from the same tetromino piece
                if(!Arrays.asList(squareUnits).contains(blockFound)){

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks whether shifting the tetromino shape down would cause it to collide.
     * <p>
     *     It will be checked whether or not it will hit the grid boundaries or overlap with another block if it were
     *     shifted one row down.
     *
     * @return true if there would be a collision, false otherwise.
     */
    boolean checkIfGrounded() {

        return wouldCollide(0, 1);
    }

    /**
     * Places all the blocks of the tetromino on the playfield
     * <p>
     *     Throws BlockAlreadyAtPocitionException exception if a block is being added to a non-empty cell.
     * </p>
     */
    void addToGrid() {

        // Loop through each of the tetromino's blocks
        for(Block b : squareUnits){

            // Attempt to add it to the playfield. If it would overlap another block,
            // throw a BlockAlreadyAtPositionException exception.
            try {

                grid.addBlock(b);
            }
            catch (BlockAlreadyAtPositonException e) {

                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Rotates the tetromino piece at a 90 degree interval.
     * @param rotation The direction that the tetromino piece should be rotated.
     */
    private void rotate(Rotation rotation){

        // Attempt to rotate three times before stopping.
        // (1): at its current position
        // (2): after one shift to the right from original position
        // (3): after one shift to the left from original position
        int[] dxValues = {0, 1, -1};

        // Loop through each x-shift value.
        for (int dx : dxValues) {

            // For when the rotation is possible,
            if (checkRotation(rotation, dx)) {

                // Get the integer value representing the rotation.
                int dr = rotation.getValue();

                // Get the number of possible orientations of the tetromino
                int n = blockPositions.size();

                int[][] blockVectors;


                // Change the rotation index by the amount given by the rotation value.
                rotationIndex += dr;
                // The index is circular - meaning it will cycle from 0 to the highest index of blockPositions.
                // When it passes the max index,
                if (rotationIndex == n) {

                    // Recycle back to the beginning.
                    rotationIndex = 0;
                }
                // When it goes below the minimum index,
                else if (rotationIndex == -1) {

                    // Recycle back to the end.
                    rotationIndex = n - 1;
                }

                // Get the relative positions that each block should be placed at.
                blockVectors = blockPositions.get(rotationIndex);

                // First remove the tetromino's blocks from the playfield.
                removeBlocks();

                // Apply the shift in x-position.
                x += dx;

                // Loop through rach index of the square blocks.
                for (int j = 0; j < squareUnits.length; j++) {

                    // Get the block and its position vector from the tetromino's origin.
                    Block block = squareUnits[j];
                    int[] vector = blockVectors[j];

                    // Calculate the column and row positions the blocks would be placed at.
                    int xPos = x + vector[0];
                    int yPos = y + vector[1];

                    // Set the position of the block.
                    block.setCoords(xPos, yPos);

                    // Attempt to add it the block to the playfield.
                    try {
                        grid.addBlock(block);
                    }
                    // Throw an exception just in case the block would overlap for whatever reason.
                    catch (BlockAlreadyAtPositonException e) {
                        throw new RuntimeException(e);
                    }

                }

                // Calculate the new position of the ghost piece.
                calculateGhostPositions();

                GameAudio.playSound(GameAudio.ROTATE_SFX_FILENAME);

                return;
            }
        }

    }

    /**
     * Checks whether rotating the tetromino is possible.
     * <p>
     *     For a given direction, the tetromino is checked if it would collide with the playfield boundaries
     *     or other blocks already on it.
     * </p>
     * @param rotation the direction of rotation.
     * @param dx the shift in x-position to be applied before rotating the tetromino.
     * @return true if there would be a collision, false otherwise.
     */
    private boolean checkRotation(Rotation rotation, int dx){

        // Get the integer value of the direction of rotation.
        int dr = rotation.getValue();

        // Use a temporary variable in place of rotation index.
        int tempIndex = rotationIndex + dr;

        // Get the number of possible orientations of the tetromino.
        int n = blockPositions.size();

        int[][] blockVectors;

        // The index is circular - meaning it will cycle from 0 to the highest index of blockPositions.
        // When it passes the max index,
        if(tempIndex == n){

            // Recycle back to the beginning.
            tempIndex = 0;
        }

        // When it goes below the minimun index,
        else if(tempIndex == -1){

            // Recycle back to the end.
            tempIndex = n - 1;
        }

        // Get the relative positions that each block should be placed at.
        blockVectors = blockPositions.get(tempIndex);

        // Iterate over each block position's vector from the tetromino's origin.
        for(int[] vector : blockVectors){

            // Calculate the column and row positions the blocks would be placed at.
            int xPos = x + vector[0] + dx;
            int yPos = y + vector[1];

            // It would collide if te position is outside of the grid boundaries.
            if(!grid.isInbounds(xPos, yPos))
                return false;


            // A collision would occur if the position is currently occupied.
            if(!grid.checkCellEmpty(xPos, yPos)){

                // Get the block at the position.
                Block blockFound = grid.get(xPos, yPos);

                // The collision would only count if the block is not from the same tetromino piece.
                if(!Arrays.asList(squareUnits).contains(blockFound)){

                    return false;
                }
            }
        }

        // At this point, no collisions were found, therefore the tetromino can rotate.
        return true;
    }

    /**
     * Rotates the tetromino piece 90 degrees clockwise.
     */
    void rotateClockwise(){

        rotate(Rotation.CLOCKWISE);
    }

    /**
     * Rotates the tetromino piece 90 degrees anticlockwise.
     */
    void rotateAnticlockwise(){

        rotate(Rotation.ANTICLOCKWISE);
    }

    /**
     * Gets the row positions the tetromino lies on.
     * <p>
     *     It will return the row indexes of the grid where at least one square unit of the tetromino can be found on.
     * </p>
     * @return a list of the row positions of where the tetromino's blocks lie. It will be in ascending order.
     */
    List<Integer> getRows(){

        // Define a new empty list, which will be filled and returned at the end.
        List<Integer> rows = new ArrayList<>();

        // Loop through each block of the tetromino.
        for(Block block : squareUnits){

            // Get the current block's row position.
            int y = block.getY();

            // Check if the row index is not already present in the list.
            if(!rows.contains(y)){

                // If not, add it to the list.
                rows.add(y);
            }
        }

        // Sort the list in ascending order before returning it.
        Collections.sort(rows);

        return rows;
    }

    /**
     * Calculates the position of each block of the ghost piece.
     * <p>
     *     This method should be called when the tetromino spawns, and every time it shifts horizontally or rotates.
     * </p>
     *
     * @since 1.1.0
     */
    private void calculateGhostPositions(){

        ghostPiece.updatePositions();

    }
}

/**
 * Represents the 'I' shaped tetromino pieces.
 */
class ShapeI extends Tetromino {

    private static final Color COLOUR = Color.CYAN;

    private static final int WIDTH_UNITS = 4;
    private static final int HEIGHT_UNITS = 1;

    private static final int[][] UNIT_POSITIONS = { {0,  0}, {1,  0}, {2,  0}, {3,  0} };

    ShapeI() {

        super(COLOUR, WIDTH_UNITS, HEIGHT_UNITS, UNIT_POSITIONS);

        int[][][] pos =  {
                { {0,  0}, {1,  0}, {2,  0}, {3,  0} } ,
                { {2, -1}, {2,  0}, {2,  1}, {2,  2} } ,
        };

        setBlockPositions(pos);
    }
}

/**
 * Represents the 'J' shaped tetromino pieces.
 */
class ShapeJ extends Tetromino {

    private static final Color COLOUR = Color.BLUE;

    private static final int WIDTH_UNITS = 3;
    private static final int HEIGHT_UNITS = 2;

    private static final int[][] UNIT_POSITIONS = { {0,  0}, {0,  1}, {1,  1}, {2,  1} };

    ShapeJ() {

        super(COLOUR, WIDTH_UNITS,HEIGHT_UNITS, UNIT_POSITIONS);

        int[][][] pos =  {
                { {0,  0}, {0,  1}, {1,  1}, {2,  1} } ,
                { {2, -1}, {1, -1}, {1,  0}, {1,  1} } ,
                { {2,  1}, {2,  0}, {1,  0}, {0,  0} } ,
                { {0,  1}, {1,  1}, {1,  0}, {1,  -1} },
        };

        setBlockPositions(pos);
    }
}

/**
 * Represents the 'L' shaped tetromino pieces.
 */
class ShapeL extends Tetromino {

    private static final Color COLOUR = Color.ORANGE;

    private static final int WIDTH_UNITS = 3;
    private static final int HEIGHT_UNITS = 2;

    private static final int[][] UNIT_POSITIONS = { {2,  0}, {0,  1}, {1,  1}, {2,  1} };

    ShapeL() {

        super(COLOUR, WIDTH_UNITS,HEIGHT_UNITS, UNIT_POSITIONS);

        int[][][] pos =  {
                { {0,  1}, {1,  1}, {2,  1}, {2,  0} } ,
                { {1, -1}, {1,  0}, {1,  1}, {2,  1} } ,
                { {2,  0}, {1,  0}, {0,  0}, {0,  1} } ,
                { {1,  1}, {1,  0}, {1, -1}, {0, -1} },
        };

        setBlockPositions(pos);
    }
}

/**
 * Represents the 'O' shaped tetromino pieces.
 */
class ShapeO extends Tetromino {

    private static final Color COLOUR = Color.YELLOW;

    private static final int WIDTH_UNITS = 2;
    private static final int HEIGHT_UNITS = 2;

    private static final int[][] UNIT_POSITIONS = { {0,  0}, {0,  1}, {1,  0}, {1,  1} };

    ShapeO() {

        super(COLOUR, WIDTH_UNITS,HEIGHT_UNITS, UNIT_POSITIONS);

        int[][][] pos =  {
                { {1,  0}, {1,  1}, {2,  1}, {2,  0} } ,
        };

        setBlockPositions(pos);
    }
}

/**
 * Represents the 'S' shaped tetromino pieces.
 */
class ShapeS extends Tetromino {

    private static final Color COLOUR = Color.GREEN;

    private static final int WIDTH_UNITS = 3;
    private static final int HEIGHT_UNITS = 2;

    private static final int[][] UNIT_POSITIONS = { {1,  0}, {0,  1}, {1,  1}, {2,  0} };

    ShapeS() {

        super(COLOUR, WIDTH_UNITS,HEIGHT_UNITS, UNIT_POSITIONS);

        int[][][] pos =  {
                { {0,  1}, {1,  1}, {1,  0}, {2,  0} } ,
                { {0, -1}, {0,  0}, {1,  0}, {1,  1} } ,
        };

        setBlockPositions(pos);
    }
}

/**
 * Represents the 'T' shaped tetromino pieces.
 */
class ShapeT extends Tetromino {

    private static final Color COLOUR = Color.MAGENTA;

    private static final int WIDTH_UNITS = 3;
    private static final int HEIGHT_UNITS = 2;

    private static final int[][] UNIT_POSITIONS = { {1,  0}, {0,  1}, {1,  1}, {2,  1} };

    ShapeT() {

        super(COLOUR, WIDTH_UNITS, HEIGHT_UNITS, UNIT_POSITIONS);

        int[][][] pos =  {
                { {1,  0}, {0,  1}, {1,  1}, {2,  1} } ,
                { {2,  0}, {1, -1}, {1,  0}, {1,  1} } ,
                { {1,  1}, {2,  0}, {1,  0}, {0,  0} } ,
                { {0,  0}, {1,  1}, {1,  0}, {1,  -1} },
        };

        setBlockPositions(pos);
    }
}

/**
 * Represents the 'Z' shaped tetromino pieces.
 */
class ShapeZ extends Tetromino {

    private static final Color COLOUR = Color.RED;

    private static final int WIDTH_UNITS = 3;
    private static final int HEIGHT_UNITS = 2;

    private static final int[][] UNIT_POSITIONS = { {0,  0}, {1,  0}, {1,  1}, {2,  1} };

    ShapeZ() {

        super(COLOUR, WIDTH_UNITS, HEIGHT_UNITS, UNIT_POSITIONS);

        int[][][] pos =  {
                { {0,  0}, {1,  0}, {1,  1}, {2,  1} } ,
                { {2, -1}, {2,  0}, {1,  0}, {1,  1} } ,
        };

        setBlockPositions(pos);
    }
}

