package graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    public static class Edge {
        public final int u;
        public final int v;
        public final int w;

        public Edge(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
    }

    private final int n;
    private final boolean directed;
    private final List<List<Edge>> adj;
    private final List<Edge> allEdges;

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        this.allEdges = new ArrayList<>();
    }

    public void addEdge(int u, int v, int w) {
        if (u < 0 || u >= n || v < 0 || v >= n) return;
        Edge edge = new Edge(u, v, w);
        adj.get(u).add(edge);
        allEdges.add(edge);
        if (!directed) {
            Edge revEdge = new Edge(v, u, w);
            adj.get(v).add(revEdge);
        }
    }

    public int size() { return n; }
    public boolean isDirected() { return directed; }
    public List<List<Edge>> getAdj() { return adj; }
    public List<Edge> getAllEdges() { return allEdges; }
}