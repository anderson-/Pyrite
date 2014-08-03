/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core;

/**
 *
 * @author antunes
 */
public class Connection extends Fixable {

    private Component a;
    private Component b;
    private String terminalA;
    private String terminalB;
    private String subComponent;
    private boolean satisfied;

    public Connection(String subComponent) {
        this.subComponent = subComponent;
        satisfied = false;
    }

    public Connection(Component a, String terminalA, Component b, String terminalB, String subComponent) {
        this(subComponent);
        this.a = a;
        this.b = b;
        this.terminalA = terminalA;
        this.terminalB = terminalB;
    }

    public Connection(Component a, Component b, String subComponent) {
        this(a, "", b, "", subComponent);
    }

    public Connection(Component a, Component b) {
        this(a, "", b, "", "");
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

    public String getTerminalA() {
        return terminalA;
    }

    public void setTerminalA(String terminalA) {
        this.terminalA = terminalA;
    }

    public String getTerminalB() {
        return terminalB;
    }

    public void setTerminalB(String terminalB) {
        this.terminalB = terminalB;
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
        return subComponent.isEmpty();
    }

    @Override
    public String toString() {
        return a + " [" + terminalA + "] --(" + subComponent + ")-> [" + terminalB + "] " + b + " (" + satisfied + ")";
    }

    public Component getOtherComponent(Component v) {
        if (a == v) {
            return b;
        } else {
            return a;
        }
    }

}
