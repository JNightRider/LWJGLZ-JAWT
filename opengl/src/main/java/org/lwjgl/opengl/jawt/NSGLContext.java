/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.opengl.jawt;

import java.awt.AWTException;
import static org.lwjgl.opengl.CGL.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.system.APIUtil.apiGetFunctionAddress;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.macosx.CoreFoundation.*;

/**
 *
 * @author wil
 */
public class NSGLContext implements JAWTGLContext {
    
    public static final class Functions {
        
        private Functions() {}
        
        /** Function address. */
        public static final long
                framework          = CFBundleGetBundleWithIdentifier(CFSTR("com.apple.opengl"));
        
        public static long CFSTR(String value) {            
            return CFStringCreateWithCString(NULL, memASCII(value), kCFStringEncodingUTF8);
        }
    }
    
    private long context;

    public NSGLContext(CocoaPlatform platform) {
    }
    
    

    @Override
    public void createContext() throws AWTException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void makeContextCurrent(boolean handle) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public long getCurrentContext() {
        return CGLGetCurrentContext();
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

        long symbol = CFBundleGetFunctionPointerForName(Functions.framework, symbolName);
        
        CFRelease(symbolName);
        return symbol;
    }

    @Override
    public long getHandle() {
        return context;
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
