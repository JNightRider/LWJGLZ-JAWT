/*
 * Copyright lwjglz-jawt. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import java.awt.Component;

/**
 *
 * @author wil
 */
public interface Window {
    
    void create(Component component);
    
    void lock();
    
    void unlock();
    
    void dispose();
}
