package com.aetheria.ui;

import com.aetheria.story.QuestLog;
import java.awt.*;

public final class HUD {
    public void render(Graphics2D g, int hp, int maxHp, QuestLog questLog) {
        // Health Bar
        g.setColor(Color.RED);
        g.fillRect(10, 10, 100, 10);
        g.setColor(Color.GREEN);
        g.fillRect(10, 10, (int)((float)hp / maxHp * 100), 10);
        g.setColor(Color.WHITE);
        g.drawRect(10, 10, 100, 10);

        // Quest Objectives
        g.setFont(new Font("Monospaced", Font.BOLD, 10));
        int y = 35;
        for (QuestLog.Quest q : questLog.getActiveQuests()) {
            g.drawString("! " + q.title(), 10, y);
            y += 12;
        }
    }
}
