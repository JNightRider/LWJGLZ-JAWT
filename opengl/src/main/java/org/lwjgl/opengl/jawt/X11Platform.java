/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import java.awt.AWTException;
import java.awt.Component;

import org.lwjgl.system.jawt.*;

import static org.lwjgl.system.jawt.JAWTFunctions.*;
import static org.lwjgl.system.linux.X11.*;

/**
 *
 * @author wil
 * @param <T>
 */
public class X11Platform<T extends Component> implements JAWTGLPlatform<T> {
    
    private JAWT awt;
    private JAWTDrawingSurface ds;
    
    private T component;
    
    private final GLFBconfig fbconfig = new GLFBconfig();
    private final GLPlatformConfig ctxconfig = new GLPlatformConfig();

    public X11Platform() { }

    @Override
    public void create(T handle) throws AWTException {
        awt = JAWT.calloc();
        awt.version(JAWT_VERSION_1_4);
        if (!JAWT_GetAWT(awt))
            throw new AssertionError("GetAWT failed");
        
        component = handle;
    }
    
    private void check_AWT_GetDrawingSurface() {
        if (ds == null) {
            // Get the drawing surface
            ds = JAWT_GetDrawingSurface(component, awt.GetDrawingSurface());
            if (ds == null) {
                throw new IllegalStateException("JAWT_GetDrawingSurface() failed");
            }
        }
    }
    
    @Override
    public void lock() throws AWTException {
        check_AWT_GetDrawingSurface();
        int lock = JAWT_DrawingSurface_Lock(ds, ds.Lock());
        if ((lock & JAWT_LOCK_ERROR) != 0) {
            throw new AWTException("JAWT_DrawingSurface_Lock() failed");
        }
    }

    @Override
    public void unlock() throws AWTException {
        check_AWT_GetDrawingSurface();
        JAWT_DrawingSurface_Unlock(ds, ds.Unlock());
    }

    @Override
    public GLFBconfig getFBconfig() {
        return fbconfig;
    }

    @Override
    public GLPlatformConfig getPlatformConfig() {
        return ctxconfig;
    }

    @Override
    public void dispose() {
        if (ds != null) {
            JAWT_FreeDrawingSurface(ds, awt.FreeDrawingSurface());
            awt.free();
            ds = null;
        }
    }

    @Override
    public T getComponent() {
        return component;
    }
    
    public int getScreen() {
        return XDefaultScreen(getDisplay());
    }
    
    @Override
    public long getDisplay() {
        check_AWT_GetDrawingSurface();
        // Get the drawing surface info
        JAWTDrawingSurfaceInfo dsi = JAWT_DrawingSurface_GetDrawingSurfaceInfo(ds, ds.GetDrawingSurfaceInfo());
        if (dsi == null) {
            throw new IllegalStateException("JAWT_DrawingSurface_GetDrawingSurfaceInfo() failed");
        }
        
        try {
            // Get the platform-specific drawing info
            JAWTX11DrawingSurfaceInfo dsi_x11 = JAWTX11DrawingSurfaceInfo.create(dsi.platformInfo());
            return dsi_x11.display();
        } finally {
            JAWT_DrawingSurface_FreeDrawingSurfaceInfo(dsi, ds.FreeDrawingSurfaceInfo());
        }
    }
    
    @Override
    public long getSurface() {
        check_AWT_GetDrawingSurface();
        // Get the drawing surface info
        JAWTDrawingSurfaceInfo dsi = JAWT_DrawingSurface_GetDrawingSurfaceInfo(ds, ds.GetDrawingSurfaceInfo());
        if (dsi == null) {
            throw new IllegalStateException("JAWT_DrawingSurface_GetDrawingSurfaceInfo() failed");
        }
        
        try {
            // Get the platform-specific drawing info
            JAWTX11DrawingSurfaceInfo dsi_x11 = JAWTX11DrawingSurfaceInfo.create(dsi.platformInfo());
            return dsi_x11.drawable();
        } finally {
            JAWT_DrawingSurface_FreeDrawingSurfaceInfo(dsi, ds.FreeDrawingSurfaceInfo());
        }
    }
}
