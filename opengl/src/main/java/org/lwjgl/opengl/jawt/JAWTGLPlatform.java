/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import java.awt.AWTException;
import java.awt.Component;

/**
 *
 * @author wil
 * @param <HANDLE>
 */
public interface JAWTGLPlatform<HANDLE extends Component> {
    
    void create(HANDLE handle) throws AWTException;
    
    void lock() throws AWTException;
    
    void unlock() throws AWTException;
    
    GLFBconfig getFBconfig();
    
    GLPlatformConfig getPlatformConfig();
 
    HANDLE getComponent();
    
    long getNativeDisplay();
    
    long getNativeWindow();
    
    void dispose();
}
