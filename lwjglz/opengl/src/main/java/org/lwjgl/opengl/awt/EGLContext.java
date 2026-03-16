/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.awt;

import java.awt.AWTException;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.egl.EGL10.*;
import static org.lwjgl.egl.EGL11.*;
import static org.lwjgl.egl.EGL14.*;
import static org.lwjgl.egl.EXTPresentOpaque.*;
import static org.lwjgl.egl.KHRContextFlushControl.*;
import static org.lwjgl.egl.KHRCreateContext.*;
import static org.lwjgl.egl.KHRCreateContextNoError.*;
import static org.lwjgl.egl.KHRGLColorspace.*;

import static org.lwjgl.awt.AWT.*;
import static org.lwjgl.opengl.awt.AWTGL.*;

import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 *
 * @author wil
 */
public class EGLContext implements GLContext {
    
    public static class Extensions {
        
        private Extensions() {}
        
        public Boolean 
                KHR_get_all_proc_addresses      = Boolean.FALSE,
                EXT_client_extensions           = Boolean.FALSE,
                EXT_platform_base               = Boolean.FALSE,
                EXT_platform_x11                = Boolean.FALSE,
                EXT_platform_wayland            = Boolean.FALSE,
                ANGLE_platform_angle            = Boolean.FALSE,
                ANGLE_platform_angle_opengl     = Boolean.FALSE,
                ANGLE_platform_angle_d3d        = Boolean.FALSE,
                ANGLE_platform_angle_vulkan     = Boolean.FALSE,
                ANGLE_platform_angle_metal      = Boolean.FALSE,
                MESA_platform_surfaceless       = Boolean.FALSE,
                KHR_create_context              = Boolean.FALSE,
                KHR_create_context_no_error     = Boolean.FALSE,
                KHR_gl_colorspace               = Boolean.FALSE,
                KHR_context_flush_control       = Boolean.FALSE,
                EXT_present_opaque              = Boolean.FALSE;
    }
    
    private final GLPlatform platform;
    private final Extensions egl;
    
    private long context;
    
    private long display;
    private long surface;

    public EGLContext(GLPlatform platform) {
        this.platform = platform;
        this.egl = new Extensions();
    }
    
    // Return a description of the specified EGL error
    //
    private static String getEGLErrorString(int error) {
        return switch (error) {
            case EGL_SUCCESS                -> "Success";
            case EGL_NOT_INITIALIZED        -> "EGL is not or could not be initialized";
            case EGL_BAD_ACCESS             -> "EGL cannot access a requested resource";
            case EGL_BAD_ALLOC              -> "EGL failed to allocate resources for the requested operation";
            case EGL_BAD_ATTRIBUTE          -> "An unrecognized attribute or attribute value was passed in the attribute list";
            case EGL_BAD_CONTEXT            -> "An EGLContext argument does not name a valid EGL rendering context";
            case EGL_BAD_CONFIG             -> "An EGLConfig argument does not name a valid EGL frame buffer configuration";
            case EGL_BAD_CURRENT_SURFACE    -> "The current surface of the calling thread is a window, pixel buffer or pixmap that is no longer valid";
            case EGL_BAD_DISPLAY            -> "An EGLDisplay argument does not name a valid EGL display connection";
            case EGL_BAD_SURFACE            -> "An EGLSurface argument does not name a valid surface configured for GL rendering";
            case EGL_BAD_MATCH              -> "Arguments are inconsistent";
            case EGL_BAD_PARAMETER          -> "One or more argument values are invalid";
            case EGL_BAD_NATIVE_PIXMAP      -> "A NativePixmapType argument does not refer to a valid native pixmap";
            case EGL_BAD_NATIVE_WINDOW      -> "A NativeWindowType argument does not refer to a valid native window";
            case EGL_CONTEXT_LOST           -> "The application must destroy all contexts and reinitialise";
            default -> "ERROR: UNKNOWN EGL ERROR";
        };
    }
    
    // Returns the specified attribute of the specified EGLConfig
    //
    private int getEGLConfigAttrib(long config, int attrib) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer value = stack.mallocInt(1);
            eglGetConfigAttrib(display, config, attrib, value);
            return value.get(0);
        }
    }

    private GLFBConfig chooseEGLConfig(GLPlatformConfig ctxconfig, GLFBConfig fbconfig) throws AWTException {
        int apiBit, surfaceTypeBit;
        boolean wrongApiAvailable = false;
        
        if (ctxconfig.client == AWT_OPENGL_ES_API)
        {
            if (ctxconfig.major == 1)
                apiBit = EGL_OPENGL_ES_BIT;
            else
                apiBit = EGL_OPENGL_ES2_BIT;
        }
        else
            apiBit = EGL_OPENGL_BIT;
        
        
        surfaceTypeBit = EGL_WINDOW_BIT;
        //if (ctxconfig.platform == EGL_PLATFORM_SURFACELESS_MESA)
        //    surfaceTypeBit = EGL_PBUFFER_BIT;
        //else
        //    surfaceTypeBit = EGL_WINDOW_BIT;
        
        if (fbconfig.stereo)
        {
            throw new AWTException("EGL: Stereo rendering not supported");
        }
        
        IntBuffer nativeCount = BufferUtils.createIntBuffer(1);        
        if (!eglGetConfigs(display, null, nativeCount)) {
            memFree(nativeCount);
            throw new AWTException("EGL: No EGLConfigs returned");
        }
        
        PointerBuffer nativeConfigs = BufferUtils.createPointerBuffer(nativeCount.get(0));
        eglGetConfigs(display, nativeConfigs, nativeCount);
        
        int usableCount = 0;
        List<GLFBConfig> usableConfigs = new ArrayList<>();
        
        for (int i = 0;  i < nativeCount.get(0);  i++)
        {
            long/*EGLConfig*/ config = nativeConfigs.get(i);
            GLFBConfig data = new GLFBConfig();
            
             // Only consider RGB(A) EGLConfigs
            if (getEGLConfigAttrib(config, EGL_COLOR_BUFFER_TYPE) != EGL_RGB_BUFFER)
                continue;

            if (!BOOL((getEGLConfigAttrib(config, EGL_SURFACE_TYPE) & surfaceTypeBit)))
                continue;
            
            if (!BOOL((getEGLConfigAttrib(config, EGL_RENDERABLE_TYPE) & apiBit)))
            {
                wrongApiAvailable = true;
                continue;
            }
            
            data.redBits = getEGLConfigAttrib(config, EGL_RED_SIZE);
            data.greenBits = getEGLConfigAttrib(config, EGL_GREEN_SIZE);
            data.blueBits = getEGLConfigAttrib(config, EGL_BLUE_SIZE);

            data.alphaBits = getEGLConfigAttrib(config, EGL_ALPHA_SIZE);
            data.depthBits = getEGLConfigAttrib(config, EGL_DEPTH_SIZE);
            data.stencilBits = getEGLConfigAttrib(config, EGL_STENCIL_SIZE);
            
            if (isWayland()) {
                 // NOTE: The wl_surface opaque region is no guarantee that its buffer
                //       is presented as opaque, if it also has an alpha channel
                // HACK: If EGL_EXT_present_opaque is unavailable, ignore any config
                //       with an alpha channel to ensure the buffer is opaque
                if (!egl.EXT_present_opaque)
                {
                    if (!fbconfig.transparent && data.alphaBits > 0)
                        continue;
                }
            }
            
            data.samples = getEGLConfigAttrib(config, EGL_SAMPLES);
            data.doublebuffer = fbconfig.doublebuffer;

            data.handle = config;
            usableConfigs.add(data);
            usableCount++;
        }
        
        GLFBConfig result = glGetChooseFBConfig(fbconfig, usableConfigs, usableCount);
        memFree(nativeCount);
        memFree(nativeConfigs);
        
        if (result == null) {
            if (wrongApiAvailable)
            {
                if (ctxconfig.client == AWT_OPENGL_ES_API)
                {
                    if (ctxconfig.major == 1) {
                        throw new AWTException("EGL: Failed to find support for OpenGL ES 1.x");
                    } else {
                        throw new AWTException("EGL: Failed to find support for OpenGL ES 2 or later");
                    }
                }
                else 
                {
                    throw new AWTException("EGL: Failed to find support for OpenGL");
                }
            } 
            else 
            {
                throw new AWTException("EGL: Failed to find a suitable EGLConfig");
            }
        }
        return result;
    }
    
    private void glTerminateEGL() {
        if (display != NULL) {
            eglTerminate(display);
            display = EGL_NO_DISPLAY;
        }
    }
    
    private void initEGL() throws AWTException {
        String extensions = eglQueryString(EGL_NO_DISPLAY, EGL_EXTENSIONS);
        if ((extensions != null && !extensions.isEmpty()) && eglGetError() == EGL_SUCCESS)
            egl.EXT_client_extensions = true;
        
        if (egl.EXT_client_extensions) {
            egl.EXT_platform_base =
                glStringInExtensionString("EGL_EXT_platform_base", extensions);
            egl.EXT_platform_x11 =
                glStringInExtensionString("EGL_EXT_platform_x11", extensions);
            egl.EXT_platform_wayland =
                glStringInExtensionString("EGL_EXT_platform_wayland", extensions);
            egl.ANGLE_platform_angle =
                glStringInExtensionString("EGL_ANGLE_platform_angle", extensions);
            egl.ANGLE_platform_angle_opengl =
                glStringInExtensionString("EGL_ANGLE_platform_angle_opengl", extensions);
            egl.ANGLE_platform_angle_d3d =
                glStringInExtensionString("EGL_ANGLE_platform_angle_d3d", extensions);
            egl.ANGLE_platform_angle_vulkan =
                glStringInExtensionString("EGL_ANGLE_platform_angle_vulkan", extensions);
            egl.ANGLE_platform_angle_metal =
                glStringInExtensionString("EGL_ANGLE_platform_angle_metal", extensions);
            egl.MESA_platform_surfaceless =
                glStringInExtensionString("EGL_MESA_platform_surfaceless", extensions);
        }
        
        display = eglGetDisplay(platform.getNativeDisplay());
        if (display == EGL_NO_DISPLAY) {
            glTerminateEGL();
            throw new IllegalStateException("EGL: Failed to get EGL display: %ss"
                        .formatted(getEGLErrorString(eglGetError())));
        }
        
        try (MemoryStack stack = stackPush()) {
            IntBuffer major = stack.callocInt(1);
            IntBuffer minor = stack.callocInt(1);
            
            if (!eglInitialize(display, major, minor)) {
                glTerminateEGL();
                throw new IllegalStateException("EGL: Failed to initialize EGL: %s"
                        .formatted(getEGLErrorString(eglGetError())));
            }
        }
        
        egl.KHR_create_context =
            extensionSupported("EGL_KHR_create_context");
        egl.KHR_create_context_no_error =
            extensionSupported("EGL_KHR_create_context_no_error");
        egl.KHR_gl_colorspace =
            extensionSupported("EGL_KHR_gl_colorspace");
        egl.KHR_get_all_proc_addresses =
            extensionSupported("EGL_KHR_get_all_proc_addresses");
        egl.KHR_context_flush_control =
            extensionSupported("EGL_KHR_context_flush_control");
        egl.EXT_present_opaque =
            extensionSupported("EGL_EXT_present_opaque");
    }
    
    @Override
    public void createContext() throws AWTException {
        initEGL();
        
        GLData gldata = platform.getGLData();
        GLPlatformConfig ctxconfig = gldata.getPlatformConfig();
        GLFBConfig fbconfig = gldata.getFBConfig();
        
        IntBuffer attribs = BufferUtils.createIntBuffer(40);
        long share = NULL;
        
        if (display == NULL) {
            throw new AWTException("EGL: API not available");
        }
        
        if (ctxconfig.share != NULL)
            share = ctxconfig.share;
        
        GLFBConfig config = chooseEGLConfig(ctxconfig, fbconfig);
        if (config == null)
            throw new AWTException("EGL: Failed to find a suitable EGLConfig");
        
        if (ctxconfig.client == AWT_OPENGL_ES_API)
        {
            if (!eglBindAPI(EGL_OPENGL_ES_API))
            {
                throw new IllegalStateException("EGL: Failed to bind OpenGL ES: %s"
                        .formatted(getEGLErrorString(eglGetError())));
            }
        }
         else
        {
            if (!eglBindAPI(EGL_OPENGL_API))
            {
                throw new IllegalStateException("EGL: Failed to bind OpenGL: %s"
                        .formatted(getEGLErrorString(eglGetError())));
            }
        }
        
        if (egl.KHR_create_context)
        {
            int mask = 0, flags = 0;

            if (ctxconfig.client == AWT_OPENGL_API)
            {
                if (ctxconfig.forward)
                    flags |= EGL_CONTEXT_OPENGL_FORWARD_COMPATIBLE_BIT_KHR;

                if (ctxconfig.profile == AWT_OPENGL_CORE_PROFILE)
                    mask |= EGL_CONTEXT_OPENGL_CORE_PROFILE_BIT_KHR;
                else if (ctxconfig.profile == AWT_OPENGL_COMPAT_PROFILE)
                    mask |= EGL_CONTEXT_OPENGL_COMPATIBILITY_PROFILE_BIT_KHR;
            }

            if (ctxconfig.debug)
                flags |= EGL_CONTEXT_OPENGL_DEBUG_BIT_KHR;

            if (BOOL(ctxconfig.robustness))
            {
                if (ctxconfig.robustness == AWT_NO_RESET_NOTIFICATION)
                {
                    attribs.put(EGL_CONTEXT_OPENGL_RESET_NOTIFICATION_STRATEGY_KHR)
                           .put(EGL_NO_RESET_NOTIFICATION_KHR);
                }
                else if (ctxconfig.robustness == AWT_LOSE_CONTEXT_ON_RESET)
                {
                    attribs.put(EGL_CONTEXT_OPENGL_RESET_NOTIFICATION_STRATEGY_KHR)
                           .put(EGL_LOSE_CONTEXT_ON_RESET_KHR);
                }

                flags |= EGL_CONTEXT_OPENGL_ROBUST_ACCESS_BIT_KHR;
            }

            if (ctxconfig.major != 1 || ctxconfig.minor != 0)
            {
                attribs.put(EGL_CONTEXT_MAJOR_VERSION_KHR).put(ctxconfig.major);
                attribs.put(EGL_CONTEXT_MINOR_VERSION_KHR).put(ctxconfig.minor);
            }

            if (ctxconfig.noerror)
            {
                if (egl.KHR_create_context_no_error)
                    attribs.put(EGL_CONTEXT_OPENGL_NO_ERROR_KHR).put(EGL_TRUE);
            }

            if (BOOL(mask))
                 attribs.put(EGL_CONTEXT_OPENGL_PROFILE_MASK_KHR).put(mask);

            if (BOOL(flags))
                 attribs.put(EGL_CONTEXT_FLAGS_KHR).put(flags);
        }
        else
        {
            if (ctxconfig.client == AWT_OPENGL_ES_API)
                attribs.put(EGL_CONTEXT_CLIENT_VERSION).put(ctxconfig.major);
        }

        if (egl.KHR_context_flush_control)
        {
            if (ctxconfig.release == AWT_RELEASE_BEHAVIOR_NONE)
            {
                attribs.put(EGL_CONTEXT_RELEASE_BEHAVIOR_KHR)
                       .put(EGL_CONTEXT_RELEASE_BEHAVIOR_NONE_KHR);
            }
            else if (ctxconfig.release == AWT_RELEASE_BEHAVIOR_FLUSH)
            {
                attribs.put(EGL_CONTEXT_RELEASE_BEHAVIOR_KHR)
                       .put(EGL_CONTEXT_RELEASE_BEHAVIOR_FLUSH_KHR);
            }
        }
        
        attribs.put(EGL_NONE);
        attribs.flip();
        
        context = eglCreateContext(display,
                                    config.handle, share, attribs);

        if (context == EGL_NO_CONTEXT) {
            throw new IllegalStateException("EGL: Failed to create context: %s"
                        .formatted(getEGLErrorString(eglGetError())));
        }
        
        // Set up attributes for surface creation
        attribs.rewind();
        
        if (fbconfig.sRGB)
        {
            if (egl.KHR_gl_colorspace)
                attribs.put(EGL_GL_COLORSPACE_KHR).put(EGL_GL_COLORSPACE_SRGB_KHR);
        }
        
        if (!fbconfig.doublebuffer)
            attribs.put(EGL_RENDER_BUFFER).put(EGL_SINGLE_BUFFER);
        
        if (isWayland())
        {
            if (egl.EXT_present_opaque) {
                attribs.put(EGL_PRESENT_OPAQUE_EXT).put((fbconfig.transparent) ? EGL_FALSE : EGL_TRUE);
            }
        }
        
        attribs.put(EGL_NONE);
        attribs.flip();
        
        long _native = platform.getNativeWindow();
        // HACK: Use a non-platform function for all, as 
        //       eglCreatePlatformWindowSurfaceEXT does not work with AWT 
        //       despite stating that it supports EGL_EXT_platform_base.
        surface = eglCreateWindowSurface(display, config.handle, _native, attribs);

        if (surface == EGL_NO_SURFACE)
        {
            throw new IllegalStateException("EGL: Failed to create window surface: %s"
                        .formatted(getEGLErrorString(eglGetError())));
        }
        
        memFree(attribs);
    }

    @Override
    public void makeContextCurrent(boolean handle) {
        if (handle) {
            if(!eglMakeCurrent(display, 
                    surface, 
                    surface, 
                    context)) {
                throw new IllegalStateException("EGL: Failed to make context current: %s"
                        .formatted(getEGLErrorString(eglGetError())));
            }
        } else {
            if (!eglMakeCurrent(display, 
                    EGL_NO_SURFACE, 
                    EGL_NO_SURFACE, 
                    EGL_NO_CONTEXT)) {
                throw new IllegalStateException("EGL: Failed to clear current context: %s"
                        .formatted(getEGLErrorString(eglGetError())));
            }
        }
    }

    @Override
    public long getCurrentContext() {
        return eglGetCurrentContext();
    }

    @Override
    public void swapBuffers() {
        eglSwapBuffers(display, surface);
    }

    @Override
    public void swapInterval(int interval) {
        eglSwapInterval(display, interval);
    }

    @Override
    public boolean extensionSupported(String extension) {
        String extensions = eglQueryString(display, EGL_EXTENSIONS);
        if (extensions != null && !extensions.isEmpty()) {
            return glStringInExtensionString(extension, extensions);
        }
        return false;
    }

    @Override
    public long getProcAddress(String procname) {
        long proc = eglGetProcAddress(procname);
        if (proc != NULL) {
            return proc;
        }
        if (egl.KHR_get_all_proc_addresses) {
             return apiGetFunctionAddress(GL.getFunctionProvider(), procname);
        }
        return NULL;
    }

    @Override
    public long getHandle() {
        return context;
    }

    @Override
    public void destroy() {
        if (surface != NULL) {
            eglDestroySurface(display, surface);
            surface = EGL_NO_SURFACE;
        }
        if (display != NULL) {
            eglDestroyContext(display, context);
            eglTerminate(display);
            display = EGL_NO_DISPLAY;
        }
        glTerminateEGL();
    }
}
