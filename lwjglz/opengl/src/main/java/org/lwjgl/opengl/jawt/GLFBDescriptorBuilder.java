/*
 * Copyright lwjglz-jawt. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

/**
 * Interface responsible for managing the construction of a configuration frame
 * buffer used to create the GL context (GLX, WGL, NSGL or EGL).
 * 
 * @see GLFBDescriptor
 * 
 * @author wil
 * @version 1.0.0
 * @since 1.0.0
 */
public interface GLFBDescriptorBuilder {
    
    /**
     * Set the configuration value to: {@code redBits}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder redBits(int value);
    
    /**
     * Set the configuration value to: {@code greenBits}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder greenBits(int value);
    
    /**
     * Set the configuration value to: {@code blueBits}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder blueBits(int value);
    
    /**
     * Set the configuration value to: {@code alphaBits}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder alphaBits(int value);
    
    /**
     * Set the configuration value to: {@code depthBits}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder depthBits(int value);
    
    /**
     * Set the configuration value to: {@code stencilBits}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder stencilBits(int value);
    
    /**
     * Set the configuration value to: {@code accumRedBits}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder accumRedBits(int value);
    
    /**
     * Set the configuration value to: {@code accumGreenBits}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder accumGreenBits(int value);
    
    /**
     * Set the configuration value to: {@code accumBlueBits}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder accumBlueBits(int value);
    
    /**
     * Set the configuration value to: {@code accumAlphaBits}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder accumAlphaBits(int value);
    
    /**
     * Set the configuration value to: {@code auxBuffers}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder auxBuffers(int value);
    
    /**
     * Set the configuration value to: {@code stereo}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder stereo(boolean value);
    
    /**
     * Set the configuration value to: {@code samples}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder samples(int value);
    
    /**
     * Set the configuration value to: {@code sRGB}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder sRGB(boolean value);
    
    /**
     * Set the configuration value to: {@code doublebuffer}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder doublebuffer(boolean value);
    
    /**
     * Set the configuration value to: {@code transparent}
     * 
     * @param value int
     * @return GLFBDescriptorBuilder
     */
    GLFBDescriptorBuilder transparent(boolean value);
    
    /**
     * Build the {@link GLFBDescriptor} that the GL context needs to create.
     * 
     * @return GLFBDescriptor
     */
    GLFBDescriptor build();
}
