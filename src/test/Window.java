package test;

import javax.swing.*;

public class Window extends JFrame {

    private GameScreen screen;

    public Window() {
        super("Physics Engine Demo in Java");
        screen = new GameScreen();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(screen);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new Window().setVisible(true);
    }
}
