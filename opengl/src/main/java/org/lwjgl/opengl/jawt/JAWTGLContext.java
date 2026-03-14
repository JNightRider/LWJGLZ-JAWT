/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import java.awt.AWTException;

/**
 * 
 * @author wil
 * @version 1.0.0
 * @since 1.0.0
 */
public interface JAWTGLContext {
    
    void createContext() throws AWTException;
    
    void makeContextCurrent(boolean handle);
    
    long getCurrentContext();
    
    void swapBuffers();
    
    void swapInterval(int interval);
    
    boolean extensionSupported(String extension);
    
    long getProcAddress(String procname);
    
    long getHandle();
    
    void destroy();
}
