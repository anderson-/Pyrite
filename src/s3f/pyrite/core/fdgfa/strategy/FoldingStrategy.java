/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core.fdgfa.strategy;

import java.util.List;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Connection;

/**
 *
 * @author andy
 */
public interface FoldingStrategy {

    public boolean hasStaticTraversal();

    public Connection getNextEdge(Circuit g);

    public List<Connection> getTraversing(Circuit g);

    public double[] decisionMaker(double[] perception);

    public double[] generatePerception(Circuit g, Connection e);

    public double performAction(double[] actionsScore, Circuit g, Connection e);
}
