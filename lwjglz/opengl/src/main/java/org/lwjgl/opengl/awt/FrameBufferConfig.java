/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.awt;

/**
 *
 * @author wil
 */
class FrameBufferConfig {
    int      redBits         = 8;
    int      greenBits       = 8;
    int      blueBits        = 8;
    int      alphaBits       = 8;
    int      depthBits       = 24;
    int      stencilBits     = 8;
    int      accumRedBits;
    int      accumGreenBits;
    int      accumBlueBits;
    int      accumAlphaBits;
    int      auxBuffers;
    boolean  stereo;
    int      samples;
    boolean  sRGB;
    boolean  doublebuffer    = true;
    boolean  transparent;
    long     handle;
}
