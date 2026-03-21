/*
 * Copyright lwjglz-jawt. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import java.awt.AWTException;

import static org.lwjgl.opengl.CGL.*;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.macosx.CoreFoundation.*;

/**
 *
 * @author wil
 */
public class NSGLContext implements GLContext {
    
    /** Contains the function pointers loaded from {@code GL.getFunctionProvider()}. */
    public static final class MacosXFunctions {
        /** private constructor */
        private MacosXFunctions() {}
        
        /** Function address. */
        public static final long
                framework          = CFBundleGetBundleWithIdentifier(CFSTR("com.apple.opengl"));
        
        public static long CFSTR(String value) {            
            return CFStringCreateWithCString(NULL, memASCII(value), kCFStringEncodingUTF8);
        }
    }
    
    private CocoaWindow window;
    
    private GLData gldata;
    private long context;

    public NSGLContext(CocoaWindow window, GLData gldata) {
        this.window = window;
        this.gldata = gldata;
    }
    
    @Override
    public void initGL() throws AWTException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void create() throws AWTException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void makeCurrent() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void releaseCurrent() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void swapBuffers() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void swapInterval(int interval) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean extensionSupported(String extension) {
        // There are no NSGL extensions
        return false;
    }

    @Override
    public long getProcAddress(String procname) {
        long symbolName = CFStringCreateWithCString(kCFAllocatorDefault, 
                memASCII(procname), 
                kCFStringEncodingASCII);

        long symbol = CFBundleGetFunctionPointerForName(MacosXFunctions.framework, symbolName);
        
        CFRelease(symbolName);
        return symbol;
    }

    @Override
    public long getCurrentContext() {
        return CGLGetCurrentContext();
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
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
