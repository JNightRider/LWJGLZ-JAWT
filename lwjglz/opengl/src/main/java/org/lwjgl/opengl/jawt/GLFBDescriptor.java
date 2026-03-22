/*
 * Copyright lwjglz-jawt. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

/**
 * Class responsible for managing all FrameBuffer configurations that OpenGL will
 * use to create the context.
 * 
 * <pre><code>
 * GLFBDescriptorBuilder builder = GLFBDescriptor.builder();
 * builder.accumAlphaBits(8)
 *        .accumBlueBits(8)
 *        .accumGreenBits(8);
 *
 * GLFBDescriptor fbconfig = builder.build();
 * </code></pre>
 * 
 * @author wil
 * @version 1.0.0
 * @since 1.0.0
 */
public class GLFBDescriptor {
    
    /** {@code GLFBDescriptor} builder to generate GL framebuffer configurations. */
    private static final class GLFBBuilder implements GLFBDescriptorBuilder {
        /** The number of bits for the color channels. */
        private int
                redBits         = 8,
                greenBits       = 8,
                blueBits        = 8,
                alphaBits       = 8,
                depthBits       = 24,
                stencilBits     = 8;
        
        /** Number of bits for the color channels of the accumulators. */
        private int
                accumRedBits,
                accumGreenBits,
                accumBlueBits,
                accumAlphaBits;
        
        /** Framebuffer auxiliary buffer. */
        private int
                auxBuffers;
        
        /** OpenGL stereoscopic rendering. */
        private boolean
                stereo;
        
        /** Framebuffer MSAA samples. */
        private int
                samples;
        
        /** Whether to use sRGB color space.*/
        private boolean
                sRGB;
        
        /** Framebuffer double buffering- */
        private boolean
                doublebuffer = true;
        
        /** Window framebuffer transparency. */
        private boolean  transparent;

        /** private builder */
        private GLFBBuilder() { }        
        
        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#redBits(int)
         */
        @Override
        public GLFBDescriptorBuilder redBits(int value) {
            redBits = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#greenBits(int)
         */
        @Override
        public GLFBDescriptorBuilder greenBits(int value) {
            greenBits = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#blueBits(int)
         */
        @Override
        public GLFBDescriptorBuilder blueBits(int value) {
            blueBits = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#alphaBits(int)
         */
        @Override
        public GLFBDescriptorBuilder alphaBits(int value) {
            alphaBits = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#depthBits(int)
         */
        @Override
        public GLFBDescriptorBuilder depthBits(int value) {
            depthBits = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#stencilBits(int)
         */
        @Override
        public GLFBDescriptorBuilder stencilBits(int value) {
            stencilBits = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#accumRedBits(int)
         */
        @Override
        public GLFBDescriptorBuilder accumRedBits(int value) {
            accumRedBits = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#accumGreenBits(int)
         */
        @Override
        public GLFBDescriptorBuilder accumGreenBits(int value) {
            accumGreenBits = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#accumBlueBits(int)
         */
        @Override
        public GLFBDescriptorBuilder accumBlueBits(int value) {
            accumBlueBits = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#accumAlphaBits(int)
         */
        @Override
        public GLFBDescriptorBuilder accumAlphaBits(int value) {
            accumAlphaBits = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#auxBuffers(int)
         */
        @Override
        public GLFBDescriptorBuilder auxBuffers(int value) {
            auxBuffers = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#stereo(boolean)
         */
        @Override
        public GLFBDescriptorBuilder stereo(boolean value) {
            stereo = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#samples(int)
         */
        @Override
        public GLFBDescriptorBuilder samples(int value) {
            samples = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#sRGB(boolean)
         */
        @Override
        public GLFBDescriptorBuilder sRGB(boolean value) {
            sRGB = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#doublebuffer(boolean)
         */
        @Override
        public GLFBDescriptorBuilder doublebuffer(boolean value) {
            doublebuffer = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#transparent(boolean)
         */
        @Override
        public GLFBDescriptorBuilder transparent(boolean value) {
            transparent = value;
            return this;
        }
        
        /*(non-Javadoc)
         * @see GLFBDescriptorBuilder#build
         */
        @Override
        public GLFBDescriptor build() {
            return new GLFBDescriptor(this);
        }

        /** @return Returns the value of: {@code redBits}  */
        public int getRedBits() { return redBits; }
        /** @return Returns the value of: {@code greenBits}  */
        public int getGreenBits() { return greenBits; }
        /** @return Returns the value of: {@code blueBits}  */
        public int getBlueBits() { return blueBits; }
        /** @return Returns the value of: {@code alphaBits}  */
        public int getAlphaBits() { return alphaBits; }
        /** @return Returns the value of: {@code depthBits}  */
        public int getDepthBits() { return depthBits; }
        /** @return Returns the value of: {@code stencilBits}  */
        public int getStencilBits() { return stencilBits; }
        /** @return Returns the value of: {@code accumRedBits}  */
        public int getAccumRedBits() { return accumRedBits; }
        /** @return Returns the value of: {@code accumGreenBits}  */
        public int getAccumGreenBits() { return accumGreenBits; }
        /** @return Returns the value of: {@code accumBlueBits}  */
        public int getAccumBlueBits() { return accumBlueBits; }
        /** @return Returns the value of: {@code accumAlphaBits}  */
        public int getAccumAlphaBits() { return accumAlphaBits; }
        /** @return Returns the value of: {@code auxBuffers}  */
        public int getAuxBuffers() { return auxBuffers; }
        /** @return Returns the value of: {@code samples}  */
        public int getSamples() { return samples; }
        
        /** @return Returns the value of: {@code stereo}  */
        public boolean isStereo() { return stereo; }        
        /** @return Returns the value of: {@code sRGB}  */
        public boolean isSRGB() { return sRGB; }
        /** @return Returns the value of: {@code doublebuffer}  */
        public boolean isDoublebuffer() { return doublebuffer; }
        /** @return Returns the value of: {@code transparent}  */
        public boolean isTransparent() { return transparent; }
    }
    
    /**
     * Method responsible for creating a new {@code builder} to manage the
     * framebuffer properties.
     * 
     * @return GLFBDescriptorBuilder
     */
    public static GLFBDescriptorBuilder builder() {
        return new GLFBBuilder();
    }
    
    /** The number of bits for the color channels. */
    private final int 
            redBits,
            greenBits,
            blueBits,
            alphaBits,
            depthBits,
            stencilBits;
    
    /** Number of bits for the color channels of the accumulators. */
    private final int
            accumRedBits,
            accumGreenBits,
            accumBlueBits,
            accumAlphaBits;
    
    /** Framebuffer auxiliary buffer. */
    private final int auxBuffers;
    
    /** Framebuffer MSAA samples. */
    private final int samples;
    
    /** Whether to use sRGB color space.*/
    private final boolean
            sRGB;
    
    /** OpenGL stereoscopic rendering. */
    private final boolean
            stereo;
    
    /** Framebuffer double buffering- */
    private final boolean
            doublebuffer;
    
    /** Window framebuffer transparency. */
    private final boolean
            transparent;
    
    /** A GLFBConfig pointer. */
    private long handle;

    /**
     * Class ({@code GLFBDescriptor}) constructor where the final configurations
     * of the framebuffer of the GL context to be created are set.
     * 
     * @param builder GLFBBuilder
     */
    private GLFBDescriptor(GLFBBuilder builder) {
        this.redBits        = builder.getRedBits();
        this.greenBits      = builder.getGreenBits();
        this.blueBits       = builder.getBlueBits();
        this.alphaBits      = builder.getAlphaBits();
        this.depthBits      = builder.getDepthBits();
        this.stencilBits    = builder.getStencilBits();
        this.accumRedBits   = builder.getAccumRedBits();
        this.accumGreenBits = builder.getAccumGreenBits();
        this.accumBlueBits  = builder.getAccumBlueBits();
        this.accumAlphaBits = builder.getAccumAlphaBits();
        this.auxBuffers     = builder.getAuxBuffers();
        this.samples        = builder.getSamples();
        this.sRGB           = builder.isSRGB();
        this.stereo         = builder.isStereo();
        this.doublebuffer   = builder.isDoublebuffer();
        this.transparent    = builder.isTransparent();
    }

    /** @return Returns the value of: {@code redBits}  */
    public int redBits() { return redBits; }
    /** @return Returns the value of: {@code greenBits}  */
    public int greenBits() { return greenBits; }
    /** @return Returns the value of: {@code samples}  */
    public int blueBits() { return blueBits; }
    /** @return Returns the value of: {@code alphaBits}  */
    public int alphaBits() { return alphaBits; }
    /** @return Returns the value of: {@code depthBits}  */
    public int depthBits() { return depthBits; }
    /** @return Returns the value of: {@code stencilBits}  */
    public int stencilBits() { return stencilBits; }
    /** @return Returns the value of: {@code accumRedBits}  */
    public int accumRedBits() { return accumRedBits; }
    /** @return Returns the value of: {@code accumGreenBits}  */
    public int accumGreenBits() { return accumGreenBits; }
    /** @return Returns the value of: {@code accumBlueBits}  */
    public int accumBlueBits() { return accumBlueBits; }
    /** @return Returns the value of: {@code accumAlphaBits}  */
    public int accumAlphaBits() { return accumAlphaBits; }
    /** @return Returns the value of: {@code auxBuffers}  */
    public int auxBuffers() { return auxBuffers; }
    /** @return Returns the value of: {@code samples}  */
    public int samples() { return samples; }    
    /** @return Returns the value of: {@code handle}  */
    public long handle() { return handle; }

    /** @return Returns the value of: {@code stereo}  */
    public boolean stereo() { return stereo; }
    /** @return Returns the value of: {@code sRGB}  */
    public boolean sRGB() { return sRGB; }
    /** @return Returns the value of: {@code doublebuffer}  */
    public boolean doublebuffer() { return doublebuffer; }
    /** @return Returns the value of: {@code transparent}  */
    public boolean transparent() { return transparent; }

    /** @param handle A GLFBConfig pointer. */
    final void handle(long handle) {
        this.handle = handle;
    }
}
