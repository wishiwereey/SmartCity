package graph;
import java.util.ArrayList;
import java.util.List;
public class Graph {
    private final int vertices;
    private final boolean directed;
    private final List<List<Edge>> adjacencyList;
    private final List<Edge> edges;
    public Graph(int vertices, boolean directed) {
        if (vertices < 1) {
            throw new IllegalArgumentException("Number of vertices must be positive");
        }
        this.vertices = vertices;
        this.directed = directed;
        adjacencyList = new ArrayList<>();
        edges = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }
    public void addEdge(int source, int destination, int weight) {
        if (source < 0 || source >= vertices || destination < 0 || destination >= vertices) {
            throw new IllegalArgumentException("Invalid vertex");
        }
        Edge edge = new Edge(source, destination, weight);
        adjacencyList.get(source).add(edge);
        edges.add(edge);
        if (!directed && source != destination) {
            adjacencyList.get(destination).add(new Edge(destination, source, weight));
        }
    }
    public int getVertices() {
        return vertices;
    }
    public boolean isDirected() {
        return directed;
    }
    public List<Edge> getNeighbors(int vertex) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException("Invalid vertex");
        }
        return java.util.Collections.unmodifiableList(
                adjacencyList.get(vertex));
    }
    public List<Edge> getEdges() {
        return java.util.Collections.unmodifiableList(edges);
    }
    public void printGraph() {
        for (int i = 0; i < vertices; i++) {
            System.out.print("Vertex " + i + ": ");
            for (Edge edge : adjacencyList.get(i)) {
                System.out.print(edge.getDestination() + "(" + edge.getWeight() + ") ");
            }
            System.out.println();
        }
    }
}
