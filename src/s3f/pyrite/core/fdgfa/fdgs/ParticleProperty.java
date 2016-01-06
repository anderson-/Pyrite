/*
    Copyright (c) 2013, 2014 pachacamac
                  2015, 2016 Anderson Antunes

    This file is part of jg3d.

    jg3d is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jg3d is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package s3f.pyrite.core.fdgfa.fdgs;

import s3f.pyrite.util.Vector;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.Connection;

public class ParticleProperty {

    private Component component;
    private Vector projection; // projection (z=0)

    private double weight = 80; // ToDo
    private double diameter = 20; // todo

    private String name;
    private Color color;

    private Vector velocity;
    private Vector selfforce;

    public ParticleProperty(Component component, String name, Color color) {
        this.component = component;
        this.name = name;
        this.color = color;
        velocity = new Vector(0, 0, 0);
        selfforce = new Vector(0, 0, 0);
    }

    public Component getComponent() {
        return component;
    }

    public boolean isFixed() {
        return component.isFixed();
    }

    public void setFixed(boolean fixed) {
        component.setFixed(fixed);
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double w) {
        this.weight = w;
    }

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public double getRadius() {
        return getDiameter() / 2;
    }

    public double getKE() {
        if (isFixed()) {
            return 0;
        }
        return velocity.absoluteValue() * weight;
    }

    public void setColor(Color c) {
        this.color = c;
    }

//    public int getAlpha() {
//        int alpha = (int) (127 - position.getZ());
//        if (alpha < 5) {
//            alpha = 5;
//        } else if (alpha > 250) {
//            alpha = 250;
//        }
//        alpha = (position.getZ() < -100) ? 0 : alpha;
//        return alpha;
//    }
    public Color getColor() {
        return color;
    }

    public void setName(String n) {
        this.name = n;
    }

    public void setPos(Vector p) {
        component.getPos().setPos(p);
    }

    public Vector getPos() {
        return component.getPos();
    }

    public List<Connection> getAdjacencies() {
        return component.getConnections();
    }

    public String getName() {
        return name;
    }

    public void project(double canvasWidth, double canvasHeight, double pseudoZoom) {
        projection = component.getPos().get2D(canvasWidth, canvasHeight, pseudoZoom);
    }

    @Override
    public String toString() {
        return new StringBuilder().append('[').append(name).append(']').toString();
    }

    public Vector getProjection() {
        return projection;
    }

    public void affect(Vector force) {
        if (!isFixed()) {
            velocity = velocity.add(force.multiply(1 / weight)); // inertia
            Vector friction = velocity.multiply(0.025); // 2.5% friction
            velocity = velocity.add(friction.invert());
            velocity = velocity.add(selfforce);
            component.getPos().setPos(component.getPos().add(velocity));
        }
    }

    public void alterSelfForceX(double d) {
        selfforce.setX(selfforce.getX() + d);
    }

    public void alterSelfForceY(double d) {
        selfforce.setY(selfforce.getY() + d);
    }

    public void alterSelfForceZ(double d) {
        selfforce.setZ(selfforce.getZ() + d);
    }

    public void setSelfForce(Vector v) {
        selfforce = v;
    }

    public void setSelfForceX(double d) {
        selfforce.setX(d);
    }

    public void setSelfForceY(double d) {
        selfforce.setY(d);
    }

    public void setSelfForceZ(double d) {
        selfforce.setZ(d);
    }

}
