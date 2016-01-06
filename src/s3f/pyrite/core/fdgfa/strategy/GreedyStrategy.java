package s3f.pyrite.core.fdgfa.strategy;

import cern.colt.Arrays;
import s3f.pyrite.util.Vector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.Connection;
import s3f.pyrite.core.fdgfa.honeycomb.ConvexUniformHoneycomb;
import s3f.pyrite.core.fdgfa.GraphFolder;
import s3f.pyrite.core.fdgfa.GraphUtils;

public class GreedyStrategy implements FoldingStrategy {

    private final ConvexUniformHoneycomb honeycomb;
    private final double[] inputValueWeights;
    private Component kernel = null;

    public GreedyStrategy(ConvexUniformHoneycomb h) {
        this(h, new double[]{1, 1, 1, 1});
    }

    public GreedyStrategy(ConvexUniformHoneycomb h, double[] inputValueWeights) {
        this.honeycomb = h;
        this.inputValueWeights = inputValueWeights;
    }

    @Override
    public boolean hasStaticTraversal() {
        return false;
    }
    int k = 0;

    @Override
    public Connection getNextEdge(Circuit g) {
        k++;
        if (kernel == null) {
            kernel = GraphUtils.setKernel(g, honeycomb);
        }
        if (k > g.getConnections().size() * 2) {
            return null;
        }

        double d = Double.MAX_VALUE;
        Connection sel = null;
        for (Connection e : g.getConnections()) {
            if ((e.getA().isFixed() && !e.getB().isFixed()) || (!e.getA().isFixed() && e.getB().isFixed())) {
                double dk;
//                dk = e.getSource().getPos().distance(e.getDestination().getPos());
                dk = kernel.getPos().distance(e.getA().getPos().midpoint(e.getB().getPos()));
//                dk = e.getSource().getAdjacencies().size() + e.getDestination().getAdjacencies().size();
                if (sel == null || dk < d) {
                    d = dk;
                    sel = e;
                }
            }
        }
//        d = Double.MAX_VALUE;
//        if (sel == null) {
//            for (Edge e : g.getEdges()) {
//                if (e.getSource().isFixed() && e.getDestination().isFixed() && !honeycomb.isSatisfied(e)) {
//                    double dk = 0;
////                dk = e.getSource().getPos().distance(e.getDestination().getPos());
//                    dk = kernel.getPos().distance(e.getSource().getPos().midpoint(e.getDestination().getPos()));
//                    if (sel == null || dk < d) {
//                        d = dk;
//                        sel = e;
//                    }
//                }
//            }
//        }

        return sel;
    }

    @Override
    public List<Connection> getTraversing(Circuit g) {
        kernel = GraphUtils.setKernel(g, honeycomb);

        if (kernel == null) {
            return new ArrayList<>();
        }

        List<Connection> elist = new ArrayList<>(g.getConnections());

        Collections.sort(elist, new Comparator<Connection>() {
            @Override
            public int compare(Connection e1, Connection e2) {
                double e1m = kernel.getPos().distance(e1.getA().getPos().midpoint(e1.getB().getPos()));
                double e2m = kernel.getPos().distance(e2.getA().getPos().midpoint(e2.getB().getPos()));
                return e1m == e2m ? 0 : (e1m < e2m ? -1 : 1);
            }
        });

        for (Connection e : elist) {
            System.out.println(kernel.getPos().distance(e.getA().getPos().midpoint(e.getB().getPos())));
        }
        return elist;
    }

    @Override
    public double[] decisionMaker(double[] perception) {
        int nbCount = honeycomb.getMaxNeighborCount();
        return java.util.Arrays.copyOf(perception, nbCount * 2);
    }

    @Override
    public double[] generatePerception(Circuit g, Connection e) {
        Component fixed = e.getB().isFixed() ? e.getB() : e.getA();
        if (!e.getA().isFixed() && !e.getB().isFixed()) {
            if (e.getA().getPos().distance(kernel.getPos()) < e.getB().getPos().distance(kernel.getPos())) {
                fixed = e.getA();
            } else {
                fixed = e.getB();
            }
        }
        List<Vector> nb = honeycomb.getNeighborhood(fixed.getPos());

        int size = nb.size();
        double[] perception = new double[size * 2 + 1];

        /*
         [!] The value of a perceptor is directly proportional to the amount of
         satisfied connections to be satisfied, and vary inversaly to the
         distance from the graph kernel and the sum of non-satisfied
         neighbor connections.
         */
        int i = 0;
        for (Vector v : nb) {
            if (honeycomb.getNode(v) != null) {
                perception[i] = 0;
                perception[i + size] = 0;
            } else if (e.getA().isFixed() && e.getB().isFixed()) {
                perception[i] = 0;
                perception[i + size] = 0;//e.getLength() / (v.distance(fixed.getPos()) + v.distance(e.getOther(fixed).getPos()));

            } else {

                double data[] = new double[5];
                data[0] = honeycomb.getSatisfiedConnectionCount(e, v) / honeycomb.getMaximumSatisfiedConnectionCount();
                data[1] = honeycomb.getUnsolvedNeighborConnectionCount(e, v) / honeycomb.getMaximumUnsolvedNeighborConnectionCount();
                data[2] = GraphUtils.getVolume(g, honeycomb) / GraphUtils.getNewVolumeWithPoint(g, honeycomb, null, v)[0];
                data[3] = e.getLength() / (v.distance(fixed.getPos()) + v.distance(e.getOtherComponent(fixed).getPos()));
                data[4] = 1 / kernel.getPos().distance(v);

                perception[i] = computeInputValue(data, inputValueWeights);
                perception[i + size] = 0;
            }

            i++;
        }

        return perception;
    }

    public double computeInputValue(double[] data, double[] weights) {
        double value = 0;
        for (int i = 0; i < data.length; i++) {
            value += GraphFolder.check(data[i], i) * weights[i];
        }
//        System.out.println(Arrays.toString(data));
//        System.out.println(Arrays.toString(weights));
//        System.out.println("V: " + value);
        return value;
    }

    @Override
    public double performAction(double[] actionsScore, Circuit g, Connection e) {
        int id = -1;
        double maxVal = 0;
        for (int i = 0; i < actionsScore.length; i++) {
            double b = actionsScore[i];
            if (b > maxVal) {
                maxVal = b;
                id = i;
            }
        }

        if (id < 0) {
//            if (honeycomb.isSatisfied(e)) {
//                System.err.println("Already solved");
//            } else {
//                System.err.println("Impossible move =(");
//            }
            throw new RuntimeException("impossible Move:" + Arrays.toString(actionsScore));
//            return -1;
        }

        int nbCount = honeycomb.getMaxNeighborCount();
        double actionScore = 0;

        Component fixed = e.getA().isFixed() ? e.getA() : e.getB();
        Vector fPos = fixed.getPos();
        if (!fixed.isFixed()) {
            fPos = honeycomb.getClosestNeighbor(fPos);
        }

        Vector pos;
        if (id >= nbCount) {
            //addNode
            pos = honeycomb.getNeighborhood(fPos).get(id - nbCount);
            Component newNode = GraphUtils.addNodeBetween(g, fixed, e.getOtherComponent(fixed), new Component());
            newNode.setFixed(true);
            newNode.setPos(pos);
            honeycomb.setNodePlaced(pos, newNode);
        } else {
            //fixNode
            pos = honeycomb.getNeighborhood(fPos).get(id);
            e.getOtherComponent(fixed).setFixed(true);
            e.getOtherComponent(fixed).setPos(pos);
            honeycomb.setNodePlaced(pos, e.getOtherComponent(fixed));
        }
        return actionScore;
    }
}
