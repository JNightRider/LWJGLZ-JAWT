/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

public final class GLData {
    
    private final GLCXTDescriptor ctxconfig;
    private final GLFBDescriptor fbconfig;
    
    public GLData(GLCXTDescriptor ctxconfig, GLFBDescriptor fbconfig) {
        this.ctxconfig = ctxconfig;
        this.fbconfig = fbconfig;
    }

    public GLCXTDescriptor getCXTConfig() {
        return ctxconfig;
    }

    public GLFBDescriptor getFBConfig() {
        return fbconfig;
    }
}

