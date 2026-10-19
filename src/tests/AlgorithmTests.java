package tests;
import connectivity.Kosaraju;
import connectivity.TopologicalSort;
import graph.Edge;
import graph.Graph;
import mst.Kruskal;
import mst.Prim;
import shortestpath.Dijkstra;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class AlgorithmTests {
    private static int passed = 0;
    private static int failed = 0;
    public static void runAll() {
        passed = 0;
        failed = 0;
        System.out.println("=== ALGORITHM TESTS ===");
        runTest("Kruskal MST", AlgorithmTests::testKruskal);
        runTest("Prim MST", AlgorithmTests::testPrim);
        runTest("Disconnected MST", AlgorithmTests::testDisconnectedMST);
        runTest("Topological Sort DAG", AlgorithmTests::testTopologicalSort);
        runTest("Topological Sort Cycle", AlgorithmTests::testCycleDetection);
        runTest("Kosaraju SCC", AlgorithmTests::testKosaraju);
        runTest("Dijkstra Shortest Paths", AlgorithmTests::testDijkstra);
        runTest("Dijkstra Unreachable", AlgorithmTests::testUnreachable);
        runTest("Dijkstra Negative Weight", AlgorithmTests::testNegativeWeight);
        System.out.println();
        System.out.println("Tests passed: " + passed);
        System.out.println("Tests failed: " + failed);
        if (failed > 0) {
            throw new AssertionError(failed + " test(s) failed");
        }
        System.out.println("ALL TESTS PASSED!");
    }
    private static void runTest(String name, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable error) {
            failed++;
            System.out.println("[FAIL] " + name + ": " + error.getMessage());
        }
    }
    private static void check(
            boolean condition,
            String message
    ) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
    private static Graph createMSTGraph() {
        Graph graph = new Graph(4, false);
        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 2, 6);
        graph.addEdge(0, 3, 5);
        graph.addEdge(1, 3, 15);
        graph.addEdge(2, 3, 4);
        return graph;
    }
    private static void testKruskal() {
        Kruskal.Result result = Kruskal.findMST(createMSTGraph());
        check(result.connected, "Graph should be connected");
        check(result.totalWeight == 19, "Expected MST weight 19");
        check(result.mstEdges.size() == 3, "MST should contain 3 edges");
    }
    private static void testPrim() {
        Prim.Result result = Prim.findMST(createMSTGraph());
        check(result.connected, "Graph should be connected");
        check(result.totalWeight == 19, "Expected MST weight 19");
        check(result.mstEdges.size() == 3, "MST should contain 3 edges");
    }
    private static void testDisconnectedMST() {
        Graph graph = new Graph(4, false);
        graph.addEdge(0, 1, 3);
        graph.addEdge(2, 3, 5);
        Kruskal.Result kruskal = Kruskal.findMST(graph);
        Prim.Result prim = Prim.findMST(graph);
        check(!kruskal.connected, "Kruskal must detect a disconnected graph");
        check(!prim.connected, "Prim must detect a disconnected graph");
    }
    private static void testTopologicalSort() {
        Graph graph = new Graph(6, true);
        graph.addEdge(5, 2, 1);
        graph.addEdge(5, 0, 1);
        graph.addEdge(4, 0, 1);
        graph.addEdge(4, 1, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 1, 1);
        TopologicalSort.Result result = TopologicalSort.sort(graph);
        check(!result.hasCycle, "DAG must not contain a cycle");
        check(result.order.size() == 6, "Topological order must contain 6 vertices");
        int[] position = new int[6];
        boolean[] seen = new boolean[6];
        for (int i = 0; i < result.order.size(); i++) {
            int vertex = result.order.get(i);
            check(vertex >= 0 && vertex < 6, "Invalid vertex in topological order");
            check(!seen[vertex], "Duplicate vertex in topological order");
            seen[vertex] = true;
            position[vertex] = i;
        }
        for (Edge edge : graph.getEdges()) {
            check(position[edge.getSource()] < position[edge.getDestination()], "Invalid topological order");
        }
    }
    private static void testCycleDetection() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        TopologicalSort.Result result = TopologicalSort.sort(graph);
        check(result.hasCycle, "Cycle should be detected");
    }
    private static void testKosaraju() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 3, 1);
        Kosaraju.Result result = Kosaraju.findSCC(graph);
        check(result.componentCount == 2, "Expected 2 SCCs");
        Set<Set<Integer>> actual = new HashSet<>();
        int totalVertices = 0;
        for (List<Integer> component : result.components) {
            actual.add(new HashSet<>(component));
            totalVertices += component.size();
        }
        Set<Set<Integer>> expected = new HashSet<>();
        expected.add(new HashSet<>(Arrays.asList(0, 1, 2)));
        expected.add(new HashSet<>(Arrays.asList(3, 4)));
        check(actual.equals(expected), "Incorrect SCC components");
        check(totalVertices == 5, "Each vertex must belong to exactly one SCC");
    }
    private static Graph createDijkstraGraph() {
        Graph graph = new Graph(6, false);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 5);
        graph.addEdge(2, 3, 1);
        graph.addEdge(2, 4, 7);
        graph.addEdge(3, 4, 3);
        return graph;
    }
    private static void testDijkstra() {
        Dijkstra.Result result = Dijkstra.shortestPaths(createDijkstraGraph(), 0);
        long[] expected = {0, 4, 2, 3, 6};
        for (int i = 0; i < expected.length; i++) {
            check(result.distances[i] == expected[i], "Incorrect distance to vertex " + i);
        }
        check(result.getPath(4).equals(Arrays.asList(0, 2, 3, 4)), "Incorrect shortest path to vertex 4");
    }
    private static void testUnreachable() {
        Dijkstra.Result result = Dijkstra.shortestPaths(createDijkstraGraph(), 0);
        check(result.distances[5] == Long.MAX_VALUE, "Vertex 5 should be unreachable");
        check(result.getPath(5).isEmpty(), "Unreachable vertex should have an empty path");
    }
    private static void testNegativeWeight() {
        Graph graph = new Graph(2, true);
        graph.addEdge(0, 1, -5);
        boolean exceptionThrown = false;
        try {
            Dijkstra.shortestPaths(graph, 0);

        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        check(exceptionThrown, "Negative weights must be rejected");
    }
}
