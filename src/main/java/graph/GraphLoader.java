package graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class GraphLoader {
    public static Graph loadFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line == null) throw new IllegalArgumentException("File is empty");

            Scanner scanner = new Scanner(line);
            int n = scanner.nextInt();
            int m = scanner.nextInt();
            boolean isDirected = scanner.hasNextBoolean() ? scanner.nextBoolean() : true;
            scanner.close();

            Graph graph = new Graph(n, isDirected);
            for (int i = 0; i < m; i++) {
                line = reader.readLine();
                if (line == null) break;
                Scanner edgeScanner = new Scanner(line);
                int u = edgeScanner.nextInt();
                int v = edgeScanner.nextInt();
                int w = edgeScanner.nextInt();
                graph.addEdge(u, v, w);
                edgeScanner.close();
            }
            return graph;
        }
    }
}