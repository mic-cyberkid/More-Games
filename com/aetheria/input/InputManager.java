package com.aetheria.input;

import java.awt.event.*;
import java.util.BitSet;

public final class InputManager implements KeyListener, MouseListener {

    private static final int KEY_COUNT   = 256;
    private static final int MOUSE_COUNT = 5;

    private final BitSet pressed   = new BitSet(KEY_COUNT);
    private final BitSet justDown  = new BitSet(KEY_COUNT);
    private final BitSet justUp    = new BitSet(KEY_COUNT);

    public void endFrame() {
        justDown.clear();
        justUp.clear();
    }

    public boolean isHeld(int keyCode)     { return pressed.get(keyCode); }
    public boolean isJustPressed(int keyCode)  { return justDown.get(keyCode); }
    public boolean isJustReleased(int keyCode) { return justUp.get(keyCode); }

    @Override public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k < KEY_COUNT) {
            if (!pressed.get(k)) justDown.set(k);
            pressed.set(k);
        }
    }

    @Override public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k < KEY_COUNT) {
            pressed.clear(k);
            justUp.set(k);
        }
    }

    @Override public void keyTyped(KeyEvent e) { /* unused */ }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
