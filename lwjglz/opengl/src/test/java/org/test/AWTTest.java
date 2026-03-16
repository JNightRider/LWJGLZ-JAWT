/*
 * Copyright LWJGLZ. All rights reserved.
 * License terms: https://opensource.org/license/BSD-3-clause
 */
package org.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

/**
 *
 * @author wil
 */
public class AWTTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("AWT test");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setPreferredSize(new Dimension(600, 600));
        GLData data = new GLData();
        AWTGLCanvas canvas;
        frame.add(canvas = new AWTGLCanvas(data) {
            private static final long serialVersionUID = 1L;
            @Override
            public void initGL() {
                createCapabilities();
                glClearColor(0.3f, 0.4f, 0.5f, 1);
            }
            @Override
            public void paintGL() {
                int w = getFramebufferWidth();
                int h = getFramebufferHeight();
                float aspect = (float) w / h;
                double now = System.currentTimeMillis() * 0.001;
                float width = (float) Math.abs(Math.sin(now * 0.3));
                glClear(GL_COLOR_BUFFER_BIT);
                glViewport(0, 0, w, h);
                glBegin(GL_QUADS);
                glColor3f(0.4f, 0.6f, 0.8f);
                glVertex2f(-0.75f * width / aspect, 0.0f);
                glVertex2f(0, -0.75f);
                glVertex2f(+0.75f * width/ aspect, 0);
                glVertex2f(0, +0.75f);
                glEnd();
                swapBuffers();
            }
        }, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.transferFocus();
    }
}
