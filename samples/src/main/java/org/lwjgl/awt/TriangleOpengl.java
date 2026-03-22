/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.lwjgl.awt;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import org.lwjgl.opengl.jawt.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.opengl.jawt.AWTGL.GLProfile.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 *
 * @author wil
 */
public class TriangleOpengl {
    
    /** The conversion factor from nano to base */
    public static final float NANO_TO_BASE = 1.0e9f;
        
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

    private final String fragment_shader_text = 
            """
              #version 330
              in vec3 color;
              out vec4 fragment;
              void main()
              {
                  fragment = vec4(color, 1.0);
              }
            """;
    
    private final float[] vertices = {
        // Positions         // Colors
        -0.6f, -0.4f,   0.0f, 1.0f, 0.0f, 0.0f,
        0.6f, -0.4f,    0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.6f,     0.0f, 0.0f, 0.0f, 1.0f
    };
        
    private int vertex_buffer;
    private int vertex_array;
    
    private int program;
    private int mvp_location;
    
    private AWTGLCanvas canvas;
    long lastTime = System.nanoTime();    
   
    public void init() {
        GLCapabilities caps = GL.createCapabilities();
        if (!caps.OpenGL33) {
            throw new IllegalStateException("This demo requires OpenGL 3.3 or higher.");
        }

        System.out.println("GL_VENDOR: " + glGetString(GL_VENDOR));
        System.out.println("GL_RENDERER: " + glGetString(GL_RENDERER));
        System.out.println("GL_VERSION: " + glGetString(GL_VERSION));

        vertex_buffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertex_buffer);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        int vertex_shader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertex_shader, vertex_shader_text);
        glCompileShader(vertex_shader);

        int fragment_shader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragment_shader, fragment_shader_text);
        glCompileShader(fragment_shader);

        program = glCreateProgram();
        glAttachShader(program, vertex_shader);
        glAttachShader(program, fragment_shader);
        glLinkProgram(program);
        glUseProgram(program);

        mvp_location = glGetUniformLocation(program, "MVP");
        int vpos_location = glGetAttribLocation(program, "vPos");
        int vcol_location = glGetAttribLocation(program, "vCol");

        vertex_array = glGenVertexArrays();
        glBindVertexArray(vertex_array);
        glEnableVertexAttribArray(vpos_location);
        glVertexAttribPointer(vpos_location, 2, GL_FLOAT, false,
                6 * Float.BYTES, 0);
        glEnableVertexAttribArray(vcol_location);
        glVertexAttribPointer(vcol_location, 3, GL_FLOAT, false,
                              6 * Float.BYTES, 3 * Float.BYTES);
    }

    public void loop() {
        
        int width  = canvas.getFramebufferWidth(),
            height = canvas.getFramebufferHeight();

        float ratio = (float)width / (float) height;

        glViewport(0, 0, width, height);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        
        Matrix4f m   = new Matrix4f(),
                 p   = new Matrix4f(),
                 mvp = new Matrix4f();

        m.identity();
        
        long time = System.nanoTime();
        double seconds = (time - lastTime) / 1_000_000_000.0;      
        m.rotateZ((float) seconds);
        
        p.ortho(-ratio, ratio, -1.f, 1.f, 1.f, -1.f);
        mvp.set(p).mul(m);
        
        glUseProgram(program);
        try (MemoryStack stack = stackPush()) {
            FloatBuffer ptrMVP = stack.mallocFloat(4 * 4);
            mvp.get(ptrMVP);

            glUniformMatrix4fv(mvp_location, false, ptrMVP);
            glBindVertexArray(vertex_array);
            glDrawArrays(GL_TRIANGLES, 0, 3);
        }
    }
    
    void run() {
        {
            GLFBDescriptorBuilder fbbuilder   = GLFBDescriptor.builder();
            GLCXTDescriptorBuilder ctxbuilder = GLCXTDescriptor.builder();
            ctxbuilder.profile(CORE_PROFILE)
                      .major(3)
                      .minor(3);

            GLFBDescriptor frameBuffer = fbbuilder.build();
            GLCXTDescriptor cxtconfig = ctxbuilder.build();
            
            canvas = new AWTGLCanvas(new GLData(cxtconfig, frameBuffer)) {
                @Override
                protected void initGL() {
                    setSwapInterval(1);
                    init();
                }
                @Override
                protected void paintGL() {
                    loop();
                }
            };
            canvas.setIgnoreRepaint(true);
        }
        {
            final JFrame window = new JFrame();
            window.setTitle("OpenGL Triangle");
            window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            window.setLayout(new BorderLayout());
            window.setPreferredSize(new Dimension(640, 480));
            
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    canvas.destroy();
                }
            });
            
            window.add(canvas);            
            window.pack();
            
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE && e.getID() == KeyEvent.KEY_PRESSED) {
                window.dispose();
                return true;
            }

            return false;
        });
            
            EventQueue.invokeLater(() -> {
                window.setVisible(true);
                window.transferFocus();
                
                Runnable renderLoop = new Runnable() {
                    @Override
                    public void run() {
                        if (!canvas.isValid()) {
                            GL.setCapabilities(null);
                            return;
                        }
                        canvas.render();
                        SwingUtilities.invokeLater(this);
                    }
                };
                SwingUtilities.invokeLater(renderLoop);
            });
        }
    }
    
    public static void main(String[] args) {
        /*Configuration.OPENGL_CONTEXT_API.set("native");*/
        new TriangleOpengl().run();
    }

}
