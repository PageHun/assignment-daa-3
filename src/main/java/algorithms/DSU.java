package algorithms;

import metrics.Metrics;

public class DSU {
    private final int[] parent;
    private final int[] rank;
    private int componentsCount;
    private final Metrics metrics;

    public DSU(int n, Metrics metrics) {
        this.metrics = metrics;
        this.parent = new int[n];
        this.rank = new int[n];
        this.componentsCount = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int i) {
        metrics.incrementDsuOps();
        int root = i;
        while (root != parent[root]) {
            root = parent[root];
        }
        // Сжатие пути
        int curr = i;
        while (curr != root) {
            int nxt = parent[curr];
            parent[curr] = root;
            curr = nxt;
        }
        return root;
    }

    public boolean union(int i, int j) {
        metrics.incrementDsuOps();
        int rootI = find(i);
        int rootJ = find(j);
        if (rootI != rootJ) {
            if (rank[rootI] < rank[rootJ]) {
                parent[rootI] = rootJ;
            } else if (rank[rootI] > rank[rootJ]) {
                parent[rootJ] = rootI;
            } else {
                parent[rootJ] = rootI;
                rank[rootI]++;
            }
            componentsCount--;
            return true;
        }
        return false;
    }

    public int getComponentsCount() {
        return componentsCount;
    }
}