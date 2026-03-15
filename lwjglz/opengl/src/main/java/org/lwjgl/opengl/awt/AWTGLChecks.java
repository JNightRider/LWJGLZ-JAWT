/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.awt;

/**
 *
 * @author wil
 */
public class AWTGLChecks {
    
    public static void checkX11(GLPlatform platform) {
        if (platform == null)
            throw new NullPointerException();
            
        if (! (platform instanceof X11Platform)) {
            throwPlatform("X11");
        }
    }
    
    public static void checkWL(GLPlatform platform) {
        if (platform == null)
            throw new NullPointerException();
        
        if ((platform instanceof Win32Platform) || (platform instanceof CocoaPlatform)) {
            throwPlatform("Wayland | XWayland");
        }
    }
        
    public static void checkWin32(GLPlatform platform) {
        if (platform == null)
            throw new NullPointerException();
        
        if (! (platform instanceof X11Platform)) {
            throwPlatform("Windows");
        }
    }
    
    // Separate calls to help inline check.
    private static void throwPlatform(String name) {
        throw new IllegalArgumentException("The platform is not " + name);
    }
}
