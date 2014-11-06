/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core.cycle;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import processing.core.PMatrix3D;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.GridFittingTool;

/**
 *
 * @author anderson
 */
public abstract class BreadcrumbPlacer implements GridFittingTool {

    public static final int RAND = 0;
    public static final int MAX = 1;
    public static final int MIN = 2;
    public static final int INDEX = 3;

    public static final int UP = 4;
    public static final int DOWN = 5;
    public static final int BACK = 6;
    public static final int LEFT = 7;
    public static final int RIGHT = 8;
    
    public static final int X = 9;
    public static final int Y = 10;
    public static final int Z = 11;

    private int index = 0;
    private int mode = MAX;

    private int[] pos;
    private int[] dir;
    private PMatrix3D matrix;
    private Iterator<Component> it;
    private boolean rightAngle = true; //use 45 degree look angle

    public BreadcrumbPlacer() {
        pos = new int[]{0, 0, 0};
        dir = new int[]{1, 0, 0};
        matrix = new PMatrix3D();
    }

    public void reset() {
        pos[0] = 0;
        pos[1] = 0;
        pos[2] = 0;
        dir[0] = 1;
        dir[1] = 0;
        dir[2] = 0;
        matrix.reset();
    }

    public void setPosition(int... pos) {
        this.pos = Arrays.copyOf(pos, 3);
    }

    public void setDirection(int[] dir) {
        this.dir = Arrays.copyOf(dir, 3);
    }

    public void setDirection(int axis) {
        switch (axis){
            case X:
                setDirection(1, 0, 0);
                break;
            case -X:
                setDirection(-1, 0, 0);
                break;
            case Y:
                setDirection(0, 1, 0);
                break;
            case -Y:
                setDirection(0, -1, 0);
                break;
            case Z:
                setDirection(0, 0, 1);
                break;
            case -Z:
                setDirection(0, 0, -1);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
    
    public void setDirection(int x, int y, int z) {
        this.dir[0] = x;
        this.dir[1] = y;
        this.dir[2] = z;
    }

    public void setRightAngle(boolean rightAngle) {
        this.rightAngle = rightAngle;
    }

    public void look(int dir) {
        float angle;
        if (rightAngle) {
            angle = (float) (Math.PI / 2);
        } else {
            angle = (float) (Math.PI / 4);
        }
        switch (dir) {
            case UP:
                BreadcrumbPlacer.this.look(0, -angle);
                break;
            case DOWN:
                BreadcrumbPlacer.this.look(0, angle);
                break;
            case BACK:
                BreadcrumbPlacer.this.look((float) Math.PI, 0);
                break;
            case LEFT:
                BreadcrumbPlacer.this.look(-angle, 0);
                break;
            case RIGHT:
                BreadcrumbPlacer.this.look(angle, 0);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void look(int d1, int d2) {
        look(d1);
        look(d2);
    }

    private void look(float yaw, float pitch) {
        matrix.reset();
        matrix.rotate(pitch, 0, 1, 0);
        matrix.rotate(yaw, 0, 0, 1);
        float[] f = new float[]{dir[0], dir[1], dir[2]};
        matrix.mult(Arrays.copyOf(f, 3), f);
        dir[0] = Math.round(f[0]);
        dir[1] = Math.round(f[1]);
        dir[2] = Math.round(f[2]);
        System.out.println(Arrays.toString(f));
        System.out.println(Arrays.toString(dir));

    }

    public void move(int n) {
        for (int i = 0; i < n; i++) {
            pos[0] += dir[0];
            pos[1] += dir[1];
            pos[2] += dir[2];
        }
    }

    public void place(int n) throws OutOfElementsException {
        for (int i = 0; i < n; i++) {
            if (it.hasNext()) {
                Component next = it.next();
                next.setPos(pos[0], pos[1], pos[2]);
            } else {
                throw new OutOfElementsException();
            }
            pos[0] += dir[0];
            pos[1] += dir[1];
            pos[2] += dir[2];
        }
    }

    @Override
    public void fit(Circuit circuit) {
        System.out.println("run");
        List<int[]> cycles = GraphCycleFinder.run(circuit, 100);
        System.out.println("done");
        int[] c = null;
        if (mode == RAND) {
            c = cycles.get((int) (cycles.size() * Math.random()));
        } else if (mode == INDEX) {
            c = cycles.get(index);
        } else {
            for (int[] cycle : cycles) {
                if (c == null) {
                    c = cycle;
                }
                if (mode == MAX && cycle.length > c.length) {
                    c = cycle;
                } else if (mode == MIN && cycle.length < c.length) {
                    c = cycle;
                }
            }
        }
        place(new Cycle(circuit, c));
    }

    public void place(Cycle cycle) {
        it = cycle.iterator();
        try {
            run(cycle);
        } catch (OutOfElementsException e) {
            //done
        }
    }

    protected abstract void run(Cycle cycle) throws OutOfElementsException;

    protected static final class OutOfElementsException extends Exception {

    }

    public static final BreadcrumbPlacer SQUARE = new BreadcrumbPlacer() {

        @Override
        protected void run(Cycle cycle) throws OutOfElementsException {
            int side = cycle.getLength() / 4 + 1;
            place(side);
            look(LEFT);
            place(side);
            look(LEFT);
            place(side);
            look(LEFT);
            place(side);
        }
    };

    public static final BreadcrumbPlacer OCT = new BreadcrumbPlacer() {

        @Override
        protected void run(Cycle cycle) throws OutOfElementsException {
            int side = cycle.getLength() / 8 + 1;
            setRightAngle(false);
            place(side);
            look(LEFT);
            place(side);
            look(LEFT);
            place(side);
            look(LEFT);
            place(side);
            look(LEFT);
            place(side);
            look(LEFT);
            place(side);
            look(LEFT);
            place(side);
            look(LEFT);
            place(side);
        }
    };

    public static final BreadcrumbPlacer CUBE = new BreadcrumbPlacer() {

        @Override
        protected void run(Cycle cycle) throws OutOfElementsException {
            int height = cycle.getLength() / 10;
            int side = (cycle.getLength() - 2 * height) / 8;
            setDirection(Z);
            place(height);
            setDirection(X);
            place(side);
            setDirection(Y);
            place(side);
            setDirection(-X);
            place(side);
            setDirection(-Y);
            place(side - 1);
            setDirection(-Z);
            place(height);
            setDirection(Y);
            place(side - 1);
            setDirection(X);
            place(side);
            setDirection(-Y);
            place(side);
            setDirection(-X);
            place(side - 1);
            setDirection(Z);
            place(height);
        }
    };
    
    public static final BreadcrumbPlacer ASD = new BreadcrumbPlacer() {

        @Override
        protected void run(Cycle cycle) throws OutOfElementsException {
            int side = (cycle.getLength()) / 4;
            look(RIGHT);
            place(1);
            look(RIGHT);
            place(1);
            look(LEFT);
            place(1);
            look(LEFT);
            place(1);
            
        }
    };

    public static final BreadcrumbPlacer TEST = CUBE;

}
