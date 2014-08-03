/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core;

import java.util.List;
import java.util.Vector;

/**
 *
 * @author antunes
 */
public class Circuit {

    private final Vector<Connection> edges;
    private final Vector<Component> nodes;
    private final Vector<Component> inputs;
    private final Vector<Component> outputs;

    public Circuit() {
        edges = new Vector<>();
        nodes = new Vector<>();
        inputs = new Vector<>();
        outputs = new Vector<>();
    }

    public void addComponent(Component c) {
        nodes.add(c);
    }

    public Component getComponent(String name) {
        return null;
    }

    public List<Component> getComponents() {
        return nodes;
    }

    public void removeComponent(Component c) {
        nodes.remove(c);
    }

    public boolean contains(Component c) {
        return nodes.contains(c);
    }

    public void addConnection(Component a, Component b, Connection c) {
        edges.add(c);
    }

    public List<Connection> getConnections() {
        return edges;
    }

    public void removeConnection(Connection c) {
        edges.remove(c);
    }

    public boolean contains(Connection c) {
        return edges.contains(c);
    }

    public void analyzeAndOptimize() {

    }

    public void clearOptimizations() {

    }

    public void saveModifications() {

    }

    public void revertModifications() {

    }

    public String save() {
        return "";
    }

    public void load(String text) {

    }

    public int getDisconectedConnections() {
        return 0;
    }

    public int calcVolume() {
        return 0;
    }

    public static Circuit union(Circuit[] cset, String... joints) {
        Circuit nc = new Circuit();
        return nc;
    }

}
