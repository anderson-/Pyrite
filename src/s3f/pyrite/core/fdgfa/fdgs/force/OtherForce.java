/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core.fdgfa.fdgs.force;

import s3f.pyrite.core.Connection;
import s3f.pyrite.core.fdgfa.fdgs.ParticleProperty;
import s3f.pyrite.util.Vector;

/**
 *
 * @author andy
 */
public class OtherForce implements ForceComputer{

    public static double maxAttraction = 100;
    public static double maxRepulsion = 100;

    // repulsive force to node
    // distance einbauen => wtf?
    @Override
    public Vector repulsiveForce(ParticleProperty a, ParticleProperty b) {
        Vector force = a.getPos().add(b.getPos().invert()); // force = a - b
        // (abs(a-b) =
        // distance!!!!)
        force = force.multiply(1 / Math.pow(force.absoluteValue(), 3)); // normalize
        force = force.multiply(a.getWeight() * b.getWeight()); // weighting

        if (force.absoluteValue() > maxRepulsion) { // reduce extraterrestrial
            // uberforces
//            System.out.println("repulsive uberforce: " + force.absoluteValue());
            force = force.multiply(maxRepulsion / force.absoluteValue());
        }

//        Vector forceK = position.add(node.getPos().invert()); // force = a - b
//        forceK = forceK.multiply(1 / Math.pow(forceK.absoluteValue(), 3)); // normalize
//        forceK = forceK.multiply((50 - position.add(node.getPos().invert()).absoluteValue()) * k);
//        force.add(forceK);
//        
//        int[] closestLatticeNode = getClosestLatticeNode(this, 50, 50);
//        int[] closestLatticeNode2 = getClosestLatticeNode(node, 50, 50);
//
//        if (Arrays.equals(closestLatticeNode, closestLatticeNode2)) {
//            Vector grid = new Vector(closestLatticeNode);
//
//            Vector n1 = grid.add(position.invert());
//            Vector n2 = grid.add(node.getPos().invert());
//
//            if (n1.absoluteValue() > n2.absoluteValue()) {
//                double d = n1.absoluteValue();
//                n1 = n1.multiply(1 / Math.pow(n1.absoluteValue(), 0.5)); // normalize
//                if (d > 0) {
//                    d = 1 / d;
//                } else {
//                    d = 100;
//                }
//                force.add(n1.multiply(G * d * 1000));
//            }
//        }

        return force;
    }
    double k = 2;
    public static double G = 0;

    // sum of attractive forces to all adjacencies
    @Override
    public Vector attractiveForce(ParticleProperty node) {
        Vector force = new Vector(0, 0, 0);
        Vector attraction;
        for (Connection con : node.getAdjacencies()) {
            attraction = node.getPos().add(con.getOtherComponent(node.getComponent()).getPos().invert()); // force
//            // =
//            // a
//            // -
//            // b
            attraction = attraction.multiply(1 / Math.pow(attraction.absoluteValue(), 0.5)); // normalize
            force = force.add(attraction.multiply(10 /*con.getWeight()*/));

//            force = force.add(attraction.multiply(-(50 - position.add(edge.getDestination().getPos().invert()).absoluteValue()) * k));
        }

//        int[] closestLatticeNode = getClosestLatticeNode(this, 50, 50);
//
//        Vector attractionGrid = new Vector(closestLatticeNode).add(position.invert());
//        double d = attractionGrid.absoluteValue();
//        attractionGrid = attractionGrid.multiply(1 / Math.pow(attractionGrid.absoluteValue(), 0.5)); // normalize
//        if (d > 0) {
//            d = 1 / d;
//        } else {
//            d = 100;
//        }
//
//        force = force.add(attractionGrid.multiply(-G * d));

        // if (force.absoluteValue() > maxAttraction) { // reduce
        // extraterrestrial uberforces
        // System.out.println("attractive uberforce: "+force.absoluteValue());
        // force = force.multiply(maxAttraction / force.absoluteValue());
        // }
        return force.invert();
    }
    
}
