package connectivity;
import graph.Edge;
import graph.Graph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Kosaraju {
    public static class Result {
        public final List<List<Integer>> components;
        public final int componentCount;
        public final long executionTime;
        public final long edgesExamined;
        public Result(
                List<List<Integer>> components,
                long executionTime,
                long edgesExamined
        ) {
            this.components = components;
            this.componentCount = components.size();
            this.executionTime = executionTime;
            this.edgesExamined = edgesExamined;
        }
    }
    private static long edgesExamined;
    public static Result findSCC(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("Kosaraju requires a directed graph");
        }
        long startTime = System.nanoTime();
        edgesExamined = 0;
        int vertices = graph.getVertices();
        boolean[] visited = new boolean[vertices];
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            if (!visited[i]) {
                dfs1(graph, i, visited, order);
            }
        }
        Graph reversed = new Graph(vertices, true);
        for (Edge edge : graph.getEdges()) {
            edgesExamined++;
            reversed.addEdge(
                    edge.getDestination(),
                    edge.getSource(),
                    edge.getWeight());
        }
        Collections.reverse(order);
        visited = new boolean[vertices];
        List<List<Integer>> components = new ArrayList<>();
        for (int vertex : order) {
            if (!visited[vertex]) {
                List<Integer> component = new ArrayList<>();
                dfs2(reversed, vertex, visited, component);
                components.add(component);
            }
        }
        long endTime = System.nanoTime();
        return new Result(
                components,
                endTime - startTime,
                edgesExamined);
    }
    private static void dfs1(
            Graph graph,
            int vertex,
            boolean[] visited,
            List<Integer> order
    ) {
        visited[vertex] = true;
        for (Edge edge : graph.getNeighbors(vertex)) {
            edgesExamined++;
            int neighbor = edge.getDestination();
            if (!visited[neighbor]) {
                dfs1(graph, neighbor, visited, order);
            }
        }
        order.add(vertex);
    }
    private static void dfs2(
            Graph graph,
            int vertex,
            boolean[] visited,
            List<Integer> component
    ) {
        visited[vertex] = true;
        component.add(vertex);
        for (Edge edge : graph.getNeighbors(vertex)) {
            edgesExamined++;
            int neighbor = edge.getDestination();
            if (!visited[neighbor]) {
                dfs2(graph, neighbor, visited, component);
            }
        }
    }
}

