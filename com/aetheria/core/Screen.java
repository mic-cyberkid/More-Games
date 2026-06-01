package com.aetheria.core;

import com.aetheria.render.Renderer;

public interface Screen {
    void   onEnter();
    void   onExit();
    void   onSuspend();
    void   onResume();
    void   update(double dt);
    void   render(Renderer r, double alpha);
    default boolean isTransparent() { return false; }
}
