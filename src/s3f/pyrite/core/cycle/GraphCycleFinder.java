/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core.cycle;

import java.util.ArrayList;
import java.util.List;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.Connection;

public class GraphCycleFinder {

//    public static void main(String[] args) {
//        //  Graph modeled as list of edges
//        run2(new int[][]{
//            {1, 2}, {1, 3}, {1, 4}, {2, 3},
//            {3, 4}, {2, 6}, {4, 6}, {7, 8},
//            {8, 9}, {9, 7}
//        });
//
//    }
//
//    public static void run2(int[][] graph) {
//        List<int[]> cycles = new ArrayList<>();
//        for (int[] graph1 : graph) {
//            for (int j = 0; j < graph1.length; j++) {
//                System.out.println(j);
//                findNewCycles(new int[]{graph1[j]}, graph, cycles, 10000);
//            }
//        }
//
//        for (int[] cy : cycles) {
//            String s = "" + cy[0];
//
//            for (int i = 1; i < cy.length; i++) {
//                s += "," + cy[i];
//            }
//
//            System.out.println(s);
//        }
//    }

    public static List<int[]> run(Circuit circuit, int capacity) {
        List<Connection> connections = circuit.getConnections();
        int size = connections.size();
        if (size == 0) {
            return null;
        }
        int[][] graph = new int[size][];
        for (int i = 0; i < size; i++) {
            Connection connection = connections.get(i);
            Component a = connection.getA();
            Component b = connection.getB();
            graph[i] = new int[]{
                circuit.getComponents().indexOf(a),
                circuit.getComponents().indexOf(b)
            };
        }
        return GraphCycleFinder.run(graph, 300);
    }

    public static List<int[]> run(int[][] graph, int capacity) {
        List<int[]> cycles = new ArrayList<>();
        int i = 0;
        for (int[] graph1 : graph) {
            for (int j = 0; j < graph1.length; j++) {
                findNewCycles(new int[]{graph1[j]}, graph, cycles, capacity);
            }
            i++;
            System.out.println(i);
        }
        return cycles;
    }

    static void findNewCycles(int[] path, int[][] graph, List<int[]> cycles, int capacity) {
        if (cycles.size() > capacity) {
            return;
        }
        int n = path[0];
        int x;
        int[] sub = new int[path.length + 1];

        for (int[] graph1 : graph) {
            for (int y = 0; y <= 1; y++) {
                if (graph1[y] == n) {
                    x = graph1[(y + 1) % 2];
                    if (!visited(x, path)) //  neighbor node not on path yet
                    {
                        sub[0] = x;
                        System.arraycopy(path, 0, sub, 1, path.length);
                        //  explore extended path
                        findNewCycles(sub, graph, cycles, capacity);
                    } else if ((path.length > 2) && (x == path[path.length - 1])) //  cycle found
                    {
                        int[] p = normalize(path);
                        int[] inv = invert(p);
                        if (isNew(p, cycles) && isNew(inv, cycles)) {
                            cycles.add(p);
                        }
                    }
                }
            }
        }
    }

    //  check of both arrays have same lengths and contents
    static Boolean equals(int[] a, int[] b) {
        Boolean ret = (a[0] == b[0]) && (a.length == b.length);

        for (int i = 1; ret && (i < a.length); i++) {
            if (a[i] != b[i]) {
                ret = false;
            }
        }

        return ret;
    }

    //  create a path array with reversed order
    static int[] invert(int[] path) {
        int[] p = new int[path.length];

        for (int i = 0; i < path.length; i++) {
            p[i] = path[path.length - 1 - i];
        }

        return normalize(p);
    }

    //  rotate cycle path such that it begins with the smallest node
    static int[] normalize(int[] path) {
        int[] p = new int[path.length];
        int x = smallest(path);
        int n;

        System.arraycopy(path, 0, p, 0, path.length);

        while (p[0] != x) {
            n = p[0];
            System.arraycopy(p, 1, p, 0, p.length - 1);
            p[p.length - 1] = n;
        }

        return p;
    }

    //  compare path against known cycles
    //  return true, iff path is not a known cycle
    static Boolean isNew(int[] path, List<int[]> cycles) {
        Boolean ret = true;

        for (int[] p : cycles) {
            if (equals(p, path)) {
                ret = false;
                break;
            }
        }

        return ret;
    }

    //  return the int of the array which is the smallest
    static int smallest(int[] path) {
        int min = path[0];

        for (int p : path) {
            if (p < min) {
                min = p;
            }
        }

        return min;
    }

    //  check if vertex n is contained in path
    static Boolean visited(int n, int[] path) {
        Boolean ret = false;

        for (int p : path) {
            if (p == n) {
                ret = true;
                break;
            }
        }

        return ret;
    }

}
