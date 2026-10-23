import algorithms.*;
import graph.Graph;
import metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTests {

    @Test
    void testDSUOperations() {
        Metrics metrics = new Metrics();
        DSU dsu = new DSU(5, metrics);

        assertEquals(5, dsu.getComponentsCount());
        dsu.union(0, 1);
        dsu.union(1, 2);
        assertEquals(3, dsu.getComponentsCount());
        assertEquals(dsu.find(0), dsu.find(2));
        assertNotEquals(dsu.find(0), dsu.find(3));
    }

    @Test
    void testMSTAgreement() {
        for (int i = 0; i < 100; i++) {
            Graph g = generateRandomConnected(50, 150);
            Metrics m1 = new Metrics();
            Metrics m2 = new Metrics();

            MST.Result kruskalRes = MST.kruskal(g, m1);
            MST.Result primRes = MST.prim(g, m2);

            assertEquals(kruskalRes.totalWeight, primRes.totalWeight, "The weight of the MST must be the same for both Kruskal’s and Prim’s algorithms!");
        }
    }

    @Test
    void testKahnCycleDetection() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1); // Цикл

        Metrics metrics = new Metrics();
        assertThrows(IllegalStateException.class, () -> SCC_Topo.kahn(g, metrics), "Kahn must detect a cycle!");
    }

    @Test
    void testStackSafetyKosaraju() {
        int n = 100000;
        Graph g = new Graph(n, true);
        for (int i = 0; i < n - 1; i++) {
            g.addEdge(i, i + 1, 1);
        }

        Metrics metrics = new Metrics();
        List<List<Integer>> sccs = SCC_Topo.kosaraju(g, metrics);
        assertEquals(n, sccs.size(), "Each vertex on the path must be a separate strongly connected component.");
        assertTrue(metrics.getMaxDepth() > 0, "The stack depth should be fixed.");
    }

    @Test
    void testDAGShortestPathNegativeWeights() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 5);
        g.addEdge(0, 2, 3);
        g.addEdge(1, 3, -2);
        g.addEdge(2, 3, 7);

        Metrics metrics = new Metrics();
        List<Integer> topo = SCC_Topo.kahn(g, metrics);
        ShortestPathDAG.Result res = ShortestPathDAG.compute(g, topo, metrics);

        assertEquals(0, res.distances[0]);
        assertEquals(5, res.distances[1]);
        assertEquals(3, res.distances[2]);
        assertEquals(3, res.distances[3]);
    }

    private Graph generateRandomConnected(int n, int m) {
        Graph g = new Graph(n, false);
        for (int i = 0; i < n - 1; i++) {
            g.addEdge(i, i + 1, 1 + (int)(Math.random() * 100));
        }
        for (int i = n - 1; i < m; i++) {
            int u = (int)(Math.random() * n);
            int v = (int)(Math.random() * n);
            if (u != v) {
                g.addEdge(u, v, 1 + (int)(Math.random() * 100));
            }
        }
        return g;
    }
}