package s3f.pyrite.core.fdgfa;

import java.util.ArrayList;
import s3f.pyrite.core.fdgfa.strategy.FoldingStrategy;
import s3f.pyrite.core.fdgfa.honeycomb.ConvexUniformHoneycomb;
import java.util.List;
import javax.swing.JOptionPane;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.Connection;
import s3f.pyrite.core.fdgfa.fdgs.ForceDirectedGraphSimulation;
import s3f.pyrite.ui.graphmonitor.GraphMonitor3D;

public class GraphFolder {

    static final int NODE_TYPE_NEW_EXTENSION = 0;
    static final int NODE_TYPE_EXTENSION = 1;
    static final int NODE_TYPE_COMPONENT = 2;

    public static void w() {
        JOptionPane.showConfirmDialog(null, "w");
    }

    public static void print(String str) {
        if (GraphMonitor3D.getConsole() != null) {
            GraphMonitor3D.getConsole().put(str);
        }
    }

    public static double fold(Circuit g, ForceDirectedGraphSimulation sim, FoldingStrategy d, ConvexUniformHoneycomb h) {
        long t = System.currentTimeMillis();
        int startingNodeCount = g.getComponents().size();
        int startingEdgeCount = g.getConnections().size();

        {
            waitForEqui(sim);

            for (Component c : new ArrayList<>(g.getComponents())) {
                if (c.getConnections().size() > 6) {
                    for (Connection con : new ArrayList<>(c.getConnections())) {
                        synchronized (g) {
                            Component n = new Component();
                            Component b = con.getOtherComponent(c);
                            GraphUtils.addNodeBetween(g, c, b, n);
                        }
                    }
                    synchronized (g) {
                        for (Connection con : new ArrayList<>(c.getConnections())) {
                            Component n = con.getOtherComponent(c);
                            if (n.getConnections().size() != 2) {
                                continue;
                            }
                            Component closest = null;
                            for (Connection con2 : c.getConnections()) {
                                if (con != con2) {
                                    Component n2 = con2.getOtherComponent(c);
                                    if (closest == null || (n2.getConnections().size() < 6
                                            && closest.getPos().distance(n.getPos()) > n2.getPos().distance(n.getPos()))) {
                                        closest = n2;
                                    }
                                }
                            }
                            n.getOtherConnection(con).replaceDeep(n, closest);
                            g.removeConnection(con);
                            g.removeComponent(n);
                            g.clean();
                        }
                    }
                }
            }
        }

        waitForEqui(sim);
        double score = 0;
        if (d.hasStaticTraversal()) {
            List<Connection> traversing = d.getTraversing(g);
            for (Connection e : traversing) {
                if (!h.isSatisfied(e)) {
                    synchronized (g) {
                        score += d.performAction(d.decisionMaker(d.generatePerception(g, e)), g, e);
                    }
                    delay();
                    int k = 0;
                    for (Connection w : g.getConnections()) {
                        k += h.isSatisfied(w) ? 1 : 0;
                    }
//                    System.out.printf("k = %d %%, added %d, satisfied = %d, unsatisfied = %d\n", 100 * k / g.getEdges().size(), g.getNodes().size() - startingNodeCount, k, g.getEdges().size() - k);
                }
            }
        } else {
            Connection e;
            while ((e = d.getNextEdge(g)) != null) {
                if (!h.isSatisfied(e)) {
                    synchronized (g) {
                        score += d.performAction(d.decisionMaker(d.generatePerception(g, e)), g, e);
                    }
                    int k = 0;
                    for (Connection w : g.getConnections()) {
                        k += h.isSatisfied(w) ? 1 : 0;
                    }
                    print("{clear}");
                    print(String.format("Starting Nodes: %d\nAdded: %d\nSatisfied: %d (%.2f %%)\nUnsatisfied: %d\nVolume: %f\n", startingNodeCount, g.getComponents().size() - startingNodeCount, k, 100f * k / g.getConnections().size(), g.getConnections().size() - k, GraphUtils.getVolume(g, h)));
                    System.out.printf("k = %d %%, added %d, satisfied = %d, unsatisfied = %d\n", 100 * k / g.getConnections().size(), g.getComponents().size() - startingNodeCount, k, g.getConnections().size() - k);
                    waitForEqui(sim);
                    delay(5);
                }
            }
        }
        t = System.currentTimeMillis() - t;
        double finalScore = getScore(g, h, startingNodeCount, startingEdgeCount);
        print("{clear}");
        print(String.format("Completed in %.2f s, Score: %.4f", t / 1000f, finalScore * 100));
        int k = 0;
        for (Connection w : g.getConnections()) {
            k += h.isSatisfied(w) ? 1 : 0;
        }
        print(String.format("Starting Nodes: %d\nAdded: %d\nSatisfied: %d (%.2f %%)\nUnsatisfied: %d\nVolume: %f\n", startingNodeCount, g.getComponents().size() - startingNodeCount, k / 2, 100f * k / g.getConnections().size(), (g.getConnections().size() - k) / 2, GraphUtils.getVolume(g, h)));
        return finalScore;
    }

    public static void waitForEqui(ForceDirectedGraphSimulation sim) {
        int i = 0;
        do {
            i++;
            delay(5);
        } while (!sim.isEqu());
    }

    public static void proximateDelay(ForceDirectedGraphSimulation sim) {
        delay((int) (Math.log10(sim.getKE()) / Math.tan(0.08266)));
    }

    public static void delay() {
        delay(80);
    }

    public static void delay(int t) {
        try {
            Thread.sleep(t);
        } catch (Exception e) {
        }
    }

    private static double getScore(Circuit g, ConvexUniformHoneycomb h, int startingNodeCount, int startingEdgeCount) {
        double[] normalizedSubScore = new double[]{0, 0, 0, 0, 0, 0, 0};
        double[] weights = new double[]{
            .03, //score center-based distribution
            .20, //score density
            .07, //score added nodes
            .10, //score fixed nodes
            .10, //score lose nodes
            .50, //score unsatisfied connections
            0 //step scores
        };

        double finalNodeCount = g.getComponents().size();
        double finalEdgeCount = g.getConnections().size();

        //score center-based distribution
        normalizedSubScore[0] = GraphUtils.scoreDistribution(g, h, GraphUtils.getDefaultKernelPos(), 30);

        //score density
        normalizedSubScore[1] = finalNodeCount / (8 * GraphUtils.getVolume(g, h));

        //score added nodes
        normalizedSubScore[2] = (2. * startingNodeCount - finalNodeCount) / startingNodeCount;

        //score fixed nodes
        normalizedSubScore[3] = (2. * startingNodeCount - GraphUtils.countPlacedNodes(g)) / startingNodeCount;

        //score lose nodes
        normalizedSubScore[4] = GraphUtils.countPlacedNodes(g) / finalNodeCount;

        //score unsatisfied connections
        normalizedSubScore[5] = GraphUtils.countSatisfiedConnections(g, h) / finalEdgeCount;

        //step scores
        normalizedSubScore[6] = 0;

//        System.out.println(Arrays.toString(normalizedSubScore));
        double finalSocre = 0;
        for (int i = 0; i < normalizedSubScore.length; i++) {
            finalSocre += check(normalizedSubScore[i] * weights[i], i);
        }
        return finalSocre;
    }

    public static double check(double v, int id) {
        if (v > 1 || v < 0) {
//            throw new RuntimeException("Invalid Value: " + v + "[" + id + "]");
        }
        return v;
    }
}
