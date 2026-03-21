/*
 * Copyright lwjglz-jawt. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Graphics;
import org.lwjgl.opengl.GL;

/**
 *
 * @author wil
 */
public class AWTGLCanvas extends Canvas {
    
    private GLData data;
    private X11Window windows;
    private GLXContext context;

    public AWTGLCanvas() {
        this.data = new GLData();
    }

    @Override
    public void paint(Graphics g) {
        render();
    }
    
    public void render() {
        try {
            if (windows == null) {
                windows = new X11Window();
                windows.create(this);
                
                System.out.println("create x11()");
            }
            
            windows.lock();
            try {
                if (context == null) {
                    context = new GLXContext(windows, data);
                    context.initGL();
                    context.create();
                    
                    System.out.println("create GL()");
                    context.makeCurrent();
                    initGL();
                } else {
                    context.makeCurrent();
                }
                
                paintGL();
                
                context.swapBuffers();
                context.releaseContext();                
            } finally {
                windows.unlock();
            }
        } catch (AWTException e) {
            e.printStackTrace(System.out);
        }
    }

    public X11Window getWindows() {
        return windows;
    }

    public GLXContext getContext() {
        return context;
    }

    public void setWindows(X11Window windows) {
        this.windows = windows;
    }

    public void setContext(GLXContext context) {
        this.context = context;
    }
    
    /**
     * Override this to do initialising of the context. It will be called once
     * from paint(), immediately after the context is created and made current.
     */
    protected void initGL() {
    }

    /**
     * Override this to do painting
     */
    protected void paintGL() {
    }
    
    public void destroy() {
        if (windows != null) {
            windows.dispose();
        }
    }
}
