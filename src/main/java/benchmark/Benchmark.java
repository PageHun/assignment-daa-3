package benchmark;

import algorithms.*;
import graph.Graph;
import metrics.Metrics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Benchmark {

    public static void main(String[] args) {
        int[] sizes = {1000, 3162, 10000, 31623, 100000};
        double[] densityMultipliers = {4.0, 20.0};

        List<String[]> csvRows = new ArrayList<>();
        csvRows.add(new String[]{"algorithm", "graph", "n", "m", "time_ms", "dsu_ops", "edge_scans", "comparisons", "max_depth", "result"});

        for (int n : sizes) {
            for (double multiplier : densityMultipliers) {
                int m = (int) (multiplier * n);

                Graph undirGraph = generateUndirected(n, m);
                Graph dirGraph = generateDirected(n, m);
                Graph dagGraph = generateDAG(n, m);

                runAndLogMST("Kruskal", undirGraph, n, m, true, csvRows);

                runAndLogMST("Prim", undirGraph, n, m, false, csvRows);

                runAndLogSCC(dirGraph, n, m, csvRows);

                runAndLogKahn(dagGraph, n, m, csvRows);

                runAndLogDagSP(dagGraph, n, m, csvRows);
            }
        }

        writeCsv(csvRows, "results/results.csv");
        System.out.println("Benchmark completed successfully. Results saved to results/results.csv");
    }

    private static Graph generateUndirected(int n, int m) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, false);
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) perm[i] = i;

        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int temp = perm[i]; perm[i] = perm[j]; perm[j] = temp;
        }

        int edgesAdded = 0;
        for (int i = 1; i < n && edgesAdded < m; i++) {
            int a = perm[rnd.nextInt(i)];
            int b = perm[i];
            int w = 1 + rnd.nextInt(1000);
            g.addEdge(a, b, w);
            edgesAdded++;
        }

        while (edgesAdded < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (a != b) {
                int w = 1 + rnd.nextInt(1000);
                g.addEdge(a, b, w);
                edgesAdded++;
            }
        }
        return g;
    }

    private static Graph generateDirected(int n, int m) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, true);
        int edgesAdded = 0;
        while (edgesAdded < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (a != b) {
                int w = 1 + rnd.nextInt(1000);
                g.addEdge(a, b, w);
                edgesAdded++;
            }
        }
        return g;
    }

    private static Graph generateDAG(int n, int m) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, true);
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) rank[i] = i;

        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int temp = rank[i]; rank[i] = rank[j]; rank[j] = temp;
        }

        int edgesAdded = 0;
        while (edgesAdded < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (a != b) {
                if (rank[a] > rank[b]) {
                    int t = a; a = b; b = t;
                }
                int w = 1 + rnd.nextInt(1000);
                g.addEdge(a, b, w);
                edgesAdded++;
            }
        }
        return g;
    }

    private static void runAndLogMST(String algoName, Graph g, int n, int m, boolean isKruskal, List<String[]> rows) {
        Metrics metrics = new Metrics();
        long startTime = System.nanoTime();
        MST.Result res = isKruskal ? MST.kruskal(g, metrics) : MST.prim(g, metrics);
        long duration = (System.nanoTime() - startTime) / 1_000_000;

        rows.add(new String[]{
                algoName, "undirected", String.valueOf(n), String.valueOf(m),
                String.valueOf(duration), String.valueOf(metrics.getDsuOps()),
                String.valueOf(metrics.getEdgeScans()), String.valueOf(metrics.getComparisons()),
                String.valueOf(metrics.getMaxDepth()), String.valueOf(res.totalWeight)
        });
    }

    private static void runAndLogSCC(Graph g, int n, int m, List<String[]> rows) {
        Metrics metrics = new Metrics();
        long startTime = System.nanoTime();
        List<List<Integer>> sccs = SCC_Topo.kosaraju(g, metrics);
        long duration = (System.nanoTime() - startTime) / 1_000_000;

        rows.add(new String[]{
                "Kosaraju", "directed", String.valueOf(n), String.valueOf(m),
                String.valueOf(duration), String.valueOf(metrics.getDsuOps()),
                String.valueOf(metrics.getEdgeScans()), String.valueOf(metrics.getComparisons()),
                String.valueOf(metrics.getMaxDepth()), String.valueOf(sccs.size())
        });
    }

    private static void runAndLogKahn(Graph g, int n, int m, List<String[]> rows) {
        Metrics metrics = new Metrics();
        long startTime = System.nanoTime();
        List<Integer> order = SCC_Topo.kahn(g, metrics);
        long duration = (System.nanoTime() - startTime) / 1_000_000;

        rows.add(new String[]{
                "Kahn", "DAG", String.valueOf(n), String.valueOf(m),
                String.valueOf(duration), String.valueOf(metrics.getDsuOps()),
                String.valueOf(metrics.getEdgeScans()), String.valueOf(metrics.getComparisons()),
                String.valueOf(metrics.getMaxDepth()), String.valueOf(order.size())
        });
    }

    private static void runAndLogDagSP(Graph g, int n, int m, List<String[]> rows) {
        Metrics metrics = new Metrics();
        List<Integer> topoOrder = SCC_Topo.kahn(g, new Metrics());
        long startTime = System.nanoTime();
        ShortestPathDAG.Result res = ShortestPathDAG.compute(g, topoOrder, metrics);
        long duration = (System.nanoTime() - startTime) / 1_000_000;

        rows.add(new String[]{
                "DagShortestPath", "DAG", String.valueOf(n), String.valueOf(m),
                String.valueOf(duration), String.valueOf(metrics.getDsuOps()),
                String.valueOf(metrics.getEdgeScans()), String.valueOf(metrics.getComparisons()),
                String.valueOf(metrics.getMaxDepth()), String.valueOf(res.sumDistances)
        });
    }

    private static void writeCsv(List<String[]> rows, String path) {
        java.io.File file = new java.io.File(path);
        java.io.File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            for (String[] row : rows) {
                writer.append(String.join(",", row)).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}