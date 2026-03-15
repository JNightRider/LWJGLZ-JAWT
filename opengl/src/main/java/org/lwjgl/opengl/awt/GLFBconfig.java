/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.awt;

/**
 *
 * @author wil
 */
public class GLFBconfig {
    public int      redBits         = 8;
    public int      greenBits       = 8;
    public int      blueBits        = 8;
    public int      alphaBits       = 8;
    public int      depthBits       = 24;
    public int      stencilBits     = 8;
    public int      accumRedBits;
    public int      accumGreenBits;
    public int      accumBlueBits;
    public int      accumAlphaBits;
    public int      auxBuffers;
    public boolean  stereo;
    public int      samples;
    public boolean  sRGB;
    public boolean  doublebuffer    = true;
    public boolean  transparent;
    public long     handle;
}
