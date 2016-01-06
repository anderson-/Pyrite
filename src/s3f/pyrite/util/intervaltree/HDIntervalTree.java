/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.util.intervaltree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author antunes
 */
public class HDIntervalTree<Type> {//Higher Dimension

    private ArrayList<IntervalTree<Type>> trees;
    private int dim;

    public HDIntervalTree(int dim) {
        this.dim = dim;
        trees = new ArrayList<>(dim);
        for (int i = 0; i < dim; i++) {
            trees.add(new IntervalTree<Type>());
        }
    }

    public List<Type> get(double[] p1, double[] p2) {
        if (p1.length < dim || p2.length < dim) {
            throw new Error("invalid point length");
        }
        List<Type> result = null;

        int i = 0;
        for (IntervalTree<Type> tree : trees) {
            if (result == null) {
                result = tree.get(p1[i], p2[i]);
            } else {
                if (result.isEmpty()) {
                    return result;
                } else {
                    List<Type> tmpResult = tree.get(p1[i], p2[i]);
                    for (Iterator<Type> it = result.iterator(); it.hasNext();) {
                        Type t = it.next();
                        if (!tmpResult.contains(t)) {
                            it.remove();
                        }
                    }
                }
            }
            i++;
        }

        return result;
    }

    public List<Type> get(double... pos) {
        if (pos.length < dim) {
            throw new Error("invalid point length");
        }
        List<Type> result = null;

        int i = 0;
        for (IntervalTree<Type> tree : trees) {
            if (result == null) {
                result = tree.get(pos[i]);
            } else {
                if (result.isEmpty()) {
                    return result;
                } else {
                    List<Type> tmpResult = tree.get(pos[i]);
                    for (Iterator<Type> it = result.iterator(); it.hasNext();) {
                        Type t = it.next();
                        if (!tmpResult.contains(t)) {
                            it.remove();
                        }
                    }
                }
            }
            i++;
        }

        return result;
    }

    public boolean inSync() {
        boolean inSync = true;
        for (IntervalTree<Type> tree : trees) {
            inSync &= tree.inSync();
        }
        return inSync;
    }

    public void build() {
        for (IntervalTree<Type> tree : trees) {
            tree.build();
        }
    }

    public void addInterval(double[] p1, double[] p2, Type data) {
        if (p1.length < dim || p2.length < dim) {
            throw new Error("invalid point length");
        }
        int i = 0;
        for (IntervalTree<Type> tree : trees) {
            tree.addInterval(p1[i], p2[i], data);
            i++;
        }
    }

    public void addPoint(Type data, double... p) {
        if (p.length < dim) {
            throw new Error("invalid point length");
        }
        int i = 0;
        for (IntervalTree<Type> tree : trees) {
            tree.addInterval(p[i], p[i], data);
            i++;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IntervalTree<Type> tree : trees) {
            sb.append(tree);
        }
        return sb.toString();
    }

    public static void quickTest() {
        HDIntervalTree<Integer> tree = new HDIntervalTree<>(2);

        tree.addInterval(new double[]{2, 18}, new double[]{10, 24}, 1);
        tree.addInterval(new double[]{6, 10}, new double[]{18, 20}, 2);

        for (int r : tree.get(8, 20)) {
            System.out.println(r);
        }
    }

    public static void heavyTest() {
        Random rand = new Random();
        int dim = 3;
        int points = 600;
        int maxValue = 10;

        HDIntervalTree<Integer> tree = new HDIntervalTree<>(dim);

        for (int i = 0; i < points; i++) {
            double[] p = new double[dim];
            for (int j = 0; j < dim; j++) {
                p[j] = rand.nextInt(maxValue)/Math.PI;
            }
            System.out.println(i + " : " + java.util.Arrays.toString(p));
            tree.addPoint(i, p);
        }

//        {
//            tree.build();
//            System.out.println(tree);
//        }
        double t = System.currentTimeMillis();
        List<Integer> res = tree.get(new double[]{0, 0, 0}, new double[]{0, 0, 0});
        System.out.println("t:" + (System.currentTimeMillis() - t) * 10);
        for (int r : res) {
            System.out.println(r);
        }

    }

    public static void main(String[] args) {
        heavyTest();
    }
}
