/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core;

import java.util.Vector;

/**
 *
 * @author antunes
 */
public class Component {

    public String name;
    public Object data;
    public boolean joint;
    public boolean consumed;
    public boolean fixed;
    private Vector<Connection> conns;
    private Vector<Component> shortcut;

    public Component() {
        conns = new Vector();
        shortcut = new Vector();
    }

    public void addConnection(Connection c) {

    }

    public void removeConnection(Connection c) {

    }

}
