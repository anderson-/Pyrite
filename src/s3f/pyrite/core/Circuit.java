/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core;

import com.falstad.circuit.CircuitElm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import s3f.pyrite.ui.components.MyLogicInputElm;
import s3f.pyrite.ui.components.MyLogicOutputElm;

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
    private String status = "";

    public Circuit() {
        edges = new Vector<>();
        nodes = new Vector<>();
        inputs = new Vector<>();
        outputs = new Vector<>();
        subCircuits = new Vector<>();
    }

    public void addComponent(Component c) {
        if (c != null && !nodes.contains(c)) {
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
        if (c != null && !edges.contains(c)) {
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

    public void insert(Circuit sub, Component c) {
        subCircuits.add(sub);

        inputs.clear();
        outputs.clear();
        for (Component com : sub.nodes) {
            Object o = com.whut;
            if (o instanceof MyLogicInputElm) {
                inputs.add(com);
            } else if (o instanceof MyLogicOutputElm) {
                outputs.add(com);
            }
        }

        Collections.sort(inputs, new Comparator<Component>() {
            @Override
            public int compare(Component o1, Component o2) {
                return ((MyLogicInputElm) o1.whut).getName().compareTo(((MyLogicInputElm) o2.whut).getName());
            }
        });

        Collections.sort(outputs, new Comparator<Component>() {
            @Override
            public int compare(Component o1, Component o2) {
                return ((MyLogicOutputElm) o1.whut).getName().compareTo(((MyLogicOutputElm) o2.whut).getName());
            }
        });

        ArrayList<Component> all = new ArrayList<>();
        all.addAll(inputs);
        all.addAll(outputs);

        ArrayList<Object[]> relate = new ArrayList<>();
        for (Connection con : c.getConnections()) {
            int t = con.getTerminal(c);
            Component io = all.get(t);
            con.replace(c, io);
            relate.add(new Object[]{io, con});
        }

        for (Object[] pair : relate) {
            ((Component) pair[0]).addConnection((Connection) pair[1]);
        }

        for (Component com : all) {
            com.setName("?");
            com.setCoupler(true);
            com.whut = null;
            com.setData(null);
        }

        c.setConsumed(true);
        removeComponent(c);

        for (Component com : sub.getComponents()) {
            addComponent(com);
        }

        for (Connection con : sub.getConnections()) {
            addConnection(con);
        }

    }

    public void clean() {
        for (Iterator<Component> it = nodes.iterator(); it.hasNext();) {
            Component c = it.next();
            if (c.isConsumed()) {
                it.remove();
            }
        }
        for (Iterator<Connection> it = edges.iterator(); it.hasNext();) {
            Connection c = it.next();
            if (c.isConsumed()) {
                it.remove();
            }
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status != null) {
            this.status = status;
        }
    }
}
