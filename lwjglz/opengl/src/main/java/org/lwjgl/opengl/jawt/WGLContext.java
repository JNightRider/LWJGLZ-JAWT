/*
 * Copyright lwjglz-jawt. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import java.awt.AWTException;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.lwjgl.system.windows.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.WGL.*;
import static org.lwjgl.opengl.WGLARBContextFlushControl.*;
import static org.lwjgl.opengl.WGLARBCreateContext.*;
import static org.lwjgl.opengl.WGLARBCreateContextNoError.*;
import static org.lwjgl.opengl.WGLARBCreateContextProfile.*;
import static org.lwjgl.opengl.WGLARBCreateContextRobustness.*;
import static org.lwjgl.opengl.WGLARBExtensionsString.*;
import static org.lwjgl.opengl.WGLARBFramebufferSRGB.*;
import static org.lwjgl.opengl.WGLARBMakeCurrentRead.*;
import static org.lwjgl.opengl.WGLARBMultisample.*;
import static org.lwjgl.opengl.WGLARBPixelFormat.*;
import static org.lwjgl.opengl.WGLEXTColorspace.*;
import static org.lwjgl.opengl.WGLEXTCreateContextES2Profile.*;
import static org.lwjgl.opengl.WGLEXTExtensionsString.*;
import static org.lwjgl.opengl.WGLEXTSwapControl.*;

import static org.lwjgl.awt.AWT.*;
import static org.lwjgl.opengl.jawt.AWTGL.*;

import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.windows.GDI32.*;
import static org.lwjgl.system.windows.User32.*;
import static org.lwjgl.system.windows.WinBase.*;
import static org.lwjgl.system.windows.WindowsUtil.*;

/**
 *
 * @author wil
 */
public class WGLContext implements GLContext {
    
    /** Contains the function pointers loaded from {@code GL.getFunctionProvider()}. */
    public static final class Win32Functions {
        /** private constructor */
        private Win32Functions() {}
        
        /** Function address. */
        public static final long
                GetExtensionsStringARB  = apiGetFunctionAddress(GL.getFunctionProvider(), "wglGetExtensionsStringARB"),
                GetExtensionsStringEXT  = apiGetFunctionAddress(GL.getFunctionProvider(), "wglGetExtensionsStringEXT");
    }
    
    /** It contains all the extensions available to create the context from {@code extensionSupported()}. */
    public static class Win32Capabilities {
        /** private constructor */
        private Win32Capabilities() {}
        
        /** Available extensions */
        public Boolean
                EXT_swap_control                = Boolean.FALSE,
                ARB_pixel_format                = Boolean.FALSE,
                ARB_multisample                 = Boolean.FALSE,
                ARB_framebuffer_sRGB            = Boolean.FALSE,
                EXT_framebuffer_sRGB            = Boolean.FALSE,
                EXT_colorspace                  = Boolean.FALSE,
                ARB_create_context              = Boolean.FALSE,
                ARB_create_context_profile      = Boolean.FALSE,
                EXT_create_context_es2_profile  = Boolean.FALSE,
                ARB_create_context_robustness   = Boolean.FALSE,
                ARB_create_context_no_error     = Boolean.FALSE,
                ARB_context_flush_control       = Boolean.FALSE;
    }

    private final Win32Window window;
    private final Win32Capabilities wgl;
    
    private final GLData gldata;
    private long context;
    private long dc;

    public WGLContext(Win32Window window, GLData data) {
        this.wgl    = new Win32Capabilities();
        this.window = window;
        this.gldata = data;
    }
    
    // Return the value corresponding to the specified attribute
    //
    private int findPixelFormatAttribValueWGL(IntBuffer attribs, IntBuffer values, int attrib) throws AWTException
    {
        int attribCount = attribs.remaining();
        for (int i = 0;  i < attribCount;  i++)
        {
            if (attribs.get(i) == attrib)
                return values.get(i);
        }
        throw new AWTException("WGL: Unknown pixel format attribute requested");
    }
    
    private boolean findBoolPixelFormatAttribValueWGL(IntBuffer attribs, IntBuffer values, int attrib) throws AWTException {
        return findPixelFormatAttribValueWGL(attribs, values, attrib) != 0;
    }
    
    private GLFBConfig choosePixelFormatWGL(GLPlatformConfig ctxconfig, GLFBConfig fbconfig) throws AWTException {
        IntBuffer attribs = BufferUtils.createIntBuffer(40);
        IntBuffer values  = BufferUtils.createIntBuffer(40);
    
        int nativeCount = DescribePixelFormat(null, 
                dc, 
                1, 
                null);
        
        if (wgl.ARB_pixel_format) {
            attribs.put(WGL_SUPPORT_OPENGL_ARB)
                   .put(WGL_DRAW_TO_WINDOW_ARB)
                   .put(WGL_PIXEL_TYPE_ARB)
                   .put(WGL_ACCELERATION_ARB)
                   .put(WGL_RED_BITS_ARB)
                   .put(WGL_RED_SHIFT_ARB)
                   .put(WGL_GREEN_BITS_ARB)
                   .put(WGL_GREEN_SHIFT_ARB)
                   .put(WGL_BLUE_BITS_ARB)
                   .put(WGL_BLUE_SHIFT_ARB)
                   .put(WGL_ALPHA_BITS_ARB)
                   .put(WGL_ALPHA_SHIFT_ARB)
                   .put(WGL_DEPTH_BITS_ARB)
                   .put(WGL_STENCIL_BITS_ARB)
                   .put(WGL_ACCUM_BITS_ARB)
                   .put(WGL_ACCUM_RED_BITS_ARB)
                   .put(WGL_ACCUM_GREEN_BITS_ARB)
                   .put(WGL_ACCUM_BLUE_BITS_ARB)
                   .put(WGL_ACCUM_ALPHA_BITS_ARB)
                   .put(WGL_AUX_BUFFERS_ARB)
                   .put(WGL_STEREO_ARB)
                   .put(WGL_DOUBLE_BUFFER_ARB);
            
            if (wgl.ARB_multisample) 
                attribs.put(WGL_SAMPLES_ARB);
            
            if (ctxconfig.client == AWT_OPENGL_API)
            {
                if (wgl.ARB_framebuffer_sRGB || wgl.EXT_framebuffer_sRGB)
                    attribs.put(WGL_FRAMEBUFFER_SRGB_CAPABLE_ARB);
            }
            else
            {
                if (wgl.EXT_colorspace)
                    attribs.put(WGL_COLORSPACE_EXT);
            }
            attribs.flip();
            
            // NOTE: In a Parallels VM WGL_ARB_pixel_format returns fewer pixel formats than
            //       DescribePixelFormat, violating the guarantees of the extension spec
            // HACK: Iterate through the minimum of both counts
            
            try (MemoryStack stack = stackPush()) {
                IntBuffer extensionCount = stack.callocInt(1);
                IntBuffer attrib = stack.mallocInt(2);
                attrib.put(WGL_NUMBER_PIXEL_FORMATS_ARB);
                attrib.flip();
                
                // HACK: A pointer with a capacity of 2 must be created so that flip() can 
                //       handle the limit by adding a 0 to the end of the queue.
                if (!BOOL(nwglGetPixelFormatAttribivARB(dc,
                                          1, 0, 1, memAddress(attrib), memAddress(extensionCount))))
                {
                    throw new AWTException( "WGL: Failed to retrieve pixel format attribute");
                }
                
                nativeCount = Math.min(nativeCount, extensionCount.get(0));
            }
        }
        
        List<GLFBConfig> usableConfigs = new ArrayList<>();
        int usableCount = 0,
            pixelFormat;
        
        for (int i = 0;  i < nativeCount;  i++) {
            GLFBConfig data = new GLFBConfig();
            pixelFormat = i + 1;

            if (wgl.ARB_pixel_format)
            {
                // Get pixel format attributes through "modern" extension

                if (!wglGetPixelFormatAttribivARB(dc,
                                                  pixelFormat, 0,
                                                  attribs, values))
                {
                    memFree(attribs);
                    memFree(values);
                    throw new AWTException("WGL: Failed to retrieve pixel format attributes");
                }

                if (!findBoolPixelFormatAttribValueWGL(attribs, values, WGL_SUPPORT_OPENGL_ARB) ||
                    !findBoolPixelFormatAttribValueWGL(attribs, values, WGL_DRAW_TO_WINDOW_ARB))
                {
                    continue;
                }

                if (findPixelFormatAttribValueWGL(attribs, values, WGL_PIXEL_TYPE_ARB) != WGL_TYPE_RGBA_ARB)
                    continue;

                if (findPixelFormatAttribValueWGL(attribs, values, WGL_ACCELERATION_ARB) == WGL_NO_ACCELERATION_ARB)
                    continue;

                if (findBoolPixelFormatAttribValueWGL(attribs, values, WGL_DOUBLE_BUFFER_ARB) != fbconfig.doublebuffer)
                    continue;

                data.redBits = findPixelFormatAttribValueWGL(attribs, values, WGL_RED_BITS_ARB);
                data.greenBits = findPixelFormatAttribValueWGL(attribs, values, WGL_GREEN_BITS_ARB);
                data.blueBits = findPixelFormatAttribValueWGL(attribs, values, WGL_BLUE_BITS_ARB);
                data.alphaBits = findPixelFormatAttribValueWGL(attribs, values, WGL_ALPHA_BITS_ARB);

                data.depthBits = findPixelFormatAttribValueWGL(attribs, values, WGL_DEPTH_BITS_ARB);
                data.stencilBits = findPixelFormatAttribValueWGL(attribs, values, WGL_STENCIL_BITS_ARB);

                data.accumRedBits = findPixelFormatAttribValueWGL(attribs, values, WGL_ACCUM_RED_BITS_ARB);
                data.accumGreenBits = findPixelFormatAttribValueWGL(attribs, values, WGL_ACCUM_GREEN_BITS_ARB);
                data.accumBlueBits = findPixelFormatAttribValueWGL(attribs, values, WGL_ACCUM_BLUE_BITS_ARB);
                data.accumAlphaBits = findPixelFormatAttribValueWGL(attribs, values, WGL_ACCUM_ALPHA_BITS_ARB);

                data.auxBuffers = findPixelFormatAttribValueWGL(attribs, values, WGL_AUX_BUFFERS_ARB);
                data.stereo = findBoolPixelFormatAttribValueWGL(attribs, values, WGL_STEREO_ARB);

                if (wgl.ARB_multisample)
                    data.samples = findPixelFormatAttribValueWGL(attribs, values, WGL_SAMPLES_ARB);

                if (ctxconfig.client == AWT_OPENGL_API)
                {
                    if (wgl.ARB_framebuffer_sRGB || wgl.EXT_framebuffer_sRGB)
                        data.sRGB = findBoolPixelFormatAttribValueWGL(attribs, values, WGL_FRAMEBUFFER_SRGB_CAPABLE_ARB);
                }
                else
                {
                    if (wgl.EXT_colorspace)
                    {
                        if (findPixelFormatAttribValueWGL(attribs, values, WGL_COLORSPACE_EXT) == WGL_COLORSPACE_SRGB_EXT)
                            data.sRGB = true;
                    }
                }
            }
            else
            {
                // Get pixel format attributes through legacy PFDs
                try (MemoryStack stack = stackPush()) {
                    PIXELFORMATDESCRIPTOR pfd = PIXELFORMATDESCRIPTOR.calloc(stack);
                    if (!BOOL(DescribePixelFormat(null, dc,
                                             pixelFormat,
                                             pfd)))
                    {
                        memFree(attribs);
                        memFree(values);
                        throw new AWTException("WGL: Failed to describe pixel format");
                    }

                    if (!BOOL(pfd.dwFlags() & PFD_DRAW_TO_WINDOW) ||
                        !BOOL(pfd.dwFlags() & PFD_SUPPORT_OPENGL))
                    {
                        continue;
                    }

                    if (!BOOL(pfd.dwFlags() & PFD_GENERIC_ACCELERATED) &&
                        BOOL(pfd.dwFlags() & PFD_GENERIC_FORMAT))
                    {
                        continue;
                    }

                    if (pfd.iPixelType() != PFD_TYPE_RGBA)
                        continue;

                    if (!!(BOOL(pfd.dwFlags() & PFD_DOUBLEBUFFER)) != fbconfig.doublebuffer)
                        continue;

                    data.redBits = pfd.cRedBits();
                    data.greenBits = pfd.cGreenBits();
                    data.blueBits = pfd.cBlueBits();
                    data.alphaBits = pfd.cAlphaBits();

                    data.depthBits = pfd.cDepthBits();
                    data.stencilBits = pfd.cStencilBits();

                    data.accumRedBits = pfd.cAccumRedBits();
                    data.accumGreenBits = pfd.cAccumGreenBits();
                    data.accumBlueBits = pfd.cAccumBlueBits();
                    data.accumAlphaBits = pfd.cAccumAlphaBits();

                    data.auxBuffers = pfd.cAuxBuffers();
                    data.stereo = BOOL(pfd.dwFlags() & PFD_STEREO);
                }
            }
            
            data.handle = pixelFormat;
            usableConfigs.add(data);
            usableCount++;
        }
        
        if (!BOOL(usableCount))
        {
            throw new AWTException("WGL: The driver does not appear to support OpenGL");
        }
        
        GLFBConfig closest = glGetChooseFBConfig(fbconfig, usableConfigs, usableCount);
        if (closest == null) {
            throw new AWTException("WGL: Failed to find a suitable pixel format");
        }
        return closest;
    }
    
    @Override
    public void initGL() throws AWTException {
        long hdc;
        
        short classAtom = 0;
        long  hwnd      = NULL;
        long  hglrc     = NULL;
        try (MemoryStack stack = stackPush()) {
            IntBuffer pi = stack.mallocInt(1);
            
            WNDCLASSEX wc = WNDCLASSEX.calloc(stack)
                .cbSize(WNDCLASSEX.SIZEOF)
                .style(CS_HREDRAW | CS_VREDRAW)
                .hInstance(WindowsLibrary.HINSTANCE)
                .lpszClassName(stack.UTF16("WGL"));
            
            memPutAddress(
                wc.address() + WNDCLASSEX.LPFNWNDPROC,
                User32.Functions.DefWindowProc
            );
            
            classAtom = RegisterClassEx(pi, wc);
            if (classAtom == 0) {
                windowsThrowException("WGL: Failed to register WGL window class", pi);
            }
            
            hwnd = nCreateWindowEx(
                memAddress(pi),
                0, classAtom & 0xFFFF, NULL,
                WS_OVERLAPPEDWINDOW | WS_CLIPCHILDREN | WS_CLIPSIBLINGS,
                0, 0, 1, 1,
                NULL, NULL, NULL, NULL
            );
            
            if (hwnd == NULL) {
                windowsThrowException("WGL: Failed to create WGL window", pi);
            }
            
            hdc = check(GetDC(hwnd));
            
            PIXELFORMATDESCRIPTOR pfd = PIXELFORMATDESCRIPTOR.calloc(stack)
                .nSize((short)PIXELFORMATDESCRIPTOR.SIZEOF)
                .nVersion((short)1)
                .dwFlags(PFD_DRAW_TO_WINDOW | PFD_SUPPORT_OPENGL | PFD_DOUBLEBUFFER) // we don't care about anything else
                .iPixelType((byte) PFD_TYPE_RGBA)
                .cColorBits((byte) 24);
            
            int pixelFormat = ChoosePixelFormat(pi, hdc, pfd);
            if (pixelFormat == 0) {
                windowsThrowException("WGL: Failed to choose an OpenGL-compatible pixel format", pi);
            }

            if (DescribePixelFormat(pi, hdc, pixelFormat, pfd) == 0) {
                windowsThrowException("WGL: Failed to obtain pixel format information", pi);
            }

            if (!SetPixelFormat(pi, hdc, pixelFormat, pfd)) {
                windowsThrowException("WGL: Failed to set the pixel format", pi);
            }
            
            hglrc = check(wglCreateContext(null, hdc));
            if (!wglMakeCurrent(pi, hdc, hglrc)) {
                windowsThrowException("WGL WGL: Failed to create dummy context", pi);
            }
            
            // NOTE: WGL_ARB_extensions_string and WGL_EXT_extensions_string are not
            //       checked below as we are already using them
            wgl.ARB_multisample =
                extensionSupported("WGL_ARB_multisample");
            wgl.ARB_framebuffer_sRGB =
                extensionSupported("WGL_ARB_framebuffer_sRGB");
            wgl.EXT_framebuffer_sRGB =
                extensionSupported("WGL_EXT_framebuffer_sRGB");
            wgl.ARB_create_context =
                extensionSupported("WGL_ARB_create_context");
            wgl.ARB_create_context_profile =
                extensionSupported("WGL_ARB_create_context_profile");
            wgl.EXT_create_context_es2_profile =
                extensionSupported("WGL_EXT_create_context_es2_profile");
            wgl.ARB_create_context_robustness =
                extensionSupported("WGL_ARB_create_context_robustness");
            wgl.ARB_create_context_no_error =
                extensionSupported("WGL_ARB_create_context_no_error");
            wgl.EXT_swap_control =
                extensionSupported("WGL_EXT_swap_control");
            wgl.EXT_colorspace =
                extensionSupported("WGL_EXT_colorspace");
            wgl.ARB_pixel_format =
                extensionSupported("WGL_ARB_pixel_format");
            wgl.ARB_context_flush_control =
                extensionSupported("WGL_ARB_context_flush_control");
        } finally {
            if (hglrc != NULL) {
                wglMakeCurrent(null, NULL, NULL);
                wglDeleteContext(null, hglrc);
            }

            if (hwnd != NULL) {
                DestroyWindow(null, hwnd);
            }

            if (classAtom != 0) {
                nUnregisterClass(NULL, classAtom & 0xFFFF, WindowsLibrary.HINSTANCE);
            }
        }
    }
    
    @Override
    public void create() throws AWTException {        
        GLPlatformConfig ctxconfig = gldata.getPlatformConfig();
        GLFBConfig fbconfig = gldata.getFBConfig();
        
        IntBuffer attribs = BufferUtils.createIntBuffer(40);
        long share = NULL;
        
        if (ctxconfig.share != NULL)
            share = ctxconfig.share;
        
        dc = GetDC(window.getHWND());
        if (dc == NULL)
        {
            throw new AWTException("WGL: Failed to retrieve DC for window");
        }
        
        GLFBConfig pixelFormat = choosePixelFormatWGL(ctxconfig, fbconfig);
        if (pixelFormat == null) {
            throw new AWTException("WGL: PilexFormat");
        }
        
        try (MemoryStack stack = stackPush()) {
            PIXELFORMATDESCRIPTOR pfd = PIXELFORMATDESCRIPTOR.calloc(stack);
            
            if (!BOOL(DescribePixelFormat(null, dc,
                             (int) pixelFormat.handle, pfd))) {
                throw new AWTException("WGL: Failed to retrieve PFD for selected pixel format");
            }            
            if (!SetPixelFormat(null, dc, (int) pixelFormat.handle, pfd)) {
                throw new AWTException("WGL: Failed to set selected pixel format");
            }
        }
        
        
        if (ctxconfig.client == AWT_OPENGL_API) {
            if (ctxconfig.forward) {
                if (!wgl.ARB_create_context) {
                    throw new AWTException("WGL: A forward compatible OpenGL context requested but WGL_ARB_create_context is unavailable");
                }
            }

            if (BOOL(ctxconfig.profile)) {
                if (!wgl.ARB_create_context_profile) {
                    throw new AWTException("WGL: OpenGL profile requested but WGL_ARB_create_context_profile is unavailable");
                }
            }
        } else {
            if (!wgl.ARB_create_context
                    || !wgl.ARB_create_context_profile
                    || !wgl.EXT_create_context_es2_profile) {
                throw new AWTException("WGL: OpenGL ES requested but WGL_ARB_create_context_es2_profile is unavailable");
            }
        }

        if (wgl.ARB_create_context)
        {
            int mask = 0, flags = 0;

            if (ctxconfig.client == AWT_OPENGL_API)
            {
                if (ctxconfig.forward)
                    flags |= WGL_CONTEXT_FORWARD_COMPATIBLE_BIT_ARB;

                if (ctxconfig.profile == AWT_OPENGL_CORE_PROFILE)
                    mask |= WGL_CONTEXT_CORE_PROFILE_BIT_ARB;
                else if (ctxconfig.profile == AWT_OPENGL_COMPAT_PROFILE)
                    mask |= WGL_CONTEXT_COMPATIBILITY_PROFILE_BIT_ARB;
            }
            else
                mask |= WGL_CONTEXT_ES2_PROFILE_BIT_EXT;

            if (ctxconfig.debug)
                flags |= WGL_CONTEXT_DEBUG_BIT_ARB;

            if (BOOL(ctxconfig.robustness))
            {
                if (wgl.ARB_create_context_robustness)
                {
                    if (ctxconfig.robustness == AWT_NO_RESET_NOTIFICATION)
                    {
                        attribs.put(WGL_CONTEXT_RESET_NOTIFICATION_STRATEGY_ARB)
                               .put(WGL_NO_RESET_NOTIFICATION_ARB);
                    }
                    else if (ctxconfig.robustness == AWT_LOSE_CONTEXT_ON_RESET)
                    {
                        attribs.put(WGL_CONTEXT_RESET_NOTIFICATION_STRATEGY_ARB)
                               .put(WGL_LOSE_CONTEXT_ON_RESET_ARB);
                    }

                    flags |= WGL_CONTEXT_ROBUST_ACCESS_BIT_ARB;
                }
            }

            if (BOOL(ctxconfig.release))
            {
                if (wgl.ARB_context_flush_control)
                {
                    if (ctxconfig.release == AWT_RELEASE_BEHAVIOR_NONE)
                    {
                        attribs.put(WGL_CONTEXT_RELEASE_BEHAVIOR_ARB)
                               .put(WGL_CONTEXT_RELEASE_BEHAVIOR_NONE_ARB);
                    }
                    else if (ctxconfig.release == AWT_RELEASE_BEHAVIOR_FLUSH)
                    {
                        attribs.put(WGL_CONTEXT_RELEASE_BEHAVIOR_ARB)
                               .put(WGL_CONTEXT_RELEASE_BEHAVIOR_FLUSH_ARB);
                    }
                }
            }

            if (ctxconfig.noerror)
            {
                if (wgl.ARB_create_context_no_error)
                    attribs.put(WGL_CONTEXT_OPENGL_NO_ERROR_ARB).put(GL_TRUE);
            }

            // NOTE: Only request an explicitly versioned context when necessary, as
            //       explicitly requesting version 1.0 does not always return the
            //       highest version supported by the driver
            if (ctxconfig.major != 1 || ctxconfig.minor != 0)
            {
                attribs.put(WGL_CONTEXT_MAJOR_VERSION_ARB).put(ctxconfig.major);
                attribs.put(WGL_CONTEXT_MINOR_VERSION_ARB).put(ctxconfig.minor);
            }

            if (BOOL(flags))
                attribs.put(WGL_CONTEXT_FLAGS_ARB).put(flags);

            if (BOOL(mask))
                attribs.put(WGL_CONTEXT_PROFILE_MASK_ARB).put(mask);

            attribs.put(0).put(0);

            context =
                wglCreateContextAttribsARB(dc, share, attribs);
            if (context == NULL)
            {
                final int error = GetLastError();

                if (error == (0xc0070000 | ERROR_INVALID_VERSION_ARB)) {
                    if (ctxconfig.client == AWT_OPENGL_API) {
                        throw new AWTException("WGL: Driver does not support OpenGL version %i.%i"
                                .formatted(ctxconfig.major,
                                        ctxconfig.minor));
                    } else {
                        throw new AWTException("WGL: Driver does not support OpenGL ES version %i.%i"
                                .formatted(ctxconfig.major,
                                        ctxconfig.minor));
                    }
                } else if (error == (0xc0070000 | ERROR_INVALID_PROFILE_ARB)) {
                    throw new AWTException("WGL: Driver does not support the requested OpenGL profile");
                } else if (error == (0xc0070000 | ERROR_INCOMPATIBLE_DEVICE_CONTEXTS_ARB)) {
                    throw new AWTException("WGL: The share context is not compatible with the requested context");
                } else {
                    if (ctxconfig.client == AWT_OPENGL_API) {
                        throw new AWTException("WGL: Failed to create OpenGL context");
                    } else {
                        throw new AWTException("WGL: Failed to create OpenGL ES context");
                    }
                }
            }
        }
        else
        {
            context = wglCreateContext(null, dc);
            if (context == NULL) {
                throw new AWTException("WGL: Failed to create OpenGL context");
            }

            if (share != NULL) {
                if (!wglShareLists(null, share, context)) {
                    throw new AWTException("WGL: Failed to enable sharing with specified OpenGL context");
                }
            }
        }
    }

    @Override
    public void makeCurrent() {
        if (!wglMakeCurrent(null, dc, context)) {
            throw new IllegalStateException("WGL: Failed to make context current");
        }
    }

    @Override
    public void releaseContext() {
        if (!wglMakeCurrent(null, NULL, NULL)) {
            throw new IllegalStateException("WGL: Failed to clear current context");
        }
    }

    @Override
    public void swapBuffers() {
        SwapBuffers(null, dc);
    }

    @Override
    public void swapInterval(int interval) {
        if (wgl.EXT_swap_control)
            wglSwapIntervalEXT(interval);
    }

    @Override
    public boolean extensionSupported(String extension) {
        String extensions = null;
        
        if (BOOL(Win32Functions.GetExtensionsStringARB)) {
            extensions = wglGetExtensionsStringARB(wglGetCurrentDC());
        } else if (BOOL(Win32Functions.GetExtensionsStringEXT)) {
            extensions = wglGetExtensionsStringEXT();
        }
        
        if (extensions == null || extensions.isEmpty()) {
            return false;
        }
        return glStringInExtensionString(extension, extensions);
    }

    @Override
    public long getProcAddress(String procname) {
        long proc = wglGetProcAddress(null, procname);
        if (proc != NULL) {
            return proc;
        }
        return apiGetFunctionAddress(GL.getFunctionProvider(), procname);
    }

    @Override
    public long getCurrentContext() {
        return wglGetCurrentContext(null);
    }

    @Override
    public long getContext() {
        return context;
    }

    @Override
    public boolean isCurrent() {
        return context == getCurrentContext();
    }

    @Override
    public void delete() {
        if (context != NULL) {
            wglDeleteContext(null, context);
            context = NULL;
        }
        if (dc != NULL) {
            ReleaseDC(window.getHWND(), dc);
        }
    }
}
