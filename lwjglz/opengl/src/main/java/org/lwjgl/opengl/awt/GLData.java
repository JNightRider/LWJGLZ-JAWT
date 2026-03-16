/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.awt;

/**
 *
 * @author wil
 */
public final class GLData {

    /**
     * Framebuffer bit depth [hint] {@link #GLFW_RED_BITS}.
     */
    public static final int GLDATA_RED_BITS = 1;

    /**
     * Framebuffer bit depth [hint] {@link #GLDATA_GREEN_BITS}.
     */
    public static final int GLDATA_GREEN_BITS = 2;

    /**
     * Framebuffer bit depth [hint] {@link #GLDATA_BLUE_BITS}.
     */
    public static final int GLDATA_BLUE_BITS = 3;

    /**
     * Framebuffer bit depth [hint] {@link #GLDATA_ALPHA_BITS}.
     */
    public static final int GLDATA_ALPHA_BITS = 4;

    /**
     * Framebuffer bit depth [hint] {@link #GLDATA_DEPTH_BITS}.
     */
    public static final int GLDATA_DEPTH_BITS = 5;

    /**
     * Framebuffer bit depth [hint] {@link #GLDATA_STENCIL_BITS}.
     */
    public static final int GLDATA_STENCIL_BITS = 6;

    /**
     * Framebuffer bit depth [hint] {@link #GLDATA_ACCUM_RED_BITS}.
     */
    public static final int GLDATA_ACCUM_RED_BITS = 7;

    /**
     * Framebuffer bit depth [hint] {@link #GLDATA_ACCUM_GREEN_BITS}.
     */
    public static final int GLDATA_ACCUM_GREEN_BITS = 8;

    /**
     * Framebuffer bit depth [hint] {@link #GLDATA_ACCUM_BLUE_BITS}.
     */
    public static final int GLDATA_ACCUM_BLUE_BITS = 9;

    /**
     * Framebuffer bit depth [hint] {@link #GLDATA_ACCUM_ALPHA_BITS}.
     */
    public static final int GLDATA_ACCUM_ALPHA_BITS = 10;

    /**
     * Framebuffer auxiliary buffer [hint] {@link #GLDATA_AUX_BUFFERS}.
     */
    public static final int GLDATA_AUX_BUFFERS = 11;

    /**
     * OpenGL stereoscopic rendering [hint] {@link #GLDATA_STEREO}.
     */
    public static final int GLDATA_STEREO = 12;

    /**
     * Framebuffer MSAA samples [hint] {@link #GLDATA_SAMPLES}.
     */
    public static final int GLDATA_SAMPLES = 13;

    /**
     * Framebuffer sRGB [hint] {@link #GLDATA_SRGB_CAPABLE}.
     */
    public static final int GLDATA_SRGB_CAPABLE = 14;

    /**
     * Monitor refresh rate [hint] {@link #GLDATA_REFRESH_RATE}.
     */
    public static final int GLDATA_REFRESH_RATE = 15;

    /**
     * Framebuffer double buffering [hint] and [attribute]
     * {@link #GLDATA_DOUBLEBUFFER}.
     */
    public static final int GLDATA_DOUBLEBUFFER = 16;

    /**
     * Context client API [hint] and [attribute] {@link #GLDATA_CLIENT_API}.
     */
    public static final int GLDATA_CLIENT_API = 17;

    /**
     * Context client API major version [hint] and [attribute]
     * {@link #GLDATA_CONTEXT_VERSION_MAJOR}.
     */
    public static final int GLDATA_CONTEXT_VERSION_MAJOR = 18;

    /**
     * Context client API minor version [hint] and [attribute]
     * {@link #GLDATA_CONTEXT_VERSION_MINOR}.
     */
    public static final int GLDATA_CONTEXT_VERSION_MINOR = 19;

    /**
     * Context client API revision number [attribute]
     * {@link #GLDATA_CONTEXT_REVISION}.
     */
    public static final int GLDATA_CONTEXT_REVISION = 20;

    /**
     * Context robustness [hint] and [attribute]
     * {@link #GLDATA_CONTEXT_ROBUSTNESS}.
     */
    public static final int GLDATA_CONTEXT_ROBUSTNESS = 21;

    /**
     * OpenGL forward-compatibility [hint] and [attribute]
     * {@link #GLDATA_OPENGL_FORWARD_COMPAT}.
     */
    public static final int GLDATA_OPENGL_FORWARD_COMPAT = 22;

    /**
     * Debug mode context [hint] and [attribute] {@link #GLDATA_CONTEXT_DEBUG}.
     */
    public static final int GLDATA_CONTEXT_DEBUG = 23;

    /**
     * Legacy name for compatibility {@link #GLDATA_OPENGL_DEBUG_CONTEXT}.
     */
    public static final int GLDATA_OPENGL_DEBUG_CONTEXT = GLDATA_CONTEXT_DEBUG;

    /**
     * OpenGL profile [hint] and [attribute] {@link #GLDATA_OPENGL_PROFILE}.
     */
    public static final int GLDATA_OPENGL_PROFILE = 24;

    /**
     * Context flush-on-release [hint] and [attribute]
     * {@link #GLDATA_CONTEXT_RELEASE_BEHAVIOR}.
     */
    public static final int GLDATA_CONTEXT_RELEASE_BEHAVIOR = 25;

    /**
     * Context error suppression [hint] and [attribute]
     * {@link #GLDATA_CONTEXT_NO_ERROR}.
     */
    public static final int GLDATA_CONTEXT_NO_ERROR = 26;

    /**
     * Context creation API [hint] and [attribute]
     * {@link #GLDATA_CONTEXT_CREATION_API}.
     */
    public static final int GLDATA_CONTEXT_CREATION_API = 27;

    private final GLFBconfig fbconfig        = new GLFBconfig();
    private final GLPlatformConfig ctxconfig = new GLPlatformConfig();

    public GLData() { }    
    public GLData glHint(int name, int value) {
        switch (name) {
            case GLDATA_RED_BITS -> {
                fbconfig.redBits = value;
                return this;
            }
            case GLDATA_GREEN_BITS -> {
                fbconfig.greenBits = value;
                return this;
            }
            case GLDATA_BLUE_BITS -> {
                fbconfig.blueBits = value;
                return this;
            }
            case GLDATA_ALPHA_BITS -> {
                fbconfig.alphaBits = value;
                return this;
            }
            case GLDATA_DEPTH_BITS -> {
                fbconfig.depthBits = value;
                return this;
            }
            case GLDATA_STENCIL_BITS -> {
                fbconfig.stencilBits = value;
                return this;
            }
            case GLDATA_ACCUM_RED_BITS -> {
                fbconfig.accumRedBits = value;
                return this;
            }
            case GLDATA_ACCUM_GREEN_BITS -> {
                fbconfig.accumGreenBits = value;
                return this;
            }
            case GLDATA_ACCUM_BLUE_BITS -> {
                fbconfig.accumBlueBits = value;
                return this;
            }
            case GLDATA_ACCUM_ALPHA_BITS -> {
                fbconfig.accumAlphaBits = value;
                return this;
            }
            case GLDATA_AUX_BUFFERS -> {
                fbconfig.auxBuffers = value;
                return this;
            }
            case GLDATA_SAMPLES -> {
                fbconfig.samples = value;
                return this;
            }
            case GLDATA_CLIENT_API -> {
                ctxconfig.client = value;
                return this;
            }
            case GLDATA_CONTEXT_CREATION_API -> {
                ctxconfig.source = value;
                return this;
            }
            case GLDATA_CONTEXT_VERSION_MAJOR -> {
                ctxconfig.major = value;
                return this;
            }
            case GLDATA_CONTEXT_VERSION_MINOR -> {
                ctxconfig.minor = value;
                return this;
            }
            case GLDATA_CONTEXT_ROBUSTNESS -> {
                ctxconfig.robustness = value;
                return this;
            }
            case GLDATA_OPENGL_PROFILE -> {
                ctxconfig.profile = value;
                return this;
            }
            case GLDATA_CONTEXT_RELEASE_BEHAVIOR -> {
                ctxconfig.release = value;
                return this;
            }
            default -> throw new AssertionError("Unexpected value: " + name);
        }
    }

    public GLData glHint(int name, boolean value) {
        switch (name) {
            case GLDATA_STEREO -> {
                fbconfig.stereo = value;
                return this;
            }
            case GLDATA_SRGB_CAPABLE -> {
                fbconfig.sRGB = value;
                return this;
            }
            case GLDATA_DOUBLEBUFFER -> {
                fbconfig.doublebuffer = value;
                return this;
            }
            case GLDATA_OPENGL_FORWARD_COMPAT -> {
                ctxconfig.forward = value;
                return this;
            }
            case GLDATA_CONTEXT_DEBUG -> {
                ctxconfig.debug = value;
                return this;
            }
            case GLDATA_CONTEXT_NO_ERROR -> {
                ctxconfig.noerror = value;
                return this;
            }
            default -> throw new AssertionError("Unexpected boolean value for: " + name);
        }
    }

    GLFBconfig getFBConfig() {
        return fbconfig;
    }

    GLPlatformConfig getPlatformConfig() {
        return ctxconfig;
    }
}
