package s3f.pyrite.core.fdgfa;

import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.Connection;
import s3f.pyrite.core.fdgfa.fdgs.ParticleProperty;
import s3f.pyrite.core.fdgfa.honeycomb.ConvexUniformHoneycomb;
import s3f.pyrite.util.Vector;

public class GraphUtils {

    public static double getDistance(ParticleProperty n1, ParticleProperty n2) {
        return n1.getPos().distance(n2.getPos());
    }

    public static double getVolume(Circuit g, ConvexUniformHoneycomb cuh) {
        double x0, y0, z0, x, y, z, x1, y1, z1;
        x0 = y0 = z0 = Double.POSITIVE_INFINITY;
        x1 = y1 = z1 = Double.NEGATIVE_INFINITY;
        for (Component n : g.getComponents()) {
            if (n.isFixed()) {
                Vector v = n.getPos();
                x = v.getX();
                y = v.getY();
                z = v.getZ();
                x0 = (x < x0) ? x : x0;
                y0 = (y < y0) ? y : y0;
                z0 = (z < z0) ? z : z0;
                x1 = (x > x1) ? x : x1;
                y1 = (y > y1) ? y : y1;
                z1 = (z > z1) ? z : z1;
            }
        }
        return (int) (((x1 - x0 + 1) * (y1 - y0 + 1) * (z1 - z0 + 1)) / cuh.getCellVolume()) - 1;
    }

    public static double[] getNewVolumeWithPoint(Circuit g, ConvexUniformHoneycomb cuh, double[] limits, Vector p) {
        double x0, y0, z0, x, y, z, x1, y1, z1;
        if (limits == null) {
            limits = new double[7];
            x0 = y0 = z0 = Double.POSITIVE_INFINITY;
            x1 = y1 = z1 = Double.NEGATIVE_INFINITY;
            for (Component n : g.getComponents()) {
                if (n.isFixed()) {
                    Vector v = n.getPos();
                    x = v.getX();
                    y = v.getY();
                    z = v.getZ();
                    x0 = (x < x0) ? x : x0;
                    y0 = (y < y0) ? y : y0;
                    z0 = (z < z0) ? z : z0;
                    x1 = (x > x1) ? x : x1;
                    y1 = (y > y1) ? y : y1;
                    z1 = (z > z1) ? z : z1;
                }
            }
            limits[1] = x0;
            limits[2] = x1;
            limits[3] = y0;
            limits[4] = y1;
            limits[5] = z0;
            limits[6] = z1;
        } else {
            x0 = limits[1];
            x1 = limits[2];
            y0 = limits[3];
            y1 = limits[4];
            z0 = limits[5];
            z1 = limits[6];
        }
        x = p.getX();
        y = p.getY();
        z = p.getZ();
        x0 = (x < x0) ? x : x0;
        y0 = (y < y0) ? y : y0;
        z0 = (z < z0) ? z : z0;
        x1 = (x > x1) ? x : x1;
        y1 = (y > y1) ? y : y1;
        z1 = (z > z1) ? z : z1;
        limits[0] = ((x1 - x0 + 1) * (y1 - y0 + 1) * (z1 - z0 + 1)) / cuh.getCellVolume();
        return limits;
    }

    public static double scoreDistribution(Circuit g, ConvexUniformHoneycomb cuh, Vector center, int maxOffset) {
        double x0, y0, z0, x, y, z, x1, y1, z1;
        x0 = y0 = z0 = Double.POSITIVE_INFINITY;
        x1 = y1 = z1 = Double.NEGATIVE_INFINITY;
        for (Component n : g.getComponents()) {
            if (n.isFixed()) {
                Vector v = n.getPos();
                x = v.getX();
                y = v.getY();
                z = v.getZ();
                x0 = (x < x0) ? x : x0;
                y0 = (y < y0) ? y : y0;
                z0 = (z < z0) ? z : z0;
                x1 = (x > x1) ? x : x1;
                y1 = (y > y1) ? y : y1;
                z1 = (z > z1) ? z : z1;
            }
        }
        x = center.getX();
        y = center.getY();
        z = center.getZ();
        double score = 0;
        score += Math.abs((x - x0) - (x1 - x)) / cuh.getShortestDistance();
        score += Math.abs((y - y0) - (y1 - y)) / cuh.getShortestDistance();
        score += Math.abs((z - z0) - (z1 - z)) / cuh.getShortestDistance();
        //normalize
        score = (maxOffset - score) / maxOffset;
        return score < 0 ? 0 : score;
    }

    public static Vector getDefaultKernelPos() {
        return new Vector();
    }

    public static Component setKernel(Circuit g, ConvexUniformHoneycomb cuh) {
        Component kernel = null;
        for (Component n : g.getComponents()) {
            if (kernel == null || n.getConnections().size() > kernel.getConnections().size()) {
                kernel = n;
            }
        }
        if (kernel == null) {
            return null;
        } else {
            kernel.setFixed(true);
            kernel.setPos(getDefaultKernelPos());
            cuh.setNodePlaced(kernel.getPos(), kernel);
            GraphFolder.delay();
            return kernel;
        }
    }

    public static int countAddedNodes(Circuit g) {
        int an = 0;
        for (Component n : g.getComponents()) {
            //TODO:
            //an += n.getType() == GraphFolder.NODE_TYPE_NEW_EXTENSION ? 1 : 0;
        }
        return an;
    }

    public static int countPlacedNodes(Circuit g) {
        int pn = 0;
        for (Component n : g.getComponents()) {
            pn += n.isFixed() ? 1 : 0;
        }
        return pn;
    }

    public static int countUnplacedNodes(Circuit g) {
        return g.getComponents().size() - countPlacedNodes(g);
    }

    public static int countUnsolvedConnections(Circuit g, ConvexUniformHoneycomb cuh) {
        return g.getConnections().size() - countSatisfiedConnections(g, cuh);
    }

    public static int countSatisfiedConnections(Circuit g, ConvexUniformHoneycomb cuh) {
        int sc = 0;
        for (Connection e : g.getConnections()) {
            sc += cuh.isSatisfied(e) ? 1 : 0;
        }
        return sc;
    }
//TODO:

    public static Component addNodeBetween(Circuit graph, Component n1, Component n2, Component newNode) {
//        graph.disconnect(n1, n2);
//        newNode.setPos(n1.getPos().midpoint(n2.getPos()));
//        graph.addNode(newNode);
//        graph.connect(n1, newNode, 100);
//        graph.connect(n2, newNode, 100);
//        return newNode;
        throw new RuntimeException("Not implemented yet");
    }
}
