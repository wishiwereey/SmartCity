import graph.Graph;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
public class GraphGenerator {
    public static Graph generateConnectedGraph(
            int vertices,
            int edges,
            long seed
    ) {
        validateParameters(vertices, edges, false);
        if (edges < vertices - 1) {
            throw new IllegalArgumentException("A connected graph needs at least V - 1 edges");
        }
        Graph graph = new Graph(vertices, false);
        Random random = new Random(seed);
        Set<Long> usedEdges = new HashSet<>();
        for (int i = 1; i < vertices; i++) {
            int parent = random.nextInt(i);
            int weight = random.nextInt(100) + 1;
            graph.addEdge(parent, i, weight);
            usedEdges.add(edgeKey(parent, i, false));
        }
        while (usedEdges.size() < edges) {
            int source = random.nextInt(vertices);
            int destination = random.nextInt(vertices);
            if (source == destination) {
                continue;
            }
            long key = edgeKey(source, destination, false);
            if (usedEdges.add(key)) {
                int weight = random.nextInt(100) + 1;
                graph.addEdge(source, destination, weight);
            }
        }
        return graph;
    }
    public static Graph generateDirectedGraph(
            int vertices,
            int edges,
            long seed
    ) {
        validateParameters(vertices, edges, true);
        Graph graph = new Graph(vertices, true);
        Random random = new Random(seed);
        Set<Long> usedEdges = new HashSet<>();
        while (usedEdges.size() < edges) {
            int source = random.nextInt(vertices);
            int destination = random.nextInt(vertices);
            if (source == destination) {
                continue;
            }
            long key = edgeKey(source, destination, true);
            if (usedEdges.add(key)) {
                int weight = random.nextInt(100) + 1;
                graph.addEdge(source, destination, weight);
            }
        }
        return graph;
    }
    public static Graph generateDAG(
            int vertices,
            int edges,
            long seed
    ) {
        validateParameters(vertices, edges, false);
        Graph graph = new Graph(vertices, true);
        Random random = new Random(seed);
        Set<Long> usedEdges = new HashSet<>();
        while (usedEdges.size() < edges) {
            int source = random.nextInt(vertices);
            int destination = random.nextInt(vertices);
            if (source >= destination) {
                continue;
            }
            long key = edgeKey(source, destination, true);
            if (usedEdges.add(key)) {
                int weight = random.nextInt(100) + 1;
                graph.addEdge(source, destination, weight);
            }
        }
        return graph;
    }
    private static void validateParameters(
            int vertices,
            int edges,
            boolean directed
    ) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("Number of vertices must be positive");
        }
        if (edges < 0) {
            throw new IllegalArgumentException("Number of edges cannot be negative");
        }
        long maxEdges;
        if (directed) {
            maxEdges = (long) vertices * (vertices - 1);
        } else {
            maxEdges = (long) vertices * (vertices - 1) / 2;
        }
        if (edges > maxEdges) {
            throw new IllegalArgumentException("Too many edges for this graph size");
        }
    }
    private static long edgeKey(
            int source,
            int destination,
            boolean directed
    ) {
        if (!directed && source > destination) {
            int temp = source;
            source = destination;
            destination = temp;
        }
        return ((long) source << 32) | (destination & 0xffffffffL);
    }
}
