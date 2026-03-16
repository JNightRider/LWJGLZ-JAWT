/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.awt;

import java.awt.*;
import javax.swing.*;

//import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.opengl.awt.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.*;
import java.nio.*;
import java.util.*;


import static org.lwjgl.opengl.awt.AWTGL.*;
import static org.lwjgl.opengl.awt.GLData.*;

//import static org.joml.Math.*;
//import static org.lwjgl.demo.util.IOUtil.*;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 *
 * @author wil
 */
public class TriangleOpengl {
    
    private final String vertex_shader_text =
            """
            #version 330
            uniform mat4 MVP;
            in vec3 vCol;
            in vec2 vPos;
            out vec3 color;
            void main()
            {
                gl_Position = MVP * vec4(vPos, 0.0, 1.0);
                color = vCol;
            }
            """;

    private final String fragment_shader_text
            = """
              #version 330
              in vec3 color;
              out vec4 fragment;
              void main()
              {
                  fragment = vec4(color, 1.0);
              }
              """;
    
    private void init() {
        GLCapabilities caps = GL.createCapabilities();
        if (!caps.OpenGL33) {
            throw new IllegalStateException("This demo requires OpenGL 3.3 or higher.");
        }

        System.err.println("GL_VENDOR: " + glGetString(GL_VENDOR));
        System.err.println("GL_RENDERER: " + glGetString(GL_RENDERER));
        System.err.println("GL_VERSION: " + glGetString(GL_VERSION));
    }
    
    private void loop() {
        
    }
    
    void run() {
        GLData gldata = new GLData();
        gldata.glHint(GLDATA_CONTEXT_VERSION_MAJOR, 3);
        gldata.glHint(GLDATA_CONTEXT_VERSION_MINOR, 3);
        gldata.glHint(GLDATA_OPENGL_PROFILE, AWT_OPENGL_CORE_PROFILE);        
        AWTGLCanvas canvas = new AWTGLCanvas(gldata) {
            @Override
            protected void initGL() {
                init();
            }
            @Override
            protected void paintGL() {
                loop();
                swapBuffers();
            }
        };
        
        {
            JFrame window = new JFrame();
            window.setTitle("OpenGL Triangle");
            window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            window.setLayout(new BorderLayout());
            window.setPreferredSize(new Dimension(640, 480));
            
            window.add(canvas);
            
            window.pack();
            window.setVisible(true);
            window.transferFocus();
        }
    }
    
    public static void main(String[] args) {
        new TriangleOpengl().run();
    }

}
