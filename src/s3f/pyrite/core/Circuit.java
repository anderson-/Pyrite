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

    public static final int INPUT = 1;
    public static final int OUTPUT = 2;

    private final Vector<Connection> edges;
    private final Vector<Component> nodes;
    private final Vector<Component> inputs;
    private final Vector<Component> outputs;
    private final Vector<Circuit> subCircuits;

    public Circuit() {
        edges = new Vector<>();
        nodes = new Vector<>();
        inputs = new Vector<>();
        outputs = new Vector<>();
        subCircuits = new Vector<>();
    }

    public void addComponent(Component c) {
        if (!nodes.contains(c)) {
            nodes.add(c);
            for (Connection con : c.getConnections()) {
                addConnection(con);
            }
        }
    }

    public void addComponent(Component c, int type) {
        if (type == INPUT) {
            inputs.add(c);
        } else if (type == OUTPUT) {
            outputs.add(c);
        }
        nodes.add(c);
    }

    public Component getComponent(String name) {
        for (Component c : nodes) {
            if (name.equals(c.getName())) {
                return c;
            }
        }
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

    public void addConnection(Connection c) {
        if (!edges.contains(c)) {
            edges.add(c);
        }
    }

    public List<Connection> getConnections() {
        return edges;
    }

    public void removeConnection(Connection c) {
        c.setConsumed(true);
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
