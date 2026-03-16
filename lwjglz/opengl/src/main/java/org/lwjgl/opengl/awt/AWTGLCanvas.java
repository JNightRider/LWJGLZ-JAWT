/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.awt;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.opengl.awt.AWTGL.*;

/**
 *
 * @author wil
 */
public class AWTGLCanvas extends Canvas {
    
    private GLPlatform<Canvas> platform;
    private GLContext context;
    private GLData gldata;
    
    private final Object SYNC_LOCK = new Object();

    /**
     * Tracks whether initGL() needs to be called
     */    
    private final AtomicBoolean firstRun = new AtomicBoolean(true);
    private final AtomicBoolean createdPlatform = new AtomicBoolean(false);
    private final AtomicBoolean createdContext  = new AtomicBoolean(false);
    
    private int framebufferWidth, framebufferHeight;
    private final ComponentListener listener = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            synchronized (SYNC_LOCK) {
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
        }
    };
    
    public AWTGLCanvas(GLData gldata) {
        this.gldata = gldata;
        AWTGLCanvas.this.addComponentListener(listener);
    }
    
    public AWTGLCanvas() {
        this(new GLData());
    }
    
    @Override
    public synchronized void addComponentListener(ComponentListener cl) {
        super.addComponentListener(cl);
    }
    
    @Override
    public void removeNotify() {
        synchronized (SYNC_LOCK) {
            destroy();
            firstRun.set(true);
            super.removeNotify();
        }
    }

    /**
     * Set swap interval.
     * 
     * @param swapInterval value
     */
    public void setSwapInterval(int swapInterval) {
        synchronized (SYNC_LOCK) {
            if (platform == null) {
                throw new IllegalStateException("Canvas not yet displayable");
            }
            context.swapInterval(swapInterval);
        }
    }

    /**
     * Enable vsync
     * 
     * @param enabled value
     */
    public void setVSyncEnabled(boolean enabled) {
        setSwapInterval(enabled ? 1 : 0);
    }

    /**
     * Swap the canvas' buffer
     */
    public void swapBuffers() {
        synchronized (SYNC_LOCK) {
            if (platform == null) {
                throw new IllegalStateException("Canvas not yet displayable");
            }
            context.swapBuffers();
        }
    }

    public boolean isCurrent() {
        synchronized (SYNC_LOCK) {
            if (platform == null) {
                throw new IllegalStateException("Canvas not yet displayable");
            }

            long ctx = context.getHandle();
            return ctx == context.getCurrentContext();
        }
    }

    /**
     * Make the canvas' context current. It is highly recommended that the
     * context is only made current inside the AWT thread (for example in an
     * overridden paintGL()).
     */
    public void makeCurrent() {
        synchronized (SYNC_LOCK) {
            if (platform == null) {
                throw new IllegalStateException("Canvas not yet displayable");
            }
            context.makeContextCurrent(true);
        }
    }

    public void releaseContext() {
        synchronized (SYNC_LOCK) {
            if (platform == null) {
                throw new IllegalStateException("Canvas not yet displayable");
            }
            if (isCurrent()) {
                context.makeContextCurrent(false);
            }
        }
    }
    
    /**
     * Destroy the OpenGL context. This happens when the component becomes
     * undisplayable
     */
    public final void destroy() {
        synchronized (SYNC_LOCK) {
            if (context != null) {
                context.destroy();
                context = null;
                createdContext.set(false);
            }
            if (platform != null) {
                platform.dispose();
                platform = null;
                createdPlatform.set(false);
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
    
    /**
     * The default paint() operation makes the context current and calls
     * paintGL() which should be overridden to do GL operations.
     */
    public final void render() {
        synchronized (SYNC_LOCK) {
            if (!isDisplayable()) {
                return;
            }
            try {
                if (!createdPlatform.get()) {
                    createdPlatform.set(true);  
                    platform = glGetAttachAWTWindow(gldata);
                    platform.create(this);
                }
                platform.lock();
                try {
                    if (!createdContext.get()) {
                        createdContext.set(true);
                        context = glNewAttachAWTContext(platform);
                        context.createContext();
                    }
                    
                    context.makeContextCurrent(true);
                    try {
                        if (firstRun.get()) {
                            firstRun.set(false);
                            initGL();
                        }
                        paintGL();
                    } finally {
                        context.makeContextCurrent(false);
                    }
                } finally {
                    platform.unlock();
                }
            } catch (AWTException e) {
                throw new RuntimeException("Exception while creating the OpenGL context", e);
            }
        }
    }
    
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        render();
        repaint();
    }

    public int getFramebufferWidth() {
        return framebufferWidth;
    }

    public int getFramebufferHeight() {
        return framebufferHeight;
    }

    public GLPlatform<Canvas> getPlatform() {
        return platform;
    }

    public GLContext getContext() {
        return context;
    }
}
