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
public class DefaultForce implements ForceComputer {

    public static double maxAttraction = 100;
    public static double maxRepulsion = 100;

    // repulsive force to node
    // distance einbauen => wtf?
    @Override
    public Vector repulsiveForce(ParticleProperty a, ParticleProperty b) {
        Vector force = a.getPos().add(b.getPos().invert()); // force = a - b
        // (abs(a-b) = distance!!!!)
        force = force.multiply(1 / Math.pow(force.absoluteValue(), 3)); // normalize
        force = force.multiply(a.getWeight() * b.getWeight()); // weighting

        if (force.absoluteValue() > maxRepulsion) { // reduce extraterrestrial
            // uberforces
            force = force.multiply(maxRepulsion / force.absoluteValue());
        }

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
            attraction = node.getPos().add(con.getOtherComponent(node.getComponent()).getPos().invert()); // force = a-b
            attraction = attraction.multiply(1 / Math.pow(attraction.absoluteValue(), 0.5)); // normalize
            force = force.add(attraction.multiply(10 /*con.getWeight()*/));
        }
        return force.invert();
    }

}
