package connectivity;
import graph.Edge;
import graph.Graph;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
public class TopologicalSort {
    public static class Result {
        public final List<Integer> order;
        public final boolean hasCycle;
        public final long executionTime;
        public final long edgesExamined;
        public Result(
                List<Integer> order,
                boolean hasCycle,
                long executionTime,
                long edgesExamined
        ) {
            this.order = order;
            this.hasCycle = hasCycle;
            this.executionTime = executionTime;
            this.edgesExamined = edgesExamined;
        }
    }
    public static Result sort(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("Topological Sort requires a directed graph");
        }
        long startTime = System.nanoTime();
        int vertices = graph.getVertices();
        int[] inDegree = new int[vertices];
        long edgesExamined = 0;
        for (int i = 0; i < vertices; i++) {
            for (Edge edge : graph.getNeighbors(i)) {
                inDegree[edge.getDestination()]++;
                edgesExamined++;
            }
        }
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < vertices; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }
        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int current = queue.poll();
            order.add(current);
            for (Edge edge : graph.getNeighbors(current)) {
                edgesExamined++;
                int neighbor = edge.getDestination();
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }
        boolean hasCycle = order.size() != vertices;
        long endTime = System.nanoTime();
        return new Result(
                order,
                hasCycle,
                endTime - startTime,
                edgesExamined);
    }
}
