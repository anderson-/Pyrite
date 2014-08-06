/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author antunes
 */
public class Component extends Fixable {

    private static int ID = 0;
    //id
    private int uid;
    public Component previous;
    public int distance;
    private String name;
    private Object data;
    private int[] pos;
    private boolean coupler = false;
    private Vector<Connection> conns;
    private Vector<Component> shortcut;

    public Component(String name, Object data) {
        uid = ID++;
        conns = new Vector<>();
        shortcut = new Vector<>();
        this.name = name;
        this.data = data;
    }

    public Component(String name) {
        this(name, null);
    }

    public Component() {
        this("", null);
        coupler = true;
    }

    public Connection createConnection(Component c) {
        return new Connection(this, c);
    }

    @Deprecated
    public void addConnection(Connection c) {
        if (!conns.contains(c)) {
            conns.add(c);
        }
    }

    public void removeConnection(Connection c) {
        conns.remove(c);
    }

    public Connection getConnection(Component b) {
        for (Connection c : conns) {
            if (c.getOtherComponent(this) == b) {
                return c;
            }
        }
        return null;
    }

    public List<Connection> getConnections() {
        return conns;
    }

    public void addShortcut(Component c) {
        if (!shortcut.contains(c)) {
            shortcut.add(c);
            if (!c.getShortcuts().contains(this)) {
                c.getShortcuts().add(this);
            }
        }
    }

    public void removeShortcut(Component c) {
        shortcut.remove(c);
    }

    public List<Component> getShortcuts() {
        return shortcut;
    }

    public int[] getPos() {
        return pos;
    }

    public void setPos(int[] pos) {
        this.pos = pos;
    }

    public boolean isCoupler() {
        return coupler;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        coupler = false;
    }

    @Deprecated
    public void setCoupler(boolean coupler) {
        this.coupler = coupler;
    }

    public String getUID() {
        return uid + (name.isEmpty() ? "" : "(" + name + ")");
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return (coupler ? "+'{" : "+{") + uid + " |" + name + ", '" + data + "', " + Arrays.toString(pos) + ", c = " + conns.size() + ", s = " + shortcut.size() + "}";
    }

    public void appendAndConsume(Component c) {
        if (coupler && c.coupler) {
            for (Connection con : c.getConnections()) {
                con.replace(c, this);
            }

            this.conns.addAll(c.getConnections());
            c.setConsumed(true);

//            this.FIXED_terminals.addAll(c.FIXED_terminals);
//            this.FIXED_connections.addAll(c.FIXED_connections);
//            this.FIXED_subComponents.addAll(c.FIXED_subComponents);
//            this.FIXED_doneConnections.addAll(c.FIXED_doneConnections);
//            this.fixed = true;
        } else {
            throw new IllegalArgumentException("this or c is not an contact");
        }
    }

    public static Component expand(Component v, Component j) {
        return null;
    }
}
