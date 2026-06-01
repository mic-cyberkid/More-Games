package com.aetheria.world.pathfinding;

import java.util.*;

public final class Pathfinder {

    private record Node(int x, int y, int g, int h, Node parent) implements Comparable<Node> {
        public int f() { return g + h; }
        @Override public int compareTo(Node o) { return Integer.compare(this.f(), o.f()); }
    }

    public List<int[]> findPath(NavGrid grid, int startX, int startY, int endX, int endY) {
        PriorityQueue<Node> open = new PriorityQueue<>();
        Set<Integer> closed = new HashSet<>();

        open.add(new Node(startX, startY, 0, heuristic(startX, startY, endX, endY), null));

        while (!open.isEmpty()) {
            Node current = open.poll();

            if (current.x == endX && current.y == endY) {
                return reconstructPath(current);
            }

            int hash = current.y * grid.getWidth() + current.x;
            if (closed.contains(hash)) continue;
            closed.add(hash);

            for (int[] dir : DIRECTIONS) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                if (grid.isWalkable(nx, ny)) {
                    open.add(new Node(nx, ny, current.g + 1, heuristic(nx, ny, endX, endY), current));
                }
            }
        }

        return Collections.emptyList();
    }

    private int heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private List<int[]> reconstructPath(Node node) {
        List<int[]> path = new ArrayList<>();
        while (node != null) {
            path.add(0, new int[]{node.x, node.y});
            node = node.parent;
        }
        return path;
    }

    private static final int[][] DIRECTIONS = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
}
