/*
 * Copyright lwjglz-jawt. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import java.awt.Component;

import org.lwjgl.system.jawt.*;
import static org.lwjgl.system.jawt.JAWTFunctions.*;

/**
 *
 * @author wil
 */
public class Win32Window implements Window {
    
    private final JAWT awt;
    private JAWTDrawingSurface ds;
    
    private long hwnd;
    private long hdc;

    public Win32Window() {
        awt = JAWT.calloc();
        awt.version(JAWT_VERSION_1_4);
        if (!JAWT_GetAWT(awt)) {
            throw new AssertionError("GetAWT failed");
        }
    }

    @Override
    public void create(Component component) {
        if (! component.isValid()) {
            throw new IllegalStateException("First, validate the component: validate()");
        }
        
        // Get the drawing surface
        ds = JAWT_GetDrawingSurface(component, awt.GetDrawingSurface());
        if (ds == null) {
            throw new NullPointerException("NULL drawing surface");
        }
        
        lock();
        try {
            // Get the drawing surface info
            JAWTDrawingSurfaceInfo dsi = JAWT_DrawingSurface_GetDrawingSurfaceInfo(ds, ds.GetDrawingSurfaceInfo());
            if (dsi == null) {
                throw new IllegalStateException("Error getting surface info");
            }

            try {
                // Get the platform-specific drawing info
                JAWTWin32DrawingSurfaceInfo dsi_win = JAWTWin32DrawingSurfaceInfo.create(dsi.platformInfo());
                hwnd = dsi_win.hwnd();
                hdc = dsi_win.hdc();
            } finally {
                // Free the drawing surface info
                JAWT_DrawingSurface_FreeDrawingSurfaceInfo(dsi, ds.FreeDrawingSurfaceInfo());
            }
        } finally {
            unlock();
        }
    }

    @Override
    public void lock() {
        // Lock the drawing surface
        int lock = JAWT_DrawingSurface_Lock(ds, ds.Lock());
        if ((lock & JAWT_LOCK_ERROR) != 0) {
            throw new IllegalStateException("Error locking surface");
        }
    }
    
    @Override
    public void unlock() {
        // Unlock the drawing surface
        JAWT_DrawingSurface_Unlock(ds, ds.Unlock());
    }

    public long getHWND() {
        return hwnd;
    }

    public long getHDC() {
        return hdc;
    }
    
    @Override
    public void dispose() {
        // Free the drawing surface
        JAWT_FreeDrawingSurface(ds, awt.FreeDrawingSurface());
        awt.free();
    }
}
