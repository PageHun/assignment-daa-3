package algorithms;

import graph.Graph;
import metrics.Metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class MST {

    public static class Result {
        public final long totalWeight;
        public final int components;
        public final List<Graph.Edge> edges;

        public Result(long totalWeight, int components, List<Graph.Edge> edges) {
            this.totalWeight = totalWeight;
            this.components = components;
            this.edges = edges;
        }
    }

    public static Result kruskal(Graph graph, Metrics metrics) {
        List<Graph.Edge> edges = new ArrayList<>(graph.getAllEdges());
        edges.sort((e1, e2) -> {
            metrics.incrementComparisons();
            return Integer.compare(e1.w, e2.w);
        });

        DSU dsu = new DSU(graph.size(), metrics);
        long totalWeight = 0;
        List<Graph.Edge> mstEdges = new ArrayList<>();

        for (Graph.Edge edge : edges) {
            metrics.incrementEdgeScans();
            if (dsu.find(edge.u) != dsu.find(edge.v)) {
                dsu.union(edge.u, edge.v);
                totalWeight += edge.w;
                mstEdges.add(edge);
            }
        }
        return new Result(totalWeight, dsu.getComponentsCount(), mstEdges);
    }

    public static Result prim(Graph graph, Metrics metrics) {
        int n = graph.size();
        boolean[] visited = new boolean[n];
        PriorityQueue<Graph.Edge> pq = new PriorityQueue<>((e1, e2) -> {
            metrics.incrementComparisons();
            return Integer.compare(e1.w, e2.w);
        });

        long totalWeight = 0;
        List<Graph.Edge> mstEdges = new ArrayList<>();
        int components = 0;

        for (int start = 0; start < n; start++) {
            if (!visited[start]) {
                components++;
                visited[start] = true;
                for (Graph.Edge edge : graph.getAdj().get(start)) {
                    metrics.incrementEdgeScans();
                    pq.add(edge);
                }

                while (!pq.isEmpty()) {
                    Graph.Edge edge = pq.poll();
                    if (visited[edge.v]) continue;
                    visited[edge.v] = true;
                    totalWeight += edge.w;
                    mstEdges.add(edge);

                    for (Graph.Edge nextEdge : graph.getAdj().get(edge.v)) {
                        metrics.incrementEdgeScans();
                        if (!visited[nextEdge.v]) {
                            pq.add(nextEdge);
                        }
                    }
                }
            }
        }
        return new Result(totalWeight, components, mstEdges);
    }
}