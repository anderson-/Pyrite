/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core;

import com.falstad.circuit.elements.WireElm;

/**
 *
 * @author antunes
 */
public class Connection extends Fixable {

    public Object whut = null;
    private static int ID = 0;
    //id
    private int uid;
    private Component a;
    private Component b;
    private int terminalA;
    private int terminalB;
    private String subComponent;
    private boolean satisfied;

    @Deprecated //adicionar manualmente
    public Connection(String subComponent) {
        uid = ID++;
        this.subComponent = subComponent;
        satisfied = false;
    }

    public Connection(Component a, int terminalA, Component b, int terminalB, String subComponent) {
        this(subComponent);
        this.a = a;
        this.b = b;
        this.terminalA = terminalA;
        this.terminalB = terminalB;
        a.addConnection(this);
        b.addConnection(this);
    }

    public Connection(Component a, Component b, String subComponent) {
        this(a, 0, b, 0, subComponent);
    }

    public Connection(Component a, Component b) {
        this(a, 0, b, 0, "");
    }

    public Component getA() {
        return a;
    }

    public void setA(Component a) {
        this.a = a;
    }

    public Component getB() {
        return b;
    }

    public void setB(Component b) {
        this.b = b;
    }

    public int getTerminalA() {
        return terminalA;
    }

    public void setTerminalA(int terminalA) {
        this.terminalA = terminalA;
    }

    public int getTerminalB() {
        return terminalB;
    }

    public int getTerminal(Component c) {
        if (c == a) {
            return terminalA;
        } else {
            return terminalB;
        }
    }

    public void setTerminalB(int terminalB) {
        this.terminalB = terminalB;
    }

    @Override
    public void setConsumed(boolean consumed) {
        super.setConsumed(consumed);
        if (consumed) {
            a.removeConnection(this);
            b.removeConnection(this);
        }
    }

    public String getSubComponent() {
        return subComponent;
    }

    public void setSubComponent(String subComponent) {
        this.subComponent = subComponent;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    public void setSatisfied(boolean satisfied) {
        this.satisfied = satisfied;
    }

    public boolean isShort() {
        return subComponent.isEmpty() && (whut == null || whut instanceof WireElm);
    }

    @Override
    public String toString() {
        return a.getUID() + " [" + terminalA + "]" + (subComponent.isEmpty() ? " -> " : " --(" + subComponent + ")-> ") + "[" + terminalB + "] " + b.getUID() + "." + whut + ".";
    }

    public Component getOtherComponent(Component c) {
        if (a == c) {
            return b;
        } else {
            return a;
        }
    }

    public void replace(Component c, Component other) {
        if (a == c) {
            setA(other);
        } else {
            setB(other);
        }
    }

    void setTerminal(Component c, int terminal) {
        if (a == c) {
            setTerminalA(terminal);
        } else {
            setTerminalB(terminal);
        }
    }

    void setOtherTerminal(Component c, int terminal) {
        if (a != c) {
            setTerminalA(terminal);
        } else {
            setTerminalB(terminal);
        }
    }

    public Connection shiftA(Component splitter) {
        Connection con = splitter.createConnection(a);
        con.setOtherTerminal(splitter, terminalA);
        terminalA = 0;
        a = splitter;
        return con;
    }

    public void swap() {
        Component t = a;
        a = b;
        b = t;
        int i = terminalA;
        terminalA = terminalB;
        terminalB = i;
    }
}
