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
package s3f.pyrite.util;

public class Vector {

    private double x, y, z;

    public Vector() {
        x = y = z = 0;
    }

    public Vector(Vector p) {
        this.x = p.getX();
        this.y = p.getY();
        this.z = p.getZ();
    }

    public Vector(int[] p) {
        this.x = p[0];
        this.y = p[1];
        this.z = p[2];
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector add(Vector p) {
        return new Vector(x + p.getX(), y + p.getY(), z + p.getZ());
    }

    public Vector abs() {
        return new Vector(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    public double sum() {
        return x + y + z;
    }

    public Vector multiply(double alpha) {
        return new Vector(x * alpha, y * alpha, z * alpha);
    }

    public Vector invert() {
        return new Vector(-x, -y, -z);
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
        z = 0;
    }

    public Vector(double rndboxsize) {
        this.x = Math.random() * rndboxsize - rndboxsize / 2;
        this.y = Math.random() * rndboxsize - rndboxsize / 2;
        this.z = Math.random() * rndboxsize - rndboxsize / 2;
    }

    @Override
    public String toString() {
        return "[x:" + round(x, 2) + " ; y:" + round(y, 2) + " ; z:" + round(z, 2) + "]"; // ToDo:
        // Stringbuilder
    }

    private double round(double val, int fraction) {
        double factor = Math.pow(10, fraction);
        return Math.round(val * factor) / factor;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setPos(Vector v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public void setPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double distance(Vector p) {
        return Math.sqrt(Math.pow(x - p.getX(), 2) + Math.pow(y - p.getY(), 2)
                + Math.pow(z - p.getZ(), 2));
    }

    public double absoluteValue() {
        // return distance(new Vector(0, 0, 0));
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector midpoint(Vector p) {
        return new Vector((x + p.getX()) / 2, (y + p.getY()) / 2, (z + p.getZ()) / 2);
    }

    public Vector get2D(double canvasWidth, double canvasHeight, double pseudoZoom) {
        return new Vector(get2Dx(canvasWidth, pseudoZoom), get2Dy(canvasHeight, pseudoZoom), 0);
    }

    public double get2Dx(double canvaswidth, double pseudoZoom) {
        return canvaswidth * (x * pseudoZoom / (z + canvaswidth)) + canvaswidth / 2;
    }

    public double get2Dy(double canvasheight, double pseudoZoom) {
        return canvasheight * (y * pseudoZoom / (z + canvasheight)) + canvasheight / 2;
    }

    public void rotateX(double beta) {
        double tmpy = y * Math.cos(beta) - z * Math.sin(beta);
        z = y * Math.sin(beta) + z * Math.cos(beta);
        y = tmpy;
    }

    public void rotateY(double beta) {
        double tmpx = z * Math.sin(beta) + x * Math.cos(beta);
        z = z * Math.cos(beta) - x * Math.sin(beta);
        x = tmpx;
    }

    public void rotateZ(double beta) {
        double tmpx = x * Math.cos(beta) - y * Math.sin(beta);
        y = y * Math.cos(beta) + x * Math.sin(beta);
        x = tmpx;
    }

    public Vector normalize() {
        return multiply(1 / Math.pow(absoluteValue(), 0.5));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector other = (Vector) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
            return false;
        }
        return true;
    }

}
