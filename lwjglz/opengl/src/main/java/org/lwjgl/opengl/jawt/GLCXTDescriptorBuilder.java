/*
 * Copyright lwjglz-jawt. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import org.lwjgl.opengl.jawt.AWTGL.*;

/**
 * Interface responsible for managing the construction of GL context properties.
 * 
 * @see GLCXTDescriptor
 * 
 * @author wil
 * @version 1.0.0
 * @since 1.0.0
 */
public interface GLCXTDescriptorBuilder {
    /**
     * Set the configuration value to: {@code client}
     * 
     * @param value GLClientAPI
     * @return GLCXTDescriptorBuilder
     */
    GLCXTDescriptorBuilder client(GLClientAPI value);
    
    /**
     * Set the configuration value to: {@code source}
     * 
     * @param value GLClientType
     * @return GLCXTDescriptorBuilder
     */
    GLCXTDescriptorBuilder source(GLClientType value);
    
    /**
     * Set the configuration value to: {@code profile}
     * 
     * @param value GLProfile
     * @return GLCXTDescriptorBuilder
     */
    GLCXTDescriptorBuilder profile(GLProfile value);
    
    /**
     * Set the configuration value to: {@code robustness}
     * 
     * @param value GLRobustness
     * @return GLCXTDescriptorBuilder
     */
    GLCXTDescriptorBuilder robustness(GLRobustness value);
    
    /**
     * Set the configuration value to: {@code release}
     * 
     * @param value GLReleaseBehavior
     * @return GLCXTDescriptorBuilder
     */
    GLCXTDescriptorBuilder release(GLReleaseBehavior value);
    
    /**
     * Set the configuration value to: {@code major}
     * 
     * @param value int
     * @return GLCXTDescriptorBuilder
     */
    GLCXTDescriptorBuilder major(int value);
    
    /**
     * Set the configuration value to: {@code minor}
     * 
     * @param value int
     * @return GLCXTDescriptorBuilder
     */
    GLCXTDescriptorBuilder minor(int value);
    
    /**
     * Set the configuration value to: {@code forward}
     * 
     * @param value boolean
     * @return GLCXTDescriptorBuilder
     */
    GLCXTDescriptorBuilder forward(boolean value);
    
    /**
     * Set the configuration value to: {@code debug}
     * 
     * @param value boolean
     * @return GLCXTDescriptorBuilder
     */
    GLCXTDescriptorBuilder debug(boolean value);
    
    /**
     * Set the configuration value to: {@code noerror}
     * 
     * @param value boolean
     * @return GLCXTDescriptorBuilder
     */
    GLCXTDescriptorBuilder noerror(boolean value);
    
    /**
     * Set the configuration value to: {@code share}
     * 
     * @param value long
     * @return GLCXTDescriptorBuilder
     */
    GLCXTDescriptorBuilder share(long value);
    
    /**
     * Build the {@link GLCXTDescriptor} that the GL context needs to create.
     * 
     * @return GLCXTDescriptor
     */
    GLCXTDescriptor build();
}
