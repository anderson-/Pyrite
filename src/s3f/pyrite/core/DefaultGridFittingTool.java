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
        public int sleep = 100;
        public boolean chain = true;
        public long seed = 0;
        public boolean shuffleNg = true;
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
        //##cria e atualiza a arvore
        Queue<Component> s = new ArrayDeque<>(circuit.getComponents());
        int size = 0, count = 0;
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
//            System.out.println(s.size());
            if (v.getPos() != null) {
                for (Connection c : v.getConnections()) {
                    Component j = c.getOtherComponent(v);
                    if (j.getPos() == null) {
                        /*
                         coloca j no lugar mais proximo, com expansão 
                         de caminhos se necessario;
                         */
                        //###arvore - resetCube(cube);
                        placeNear(v, j, tree, grid);
                        count = 0;
                        //makePathAndPlace(v, j, cube, t);
                        sleep();
                        if (!parameters.chain) {
                            break;
                        }
                    } else {
                        if (!c.isSatisfied()) {
                            /*
                             procura o no mais proximo* para satisfazer a 
                             conexão;
                             */
                            if (buildPathAndSatisfy(circuit, c, grid)) {
                                count = 0;
                                sleep();
                            } else {
                                if (!s.contains(v)) {
                                    s.offer(v);
                                }
                                System.out.println(v.getUID() + " -/-> " + j.getUID());
                            }
                            if (!parameters.chain) {
                                if (!s.contains(v)) {
                                    s.offer(v);
                                }
                                break;
                            }
                        }
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
        List<int[]> neighborhood = grid.getNeighborhood(v.getPos());
        int nSize = neighborhood.size();
        for (int[] w : neighborhood) {
            if (nSize - countNg(tree, w, grid) >= j.getConnections().size()) {
                list.add(w);
            }
        }
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

    private Component expand(Component v, Component j) {
        Component c = new Component();
        v.createConnection(c);
        c.createConnection(j);
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

        for (Component com : v.getShortcuts()) {
            c.addShortcut(com);
        }

        for (Component com : j.getShortcuts()) {
            c.addShortcut(com);
        }

        return c;
    }

    private boolean buildPathAndSatisfy(Circuit circuit, final Connection connection, Grid grid) {
        if (isNeighbor(connection.getA(), connection.getB(), grid)) {
            connection.setSatisfied(true);
            return true;
        } else {
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

            d.computePaths(a.getPos(), b.getPos(), grid, circuit.getComponents(), validShortcutsPos);
            List<int[]> directions = null;
            System.out.println("validShortcuts: " + validShortcuts.size());
            for (Component c : validShortcuts) {
                List<int[]> dt = d.getShortestPathTo(c.getPos());
                System.out.println("." + dt);
                if ((directions == null || dt.size() < directions.size()) && dt.size() > 0) {
                    directions = dt;
                }
            }

            System.out.println(directions);

            boolean pathExpanded = false;

            if (directions != null) {
                Component n = a;
                for (int[] c : directions) {
                    n = expand(n, b);
                    n.setPos(c);
                    pathExpanded = true;
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
                        vertexQueue.remove((Integer) v);
                        distances.put(v, distanceThroughU);
                        prev.put(v, u);
                        vertexQueue.add(v);
                    }
                }
            }
        }

        public List<int[]> getShortestPathTo(int[] target) {
            List<int[]> path = new ArrayList<>();
            for (Integer vertex = toInt(target); vertex != null; vertex = prev.get(vertex)) {
                if (vertex != toInt(target) && vertex != source) {
                    path.add(toVet(vertex));
                }
            }
            Collections.reverse(path);
            return path;
        }
    }
}
