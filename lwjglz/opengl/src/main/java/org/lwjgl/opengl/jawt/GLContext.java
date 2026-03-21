/*
 * Copyright lwjglz-jawt. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import java.awt.AWTException;

/**
 *
 * @author wil
 */
public interface GLContext {
    
    void initGL() throws AWTException;
    
    void create() throws AWTException;
    
    void makeCurrent();
    
    void releaseContext();

    void swapBuffers();
    
    void swapInterval(int interval);
    
    boolean extensionSupported(String extension);
    
    long getProcAddress(String procname);
    
    long getCurrentContext();
    
    long getContext();
    
    boolean isCurrent();
    
    void delete();
}
