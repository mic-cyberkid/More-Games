package com.aetheria.ecs.systems;

import com.aetheria.ecs.World;
import com.aetheria.ecs.components.CollisionComponent;
import com.aetheria.ecs.components.TransformComponent;
import com.aetheria.util.Rect;
import com.aetheria.world.WorldMap;
import com.aetheria.world.ObjectLayer;
import com.aetheria.core.event.EventBus;
import com.aetheria.core.event.events.MapTransitionEvent;

public final class CollisionSystem {

    public void resolve(World world, int entityId, float dx, float dy, WorldMap map) {
        var transformMapper = world.getMapper(TransformComponent.class);
        var collisionMapper = world.getMapper(CollisionComponent.class);

        TransformComponent tc = transformMapper.get(entityId);
        CollisionComponent cc = collisionMapper.get(entityId);

        if (tc == null || cc == null) return;

        // X Axis
        tc.x += dx;
        if (isColliding(tc.x, tc.y, cc.bounds, map)) {
            if (dx > 0) tc.x = (float) (Math.floor((tc.x + cc.bounds.x() + cc.bounds.w()) / 16) * 16 - cc.bounds.x() - cc.bounds.w() - 0.01f);
            if (dx < 0) tc.x = (float) (Math.ceil((tc.x + cc.bounds.x()) / 16) * 16 - cc.bounds.x() + 0.01f);
        }

        // Y Axis
        tc.y += dy;
        if (isColliding(tc.x, tc.y, cc.bounds, map)) {
            if (dy > 0) tc.y = (float) (Math.floor((tc.y + cc.bounds.y() + cc.bounds.h()) / 16) * 16 - cc.bounds.y() - cc.bounds.h() - 0.01f);
            if (dy < 0) tc.y = (float) (Math.ceil((tc.y + cc.bounds.y()) / 16) * 16 - cc.bounds.y() + 0.01f);
        }

        checkTriggers(tc.x, tc.y, cc.bounds, map);
    }

    private void checkTriggers(float x, float y, Rect bounds, WorldMap map) {
        Rect playerRect = new Rect(x + bounds.x(), y + bounds.y(), bounds.w(), bounds.h());
        for (ObjectLayer.MapTrigger trigger : map.getObjectLayer().getTriggers()) {
            if (playerRect.intersects(trigger.bounds())) {
                EventBus.get().post(new MapTransitionEvent(trigger.transition().targetMapId()));
                break;
            }
        }
    }

    private boolean isColliding(float x, float y, Rect bounds, WorldMap map) {
        float left = x + bounds.x();
        float right = x + bounds.x() + bounds.w();
        float top = y + bounds.y();
        float bottom = y + bounds.y() + bounds.h();

        int startX = (int) Math.floor(left / 16);
        int endX = (int) Math.floor(right / 16);
        int startY = (int) Math.floor(top / 16);
        int endY = (int) Math.floor(bottom / 16);

        for (int ty = startY; ty <= endY; ty++) {
            for (int tx = startX; tx <= endX; tx++) {
                if (map.isSolid(tx, ty)) return true;
            }
        }
        return false;
    }
}
