/*
 * Copyright lwjglz-jawt. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import org.lwjgl.opengl.jawt.AWTGL.*;
import static org.lwjgl.opengl.jawt.AWTGL.GLClientAPI.*;
import static org.lwjgl.opengl.jawt.AWTGL.GLClientType.*;

/**
 * Class responsible for managing all GL context configurations.
 * 
 * <pre><code>
 * GLCXTDescriptorBuilder builder = GLCXTDescriptor.builder();
 * builder.client(OPENGL)
 *        .profile(CORE)
 *        .major(4)
 *        .minor(5);
 *
 * GLCXTDescriptor cxtconfig = builder.build();
 * </code></pre>
 * 
 * @author wil
 * @version 1.0.0
 * @since 1.0.0
 */
public class GLCXTDescriptor {
    
    /** {@code GLCXTDescriptor} builder to generate GL context configurations. */
    private static final class GLCXTBuilder implements GLCXTDescriptorBuilder {
        /** The client API to use. */
        private GLClientAPI client  = OPENGL;        
        /** The type of client, whether native or integrated. */
        private GLClientType source = NATIVE;
        
        /** The client version that should be used. */
        private int
                major       = 1,
                minor       = 0;
        
        /** OpenGL forward-compatibility */
        private boolean forward;
        /** Debug mode context*/
        private boolean debug;
        /** Context error suppression. */
        private boolean noerror;
        
        /** The profile to use. */
        private GLProfile  profile;        
        /** Context client API revision number */
        private GLRobustness  robustness;
        /** Context flush-on-release. */
        private GLReleaseBehavior release;
        
        /**
         * The {@link GLContext} whose context objects should be shared with
         * the created context.
         */
        private long share;
        /** private builder */
        private GLCXTBuilder() { }
    
        /*(non-Javadoc)
         * @see GLCXTDescriptorBuilder#client(GLClientAPI)
         */
        @Override
        public GLCXTDescriptorBuilder client(GLClientAPI value) {
            client = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLCXTDescriptorBuilder#source(GLClientType)
         */
        @Override
        public GLCXTDescriptorBuilder source(GLClientType value) {
            source = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLCXTDescriptorBuilder#profile(GLProfile)
         */
        @Override
        public GLCXTDescriptorBuilder profile(GLProfile value) {
            profile = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLCXTDescriptorBuilder#robustness(GLRobustness)
         */
        @Override
        public GLCXTDescriptorBuilder robustness(GLRobustness value) {
            robustness = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLCXTDescriptorBuilder#release(GLReleaseBehavior)
         */
        @Override
        public GLCXTDescriptorBuilder release(GLReleaseBehavior value) {
            release = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLCXTDescriptorBuilder#GLReleaseBehavior(int)
         */
        @Override
        public GLCXTDescriptorBuilder major(int value) {
            major = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLCXTDescriptorBuilder#minor(int)
         */
        @Override
        public GLCXTDescriptorBuilder minor(int value) {
            minor = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLCXTDescriptorBuilder#forward(boolean)
         */
        @Override
        public GLCXTDescriptorBuilder forward(boolean value) {
            forward = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLCXTDescriptorBuilder#debug(boolean)
         */
        @Override
        public GLCXTDescriptorBuilder debug(boolean value) {
            debug = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLCXTDescriptorBuilder#noerror(boolean)
         */
        @Override
        public GLCXTDescriptorBuilder noerror(boolean value) {
            noerror = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLCXTDescriptorBuilder#share(long)
         */
        @Override
        public GLCXTDescriptorBuilder share(long value) {
            share = value;
            return this;
        }

        /*(non-Javadoc)
         * @see GLCXTDescriptorBuilder#build
         */
        @Override
        public GLCXTDescriptor build() {
            return new GLCXTDescriptor(this);
        }
        
        /** @return Returns the value of: {@code client}  */
        public GLClientAPI getClient() { return client; }
        /** @return Returns the value of: {@code source}  */        
        public GLClientType getSource() { return source; }
        /** @return Returns the value of: {@code major}  */
        public int getMajor() { return major; }
        /** @return Returns the value of: {@code minor}  */
        public int getMinor() { return minor; }
        /** @return Returns the value of: {@code forward}  */
        public boolean isForward() { return forward; }
        /** @return Returns the value of: {@code debug}  */
        public boolean isDebug() { return debug; }
        /** @return Returns the value of: {@code noerror}  */
        public boolean isNoerror() { return noerror; }
        /** @return Returns the value of: {@code profile}  */
        public GLProfile getProfile() { return profile; }
        /** @return Returns the value of: {@code robustness}  */
        public GLRobustness getRobustness() { return robustness; }
        /** @return Returns the value of: {@code release}  */
        public GLReleaseBehavior getRelease() { return release; }
        /** @return Returns the value of: {@code share}  */
        public long getShare() { return share; }
    }
    
    /**
     * Method responsible for creating a new {@code builder} to manage the
     * context properties.
     * 
     * @return GLCXTDescriptorBuilder
     */
    public static final GLCXTDescriptorBuilder builder() {
        return new GLCXTBuilder();
    }
    
    /** The client API to use. */
    private final GLClientAPI client;
    /** The type of client, whether native or integrated. */
    private final GLClientType source;

    /** The client version that should be used. */
    private final int
            major,
            minor;
    
    /** OpenGL forward-compatibility */
    private final boolean forward;
    /** Debug mode context*/
    private final boolean debug;
    /** Context error suppression. */
    private final boolean noerror;

    /** The profile to use. */
    private final GLProfile profile;
    /** Context client API revision number */
    private final GLRobustness robustness;
    /** Context flush-on-release. */
    private final GLReleaseBehavior release;
    
    /**
     * The {@link GLContext} whose context objects should be shared with the
     * created context.
     */
    private final long share;

    /**
     * Class constructor ({@code GLCXTDescriptor}) where the final configurations
     * for the context to be created are set.
     * 
     * @param builder GLCXTBuilder
     */
    private GLCXTDescriptor(GLCXTBuilder builder) {
        this.client     = builder.getClient();
        this.source     = builder.getSource();
        this.major      = builder.getMajor();
        this.minor      = builder.getMinor();
        this.forward    = builder.isForward();
        this.debug      = builder.isDebug();
        this.noerror    = builder.isNoerror();
        this.profile    = builder.getProfile();
        this.robustness = builder.getRobustness();
        this.release    = builder.getRelease();
        this.share      = builder.getShare();
    }
    
    /** @return Returns the value of: {@code client}  */
    public GLClientAPI client() { return client; }
    /** @return Returns the value of: {@code source}  */
    public GLClientType source() { return source; }
    /** @return Returns the value of: {@code major}  */
    public int major() { return major; }
    /** @return Returns the value of: {@code minor}  */
    public int minor() { return minor; }
    /** @return Returns the value of: {@code forward}  */
    public boolean forward() { return forward; }
    /** @return Returns the value of: {@code debug}  */
    public boolean debug() { return debug; }
    /** @return Returns the value of: {@code noerror}  */
    public boolean noerror() { return noerror; }
    /** @return Returns the value of: {@code profile}  */
    public GLProfile profile() { return profile; }
    /** @return Returns the value of: {@code robustness}  */
    public GLRobustness robustness() { return robustness; }
    /** @return Returns the value of: {@code release}  */
    public GLReleaseBehavior release() { return release; }
    /** @return Returns the value of: {@code share}  */
    public long share() { return share; }
}
