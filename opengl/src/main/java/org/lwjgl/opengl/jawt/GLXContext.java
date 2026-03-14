/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import java.awt.AWTException;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.opengl.GLX.*;
import static org.lwjgl.opengl.GLX14.*;
import static org.lwjgl.opengl.GLXARBContextFlushControl.*;
import static org.lwjgl.opengl.GLXARBCreateContext.*;
import static org.lwjgl.opengl.GLXARBCreateContextNoError.*;
import static org.lwjgl.opengl.GLXARBCreateContextProfile.*;
import static org.lwjgl.opengl.GLXARBCreateContextRobustness.*;
import static org.lwjgl.opengl.GLXARBFramebufferSRGB.*;
import static org.lwjgl.opengl.GLXARBGetProcAddress.*;
import static org.lwjgl.opengl.GLXEXTCreateContextES2Profile.*;
import static org.lwjgl.opengl.GLXEXTSwapControl.*;
import static org.lwjgl.opengl.GLXSGISwapControl.*;
import static org.lwjgl.opengl.jawt.GLPlatformConfig.*;
import static org.lwjgl.opengl.jawt.GLUtils.*;
import static org.lwjgl.awt.Int.*;

import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.JNI.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.linux.X11.*;

/**
 *
 * @author wil
 */
public class GLXContext implements JAWTGLContext {
    
    /** Contains the function pointers loaded from {@code GL.getFunctionProvider()}. */
    public static final class Functions {
        
        private Functions() {}
        
        /** Function address. */
        public static final long
                GetProcAddress          = apiGetFunctionAddress(GL.getFunctionProvider(), "glXGetProcAddress"),
                GetProcAddressARB       = apiGetFunctionAddress(GL.getFunctionProvider(), "glXGetProcAddressARB"),
                SwapIntervalMESA        = apiGetFunctionAddress(GL.getFunctionProvider(), "glXSwapIntervalMESA"),
                SwapIntervalEXT         = apiGetFunctionAddress(GL.getFunctionProvider(), "glXSwapIntervalEXT"),
                SwapIntervalSGI         = apiGetFunctionAddress(GL.getFunctionProvider(), "glXSwapIntervalSGI"),
                CreateContextAttribsARB = apiGetFunctionAddress(GL.getFunctionProvider(), "glXCreateContextAttribsARB");
    }
    
    public static class Extensions {
        
        private Extensions() {}
        
        public Boolean 
                EXT_swap_control                = Boolean.FALSE,
                MESA_swap_control               = Boolean.FALSE,
                SGI_swap_control                = Boolean.FALSE,
                ARB_multisample                 = Boolean.FALSE,
                ARB_framebuffer_sRGB            = Boolean.FALSE,
                EXT_framebuffer_sRGB            = Boolean.FALSE,                
                ARB_create_context              = Boolean.FALSE,
                ARB_create_context_robustness   = Boolean.FALSE,
                ARB_create_context_profile      = Boolean.FALSE,
                EXT_create_context_es2_profile  = Boolean.FALSE,
                ARB_create_context_no_error     = Boolean.FALSE,
                ARB_context_flush_control       = Boolean.FALSE;
    }
    
    private final X11Platform platform;
    private final Extensions glx;
    private long context;
    
    public GLXContext(X11Platform platform) {
        this.platform   = platform;
        this.glx        = new Extensions();
    }
    
    private void initGLX() throws AWTException {
        try (MemoryStack stack = stackPush()) {
            IntBuffer errorBase = stack.mallocInt(1);
            IntBuffer eventBase = stack.mallocInt(1);
            
            if (!glXQueryExtension(platform.getDisplay(),
                           errorBase,
                           eventBase))
            {
                throw new AWTException("GLX: GLX extension not found");
            }
            
            IntBuffer major = stack.mallocInt(1);
            IntBuffer minor = stack.mallocInt(1);
            if (!glXQueryVersion(platform.getDisplay(), major, minor))
            {
                throw new AWTException( "GLX: Failed to query GLX version");
            }
            
            if (major.get(0) == 1 && minor.get(0) < 3)
            {
                throw new AWTException("GLX: GLX version 1.3 is required");
            }
            
            if (extensionSupported("GLX_EXT_swap_control"))
            {
                if (Functions.SwapIntervalEXT != NULL)
                    glx.EXT_swap_control = true;
            }
            if (extensionSupported("GLX_SGI_swap_control"))
            {
                if (Functions.SwapIntervalSGI != NULL)
                    glx.SGI_swap_control = true;
            }
            if (extensionSupported("GLX_MESA_swap_control"))
            {

                if (Functions.SwapIntervalMESA != NULL)
                    glx.MESA_swap_control = true;
            }
            
            if (extensionSupported("GLX_ARB_multisample"))
                glx.ARB_multisample = true;

            if (extensionSupported("GLX_ARB_framebuffer_sRGB"))
                glx.ARB_framebuffer_sRGB = true;

            if (extensionSupported("GLX_EXT_framebuffer_sRGB"))
                glx.EXT_framebuffer_sRGB = true;
            
            if (extensionSupported("GLX_ARB_create_context"))
            {
                if (Functions.CreateContextAttribsARB != NULL)
                    glx.ARB_create_context = true;
            }
            
            if (extensionSupported("GLX_ARB_create_context_robustness"))
                glx.ARB_create_context_robustness = true;

            if (extensionSupported("GLX_ARB_create_context_profile"))
                glx.ARB_create_context_profile = true;

            if (extensionSupported("GLX_EXT_create_context_es2_profile"))
                glx.EXT_create_context_es2_profile = true;

            if (extensionSupported("GLX_ARB_create_context_no_error"))
                glx.ARB_create_context_no_error = true;

            if (extensionSupported("GLX_ARB_context_flush_control"))
                glx.ARB_context_flush_control = true;
            
        }
    }
    
    private int getGLXFBConfigAttrib(long fbconfig, int attrib) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer value = stack.mallocInt(1);
            glXGetFBConfigAttrib(platform.getDisplay(), fbconfig, attrib, value);
            return value.get(0);
        }
    }
    
    private GLFBconfig chooseGLXFBConfig(GLFBconfig desired) throws AWTException {
        // HACK: This is a (hopefully temporary) workaround for Chromium
        //       (VirtualBox GL) not setting the window bit on any GLXFBConfigs
        boolean trustWindowBit = true;
        String vendor = glXGetClientString(platform.getDisplay(), GLX_VENDOR);
        if (vendor != null && vendor.equals("Chromium"))
            trustWindowBit = false;
        
        PointerBuffer nativeConfigs = glXGetFBConfigs(platform.getDisplay(),0);
        if (nativeConfigs == null) {
            throw new AWTException("GLX: No GLXFBConfigs returned");
        }
        
        List<GLFBconfig> usableConfigs = new ArrayList<>();
        int usableCount = 0;
        
        for (int i = 0; i < nativeConfigs.remaining(); i++) {
            long /*GLXFBConfig*/ config = nativeConfigs.get(i);
            GLFBconfig data = new GLFBconfig();
            
            // Only consider RGBA GLXFBConfigs
            if (!((getGLXFBConfigAttrib(config, GLX_RENDER_TYPE) & GLX_RGBA_BIT) > 0))
                continue;

            // Only consider window GLXFBConfigs
            if (!((getGLXFBConfigAttrib(config, GLX_DRAWABLE_TYPE) & GLX_WINDOW_BIT) > 0))
            {
                if (trustWindowBit)
                    continue;
            }

            if ((getGLXFBConfigAttrib(config, GLX_DOUBLEBUFFER) > 0) != desired.doublebuffer)
                continue;
            
            data.redBits    = getGLXFBConfigAttrib(config, GLX_RED_SIZE);
            data.greenBits  = getGLXFBConfigAttrib(config, GLX_GREEN_SIZE);
            data.blueBits   = getGLXFBConfigAttrib(config, GLX_BLUE_SIZE);

            data.alphaBits   = getGLXFBConfigAttrib(config, GLX_ALPHA_SIZE);
            data.depthBits   = getGLXFBConfigAttrib(config, GLX_DEPTH_SIZE);
            data.stencilBits = getGLXFBConfigAttrib(config, GLX_STENCIL_SIZE);

            data.accumRedBits   = getGLXFBConfigAttrib(config, GLX_ACCUM_RED_SIZE);
            data.accumGreenBits = getGLXFBConfigAttrib(config, GLX_ACCUM_GREEN_SIZE);
            data.accumBlueBits  = getGLXFBConfigAttrib(config, GLX_ACCUM_BLUE_SIZE);
            data.accumAlphaBits = getGLXFBConfigAttrib(config, GLX_ACCUM_ALPHA_SIZE);

            data.auxBuffers = getGLXFBConfigAttrib(config, GLX_AUX_BUFFERS);
            data.stereo     = getGLXFBConfigAttrib(config, GLX_STEREO) > 0;
            
            if (glx.ARB_multisample)
                data.samples = getGLXFBConfigAttrib(config, GLX_SAMPLES);
            
            if (glx.ARB_framebuffer_sRGB || glx.EXT_framebuffer_sRGB)
                data.sRGB = getGLXFBConfigAttrib(config, GLX_FRAMEBUFFER_SRGB_CAPABLE_ARB) > 0;
            
            data.handle = config;
            usableCount++;
            usableConfigs.add(data);
        }
        
        GLFBconfig result = glGetChooseFBConfig(desired, usableConfigs, usableCount);
        XFree(nativeConfigs);
        return result;
    }
    
    //
    // ----------     Create the OpenGL context using legacy API      ----------
    //
    
    private long createLegacyContextGLX(long fbconfig, long share) {
        return glXCreateNewContext(platform.getDisplay(), 
                            fbconfig, 
                            GLX_RGBA_TYPE, 
                            share, 
                            true);
    }
    
    @Override
    public void createContext() throws AWTException {
        initGLX();
        
        GLPlatformConfig ctxconfig = platform.getPlatformConfig();
        GLFBconfig fbconfig = platform.getFBconfig();
        
        IntBuffer attribs = BufferUtils.createIntBuffer(40);
        long share = NULL;
        
        if (ctxconfig.share != NULL) {
            share = ctxconfig.share;
        }
        
        GLFBconfig _native = chooseGLXFBConfig(fbconfig);
        if (_native == null) {
            throw new AWTException("GLX: Failed to find a suitable GLXFBConfig");
        }
        
        if (ctxconfig.client == JAWT_OPENGL_ES_API)
        {
            if (!glx.ARB_create_context ||
                !glx.ARB_create_context_profile ||
                !glx.EXT_create_context_es2_profile)
            {
                throw new AWTException("GLX: OpenGL ES requested but GLX_EXT_create_context_es2_profile is unavailable");
            }
        }        
        if (ctxconfig.forward)
        {
            if (!glx.ARB_create_context)
            {
                throw new AWTException("GLX: Forward compatibility requested but GLX_ARB_create_context_profile is unavailable");
            }
        }        
        if (toBoolean(ctxconfig.profile))
        {
            if (!glx.ARB_create_context ||
                !glx.ARB_create_context_profile)
            {
                throw new AWTException( "GLX: An OpenGL profile requested but GLX_ARB_create_context_profile is unavailable");
            }
        }
        
        if (glx.ARB_create_context)
        {
            int mask = 0, flags = 0;

            if (ctxconfig.client == JAWT_OPENGL_API)
            {
                if (ctxconfig.forward)
                    flags |= GLX_CONTEXT_FORWARD_COMPATIBLE_BIT_ARB;

                if (ctxconfig.profile == JAWT_OPENGL_CORE_PROFILE)
                    mask |= GLX_CONTEXT_CORE_PROFILE_BIT_ARB;
                else if (ctxconfig.profile == JAWT_OPENGL_COMPAT_PROFILE)
                    mask |= GLX_CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB;
            }
            else
                mask |= GLX_CONTEXT_ES2_PROFILE_BIT_EXT;
            
            if (ctxconfig.debug)
                flags |= GLX_CONTEXT_DEBUG_BIT_ARB;
            
            if (toBoolean(ctxconfig.robustness))
            {
                if (glx.ARB_create_context_robustness)
                {
                    if (ctxconfig.robustness == JAWT_NO_RESET_NOTIFICATION)
                    {
                        attribs.put(GLX_CONTEXT_RESET_NOTIFICATION_STRATEGY_ARB)
                               .put(GLX_NO_RESET_NOTIFICATION_ARB);
                    }
                    else if (ctxconfig.robustness == JAWT_LOSE_CONTEXT_ON_RESET)
                    {
                        attribs.put(GLX_CONTEXT_RESET_NOTIFICATION_STRATEGY_ARB)
                               .put(GLX_LOSE_CONTEXT_ON_RESET_ARB);
                    }

                    flags |= GLX_CONTEXT_ROBUST_ACCESS_BIT_ARB;
                }
            }
            
            if (toBoolean(ctxconfig.release))
            {
                if (glx.ARB_context_flush_control)
                {
                    if (ctxconfig.release == JAWT_RELEASE_BEHAVIOR_NONE)
                    {
                        attribs.put(GLX_CONTEXT_RELEASE_BEHAVIOR_ARB)
                               .put(GLX_CONTEXT_RELEASE_BEHAVIOR_NONE_ARB);
                    }
                    else if (ctxconfig.release == JAWT_RELEASE_BEHAVIOR_FLUSH)
                    {
                        attribs.put(GLX_CONTEXT_RELEASE_BEHAVIOR_ARB)
                               .put(GLX_CONTEXT_RELEASE_BEHAVIOR_FLUSH_ARB);
                    }
                }
            }
            
            if (ctxconfig.noerror)
            {
                if (glx.ARB_create_context_no_error)
                    attribs.put(GLX_CONTEXT_OPENGL_NO_ERROR_ARB).put(True);
            }
            
            // NOTE: Only request an explicitly versioned context when necessary, as
            //       explicitly requesting version 1.0 does not always return the
            //       highest version supported by the driver
            if (ctxconfig.major != 1 || ctxconfig.minor != 0)
            {
                attribs.put(GLX_CONTEXT_MAJOR_VERSION_ARB).put(ctxconfig.major);
                attribs.put(GLX_CONTEXT_MINOR_VERSION_ARB).put(ctxconfig.minor);
            }
            
            if (toBoolean(mask))
                attribs.put(GLX_CONTEXT_PROFILE_MASK_ARB).put(mask);

            if (toBoolean(flags))
                attribs.put(GLX_CONTEXT_FLAGS_ARB).put(flags);
            
            attribs.put(None).put(None);
            
            context = glXCreateContextAttribsARB(platform.getDisplay(), 
                    _native.handle, 
                    share, 
                    true, 
                    attribs);
            
            // HACK: This is a fallback for broken versions of the Mesa
            //       implementation of GLX_ARB_create_context_profile that fail
            //       default 1.0 context creation with a GLXBadProfileARB error in
            //       violation of the extension spec
            if (context == NULL)
            {
                if (ctxconfig.client == JAWT_OPENGL_API &&
                    ctxconfig.profile == JAWT_OPENGL_ANY_PROFILE &&
                    ctxconfig.forward == false)
                {
                    context = createLegacyContextGLX(_native.handle, share);
                }
            }
        }
        else
        {
            context = createLegacyContextGLX(_native.handle, share);
        }
        
        if (context == NULL) {
            throw new AWTException("GLX: Failed to create context");
        }
        
        memFree(attribs);
    }

    @Override
    public void makeContextCurrent(boolean handle) {
        if (handle) {
            if (!glXMakeCurrent(platform.getDisplay(), platform.getSurface(), context))
            {
                throw new IllegalStateException("GLX: Failed to make context current");
            }
        } else {
            if (!glXMakeCurrent(platform.getDisplay(), NULL, NULL)) {
                throw new IllegalStateException("GLX: Failed to clear current context");
            }
        }
    }

    @Override
    public long getCurrentContext() {
        return glXGetCurrentContext();
    }

    @Override
    public void swapBuffers() {
        glXSwapBuffers(platform.getDisplay(), platform.getSurface());
    }

    @Override
    public void swapInterval(int interval) {
        if (glx.EXT_swap_control)
        {
            glXSwapIntervalEXT(platform.getDisplay(), platform.getDisplay(), interval);
        } 
        else if (glx.MESA_swap_control && Functions.SwapIntervalMESA != NULL)
        {
            long __functionAddress = Functions.SwapIntervalMESA;
            callI(interval, __functionAddress);
        }
        else if (glx.SGI_swap_control) {
            if (interval > 0)
                glXSwapIntervalSGI(interval);
        }
    }

    @Override
    public boolean extensionSupported(String extension) {
        String _extensions = glXQueryExtensionsString(
            platform.getDisplay(), 
            platform.getScreen()
        );

        if (_extensions == null || _extensions.isEmpty()) {
            return false;
        }          
        return glStringInExtensionString(extension, _extensions);
    }

    @Override
    public long getProcAddress(String procname) {
        if (Functions.GetProcAddress != NULL) {
            return glXGetProcAddress(procname);
        } else if (Functions.GetProcAddressARB != NULL) {
            return glXGetProcAddressARB(procname);
        }
        return apiGetFunctionAddress(GL.getFunctionProvider(), procname);
    }

    @Override
    public long getHandle() {
        return context;
    }

    @Override
    public void destroy() {
        if (platform != null && context != NULL) {
            glXDestroyContext(platform.getDisplay(), context);
        }
    }
}
