/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JLabel;
import s3f.pyrite.core.intervaltree.HDIntervalTree;
import s3f.pyrite.ui.ConfigurationTab;
import s3f.pyrite.ui.ConfigurationTab.Checkbox;
import s3f.pyrite.ui.ConfigurationTab.CustomComponent;
import s3f.pyrite.ui.ConfigurationTab.Panel;

/**
 *
 * @author antunes
 */
public class DefaultGridFittingTool implements GridFittingTool {

    private static class Parameters {

        public Parameters() {
//            new Thread() {
//                @Override
//                public void run() {
//                    while (true) {
//                        System.out.println(optimize);
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }
//            }.start();
        }

        private static class SubP0 {

            @Checkbox(name = "test1")
            public boolean t1 = true;

            @Checkbox(name = "asdasd")
            public boolean t2 = true;
        }

        @Checkbox(name = "optimize")
        public boolean optimize2 = true;

        @Panel(name = "caixa pra p0")
        public SubP0 p0 = new SubP0();

        @Panel(name = "p1")
        public SubP0 p1 = new SubP0();

        @CustomComponent(method = "buildAsd")
        public String asd = "Testasdasdasdse UHUL!";

        private JComponent buildAsd() {
            return new JLabel(asd);
        }

        //---x---
        public int sleep = 0;
        public boolean chain = true;
        public long seed = 0;
        public boolean shuffleNg = false;
        public boolean optimize = false;

    }

    private final Parameters parameters;
    public static final Random rand = new Random(0);

    public static void main(String[] args) {
        new ConfigurationTab(new Parameters());
    }

    public DefaultGridFittingTool() {
        parameters = new Parameters();
    }

    @Override
    public void fit(Circuit circuit, Grid grid) {
        if (parameters.shuffleNg) {
            rand.setSeed(parameters.seed);
        }

        HDIntervalTree<Component> tree = new HDIntervalTree<>(3);
        for (Component c : circuit.getComponents()) {
            if (c.getPos() != null) {
                tree.addPoint(c, c.getPos()[0], c.getPos()[1], c.getPos()[2]);
            }
        }
        //##cria e atualiza a arvore
        Queue<Component> s = new ArrayDeque<>(circuit.getComponents());
        int size = 0, count = 0;
        fit:
        while (!s.isEmpty()) {
            Component v = s.remove();
            if (s.size() != size) {
                size = s.size();
                count = 0;
            } else {
                count++;
                if (count > size) {
                    break;
                }
            }

            if (v.getPos() != null) {
                for (Connection c : new ArrayList<>(v.getConnections())) {
                    Component j = c.getOtherComponent(v);
                    if (j.getPos() == null) {
                        /*
                         coloca j no lugar mais proximo, com expansão 
                         de caminhos se necessario;
                         */
                        placeNear(v, j, tree, grid);
                        count = 0;
                    } else if (!c.isSatisfied()) {
                        /*
                         procura o no mais proximo* para satisfazer a 
                         conexão;
                         */
                        if (buildPathAndSatisfy(circuit, c, tree, grid)) {
                            count = 0;
                            sleep();
                        } else {
//                                System.out.println(v.getUID() + " -/-> " + j.getUID());
                        }
                    } else {
                        continue;
                    }
                    if (!parameters.chain) {
                        break;
                    }
                }
                for (Connection c : v.getConnections()) {
                    if (!c.isSatisfied()) {
                        s.offer(v);
                        continue fit;
                    }
                }
            } else {
                s.offer(v);
            }
        }
    }

    public void sleep() {
        sleep(parameters.sleep);
    }

    public void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException ex) {

        }
    }

    private int countNg(HDIntervalTree tree, int[] pos, Grid grid) {
        int i = 0;
        for (int[] l : grid.getNeighborhood(pos)) {
            List nh = tree.get(l[0], l[1], l[2]);
            i += nh.size();
        }
        return i;
    }

    private void placeNear(Component v, Component j, HDIntervalTree<Component> tree, Grid grid) {
        if (j.getPos() != null) {
            throw new IllegalStateException("Position of " + j + " is already defined.");
        }
        ArrayList<int[]> list = new ArrayList<>();
        long t = System.currentTimeMillis();
        List<int[]> neighborhood = grid.getNeighborhood(v.getPos());
        int nSize = neighborhood.size();
        for (int[] w : neighborhood) {
            if (tree.get(w[0], w[1], w[2]).isEmpty()) {
                if (nSize - countNg(tree, w, grid) >= j.getConnections().size()) {
                    list.add(w);
                }
            }
        }
        System.out.println("p: " + (System.currentTimeMillis() - t));
        if (parameters.shuffleNg) {
            Collections.shuffle(list, rand);
        }

        if (!list.isEmpty()) {
            int[] pos = list.remove(0);
            tree.addPoint(j, pos[0], pos[1], pos[2]);
            j.setPos(pos);
            v.getConnection(j).setSatisfied(true);
        } else {
            System.err.println("Warning: Empty neighborhood!");
        }
    }

    private boolean isNeighbor(Component a, Component b, Grid grid) {
        for (int[] ng : grid.getNeighborhood(a.getPos())) {
            if (Arrays.equals(ng, b.getPos())) {
                return true;
            }
        }
        return false;
    }

    private Component expand(Component v, Component j, Circuit circuit) {
        Component c = new Component("_");
        circuit.addComponent(c);
        Connection c0 = v.getConnection(j);
        Connection c1 = v.createConnection(c);
        c1.setTerminalA(c0.getTerminal(v));
        Connection c2 = c.createConnection(j);
        c2.setTerminalB(c0.getTerminal(j));
        circuit.removeConnection(c0);
        circuit.addConnection(c1);
        circuit.addConnection(c2);
        c1.whut = c0.whut;
        c2.whut = c0.whut;
        c1.setSatisfied(true);
        c2.setSatisfied(true);
        for (Connection con : v.getConnections()) {
            if (con.isShort()) {
                c.addShortcut(con.getOtherComponent(v));
            }
        }

        for (Connection con : j.getConnections()) {
            if (con.isShort()) {
                c.addShortcut(con.getOtherComponent(j));
            }
        }

//        for (Component com : v.getShortcuts()) {
//            c.addShortcut(com);
//        }
//
//        for (Component com : j.getShortcuts()) {
//            c.addShortcut(com);
//        }
        return c;
    }

    private boolean buildPathAndSatisfy(Circuit circuit, final Connection connection, HDIntervalTree<Component> tree, Grid grid) {
        if (isNeighbor(connection.getA(), connection.getB(), grid)) {
            connection.setSatisfied(true);
            return true;
        } else {
//            System.out.println(connection.toString());
            Dijkstra d = new Dijkstra();
            Component a = connection.getA();
            Component b = connection.getB();

            List<Integer> validShortcutsPos = new ArrayList<>();
            List<Component> validShortcuts = b.getShortcuts();
            for (Component c : validShortcuts) {
                if (c.getPos() != null) {
                    validShortcutsPos.add(Dijkstra.toInt(c.getPos()));
                }
            }
            validShortcuts.clear();//TEMP
            long t = System.currentTimeMillis();
            d.computePaths(a.getPos(), b.getPos(), grid, circuit.getComponents(), validShortcutsPos);
            System.out.println("d: " + (System.currentTimeMillis() - t));
            validShortcuts.add(b);
            List<int[]> directions = null;
//            System.out.println("validShortcuts: " + validShortcuts.size());
            for (Component c : validShortcuts) {
                List<int[]> dt = d.getShortestPathTo(c.getPos());
//                System.out.println("." + dt.size());
                if ((directions == null || dt.size() < directions.size()) && dt.size() > 0) {
                    directions = dt;
                }
            }

            boolean pathExpanded = false;

            if (directions != null) {
//                System.out.println("s:" + directions.size());
                Component n = a;
                for (int[] c : directions) {
                    n = expand(n, b, circuit);
                    n.setPos(c);
                    tree.addPoint(n, c[0], c[1], c[2]);
                    pathExpanded = true;
//                    System.out.println(n);
                }
            }

            connection.setSatisfied(pathExpanded);
            return pathExpanded;
        }
    }

    private static class Dijkstra {

        static int toInt(int x, int y, int z) {
            int rgb = x;
            rgb = (rgb << 8) + y;
            rgb = (rgb << 8) + z;
            return rgb;
        }

        static int toInt(int... i) {
            int rgb = i[0];
            rgb = (rgb << 8) + i[1];
            rgb = (rgb << 8) + i[2];
            return rgb;
        }

        static int[] toVet(int i) {
            int x = (i >> 16) & 0xFF;
            int y = (i >> 8) & 0xFF;
            int z = i & 0xFF;
            return new int[]{x, y, z};
        }

        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();

        int source = -1;
        int target = -1;

        public void computePaths(int[] source, int[] target, Grid grid, List<Component> vertices, List<Integer> validShortcuts) {
            this.source = toInt(source);
            this.target = toInt(target);

            for (Component c : vertices) {
                if (c.getPos() != null) {
                    int i = toInt(c.getPos());
                    if (i != this.source && i != this.target) {
                        if (!validShortcuts.contains(i)) {
                            distances.put(i, 12000);
                        } else {
//                            distances.put(i, 12000);
                        }
                    }
                }
            }

            distances.put(this.source, 0);
            LinkedList<Integer> vertexQueue = new LinkedList<>();
            vertexQueue.add(toInt(source));
            int minDistance = Integer.MAX_VALUE;//break if is gt this

            while (!vertexQueue.isEmpty()) {
                int u = vertexQueue.poll();

                // Visit each edge exiting u
                for (int[] vv : grid.getNeighborhood(toVet(u))) {
                    int v = toInt(vv);
                    Integer distV = distances.get(v);
                    if (distV == null) {
                        distV = 9000;
                    }

                    Integer distU = distances.get(u);
                    if (distU == null) {
                        distU = 9000;
                    }

                    int distanceThroughU = distU + 1;
                    if (distanceThroughU < distV && distV != 12000) {
                        if (v == this.target) {
                            minDistance = distanceThroughU;
                        }
                        vertexQueue.remove((Integer) v);
                        if (distanceThroughU <= minDistance) {
                            distances.put(v, distanceThroughU);
                            prev.put(v, u);
                            vertexQueue.add(v);
                        } else {
                            distances.put(v, 12000);
                        }
//                        {
//                            vertexQueue.remove((Integer) v);
//                            distances.put(v, distanceThroughU);
//                            prev.put(v, u);
//                            vertexQueue.add(v);
//                        }
                    }
                }
            }
        }

        public List<int[]> getShortestPathTo(int[] target) {
            List<int[]> path = new ArrayList<>();
            boolean ok = false;
            for (Integer vertex = toInt(target); vertex != null; vertex = prev.get(vertex)) {
                if (vertex != toInt(target)) {
                    if (vertex != source) {
                        path.add(toVet(vertex));
                    } else {
                        ok = true;
                    }
                }
            }
            if (ok) {
                Collections.reverse(path);
            } else {
                path.clear();
            }
            return path;
        }
    }
}
