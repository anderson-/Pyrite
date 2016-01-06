/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core.fdgfa.honeycomb;

import s3f.pyrite.core.fdgfa.fdgs.ParticleProperty;
import s3f.pyrite.util.Vector;
import java.util.List;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.Connection;

/**
 *
 * @author andy
 */
public interface ConvexUniformHoneycomb {

    public interface Builder {

        public void drawNode(Vector p);

        public void drawEdge(Vector p1, Vector p2);
    }

    public double getCellVolume();
    
    public double getShortestDistance();
    
    public boolean isSatisfied(Connection e);

    public boolean isSatisfiedOnPosition(Connection e, Vector pos);

    public double normalizeDensity(int nodeCount, double volume);

    public int getMaxNeighborCount();

    public Component getNode(Vector point);

    public void setNodePlaced(Vector point, Component node);

    public List<Vector> getNeighborhood(Vector point);

    public Vector getClosestNeighbor(Vector point);

    public int getSatisfiedConnectionCount(Connection e, Vector pos);

    public int getUnsolvedNeighborConnectionCount(Connection e, Vector pos);

    public int getMaximumSatisfiedConnectionCount();

    public int getMaximumUnsolvedNeighborConnectionCount();

    public void spawn(Builder builder, int width, int height, int depth);
}
