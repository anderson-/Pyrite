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
public class Circuit {

    private final Vector<Component> nodes;
    private final Vector<Connection> edges;

    public Circuit() {
        nodes = new Vector<>();
        edges = new Vector<>();
    }

    public void addComponent(Component c) {
        nodes.add(c);
    }

    public Component getComponent(String name) {
        return null;
    }

    public Component getComponent(int index) {
        return nodes.get(index);
    }

    public int getComponentCount() {
        return nodes.size();
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

    public Connection getConnection(String name) {
        return null;
    }

    public Connection getConnection(int index) {
        return edges.get(index);
    }

    public int getConnectionCount() {
        return edges.size();
    }

    public void removeConnection(Connection c) {
        edges.remove(c);
    }

    public boolean contains(Connection c) {
        return edges.contains(c);
    }
    
    public void analyzeAndOptimize(){
        
    }
    
    public void clearOptimizations(){
        
    }

    public void saveModifications(){
        
    }
    
    public void revertModifications() {

    }

    public String save() {
        return "";
    }

    public void load(String text) {

    }

}
