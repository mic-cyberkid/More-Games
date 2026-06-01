package com.aetheria;

import com.aetheria.core.Game;
import com.aetheria.util.Logger;
import javax.swing.*;

public final class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Echoes of Aetheria");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);

                Game game = new Game();
                frame.add(game);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                new Thread(game::start, "GameLoopThread").start();

                Logger.info(Main.class, "Aetheria Engine Initialized.");
            } catch (Exception e) {
                Logger.error(Main.class, "Critical engine failure!", e);
                System.exit(1);
            }
        });
    }
}
