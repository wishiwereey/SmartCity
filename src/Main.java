import tests.AlgorithmTests;
import java.io.IOException;
import java.util.List;
public class Main {
    public static void main(String[] args) {
        System.out.println("================================");
        System.out.println(" SMART CITY GRAPH ALGORITHMS");
        System.out.println("================================");
        AlgorithmTests.runAll();
        System.out.println();
        System.out.println("================================");
        System.out.println(" PERFORMANCE BENCHMARKS");
        System.out.println("================================");
        List<Benchmark.Row> results = Benchmark.runAll();
        Benchmark.printResults(results);
        try {
            Benchmark.saveCSV(results, "benchmark_results.csv");
        } catch (IOException e) {
            System.err.println("Failed to save benchmark results: " + e.getMessage());
        }
        System.out.println();
        System.out.println(" PROJECT EXECUTION COMPLETED");
    }
}
