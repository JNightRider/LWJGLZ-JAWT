/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.awt;

/**
 *
 * @author wil
 */
public class Int {
    
    public static final long UINT_MAX = 4294967295L;
    
    public static boolean toBoolean(Integer value) {
        if (value == null) {
            return false;
        }
        return value > 0;
    }
}
