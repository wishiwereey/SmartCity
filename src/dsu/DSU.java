package dsu;
public class DSU {
    private final int[] parent;
    private final int[] rank;
    private int components;
    private long findOperations;
    private long unionOperations;
    public DSU(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Number of elements must be positive");
        }
        parent = new int[n];
        rank = new int[n];
        components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }
    public int find(int x) {
        findOperations++;
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }
    public boolean union(int x, int y) {
        unionOperations++;
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) {
            return false;
        }
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        components--;
        return true;
    }
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
    public int getComponents() {
        return components;
    }
    public long getFindOperations() {
        return findOperations;
    }
    public long getUnionOperations() {
        return unionOperations;
    }
}
