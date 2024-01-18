package com.yahoo.dodeleye.tetris.controller;

import com.yahoo.dodeleye.GameAudio;
import com.yahoo.dodeleye.gamelib.gamethread.GameThread;
import com.yahoo.dodeleye.tetris.Tetris1;
import com.yahoo.dodeleye.tetris.model.GameLogic;

import java.util.HashMap;

/**
 * A thread used to create the main loop of the program.
 */
public class MyGameThread extends GameThread {

    // Handles the user input events of the game.
    private InputController inputController;

    // A map holding the state of all the input controls for the game.
    private HashMap<String, Boolean> inputMap;

    // The counter used for auto-repeat of the left and right keys.
    private float autorepeatCounter = 0f;

    // Used to determine whether the left input key is held.
    private boolean leftKeyHeld = false;

    // Used to determine whether the left input key is held.
    private boolean rightKeyHeld = false;

    public MyGameThread() {

        // Initialise the input controller.
        inputController = new InputController();

        // Get input map from the new input controller.
        inputMap = inputController.getInputMapCopy();

        // Play the background music.
        GameAudio.BG_MUSIC_CLIP.play(true);

    }

    // Used to end the thread, thus ending the game.
    public void endThread() {
        running = false;
    }

    @Override
    public void gameLogic(double dt) {

        // Update the model component.
        Tetris1.getGameModel().update(dt);

    }

    @Override
    public void repaint(double dt) {

        // Repaint the view component.
        Tetris1.getMainFrame().repaint();

    }


    /**
     * Determines whether a key was pressed down on this frame.
     * <p>
     * It compares the input key maps from the current frame and the last frame to determine whether the value
     * stored for the given key has changed.
     * </p>
     *
     * @param key    the input key to check.
     * @param newMap the state of the input map on the frame after the current one (this frame).
     * @return true if key was pressed down this frame, otherwise false.
     */
    private boolean keyDown(String key, HashMap<String, Boolean> newMap) {

        // Get value from the current map (from last frame).
        boolean held1 = inputMap.get(key);

        // Get value from the new map (from this frame).
        boolean held2 = newMap.get(key);

        // The key will have been pressed down if values have shifted from false -> true.
        return (held2 && !held1);
    }

    /**
     * Determines whether a key was released on this frame.
     * <p>
     * It compares the input key maps from the current frame and the last frame to determine whether the value
     * stored for the given key has changed.
     * </p>
     *
     * @param key    the input key to check.
     * @param newMap the state of the input map on the frame after the current one (this frame).
     * @return true if key was released this frame, otherwise false.
     */
    private boolean keyUp(String key, HashMap<String, Boolean> newMap) {

        // Get value from the current map (from last frame).
        boolean held1 = inputMap.get(key);

        // Get value from the new map (from this frame).
        boolean held2 = newMap.get(key);

        // The key will have been pressed down if values have shifted from true -> false.
        return (held1 && !held2);
    }

    @Override
    public void processInput(double dt) {

        // Get the game model.
        GameLogic model = Tetris1.getGameModel();

        // Get the input map from this frame.
        HashMap<String, Boolean> newMap = inputController.getInputMapCopy();

        // Get all input key values.
        String clockwiseKey     = InputController.InputId.ROTATE_CLOCKWISE.getValue();
        String anticlockwiseKey = InputController.InputId.ROTATE_ANTICLOCKWISE.getValue();
        String softDropKey      = InputController.InputId.SOFT_DROP.getValue();
        String hardDropKey      = InputController.InputId.HARD_DROP.getValue();
        String leftKey          = InputController.InputId.LEFT.getValue();
        String rightKey         = InputController.InputId.RIGHT.getValue();

        // First process auto-repeat.
        autorepeat(dt);

        // If the left key is first held down this frame:
        if (keyDown(leftKey, newMap)) {

            // Shift the active tetromino to the left.
            model.shiftTetrominoLeft();
            // Left key is now being held down.
            leftKeyHeld = true;
            // Both keys should not be able to be held down simultaneously.
            rightKeyHeld = false;
            // Ensure that the auto-repeat counter is reset.
            autorepeatCounter = 0f;
        }

        // If the left key is first held down this frame:
        else if (keyDown(rightKey, newMap)) {

            // Shift the active tetromino to the right.
            model.shiftTetrominoRight();
            // Right key is now being held down.
            rightKeyHeld = true;
            // Both keys should not be able to be held down simultaneously.
            leftKeyHeld = false;
            // Ensure that the auto-repeat counter is reset.
            autorepeatCounter = 0f;

        }

        // If the clockwise rotation key was pressed down:
        if (keyDown(clockwiseKey, newMap)) {
            // Rotate the tetromino 90 degrees clockwise.
            model.rotateClockwise();
        }
        // Or if the anticlockwise rotation key was pressed down:
        else if (keyDown(anticlockwiseKey, newMap)) {
            // Rotate the tetromino 90 degrees anticlockwise.
            model.rotateAnticlockwise();
        }

        // If the soft drop key was pressed down:
        if (keyDown(softDropKey, newMap))
            // Make tetromino fall faster.
            model.activateSoftDrop();

        // If the hard drop key was pressed down:
        if (keyDown(hardDropKey, newMap))
            // Drop the tetromino instantaneously
            model.hardDrop();

        // If the left input was released:
        if (keyUp(leftKey, newMap)) {
            // Left key is no longer being held down.
            leftKeyHeld = false;
            // Ensure that the auto-repeat counter is reset.
            autorepeatCounter = 0f;
        }
        // Or if the right input was released:
        else if (keyUp(rightKey, newMap)) {
            // Right key is no longer being held down.
            rightKeyHeld = false;
            // Ensure that the auto-repeat counter is reset.
            autorepeatCounter = 0f;
        }

        // If the soft drop key was released:
        if (keyUp(softDropKey, newMap))
            // Male the tetromino fall at normal speed.
            model.deactivateSoftDrop();


        // End by setting the current map to the new one.
        inputMap = newMap;

    }

    /**
     * Handles the functionality for delayed auto-shift (DAS), checking whether it should be applied.
     * @param dt difference in time (in seconds) between the last frame and this one.
     * @see MyGameThread processInput(double dt)
     */
    private void autorepeat(double dt) {

        // Get the game model.
        GameLogic model = Tetris1.getGameModel();

        // Increase the auto-repeat counter for every frame where the left or right key is held down.
        if (leftKeyHeld || rightKeyHeld)
            autorepeatCounter += dt;
        // Do nothing if neither are held down.
        else
            return;


        // While the auto-repeat counter has surpassed the number of frames required to activate DAS:
        while (autorepeatCounter > InputController.AUTOREPEAT_TIME) {

            // If holding the left key:
            if (leftKeyHeld) {
                // Shift the tetromino piece one space to the left.
                model.shiftTetrominoLeft();
                // Push back the counter by the delay interval value to allow for delays between shifts.
                autorepeatCounter -= InputController.AUTOREPEAT_DELAYS;
            }

            // If holding the right key:
            if (rightKeyHeld) {
                // Shift the tetromino piece one space to the right.
                model.shiftTetrominoRight();
                // Push back the counter by the delay interval value to allow for delays between shifts.
                autorepeatCounter -= InputController.AUTOREPEAT_DELAYS;
            }

        }

    }

}
