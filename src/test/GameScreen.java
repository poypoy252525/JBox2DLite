package test;

import box2dlite.Body;
import box2dlite.World;
import box2dlite.mathutils.Vec2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameScreen extends JPanel implements Runnable {

    float speed = 1;

    public static final int width = 700, height = 500;
    private Thread thread;

    World world;
    Body body = null;

    public GameScreen() {
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.black);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Body body = new Body();
                body.set(new Vec2(35, 35), 10);
                body.position.set(e.getX(), e.getY());
                world.add(body);
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                //body.position.set(e.getX(), e.getY());
            }
        });
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    body.rotation += 0.1f;
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    body.rotation -= 0.1f;
                }
            }
        });
        create();
    }

    public void demo5(Body b)
    {
        b = new Body();
        b.set(new Vec2(width + 2, 20.0f), Float.MAX_VALUE);
        b.friction = 0.2f;
        b.position.set(width / 2.0f, height);
        b.rotation = 0.0f;
        world.add(b);

        int num = 10;

        Vec2 x = new Vec2(width * 0.5f - (num * 38.0f) * 0.5f, height - 100);
        Vec2 y;

        for (int i = 0; i < num; ++i)
        {
            y = new Vec2(x.x, x.y);

            for (int j = i; j < num; ++j)
            {
                b = new Body();
                b.set(new Vec2(35.0f, 35.0f), 10.0f);
                b.friction = 0.2f;
                b.position.set(y.x, y.y);
                world.add(b);
                y.add(new Vec2(38.0f, 0.0f));
            }
            x.add(new Vec2(17.5f, -50.0f));
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
        }
        thread.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        world.render(g);
    }

    public void create() {
        world = new World(new Vec2(0, 30), 10);

        demo5(body);
    }

    @Override
    public void run() {
        long last = System.nanoTime();
        double accumulator = 0;
        while (thread != null) {
            double inv_dt = 1000000000.0 / (100.0f);
            long current = System.nanoTime();
            accumulator += (current - last);
            last = current;
            while (accumulator >= inv_dt) {
                world.step(1.0f / 30.0f);
                accumulator -= inv_dt;
            }
            repaint();
        }
    }
}
