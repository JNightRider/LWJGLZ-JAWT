package org.lwjgl.awt;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.jawt.AWTGLCanvas;
import org.lwjgl.system.Configuration;

public class Main {
    public static void main(String[] args) {
        Configuration.OPENGL_CONTEXT_API.set("native");
        
        AWTGLCanvas canvas = new AWTGLCanvas(){
            @Override
            protected void initGL() {
                GL.createCapabilities();
                glClearColor(0.3f, 0.4f, 0.5f, 1);
            }

            @Override
            protected void paintGL() {
                int w = getWidth();
                int h = getHeight();
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
            }
        };
        canvas.setSize(640, 480);
        canvas.setIgnoreRepaint(true);

        JPanel options = new JPanel();
        options.setLayout(new BorderLayout());
        
        JFrame frame = new JFrame("JAWT Demo");

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                canvas.destroy();
            }
        });

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE && e.getID() == KeyEvent.KEY_PRESSED) {
                frame.dispose();
                return true;
            }

            return false;
        });

        
        JButton add = new JButton("add");
        add.addActionListener((ActionEvent ae) -> {
            frame.add(canvas, BorderLayout.CENTER);
            canvas.validate();
            runGL(canvas);
        });
        JButton remove = new JButton("remove");
        remove.addActionListener((ActionEvent ae) -> {
            canvas.getContext().delete();
            frame.remove(canvas);
            canvas.getWindows().dispose();
            
            canvas.setContext(null);
            canvas.setWindows(null);
        });
        
        
        options.add(add, BorderLayout.LINE_START);
        options.add(remove, BorderLayout.LINE_END);
        
        frame.setLayout(new BorderLayout());
        frame.add(canvas, BorderLayout.CENTER);
        frame.add(options, BorderLayout.SOUTH);

        frame.pack();
        
        EventQueue.invokeLater(() -> {
            frame.setVisible(true);
            
            runGL(canvas);
        });
    }
    
    private static void runGL(final AWTGLCanvas canvas) {
        Runnable renderLoop = new Runnable() {
            @Override
            public void run() {
                System.out.println(canvas.isValid());
                
                if (!canvas.isValid()) {
                    GL.setCapabilities(null);
                    return;
                }
                canvas.render();
                SwingUtilities.invokeLater(this);
            }
        };
        SwingUtilities.invokeLater(renderLoop);
        System.out.println("org.lwjgl.awt.Main.runGL()");
    }
}
