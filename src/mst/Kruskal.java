package mst;
import graph.Edge;
import graph.Graph;
import dsu.DSU;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class Kruskal {
    public static class Result {
        public final List<Edge> mstEdges;
        public final long totalWeight;
        public final boolean connected;
        public final long executionTime;
        public final long edgesExamined;
        public final long findOperations;
        public final long unionOperations;
        public Result(
                List<Edge> mstEdges,
                long totalWeight,
                boolean connected,
                long executionTime,
                long edgesExamined,
                long findOperations,
                long unionOperations
        ) {
            this.mstEdges = mstEdges;
            this.totalWeight = totalWeight;
            this.connected = connected;
            this.executionTime = executionTime;
            this.edgesExamined = edgesExamined;
            this.findOperations = findOperations;
            this.unionOperations = unionOperations;
        }
    }
    public static Result findMST(Graph graph) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException("Kruskal requires an undirected graph");
        }
        long startTime = System.nanoTime();
        int vertices = graph.getVertices();
        List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());
        sortedEdges.sort(Comparator.comparingInt(Edge::getWeight));
        DSU dsu = new DSU(vertices);
        List<Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        long edgesExamined = 0;
        for (Edge edge : sortedEdges) {
            edgesExamined++;
            int source = edge.getSource();
            int destination = edge.getDestination();
            if (dsu.union(source, destination)) {
                mstEdges.add(edge);
                totalWeight += edge.getWeight();
                if (mstEdges.size() == vertices - 1) {
                    break;
                }
            }
        }
        boolean connected = dsu.getComponents() == 1;
        long endTime = System.nanoTime();
        return new Result(
                mstEdges,
                totalWeight,
                connected,
                endTime - startTime,
                edgesExamined,
                dsu.getFindOperations(),
                dsu.getUnionOperations());
    }
}
