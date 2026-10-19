package shortestpath;
import graph.Edge;
import graph.Graph;
import java.util.*;
public class Dijkstra {
    public static class Result {
        public final long[] distances;
        public final int[] previous;
        public final long executionTime;
        public final long edgesExamined;
        public final long queueOperations;
        public Result(
                long[] distances,
                int[] previous,
                long executionTime,
                long edgesExamined,
                long queueOperations
        ) {
            this.distances = distances;
            this.previous = previous;
            this.executionTime = executionTime;
            this.edgesExamined = edgesExamined;
            this.queueOperations = queueOperations;
        }
        public List<Integer> getPath(int destination) {
            if (distances[destination] == Long.MAX_VALUE) {
                return Collections.emptyList();
            }
            List<Integer> path = new ArrayList<>();
            int current = destination;
            while (current != -1) {
                path.add(current);
                current = previous[current];
            }
            Collections.reverse(path);
            return path;
        }
    }
    private static class Node {
        int vertex;
        long distance;
        Node(int vertex, long distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }
    public static Result shortestPaths(Graph graph, int source) {
        int vertices = graph.getVertices();
        if (source < 0 || source >= vertices) {
            throw new IllegalArgumentException("Invalid source vertex");
        }
        for (Edge edge : graph.getEdges()) {
            if (edge.getWeight() < 0) {
                throw new IllegalArgumentException("Dijkstra requires non-negative weights");
            }
        }
        long startTime = System.nanoTime();
        long[] distances = new long[vertices];
        int[] previous = new int[vertices];
        Arrays.fill(distances, Long.MAX_VALUE);
        Arrays.fill(previous, -1);
        distances[source] = 0;
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingLong(node -> node.distance));
        pq.offer(new Node(source, 0));
        long edgesExamined = 0;
        long queueOperations = 1;
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            queueOperations++;
            int vertex = current.vertex;
            if (current.distance != distances[vertex]) {
                continue;
            }
            for (Edge edge : graph.getNeighbors(vertex)) {
                edgesExamined++;
                int neighbor = edge.getDestination();
                int weight = edge.getWeight();
                if (distances[vertex] > Long.MAX_VALUE - weight) {
                    continue;
                }
                long newDistance = distances[vertex] + weight;
                if (newDistance < distances[neighbor]) {
                    distances[neighbor] = newDistance;
                    previous[neighbor] = vertex;
                    pq.offer(new Node(neighbor, newDistance));
                    queueOperations++;
                }
            }
        }
        long endTime = System.nanoTime();
        return new Result(
                distances,
                previous,
                endTime - startTime,
                edgesExamined,
                queueOperations);
    }
}
