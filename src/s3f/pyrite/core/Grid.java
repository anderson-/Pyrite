/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author antunes
 */
public abstract class Grid {

    public static class FiniteGrid extends Grid {

        public static interface BoundingFunction {

            public boolean isValid(int... pos);

        }

        private Grid gen;
        private final ArrayList<int[]> boundaries = new ArrayList<>();
        private final ArrayList<BoundingFunction> functions = new ArrayList<>();

        public FiniteGrid(Grid gen) {
            this.gen = gen;
        }

        public void addBoundingFunction(BoundingFunction f) {
            functions.add(f);
        }

        public void removeBoundingFunction(BoundingFunction f) {
            functions.remove(f);
        }

        public void addBoundary(int x1, int y1, int z1, int x2, int y2, int z2) {
            boundaries.add(new int[]{x1, y1, z1, x2, y2, z2});
        }

        public void addBoundary(int[] b) {
            if (b.length >= 6 && !boundaries.contains(b)) {
                boundaries.add(b);
            }
        }

        public void removeBoundary(int[] b) {
            boundaries.remove(b);
        }
        
        public List<int[]> getBoundaries(){
            return boundaries;
        }

        @Override
        public List<int[]> getNeighborhood(int... pos) {
            List<int[]> nd = gen.getNeighborhood(pos);

            for (BoundingFunction f : functions) {
                for (Iterator<int[]> it = nd.iterator(); it.hasNext();) {
                    int[] p = it.next();
                    if (!f.isValid(p)) {
                        it.remove();
                    }
                }
            }

            for (Iterator<int[]> it = nd.iterator(); it.hasNext();) {
                int[] p = it.next();
                for (int[] b : boundaries) {
                    if (((((p[0] < b[0] || p[1] < b[1]) || p[2] < b[2]) || p[0] >= b[3]) || p[1] >= b[4]) || p[2] >= b[5]) {
                        it.remove();
                        break;
                    }
                }
            }

            return nd;
        }

    }

    public static final Grid SIMPLE = new Grid() {
        @Override
        public List<int[]> getNeighborhood(int... pos) {
            ArrayList<int[]> nd = new ArrayList<>();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        int x = pos[0] + i;
                        int y = pos[1] + j;
                        int z = pos[2] + k;

                        if (Math.abs(i) + Math.abs(j) + Math.abs(k) == 1) {
                            nd.add(new int[]{x, y, z});
                        }
                    }
                }
            }
            return nd;
        }
    };

    public static final Grid SIMPLE2 = new Grid() {
        @Override
        public List<int[]> getNeighborhood(int... pos) {
            ArrayList<int[]> nd = new ArrayList<>();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        int x = pos[0] + i;
                        int y = pos[1] + j;
                        int z = pos[2] + k;

                        if (Math.abs(i) + Math.abs(j) + Math.abs(k) <= 2) {
                            nd.add(new int[]{x, y, z});
                        }
                    }
                }
            }
            return nd;
        }
    };

    public static final Grid SIMPLE3 = new Grid() {
        @Override
        public List<int[]> getNeighborhood(int... pos) {
            ArrayList<int[]> nd = new ArrayList<>();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        int x = pos[0] + i;
                        int y = pos[1] + j;
                        int z = pos[2] + k;

                        if (Math.abs(i) + Math.abs(j) + Math.abs(k) != 0) { //3d
                            nd.add(new int[]{x, y, z});
                        }
                    }
                }
            }
            return nd;
        }
    };

    private static void add(ArrayList<int[]> nd, int[] pos) {
        int x = pos[0];
        int y = pos[1];
        int z = pos[2];

        nd.add(pos);
    }

    public static final Grid HEX = new Grid() {

        @Override
        public List<int[]> getNeighborhood(int... pos) {
            ArrayList<int[]> nd = new ArrayList<>();

            int x = pos[0];
            int y = pos[1];
            int z = pos[2];

            switch (y % 4) {
                case 0:
                    if (x % 2 == 0) {
                        return nd;
                    } else {
                        add(nd, new int[]{x - 1, y + 1, z});
                        add(nd, new int[]{x + 1, y + 1, z});
                        add(nd, new int[]{x, y - 1, z});
                        add(nd, new int[]{x, y, z + 1});
                        add(nd, new int[]{x, y, z - 1});
                    }
                    break;
                case 1:
                    if (x % 2 == 0) {
                        add(nd, new int[]{x, y + 1, z});
                        add(nd, new int[]{x - 1, y - 1, z});
                        add(nd, new int[]{x + 1, y - 1, z});
                        add(nd, new int[]{x, y, z + 1});
                        add(nd, new int[]{x, y, z - 1});
                    } else {
                        return nd;
                    }
                    break;
                case 2:
                    if (x % 2 == 0) {
                        add(nd, new int[]{x - 1, y + 1, z});
                        add(nd, new int[]{x + 1, y + 1, z});
                        add(nd, new int[]{x, y - 1, z});
                        add(nd, new int[]{x, y, z + 1});
                        add(nd, new int[]{x, y, z - 1});
                    } else {
                        return nd;
                    }
                    break;
                case 3:
                    if (x % 2 == 0) {
                        return nd;
                    } else {
                        add(nd, new int[]{x, y + 1, z});
                        add(nd, new int[]{x - 1, y - 1, z});
                        add(nd, new int[]{x + 1, y - 1, z});
                        add(nd, new int[]{x, y, z + 1});
                        add(nd, new int[]{x, y, z - 1});
                    }
                    break;
                default:
                    return nd;
            }
            return nd;
        }
    };

    public static final Grid SIMPLE4 = new Grid() {

        ArrayList<Boolean> a = new ArrayList<>();

        {
            getNeighborhood(0, 0, 0, 0);
        }

        @Override
        public List<int[]> getNeighborhood(int... pos) {
            if (pos.length > 3) {
                a.clear();
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        for (int k = -1; k <= 1; k++) {
//                            a.add(DefaultGridFittingTool.rand.nextBoolean());
                            throw new RuntimeException("Not implemented");
                        }
                    }
                }
            }

            ArrayList<int[]> nd = new ArrayList<>();

            int e = 0;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        int x = pos[0] + i;
                        int y = pos[1] + j;
                        int z = pos[2] + k;

                        if (Math.abs(i) + Math.abs(j) + Math.abs(k) != 0 && a.get(e)) {
                            nd.add(new int[]{x, y, z});
                        }
                        e++;
                    }
                }
            }
            return nd;
        }
    };

    public abstract List<int[]> getNeighborhood(int... pos);

}
