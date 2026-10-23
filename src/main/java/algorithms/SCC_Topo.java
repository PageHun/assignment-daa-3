package algorithms;

import graph.Graph;
import metrics.Metrics;

import java.util.*;

public class SCC_Topo {

    public static List<List<Integer>> kosaraju(Graph graph, Metrics metrics) {
        int n = graph.size();
        boolean[] visited = new boolean[n];
        ArrayDeque<Integer> finishOrder = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                iterativeDfsFirst(graph, i, visited, finishOrder, metrics);
            }
        }

        Graph revGraph = new Graph(n, true);
        for (Graph.Edge edge : graph.getAllEdges()) {
            revGraph.addEdge(edge.v, edge.u, edge.w);
        }

        Arrays.fill(visited, false);
        List<List<Integer>> sccs = new ArrayList<>();

        while (!finishOrder.isEmpty()) {
            int u = finishOrder.pop();
            if (!visited[u]) {
                List<Integer> component = new ArrayList<>();
                iterativeDfsSecond(revGraph, u, visited, component, metrics);
                sccs.add(component);
            }
        }
        return sccs;
    }

    private static void iterativeDfsFirst(Graph graph, int start, boolean[] visited, ArrayDeque<Integer> finishOrder, Metrics metrics) {
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        ArrayDeque<Integer> edgeIndexStack = new ArrayDeque<>();

        stack.push(start);
        edgeIndexStack.push(0);
        visited[start] = true;
        metrics.updateMaxDepth(stack.size());

        while (!stack.isEmpty()) {
            int u = stack.peek();
            int edgeIdx = edgeIndexStack.pop();
            List<Graph.Edge> neighbors = graph.getAdj().get(u);

            if (edgeIdx < neighbors.size()) {
                edgeIndexStack.push(edgeIdx + 1);
                Graph.Edge edge = neighbors.get(edgeIdx);
                metrics.incrementEdgeScans();
                int v = edge.v;
                if (!visited[v]) {
                    visited[v] = true;
                    stack.push(v);
                    edgeIndexStack.push(0);
                    metrics.updateMaxDepth(stack.size());
                }
            } else {
                stack.pop();
                finishOrder.push(u);
            }
        }
    }

    private static void iterativeDfsSecond(Graph graph, int start, boolean[] visited, List<Integer> component, Metrics metrics) {
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        ArrayDeque<Integer> edgeIndexStack = new ArrayDeque<>();

        stack.push(start);
        edgeIndexStack.push(0);
        visited[start] = true;
        component.add(start);
        metrics.updateMaxDepth(stack.size());

        while (!stack.isEmpty()) {
            int u = stack.peek();
            int edgeIdx = edgeIndexStack.pop();
            List<Graph.Edge> neighbors = graph.getAdj().get(u);

            if (edgeIdx < neighbors.size()) {
                edgeIndexStack.push(edgeIdx + 1);
                Graph.Edge edge = neighbors.get(edgeIdx);
                metrics.incrementEdgeScans();
                int v = edge.v;
                if (!visited[v]) {
                    visited[v] = true;
                    component.add(v);
                    stack.push(v);
                    edgeIndexStack.push(0);
                    metrics.updateMaxDepth(stack.size());
                }
            } else {
                stack.pop();
            }
        }
    }

    public static List<Integer> kahn(Graph graph, Metrics metrics) {
        int n = graph.size();
        int[] inDegree = new int[n];

        for (Graph.Edge edge : graph.getAllEdges()) {
            metrics.incrementEdgeScans();
            inDegree[edge.v]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> topoOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            topoOrder.add(u);

            for (Graph.Edge edge : graph.getAdj().get(u)) {
                metrics.incrementEdgeScans();
                inDegree[edge.v]--;
                if (inDegree[edge.v] == 0) {
                    queue.add(edge.v);
                }
            }
        }

        if (topoOrder.size() < n) {
            throw new IllegalStateException("Detected a loop in the graph!");
        }
        return topoOrder;
    }
}
