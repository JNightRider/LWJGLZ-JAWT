/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.awt;

import java.awt.Component;
import java.util.List;

import org.lwjgl.system.Platform;
import static org.lwjgl.awt.AWT.*;

/**
 *
 * @author wil
 */
public final class AWTGL {
    
    public static <T extends Component> GLPlatform<T> glGetAttachAWTWindow() {
        switch (Platform.get()) {
            case FREEBSD, LINUX -> {
                return new X11Platform<>();
            }
            case WINDOWS -> {
                return new Win32Platform<>();
            }
            case MACOSX -> {
                return new CocoaPlatform<>();
            }
            default ->
                throw new UnsupportedOperationException("Platform " + Platform.get() + " not yet supported");
        }
    }
    
    public static <T extends Component, E extends GLPlatform<T>> GLContext glNewAttachAWTContext(E platform) {
        switch (Platform.get()) {
            case FREEBSD, LINUX -> {
                if (isWayland()) {
                    return new EGLContext(platform);
                }
                return new GLX_Context((X11Platform) platform);
            }
            case WINDOWS -> {
                return new WGLContext((Win32Platform) platform);
            }
            case MACOSX -> {
                return new NSGLContext((CocoaPlatform) platform);
            }
            default ->
                throw new UnsupportedOperationException("Platform " + Platform.get() + " not yet supported");
        }
    }
    
    public static boolean glStringInExtensionString(String extension, String extensions) {
        String[] _extensions = extensions.split(" ");
        for (String _extension : _extensions) {
            if (_extension.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    public static GLFBconfig glGetChooseFBConfig(GLFBconfig desired, List<GLFBconfig> alternatives, int count) {
        int i;
        long missing,   leastMissing   = UINT_MAX;
        long colorDiff, leastColorDiff = UINT_MAX;
        long extraDiff, leastExtraDiff = UINT_MAX;
        
        GLFBconfig current;
        GLFBconfig closest = null;
    
        for (i = 0; i < count; i++) {
            current = alternatives.get(i);
            
            if (desired.stereo && !current.stereo)
            {
                // Stereo is a hard constraint
                continue;
            }
            
            // Count number of missing buffers
            {
                missing = 0;

                if (desired.alphaBits > 0 && current.alphaBits == 0)
                    missing++;

                if (desired.depthBits > 0 && current.depthBits == 0)
                    missing++;

                if (desired.stencilBits > 0 && current.stencilBits == 0)
                    missing++;

                if (desired.auxBuffers > 0 &&
                    current.auxBuffers < desired.auxBuffers)
                {
                    missing += desired.auxBuffers - current.auxBuffers;
                }

                if (desired.samples > 0 && current.samples == 0)
                {
                    // Technically, several multisampling buffers could be
                    // involved, but that's a lower level implementation detail and
                    // not important to us here, so we count them as one
                    missing++;
                }
            }
            
            // These polynomials make many small channel size differences matter
            // less than one large channel size difference

            // Calculate color channel size difference value
            {
                colorDiff = 0;

                if (desired.redBits != -1)
                {
                    colorDiff += (desired.redBits - current.redBits) *
                                 (desired.redBits - current.redBits);
                }

                if (desired.greenBits != -1)
                {
                    colorDiff += (desired.greenBits - current.greenBits) *
                                 (desired.greenBits - current.greenBits);
                }

                if (desired.blueBits != -1)
                {
                    colorDiff += (desired.blueBits - current.blueBits) *
                                 (desired.blueBits - current.blueBits);
                }
            }
            
            // Calculate non-color channel size difference value
            {
                extraDiff = 0;

                if (desired.alphaBits != -1)
                {
                    extraDiff += (desired.alphaBits - current.alphaBits) *
                                 (desired.alphaBits - current.alphaBits);
                }

                if (desired.depthBits != -1)
                {
                    extraDiff += (desired.depthBits - current.depthBits) *
                                 (desired.depthBits - current.depthBits);
                }

                if (desired.stencilBits != -1)
                {
                    extraDiff += (desired.stencilBits - current.stencilBits) *
                                 (desired.stencilBits - current.stencilBits);
                }

                if (desired.accumRedBits != -1)
                {
                    extraDiff += (desired.accumRedBits - current.accumRedBits) *
                                 (desired.accumRedBits - current.accumRedBits);
                }

                if (desired.accumGreenBits != -1)
                {
                    extraDiff += (desired.accumGreenBits - current.accumGreenBits) *
                                 (desired.accumGreenBits - current.accumGreenBits);
                }

                if (desired.accumBlueBits != -1)
                {
                    extraDiff += (desired.accumBlueBits - current.accumBlueBits) *
                                 (desired.accumBlueBits - current.accumBlueBits);
                }

                if (desired.accumAlphaBits != -1)
                {
                    extraDiff += (desired.accumAlphaBits - current.accumAlphaBits) *
                                 (desired.accumAlphaBits - current.accumAlphaBits);
                }

                if (desired.samples != -1)
                {
                    extraDiff += (desired.samples - current.samples) *
                                 (desired.samples - current.samples);
                }

                if (desired.sRGB && !current.sRGB)
                    extraDiff++;
            }
            
            // Figure out if the current one is better than the best one found so far
            // Least number of missing buffers is the most important heuristic,
            // then color buffer size match and lastly size match for other buffers

            if (missing < leastMissing)
                closest = current;
            else if (missing == leastMissing)
            {
                if ((colorDiff < leastColorDiff) ||
                    (colorDiff == leastColorDiff && extraDiff < leastExtraDiff))
                {
                    closest = current;
                }
            }

            if (current == closest)
            {
                leastMissing = missing;
                leastColorDiff = colorDiff;
                leastExtraDiff = extraDiff;
            }
        }
        
        return closest;
    }
}
