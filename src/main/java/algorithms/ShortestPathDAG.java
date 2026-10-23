package algorithms;

import graph.Graph;
import metrics.Metrics;

import java.util.Arrays;
import java.util.List;

public class ShortestPathDAG {

    public static class Result {
        public final long[] distances;
        public final int[] parent;
        public final long sumDistances;

        public Result(long[] distances, int[] parent, long sumDistances) {
            this.distances = distances;
            this.parent = parent;
            this.sumDistances = sumDistances;
        }
    }

    public static Result compute(Graph graph, List<Integer> topoOrder, Metrics metrics) {
        int n = graph.size();
        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Long.MAX_VALUE / 2);
        Arrays.fill(parent, -1);

        int[] inDegree = new int[n];
        for (Graph.Edge edge : graph.getAllEdges()) {
            inDegree[edge.v]++;
        }

        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                dist[i] = 0;
            }
        }

        for (int u : topoOrder) {
            if (dist[u] != Long.MAX_VALUE / 2) {
                for (Graph.Edge edge : graph.getAdj().get(u)) {
                    metrics.incrementEdgeScans();
                    metrics.incrementComparisons();
                    if (dist[u] + edge.w < dist[edge.v]) {
                        dist[edge.v] = dist[u] + edge.w;
                        parent[edge.v] = u;
                    }
                }
            }
        }

        long sumDistances = 0;
        for (int i = 0; i < n; i++) {
            if (dist[i] < Long.MAX_VALUE / 2) {
                sumDistances += dist[i];
            }
        }

        return new Result(dist, parent, sumDistances);
    }
}