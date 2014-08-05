/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author antunes
 */
public abstract class Grid {

    public static int X = 10;
    public static int Y = 10;
    public static int Z = 10;

    public static final Grid SIMPLE = new Grid() {
        @Override
        public ArrayList<int[]> getNeighborhood(int... pos) {
            ArrayList<int[]> nd = new ArrayList<>();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        int x = pos[0] + i;
                        int y = pos[1] + j;
                        int z = pos[2] + k;

                        if (x >= 0 && y >= 0 && z >= 0 && x < X && y < Y && z < Z) {
                            if (Math.abs(i) + Math.abs(j) + Math.abs(k) == 1) {
                                nd.add(new int[]{x, y, z});
                            }
                        }
                    }
                }
            }
            return nd;
        }
    };

    public static final Grid SIMPLE2 = new Grid() {
        @Override
        public ArrayList<int[]> getNeighborhood(int... pos) {
            ArrayList<int[]> nd = new ArrayList<>();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        int x = pos[0] + i;
                        int y = pos[1] + j;
                        int z = pos[2] + k;

                        if (x >= 0 && y >= 0 && z >= 0 && x < X && y < Y && z < Z) {
                            if (Math.abs(i) + Math.abs(j) + Math.abs(k) <= 2) {
                                //if (Math.abs(i) + Math.abs(j) + Math.abs(k) > 2) {
                                nd.add(new int[]{x, y, z});
                            }
                        }
                    }
                }
            }
            return nd;
        }
    };

    public static final Grid SIMPLE3 = new Grid() {
        @Override
        public ArrayList<int[]> getNeighborhood(int... pos) {
            ArrayList<int[]> nd = new ArrayList<>();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        int x = pos[0] + i;
                        int y = pos[1] + j;
                        int z = pos[2] + k;

                        if (x >= 0 && y >= 0 && z >= 0 && x < X && y < Y && z < Z) {
                            //if (Math.abs(i) + Math.abs(j) + Math.abs(k) <= 2) { //apenas planos
                            if (Math.abs(i) + Math.abs(j) + Math.abs(k) != 0) { //3d
                                //if (Math.abs(i) + Math.abs(j) + Math.abs(k) > 2) {
                                nd.add(new int[]{x, y, z});
                            }
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

        if (x >= 0 && y >= 0 && z >= 0 && x < X && y < Y && z < Z) {
            nd.add(pos);
        }
    }

    public static final Grid HEX = new Grid() {

        @Override
        public ArrayList<int[]> getNeighborhood(int... pos) {
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
        public ArrayList<int[]> getNeighborhood(int... pos) {
            if (pos.length > 3) {
                a.clear();
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        for (int k = -1; k <= 1; k++) {
                            a.add(DefaultGridFittingTool.rand.nextBoolean());
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

                        if (x >= 0 && y >= 0 && z >= 0 && x < X && y < Y && z < Z) {
                            if (Math.abs(i) + Math.abs(j) + Math.abs(k) != 0 && a.get(e)) {
                                nd.add(new int[]{x, y, z});
                            }
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
