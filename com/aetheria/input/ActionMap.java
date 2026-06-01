package com.aetheria.input;

import java.awt.event.KeyEvent;
import java.util.EnumMap;

public final class ActionMap {

    private final InputManager input;
    private final EnumMap<Action, Integer> bindings = new EnumMap<>(Action.class);

    public ActionMap(InputManager input) {
        this.input = input;
        applyDefaults();
    }

    private void applyDefaults() {
        bindings.put(Action.MOVE_UP,      KeyEvent.VK_W);
        bindings.put(Action.MOVE_DOWN,    KeyEvent.VK_S);
        bindings.put(Action.MOVE_LEFT,    KeyEvent.VK_A);
        bindings.put(Action.MOVE_RIGHT,   KeyEvent.VK_D);
        bindings.put(Action.RUN,          KeyEvent.VK_SHIFT);
        bindings.put(Action.INTERACT,     KeyEvent.VK_E);
        bindings.put(Action.ATTACK,       KeyEvent.VK_SPACE);
        bindings.put(Action.OPEN_INVENTORY, KeyEvent.VK_I);
        bindings.put(Action.PAUSE,        KeyEvent.VK_ESCAPE);
        bindings.put(Action.CONFIRM,      KeyEvent.VK_ENTER);
        bindings.put(Action.CANCEL,       KeyEvent.VK_BACK_SPACE);
        bindings.put(Action.DEBUG_TOGGLE, KeyEvent.VK_F3);
    }

    public boolean isHeld(Action a)        { return input.isHeld(bindings.get(a)); }
    public boolean isJustPressed(Action a)  { return input.isJustPressed(bindings.get(a)); }
    public boolean isJustReleased(Action a) { return input.isJustReleased(bindings.get(a)); }

    public void rebind(Action a, int keyCode) {
        bindings.put(a, keyCode);
    }
}
