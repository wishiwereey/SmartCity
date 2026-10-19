import connectivity.Kosaraju;
import connectivity.TopologicalSort;
import graph.Graph;
import mst.Kruskal;
import mst.Prim;
import shortestpath.Dijkstra;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class Benchmark {
    private static final int[] VERTICES = {10, 100, 1000};
    private static final int[] EDGES = {30, 500, 5000};
    private static final int REPETITIONS = 5;
    private static final long BASE_SEED = 42L;
    public static class Row {
        public final String algorithm;
        public final int vertices;
        public final int edges;
        public final double averageTimeNs;
        public final double averageOperations;
        public Row(
                String algorithm,
                int vertices,
                int edges,
                double averageTimeNs,
                double averageOperations
        ) {
            this.algorithm = algorithm;
            this.vertices = vertices;
            this.edges = edges;
            this.averageTimeNs = averageTimeNs;
            this.averageOperations = averageOperations;
        }
    }
    public static List<Row> runAll() {
        List<Row> results = new ArrayList<>();
        warmUp();
        for (int size = 0; size < VERTICES.length; size++) {
            int v = VERTICES[size];
            int e = EDGES[size];
            System.out.println("\nRunning benchmarks: V=" + v + ", E=" + e);
            long kruskalTime = 0;
            long kruskalOperations = 0;
            long primTime = 0;
            long primOperations = 0;
            long topoTime = 0;
            long topoOperations = 0;
            long kosarajuTime = 0;
            long kosarajuOperations = 0;
            long dijkstraTime = 0;
            long dijkstraOperations = 0;
            for (int repetition = 0;
                 repetition < REPETITIONS;
                 repetition++) {
                long seed = BASE_SEED + repetition;
                Graph undirected = GraphGenerator.generateConnectedGraph(v, e, seed);
                Graph directed = GraphGenerator.generateDirectedGraph(v, e, seed);
                Graph dag = GraphGenerator.generateDAG(v, e, seed);
                Kruskal.Result kruskal = Kruskal.findMST(undirected);
                kruskalTime += kruskal.executionTime;
                kruskalOperations += kruskal.edgesExamined;
                Prim.Result prim = Prim.findMST(undirected);
                primTime += prim.executionTime;
                primOperations += prim.edgesExamined;
                if (kruskal.totalWeight != prim.totalWeight) {
                    throw new IllegalStateException("Kruskal and Prim MST weights differ!");
                }
                if (!kruskal.connected || !prim.connected) {
                    throw new IllegalStateException("Generated graph is not connected!");
                }
                TopologicalSort.Result topo = TopologicalSort.sort(dag);
                topoTime += topo.executionTime;
                topoOperations += topo.edgesExamined;
                if (topo.hasCycle) {
                    throw new IllegalStateException("Generated DAG contains a cycle!");
                }
                Kosaraju.Result kosaraju = Kosaraju.findSCC(directed);
                kosarajuTime += kosaraju.executionTime;
                kosarajuOperations += kosaraju.edgesExamined;
                int countedVertices = 0;
                for (List<Integer> component :
                        kosaraju.components) {
                    countedVertices += component.size();
                }
                if (countedVertices != v) {
                    throw new IllegalStateException("SCC vertex count is incorrect!");
                }
                Dijkstra.Result dijkstra = Dijkstra.shortestPaths(undirected, 0);
                dijkstraTime += dijkstra.executionTime;
                dijkstraOperations += dijkstra.edgesExamined;
                if (dijkstra.distances[0] != 0) {
                    throw new IllegalStateException("Dijkstra source distance is incorrect!");
                }
                System.out.println("  Repetition " + (repetition + 1) + "/" + REPETITIONS + " completed");
            }
            results.add(new Row("Kruskal", v, e, (double) kruskalTime / REPETITIONS, (double) kruskalOperations / REPETITIONS));
            results.add(new Row("Prim", v, e, (double) primTime / REPETITIONS, (double) primOperations / REPETITIONS));
            results.add(new Row("TopologicalSort", v, e, (double) topoTime / REPETITIONS, (double) topoOperations / REPETITIONS));
            results.add(new Row("Kosaraju", v, e, (double) kosarajuTime / REPETITIONS, (double) kosarajuOperations / REPETITIONS));
            results.add(new Row("Dijkstra", v, e, (double) dijkstraTime / REPETITIONS, (double) dijkstraOperations / REPETITIONS));
        }
        return results;
    }
    private static void warmUp() {
        Graph undirected = GraphGenerator.generateConnectedGraph(100, 500, BASE_SEED);
        Graph directed = GraphGenerator.generateDirectedGraph(100, 500, BASE_SEED);
        Graph dag = GraphGenerator.generateDAG(100, 500, BASE_SEED);
        for (int i = 0; i < 10; i++) {
            Kruskal.findMST(undirected);
            Prim.findMST(undirected);
            TopologicalSort.sort(dag);
            Kosaraju.findSCC(directed);
            Dijkstra.shortestPaths(undirected, 0);
        }
    }
    public static void printResults(List<Row> results) {
        System.out.println("\n=== BENCHMARK RESULTS ===");
        System.out.printf("%-18s %8s %8s %18s %18s%n", "Algorithm", "V", "E", "Avg Time (ns)", "Avg Operations");
        System.out.println("--------------------------------------------------------------------------");
        for (Row row : results) {
            System.out.printf(
                    Locale.US,
                    "%-18s %8d %8d %18.2f %18.2f%n",
                    row.algorithm,
                    row.vertices,
                    row.edges,
                    row.averageTimeNs,
                    row.averageOperations);
        }
    }
    public static void saveCSV(List<Row> results, String filename) throws IOException {
        Path path = Path.of("results", filename);
        Files.createDirectories(path.getParent());
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path))) {
            writer.println("Algorithm,Vertices,Edges,AverageTimeNs,AverageOperations");
            for (Row row : results) {
                writer.printf(
                        Locale.US,
                        "%s,%d,%d,%.2f,%.2f%n",
                        row.algorithm,
                        row.vertices,
                        row.edges,
                        row.averageTimeNs,
                        row.averageOperations);
            }
        }
        System.out.println("\nResults saved to: " + path.toAbsolutePath());
    }
}
