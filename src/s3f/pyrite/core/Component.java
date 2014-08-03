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
public class Component extends Fixable {

    private String name;
    private Object data;
    private int [] pos;
    private boolean joint;
    private Vector<Connection> conns;
    private Vector<Component> shortcut;

    public Component(String name, Object data) {
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
    }

    public void addConnection(Connection c) {
        if (!conns.contains(c)) {
            conns.add(c);
        }
    }

    public void removeConnection(Connection c) {
        conns.remove(c);
    }
    
    public List<Connection> getConnections(){
        return conns;
    }
    
    public void addShortcut(Component c) {
        if (!shortcut.contains(c)) {
            shortcut.add(c);
        }
    }

    public void removeShortcut(Component c) {
        shortcut.remove(c);
    }

    public int[] getPos() {
        return pos;
    }

    public void setPos(int[] pos) {
        this.pos = pos;
    }

    public static Component expand(Component v, Component j) {
        return null;
    }
    
}
