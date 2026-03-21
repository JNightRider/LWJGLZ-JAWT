/*
 * Copyright lwjglz-jawt. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;

import static org.lwjgl.opengl.jawt.AWTGL.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 *
 * @author wil
 */
public class AWTGLCanvas extends Canvas {
    
    private final Object SYNC_LOCK = new Object();
    
    private GLData data;
    private Window window;
    private GLContext context;
    
    private int framebufferWidth, framebufferHeight;
    private final ComponentListener listener = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            GraphicsConfiguration gc = AWTGLCanvas.this.getGraphicsConfiguration();
            if (gc == null) {
                return;
            }

            AffineTransform at = gc.getDefaultTransform();
            float sx = (float) at.getScaleX(),
                  sy = (float) at.getScaleY();

            int fw = (int) (AWTGLCanvas.this.getWidth() * sx);
            int fh = (int) (AWTGLCanvas.this.getHeight() * sy);

            if (fw != framebufferWidth || fh != framebufferHeight) {
                framebufferWidth = Math.max(fw, 1);
                framebufferHeight = Math.max(fh, 1);
            }
        }
    };

    public AWTGLCanvas() {
        this(new GLData(), null, null);
    }

    public AWTGLCanvas(GLData data) {
        this(data, null, null);
    }

    public AWTGLCanvas(GLData data, Window window, GLContext context) {
        AWTGLCanvas.this.addComponentListener(listener);
        this.data    = data;
        this.window  = window;
        this.context = context;
    }
    
    @Override
    public synchronized void addComponentListener(ComponentListener cl) {
        super.addComponentListener(cl);
    }
    
    /**
     * Set swap interval.
     * 
     * @param interval int
     */
    public void setSwapInterval(int interval) {
        synchronized (SYNC_LOCK) {
            if (context == null) {
                throw new IllegalStateException("Canvas not yet displayable");
            }
            context.swapInterval(interval);
        }
    }
    
    /**
     * Enable vsync
     * @see #setSwapInterval(int) 
     * 
     * @param enabled boolean
     */
    public void setVSyncEnabled(boolean enabled) {
        setSwapInterval(enabled ? 1 : 0);
    }
    
    /**
     * Swap the canvas' buffer
     */
    public void swapBuffers() {
        synchronized (SYNC_LOCK) {
            if (context == null) {
                throw new IllegalStateException("Canvas not yet displayable");
            }
            context.swapBuffers();
        }
    }
    
    public boolean isCurrent() {
        synchronized (SYNC_LOCK) {
            if (context == null) {
                throw new IllegalStateException("Canvas not yet displayable");
            }

            return context.isCurrent();
        }
    }

    /**
     * Make the canvas' context current. It is highly recommended that the
     * context is only made current inside the AWT thread (for example in an
     * overridden paintGL()).
     */
    public void makeCurrent() {
        synchronized (SYNC_LOCK) {
            if (context == null) {
                throw new IllegalStateException("Canvas not yet displayable");
            }
            context.makeCurrent();
        }
    }

    public void releaseContext() {
        synchronized (SYNC_LOCK) {
            if (context == null) {
                throw new IllegalStateException("Canvas not yet displayable");
            }
            if (context.isCurrent()) {
                context.releaseCurrent();
            }
        }
    }

    @Override
    public void paint(Graphics g) { }
    
    public void render() {
        synchronized (SYNC_LOCK) {
            try {
                if (window == null) {
                    window = glGetAttachWindow();
                    window.create(this);
                }            
                window.lock();
                try {
                    if (context == null) {
                        context = glNewAttachContext(window, data, NULL);
                        context.initGL();
                        context.create();
                        context.makeCurrent();
                        initGL();
                    } else {
                        context.makeCurrent();
                    }

                    paintGL();

                    context.swapBuffers();
                    context.releaseCurrent();                
                } finally {
                    window.unlock();
                }
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        }
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

    public int getFramebufferWidth() {
        return framebufferWidth;
    }

    public int getFramebufferHeight() {
        return framebufferHeight;
    }

    /**
     * Destroy the OpenGL context. This happens when the component becomes
     * undisplayable
     */
    public void destroy() {
        if (context != null) {
            context.releaseCurrent();
            context.delete();
            context = null;
        }
        if (window != null) {
            window.dispose();
        }
        window = null;
    }

    public Window getWindow() {
        return window;
    }

    public GLContext getContext() {
        return context;
    }
}
