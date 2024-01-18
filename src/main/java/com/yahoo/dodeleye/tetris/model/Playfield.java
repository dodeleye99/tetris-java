package com.yahoo.dodeleye.tetris.model;

import com.yahoo.dodeleye.GameAudio;
import com.yahoo.dodeleye.tetris.BlockAlreadyAtPositonException;
import com.yahoo.dodeleye.tetris.BlockNotFoundException;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class used to represent the grid component of the game interface called the playfield where Tetris is actually
 * played on.
 *
 * @author daniel
 * @author dodeleye@yahoo.com
 * @version 1.0.0
 * @since 1.0.0
 */

public class Playfield {

    /**
     * Used to determine soft drop speed.
     * <p>
     * The current gravity will be multiplied by this value to determine the speed.
     * </p>
     */
    private static final float SOFT_DROP_MODIFIER = 1 / 20f;

    /**
     * The value used for hard-drop.
     * <p>
     * HoLds the time in seconds it should take for the active tetromino to fall one cell down while under
     * hard drop gravity.
     * </p>
     */
    private static final float HARD_DROP_GRAVITY = 0.0005f;

    /**
     * The delay time before locking a piece.
     * <p>
     * It is amount of time, in seconds, that the current tetromino piece that is grounded can be controlled
     * before it becomes inactive.
     * </p>
     */
    private static final float LOCK_DELAY = 0.5f;

    /**
     * The delay before spawning the next piece.
     * <p>
     * It is the amount of time, in seconds, that needs to be passed before spawning the next tettomino after
     * the previous tetromino locks onto the playfield.
     * </p>
     */
    private static final float ENTRY_DELAY = 0.42f;

    /**
     * The delay for clearing full rows of blocks from the playfield.
     * <p>
     * It is the amount of time, in seconds, that the game uses to clear full rows on the playfield after the
     * tetromino locks.
     * </p>
     */
    private static final float CLEAR_DELAY = 0.65f;

    /**
     * The delay time after the game over condition has been reached.
     * <p>
     * It is the amount of time, in seconds, that the game uses to clear all remaining blocks on the playfield, and
     * until the game officially ends.
     * </p>
     */
    private static final float GAME_OVER_DELAY = 5f;

    /**
     * The width of the playfield.
     * <p>
     * Determines the number of units that can fit on a single row.
     * </p>
     */
    private static int gridWidth = 10;

    /**
     * The height of the playfield.
     * <p>
     * Determines the number of units that can fit in a single column.
     * </p>
     */
    private static int gridHeight = 22;

    /**
     * The number of rows (from the top) that will be hidden from view.
     * <p>
     * Note that it must always be less than the grid height, otherwise an error will arise.
     * </p>
     */
    public static final int HIDDEN_ROWS = 2;

    /**
     * The main data structure used to hold the units currently on the playfield.
     */
    private Block[][] grid = new Block[gridHeight][gridWidth];

    /**
     * A reference to the GameLogic instance holding this Playfield instance.
     */
    private GameLogic model;

    /**
     * Stores the colour of each block unit.
     * <p>
     * Its dimension matches the grid variable, allowing it to store the colour of each block at the position
     * corresponding to where it is held on the grid.
     * </p>
     */
    private Color[][] colourGrid = new Color[gridHeight][gridWidth];

    /**
     * The tetromino piece currently being controlled (by the player) on the playfield.
     */
    private Tetromino activeTetromino;

    /**
     * Use to hold time that has currently passed in order to be transferred.
     * <p>
     * Its value will be increased by the time interval of the current frame, and will be passed on to be used in
     * other procedures, such as for counting the gravity, lock and entry delays.
     * </p>
     */
    private float accumulator = 0f;

    /**
     * A second accumulator for holding time passed in the lock phase.
     * <p>
     * Every time lock delay is cancelled, the time spent in the lock phase needs to be saved. When the lock phase
     * is re-entered again without the tetromino dropping further, the timer starts from the saved value.
     * </p>
     */
    private float lockAccumulator = 0f;

    /**
     * The time (in seconds) that is taken for the piece to fall down a single row.
     */
    private float gravity;

    /**
     * A flag used to determine whether soft drop (fast falling) should be applied.
     */
    private boolean softDropIsActive = false;
    /**
     * A flag used to determine whether hard (instant) drop should be applied.
     */
    private boolean hardDropIsActive = false;

    /**
     * The current state of the game.
     * <p>The state refers to the current phase the game is in, such as the drop phase, lock phase and entry phase.
     * </p>
     */
    private StateMachine currentState = new FallState();

    /**
     * Identifiers for each possible state of the game.
     */
    private enum State {
        /**
         * For when the active piece is falling under gravity.
         */
        DROP,
        /**
         * For when entry delay before spawning the next piece occurs.
         */
        ENTRY_PHASE,
        /**
         * For when delay occurs for clearing full rows.
         */
        LINE_CLEAR,
        /**
         * For when the active piece is getting ready to lock down.
         */
        LOCK_PHASE,
        /**
         * For when a game over condition has been reached.
         */
        GAME_OVER
    }

    /**
     * Returns the width of the grid.
     *
     * @return the width of the grid.
     */
    @Deprecated
    public static int getGridWidth() {

        return gridWidth;
    }

    /**
     * Returns the dimensional size of the grid that will be shown (width, height)
     *
     * @return the size of the grid that is visible (width, height)
     */
    public static Dimension getVisibleGridSize() {

        return new Dimension(gridWidth, gridHeight - HIDDEN_ROWS);
    }

    /**
     * Returns the dimensional size of the grid (width, height)
     *
     * @return the size of the grid (width, height)
     */
    @Deprecated
    public static Dimension getGridSize() {

        return new Dimension(gridWidth, gridHeight);
    }

    /**
     * Initialises the Playfield instance.
     *
     * @param gameLogic The model component that holds this instance.
     */
    Playfield(GameLogic gameLogic) {

        this.model = gameLogic;

        gravity = model.calculateGravity();
    }

    /**
     * Returns a block component from the grid at a given position.
     *
     * @param x The column of the block to get.
     * @param y The row of the block to get.
     * @return the block component at the given position.
     */
    Block get(int x, int y) {

        return grid[y][x];
    }

    /**
     * Returns the grid of colours of each block on the playfield.
     *
     * @return the grid of colours of each blocks on the playfield.
     */
    Color[][] getColourGrid() {

        return colourGrid;
    }

    /**
     * Gets the ghost piece of the active tetromino.
     * @return the ghost piece of the active tetromino.
     * @since 1.1.0
     */
    GhostStructure getGhostStructure(){
        // Check if a tetromino is active.
        if(checkTetrominoActive())
            return activeTetromino.getGhostStructure();
        else
            return null;
    }

    /**
     * Places a block onto the grid, at a given position.
     *
     * @param x The column position to set the block to.
     * @param y The row position to set the block to.
     * @param b The block to add to the grid.
     */
    private void set(int x, int y, Block b) {

        grid[y][x] = b;

        colourGrid[y][x] = b.getColour();
    }

    /**
     * Erases a block from the grid at a given position.
     *
     * @param x The column position to remove the block from.
     * @param y The row position to remove the block from.
     */
    private void setNull(int x, int y) {

        grid[y][x] = null;

        colourGrid[y][x] = null;
    }

    /**
     * Replaces the blocks of a single row with another row of blocks.
     *
     * @param rowIndex The index of the row to set the new blocks to.
     * @param blocks   The new blocks to place on the row.
     */
    private void setRow(int rowIndex, Block[] blocks) {

        grid[rowIndex] = blocks;

        // Form an array for the colours of each block.
        Color[] c = new Color[blocks.length];
        for (int i = 0; i < c.length; i++) {

            Block b = blocks[i];
            if (b == null)
                c[i] = null;
            else
                c[i] = b.getColour();
        }
        // Set this array to the same position on the grid of colours.
        colourGrid[rowIndex] = c;
    }

    /**
     * Empties a single row on the grid at a given row index.
     *
     * @param rowIndex The index of the row to empty
     */
    private void clearRow(int rowIndex) {

        grid[rowIndex] = new Block[grid[rowIndex].length];
        colourGrid[rowIndex] = new Color[colourGrid[rowIndex].length];
    }

    /**
     * Places a block onto the grid, at the position given by its coordinate attributes
     *
     * @param b The block to add to the grid
     */
    void addBlock(Block b) throws BlockAlreadyAtPositonException {

        // Get the x and y coordinates of the Block instance passed.
        int x = b.getX();
        int y = b.getY();

        // Raise an error if there is already a block at the position.
        if (!checkCellEmpty(x, y)) {
            throw new BlockAlreadyAtPositonException(x, y);
        } else {
            // Add the block to the position.
            set(x, y, b);
        }

    }

    /**
     * Removes a block from the grid, at the position given by its own coordinates,
     *
     * @param b The block to be removed from the grid.
     */
    void removeBlock(Block b) throws BlockNotFoundException {

        // get the coordinates of the block.
        int x = b.getX();
        int y = b.getY();

        // In case the block is not at the position, throw an exception.
        if (!checkBlockPosition(b)) {

            throw new BlockNotFoundException(x, y);
        }

        // set the value stored at the coordinates to null, thus removing it from the grid.
        setNull(x, y);
    }

    /**
     * Applies line clear gravity to the rows of blocks above the cleared lines (They are shifted downwards).
     *
     * @param fullRows: the row indexes of the playfield that were completely filled with blocks.
     */
    private void shiftRowsDown(List<Integer> fullRows) {

        // No need to shift any rows if the grid is completely cleared.
        if (checkGridEmpty())
            return;

        // Loop through each index of the full rows list.
        for (Integer fullRow : fullRows) {

            // Shift from the top row of blocks, to the cleared row currently pointed to.
            int startRow = getTopRow();
            int endRow = fullRow;

            // Check if the row where the line was cleared WAS the top row.
            if(startRow > endRow){
                // No shift needs to be performed for this line.
                continue;
            }


            // Define a variable to temporarily store block rows on the grid.
            // Start with the top block row.
            Block[] tempBlocks = grid[startRow];

            // Empty the start row of blocks from the grid, as they need to be shifted downwards.
            clearRow(startRow);

            // Loop from the start and end row indexes.
            for (int y = startRow; y < endRow; y++) {

                // Temporarily hold the block array at the current index.
                Block[] blocks = grid[y + 1];

                // Remove that row of blocks from the grid, replacing it with the blocks held by tempBlocks.
                setRow(y + 1, tempBlocks);

                // Now have tempBlocks hold the blocks that were removed.
                tempBlocks = blocks;
            }
        }
    }

    /**
     * Updates the state of the playfield.
     * <p>
     * This should not be called within the class.
     * </p>
     *
     * @param dt the current time step (in seconds) that has passed
     */

    void update(double dt) {

        // Increase the accumulator by delta time.
        accumulator += dt;

        // Update the current state of the game.
        currentState.updateState();

    }

    /**
     * Sets the next tetromino piece to be placed onto the playfield.
     *
     * @param nextPiece The tetromino piece that will be placed on the grid as the active piece.
     */
    void setTetromino(Tetromino nextPiece) {

        // Define the top-left coordinates of where the tetromino piece will spawn.
        int startX = 3;
        int startY = 2;

        // Set the next piece as the active tetromino on the playfield.
        activeTetromino = nextPiece;

        // Setup the square units of the active tetromino.
        activeTetromino.setupBlocks(startX, startY, this);

        // Should tetromino collide at its current position,
        if (activeTetromino.wouldCollide(0, 0)) {

            // Attempt to spawn the piece again one cell higher.
            activeTetromino.setupBlocks(startX, startY - 1, this);

            // Should it still collide, then...
            if (activeTetromino.wouldCollide(0, 0)) {

                Block[] blocks = activeTetromino.getBlocks();
                for (Block b : blocks) {

                    int x = b.getX();
                    int y = b.getY();
                    set(x, y, b);
                }
                //...GAME OVER CODE GOES HERE
                setState(new GameOverState());

            }

            // No collisions will occur if shifted upwards, so the tetromino is added
            else {
                activeTetromino.addToGrid();
                setState(new FallState());
            }

        }

        // No collisions will occur if the tetromino is added.
        else {

            // Have the tetromino add itself to the playfield.
            activeTetromino.addToGrid();
            setState(new FallState());
        }
    }

    /**
     * Shifts the tetromino piece one space to the left.
     */
    void shiftTetrominoLeft() {

        // Can only be shifted if there is an active tetromino
        if (checkTetrominoActive()) {

            // Get the grouned state before shifting.
            boolean onGroundPrev = activeTetromino.checkIfGrounded();
            activeTetromino.shiftLeft();

            // Check if any phases should be cancelled as a result of the shift.
            checkCancel(onGroundPrev);
        }

    }

    /**
     * Shifts the tetromino piece one space to the right.
     */
    void shiftTetrominoRight() {

        // Can only be shifted if there is an active tetromino
        if (checkTetrominoActive()) {
            boolean onGroundPrev = activeTetromino.checkIfGrounded();
            activeTetromino.shiftRight();

            // Check if any phases should be cancelled as a result of the shift.
            checkCancel(onGroundPrev);
        }

    }

    /**
     * Gets the highest row index that is not empty.
     * <p>
     * If the playfield is completely empty, -1 is returned.
     * </p>
     *
     * @return The highest (lowest value) row index where the row is not empty. Otherwise -1.
     */
    private int getTopRow() {

        // Loop through each row index of the playfield
        for (int r = 0; r < gridHeight; r++) {

            // Once a row is found to not be empty, then this is the top row with at least one block.
            if (!checkRowEmpty(r))
                return r;
        }

        // -1 is returned if all rows are empty.
        return -1;
    }

    /**
     * Checks whether there is nothing on the grid at the given coordinates.
     *
     * @param x the column of the cell to check for emptiness
     * @param y the row of the cell to check for emptiness
     * @return Returns true if the cell is empty, and false if not.
     */
    boolean checkCellEmpty(int x, int y) {

        return (get(x, y) == null);
    }

    /**
     * Checks whether or not a given row of the playfield is empty
     *
     * @param row: the index of the row being checked for emptiness
     * @return true if the row is empty, false if not.
     */
    private boolean checkRowEmpty(int row) {

        // Get the row of blocks from grid at position given by the row index passed
        Block[] blockRow = grid[row];

        // Loop through each (apparent) block on the row
        for (Block b : blockRow) {

            // If not a null type, then this must be a block
            if (b != null) {

                // This means that the row is not empty
                return false;
            }
        }

        // At this point, all elements of the row are null, so it is empty
        return true;
    }

    /**
     * Checks whether or not the whole playfield is empty.
     *
     * @return true if it is empty, and false if not.
     */
    private boolean checkGridEmpty() {

        // Loop through each row index of the grid
        for (int r = 0; r < gridHeight; r++) {

            // If the row is not empty, then the grid is not completely empty
            if (!checkRowEmpty(r)) {

                return false;
            }
        }

        // At this point, all rows are checked, and they are empty. Therefore the whole grid is empty.
        return true;
    }

    /**
     * Checks whether or not a given row of the playfield is completely full of blocks
     *
     * @param row the index of the row being checked
     * @return true if the row is full, and false if not.
     */
    private boolean checkRowFull(int row) {

        // Get the row of blocks from grid at position given by the row index passed
        Block[] blockRow = grid[row];

        // Loop through each (apparent) block on the row
        for (Block b : blockRow) {

            // If it is a null type, then the cell is empty
            if (b == null) {
                // This means that the row is not full.
                return false;
            }
        }

        // At this point, all elements of the row list are blocks, so it is full.
        return true;
    }

    /**
     * Used to verify whether or not a block is located on the grid at its own supposed coordinates
     *
     * @param block The block that is being checked.
     * @return true if it is at the correct position, false otherwise.
     */
    private boolean checkBlockPosition(Block block) {

        // Get the apparent coordinates of where the block is stored at
        int x = block.getX();
        int y = block.getY();

        // get the Block instance found at that position
        Block blockFound = get(x, y);

        // Compare the block passed with the block found to verify its position.
        return block == blockFound;
    }

    /**
     * Checks whether or not that the passed position lies within the grid boundaries.
     *
     * @param x column position to check whether it lies within bounds.
     * @param y row position to check whether it lies within bounds.
     * @return true if it does not lie within the boundaries, false otherwise.
     */
    boolean isInbounds(int x, int y) {

        // will be out of bounds if x is not 0 - (<width of grid> - 1)
        if (x < 0 || x > gridWidth - 1) {

            return false;
        }

        // Will be in bounds if y is in range 0 - (<height of grid> - 1) inclusive.
        return (y >= 0) && (y <= (gridHeight - 1));
    }

    /**
     * Locks the active tetromino on the playfield.
     */
    private void lockTetromino() {

        // If the tetromino locked as a result of the lock delay,
        if (lockAccumulator > LOCK_DELAY) {

            // set the main accumulator to the excess time in the lock accumulator.
            accumulator = lockAccumulator - LOCK_DELAY;

        }

        // Otherwise reset the main accumulator.
        else
            accumulator = 0f;


        // Reset the lock accumulator.
        lockAccumulator = 0f;

        // For when the tetromino dropped with hard-drop:
        if (hardDropIsActive) {

            // Deactivate hard drop.
            hardDropIsActive = false;
            // Recalculate gravity to get its original value.
            gravity = model.calculateGravity();
        }

        //--- PATTERN PHASE START ---

        // Get the list of any full rows that are on the grid.
        List<Integer> fullRows = getFullRows();

        // If there does exist at least one full row,
        if (fullRows.size() > 0) {

            // Begin the line clear phase.
            setState(new ClearState(fullRows));
        }

        //--- PATTERN PHASE END ---

        // For when there are no rows to be cleared.
        else {
            // Get the row indexes of where the tetromino locked.
            List<Integer> rows = activeTetromino.getRows();

            // Check if the lowest row the tetromino locked at was out of sight (a 'lock out').
            if (Collections.max(rows) < HIDDEN_ROWS) {

                // A game over condition has been reached, so begin to end the game.
                setState(new GameOverState());

            } else {
                // Begin the spawn phase.
                setState(new EntryState());
            }


        }

        // The tetromino is now locked, thus no longer active.
        activeTetromino = null;

    }

    /**
     * Used to rotate the active tetromino 90 degrees clockwise.
     */
    void rotateClockwise() {

        // Only rotate if the tetromino is active.
        if (checkTetrominoActive()) {

            // Get the grounded state before shifting.
            boolean onGroundPrev = activeTetromino.checkIfGrounded();

            // Call the method of the same name from the active tetromino.
            activeTetromino.rotateClockwise();

            // Check if any phases should be cancelled as a result of the shift.
            checkCancel(onGroundPrev);
        }
    }

    /**
     * Used to rotate the active tetromino 90 degrees anticlockwise.
     */
    void rotateAnticlockwise() {

        // Only rotate if the tetromino is active.
        if (checkTetrominoActive()) {
            // Get the grounded state before shifting.
            boolean onGroundPrev = activeTetromino.checkIfGrounded();

            // Call the method of the same name from the active tetromino.
            activeTetromino.rotateAnticlockwise();

            // Check if any phases should be cancelled as a result of the shift.
            checkCancel(onGroundPrev);
        }
    }

    /**
     * Used to determine whether lock delay or gravity delay should be cancelled.
     * <p>
     * After a tetromino is shifted or rotated, it may change from grounded to not grounded, or vice versa.
     * In the first case, lock delay should be cancelled immediately.
     * In the second case, the tetromino should immediately stop falling and be under lock delay.
     * </p>
     *
     * @param onGroundPrev was the tetromino grounded before it was shifted/rotated?
     */
    private void checkCancel(boolean onGroundPrev) {

        // Cancel lock delay if no longer grounded.
        if (currentState.state == State.LOCK_PHASE && !activeTetromino.checkIfGrounded()) {
            setState(new FallState(), false);
        }

        // Begin lock delay if it is now grounded.
        else if (!onGroundPrev && currentState.state == State.DROP && activeTetromino.checkIfGrounded()) {
            // Reset accumulator.
            accumulator = 0f;
            setState(new LockState(), false);
        }

    }

    /**
     * Uses the playfield's current state to determine whether there is a controllable tetromino piece.
     *
     * @return true if there is an active tetromino, false otherwise.
     */
    private boolean checkTetrominoActive() {

        // Will be active if in the fall state of lock phase.
        return (currentState.state == State.DROP || currentState.state == State.LOCK_PHASE);

    }

    /**
     * Increases the rate at which the active tetromino piece falls down.
     */
    void activateSoftDrop() {

        // Determine the current soft drop gravity value using the constant soft drop modifier
        gravity = model.calculateGravity() * SOFT_DROP_MODIFIER;

        // Soft drop is now active.
        softDropIsActive = true;

        // When in lock phase,
        if (currentState.state == State.LOCK_PHASE) {

            // If the active tetromino is on the ground,
            if (activeTetromino.checkIfGrounded()) {

                GameAudio.FAST_LAND_CLIP.play();

                // Lock delay cancels when soft drop is activated.
                lockTetromino();
            }
        }

        // If in the falling state,
        else if (currentState.state == State.DROP) {

            // Set the accumulator to the soft drop gravity if it has already surpassed it.
            if (accumulator > gravity) {
                accumulator = gravity;
            }
        }
    }

    /**
     * Sets the gravity of the active tetromino piece on the playfield back to normal.
     */
    void deactivateSoftDrop() {

        // Set the gravity back to normal by recalculating the value depending on level.
        gravity = model.calculateGravity();
        // Soft drop is no longer active.
        softDropIsActive = false;

    }

    /**
     * Increases the gravity to such a high value that the tetromino will drop instantly on the playfield.
     */
    void hardDrop() {


        // Do not apply hard drop if there is no active tetromino piece.
        if (!checkTetrominoActive()) {
            return;
        }

        // Set the hard drop flag to true.
        hardDropIsActive = true;
        // Soft drop ends, so set its flag to false.
        softDropIsActive = false;

        // Set gravity to the constant hard drop gravity.
        gravity = HARD_DROP_GRAVITY;

        // If lock delay is being applied,
        if (currentState.state == State.LOCK_PHASE) {

            // If the tetromino is grounded,
            if (activeTetromino.checkIfGrounded()) {
                // Skip the lock delay and instantly lock the active tetromino.
                lockTetromino();

                // Play sound effect for hard drop.
                GameAudio.FAST_LAND_CLIP.play();
            }
        }

        // If in the falling state,
        else if (currentState.state == State.DROP) {

            // Set the accumulator to the hard drop gravity if it has already surpassed it.
            if (accumulator > gravity) {
                accumulator = gravity;
            }
        }

    }

    /**
     * Gets the positions of any full rows on the playfield.
     * <p>
     * These full rows will be cleared from the grid during the line clear phase.
     * </p>
     *
     * @return A list of indexes for any full rows on the grid.
     */
    private List<Integer> getFullRows() {

        // Define an empty list, to hold the row indexes of full blocks.
        List<Integer> fullRows = new ArrayList<>();

        // Get the row indexes of where the active tetromino locked.
        List<Integer> blockRows = activeTetromino.getRows();

        // Iterate through each row the active tetromino locked at.
        for (int rowIndex : blockRows) {

            // Check whether or not the row is full.
            if (checkRowFull(rowIndex)) {

                // If it is, add it to the fullRows list.
                fullRows.add(rowIndex);
            }
        }

        return fullRows;

    }

    /**
     * Set the state (or phase) of the game
     *
     * @param newState    the new state the game should be set to.
     * @param updateState determines whether the new state should be updated immediately after it is entered.
     */
    private void setState(StateMachine newState, boolean updateState) {

        // Exit the current state, passing in the new state.
        currentState.exitState(newState.getState());

        State oldState = currentState.getState();

        // Set the current state to the new state.
        currentState = newState;

        // Enter the new state, passing in the old state.
        newState.enterState(oldState, updateState);
    }

    /**
     * Set the state (or phase) of the game
     *
     * @param newState the new state the game should be set to.
     */
    private void setState(StateMachine newState) {

        // By default, the new state should be updated upon entering.
        setState(newState, true);
    }


    ////////////////////////////////////////////////////////////////

    /**
     * Represents a finite state machine for managing the various states of the game.
     */
    abstract class StateMachine {

        private State state;

        StateMachine(State state) {

            this.state = state;
        }

        State getState() {

            return state;
        }

        public abstract void enterState(State prev, boolean updateState);

        public abstract void exitState(State next);

        public abstract void updateState();
    }

    /**
     * The neutral state of the game - where the active tetromino drops under gravity.
     */
    class FallState extends StateMachine {

        FallState() {

            super(State.DROP);

        }

        @Override
        public void enterState(State prev, boolean updateState) {

            if (updateState)
                updateState();
        }

        @Override
        public void exitState(State next) {

        }

        @Override
        public void updateState() {

            // When the accumulator is pass the gravity delay, the tetromino should be dropped.
            while (accumulator > gravity) {

                // Check if the active tetromino is grounded.
                if (activeTetromino.checkIfGrounded()) {

                    if (hardDropIsActive) {
                        GameAudio.FAST_LAND_CLIP.play();
                        lockTetromino();
                        return;
                    }

                    // Engage the lock phase to allow for lock delay.
                    else {
                        setState(new LockState());
                        return;
                    }
                }

                // Apply gravity to the active tetromino piece.
                activeTetromino.shiftDown();

                // Reset the lock accumulator every time the tetromino drops
                lockAccumulator = 0f;

                // IF SOFT DROP IS ACTIVE, INCREASE SCORE BY 1.
                if (softDropIsActive)
                    model.addSoftDropPoints();

                // IF HARD DROP IS ACTIVE, INCREASE POINTS ACCORDINGLY.
                if (hardDropIsActive)
                    model.addHardDropPoints();

                // Decrease the accumulator by the gravity time interval.
                accumulator -= gravity;
            }

            // Always end by checking whether the tetromino is grounded.
            if (activeTetromino.checkIfGrounded()) {

                if (hardDropIsActive) {
                    lockTetromino();
                }

                // Engage the lock phase to allow for lock delay.
                else {
                    setState(new LockState());
                }
            }

        }
    }

    /**
     * Represents the state that handles the locking of the active tetromino piece onto the playfield after managing
     * lock delay.
     */
    class LockState extends StateMachine {

        LockState() {

            super(State.LOCK_PHASE);
        }

        @Override
        public void enterState(State prev, boolean updateState) {

            if (updateState)
                updateState();
        }

        @Override
        public void exitState(State next) {

        }

        @Override
        public void updateState() {

            // Feed the main accumulator into the lock accumulator.
            lockAccumulator += accumulator;
            accumulator = 0f;

            // If the active tetromino should lock on this frame, where lock delay has ended,
            if (lockAccumulator > LOCK_DELAY) {

                GameAudio.LAND_CLIP.play();

                // Lock the tetromino piece.
                lockTetromino();


            }

        }
    }

    /**
     * Represents the state of the game where there the next piece is spawned on the playfield after a specific delay.
     */
    class EntryState extends StateMachine {

        EntryState() {

            super(State.ENTRY_PHASE);
        }

        @Override
        public void enterState(State prev, boolean updateState) {

            if (updateState)
                updateState();
        }

        @Override
        public void exitState(State next) {
        }

        @Override
        public void updateState() {

            if (accumulator > ENTRY_DELAY) {

                // activate tetromino.
                accumulator -= ENTRY_DELAY;

                model.setActiveTetromino();
            }

        }
    }

    /**
     * Represents the state where the game clears full lines.
     */
    class ClearState extends StateMachine {

        private List<Integer> fullRows;

        private float clearInterval = 0.02f;

        private float remainingDelay = CLEAR_DELAY;

        private int currentColumn = 0;

        ClearState(List<Integer> fullRows) {

            super(State.LINE_CLEAR);

            Collections.sort(fullRows);
            this.fullRows = fullRows;


        }

        @Override
        public void enterState(State prev, boolean updateState) {

            if(fullRows.size() == 4){
                GameAudio.TETRIS_CLIP.play();
            }
            else {
                GameAudio.CLEAR_CLIP.play();
            }
        }

        @Override
        public void exitState(State next) {

        }

        @Override
        public void updateState() {

            while (accumulator > clearInterval && currentColumn < gridWidth) {

                for (int rowIndex : fullRows) {

                    setNull(currentColumn, rowIndex);
                }

                accumulator -= clearInterval;
                remainingDelay -= clearInterval;
                currentColumn += 1;
            }

            if (currentColumn >= gridWidth && accumulator > remainingDelay) {

                shiftRowsDown(fullRows);

                // INCREASE LINES SCORE
                model.increaseLines(fullRows.size());

                if (!softDropIsActive)
                    gravity = model.calculateGravity();

                else
                    gravity = model.calculateGravity() * SOFT_DROP_MODIFIER;

                accumulator -= remainingDelay;
                setState(new EntryState());
            }
        }
    }

    class GameOverState extends StateMachine {

        private static final float INITIAL_DELAY = 2f;
        private static final float DELAY_INTERVAL = 0.05f;

        private boolean hasInitialDelay = false;
        private int currentRow = 0;

        // private boolean hasEnded = false;

        private float remainingDelay = GAME_OVER_DELAY;

        GameOverState() {

            super(State.GAME_OVER);
        }

        @Override
        public void enterState(State prev, boolean updateState) {

        }

        @Override
        public void exitState(State next) {

        }

        @Override
        public void updateState() {

            if (accumulator > INITIAL_DELAY) {

                hasInitialDelay = true;
                accumulator -= INITIAL_DELAY;
                remainingDelay -= INITIAL_DELAY;
                GameAudio.GAME_OVER_CLIP.play();
            }

            while (hasInitialDelay && accumulator > DELAY_INTERVAL && currentRow < gridHeight) {

                clearRow(currentRow);

                accumulator -= DELAY_INTERVAL;
                remainingDelay -= DELAY_INTERVAL;
                currentRow += 1;
            }

            if (currentRow >= gridHeight && accumulator > remainingDelay) {

                model.gameOver();

            }


        }
    }


}