package com.yahoo.dodeleye.tetris.controller;

import com.yahoo.dodeleye.tetris.Tetris1;
import com.yahoo.dodeleye.tetris.view.GamePanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Handles all input controls from the player for the game.
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class InputController {

    /**
     * The initial delay before applying auto-repeat.
     */
    static final float AUTOREPEAT_TIME = 0.15f;
    /**
     * The delays between auto-repeat shifts.
     */
    static final float AUTOREPEAT_DELAYS = 0.05f;

    /**
     * The map used to determine whether a key for a given input is being held or not.
     * <p>
     * Its key is a string used as an identifier for the input.
     * The value part of the pair true if the key is held or false if it is not being held.
     * </p>
     */
    private HashMap<String, Boolean> inputMap = new HashMap<>();

    InputController() {

        List<InputController.InputId> inputs = new ArrayList<>(Arrays.asList(InputController.InputId.values()));

        for(InputController.InputId input : inputs){

            String id = input.getValue();
            int keyCode = input.getKeyCode();
            addInput(id, keyCode);
        }
    }

    /**
     * Creates a new input map identical to the current one.
     *
     * @return an input map identical to the current one.
     */
    HashMap<String, Boolean> getInputMapCopy() {

        return new HashMap<>(inputMap);
    }

    /**
     * Registers a new input to the controller.
     *
     * @param inputKey a key id for the input
     * @param keyCode  the key code for the key used to detect the input.
     */
    private void addInput(String inputKey, int keyCode) {

        // Get the game panel.
        GamePanel gamePanel = Tetris1.getGamePanel();

        // Create a new action for when the key is pressed.
        Action pressAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // Set the 'is key pressed?' value to true.
                inputMap.put(inputKey, true);
            }
        };

        // Create a new action for when the key is released.
        Action releaseAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Set the 'is key pressed?' value to false.
                inputMap.put(inputKey, false);
            }
        };

        // Create key ids for when the key is pressed and released.
        String pressKey = inputKey + "Pressed";
        String releaseKey = inputKey + "Released";

        // Add the key to the input map, setting the 'is key pressed' value to false.
        inputMap.put(inputKey, false);

        // Add to the game panel's own input map and action map assigned to it.
        gamePanel.getInputMap().put(KeyStroke.getKeyStroke(keyCode, 0), pressKey);
        gamePanel.getInputMap().put(KeyStroke.getKeyStroke(keyCode, 0, true), releaseKey);

        gamePanel.getActionMap().put(pressKey, pressAction);
        gamePanel.getActionMap().put(releaseKey, releaseAction);
    }

    @Deprecated
    public void removeInput(String inputKey, int keyCode) {

        GamePanel gamePanel = Tetris1.getGamePanel();

        String pressKey = inputKey + "Pressed";
        String releaseKey = inputKey + "Released";

        inputMap.remove(inputKey);
        gamePanel.getInputMap().remove(KeyStroke.getKeyStroke(keyCode, 0));
        gamePanel.getInputMap().remove(KeyStroke.getKeyStroke(keyCode, 0, true));

        gamePanel.getActionMap().remove(pressKey);
        gamePanel.getActionMap().remove(releaseKey);
    }

    @Deprecated
    public boolean keyHeld(String key) {

        return inputMap.get(key);

    }

    /**
     * Defines a unique identifier and the keyboard button used for each input control of the game.
     * @version 1.0.0
     * @since 1.0.2
     */
    public enum InputId {

        LEFT                ("l" , KeyEvent.VK_LEFT),
        RIGHT               ("r" , KeyEvent.VK_RIGHT),
        ROTATE_CLOCKWISE    ("rc", KeyEvent.VK_X),
        ROTATE_ANTICLOCKWISE("ra", KeyEvent.VK_Z),
        SOFT_DROP           ("sd", KeyEvent.VK_DOWN),
        HARD_DROP           ("hd", KeyEvent.VK_SPACE);

        // The id for the input.
        private final String value;

        // The default keycode for the input.
        private final int defaultKeyCode;
        // The current keycode used for the input.
        private int keyCode;

        InputId(String value, int keyCode){

            this.value = value;
            this.keyCode = keyCode;
            this.defaultKeyCode = keyCode;
        }

        public String getValue(){

            return value;
        }

        public int getKeyCode(){

            return keyCode;
        }

        @Deprecated
        public void setKeyCode(int keyCode){

            this.keyCode = keyCode;
        }

        @Deprecated
        public void resetKeyCode(){

            this.keyCode = defaultKeyCode;
        }
    }

}
