package mst;
import graph.Edge;
import graph.Graph;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
public class Prim {
    public static class Result {
        public final List<Edge> mstEdges;
        public final long totalWeight;
        public final boolean connected;
        public final long executionTime;
        public final long edgesExamined;
        public final long queueOperations;
        public Result(
                List<Edge> mstEdges,
                long totalWeight,
                boolean connected,
                long executionTime,
                long edgesExamined,
                long queueOperations
        ) {
            this.mstEdges = mstEdges;
            this.totalWeight = totalWeight;
            this.connected = connected;
            this.executionTime = executionTime;
            this.edgesExamined = edgesExamined;
            this.queueOperations = queueOperations;
        }
    }
    public static Result findMST(Graph graph) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException("Prim requires an undirected graph");
        }
        long startTime = System.nanoTime();
        int vertices = graph.getVertices();
        boolean[] visited = new boolean[vertices];
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(Edge::getWeight));
        List<Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        long edgesExamined = 0;
        long queueOperations = 0;
        int visitedCount = 1;
        visited[0] = true;
        for (Edge edge : graph.getNeighbors(0)) {
            edgesExamined++;
            pq.offer(edge);
            queueOperations++;
        }
        while (!pq.isEmpty() && visitedCount < vertices) {
            Edge edge = pq.poll();
            queueOperations++;
            int destination = edge.getDestination();
            if (visited[destination]) {
                continue;
            }
            visited[destination] = true;
            visitedCount++;
            mstEdges.add(edge);
            totalWeight += edge.getWeight();
            for (Edge nextEdge : graph.getNeighbors(destination)) {
                edgesExamined++;
                if (!visited[nextEdge.getDestination()]) {
                    pq.offer(nextEdge);
                    queueOperations++;
                }
            }
        }
        boolean connected = visitedCount == vertices;
        long endTime = System.nanoTime();
        return new Result(
                mstEdges,
                totalWeight,
                connected,
                endTime - startTime,
                edgesExamined,
                queueOperations);
    }
}
