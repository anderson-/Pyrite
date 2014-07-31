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
public class Connection {

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

    public Connection(Component a, Component b, String subComponent) {
        this(subComponent);
        this.a = a;
        this.b = b;
    }

    public Connection(Component a, Component b) {
        this(a, b, "");
    }

}
