package metrics;

public class Metrics {
    private long dsuOps = 0;
    private long edgeScans = 0;
    private long comparisons = 0;
    private int maxDepth = 0;

    public void reset() {
        dsuOps = 0;
        edgeScans = 0;
        comparisons = 0;
        maxDepth = 0;
    }

    public void incrementDsuOps() {
        dsuOps++;
    }

    public void incrementEdgeScans() {
        edgeScans++;
    }

    public void incrementComparisons() {
        comparisons++;
    }

    public void updateMaxDepth(int currentDepth) {
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
    }

    public long getDsuOps() { return dsuOps; }
    public long getEdgeScans() { return edgeScans; }
    public long getComparisons() { return comparisons; }
    public int getMaxDepth() { return maxDepth; }
}
