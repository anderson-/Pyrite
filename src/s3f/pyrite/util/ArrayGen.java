/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author andy
 */
public final class ArrayGen {

    private ArrayGen() {

    }

    public static int I = 0;

    public static double[] generateNormalizedArrayBruteForce(int size, int steps, int nZmin, int nZmax) {
        double[] r = new double[size];
        int sum;
        int nonZero;
        do {
            sum = 0;
            nonZero = 0;
            int i = I;
            for (int k = 0; k < size; k++) {
                int p = (int) Math.pow(steps + 1, size - k - 1);
                int w = i / p;
                i -= w * p;
                r[k] = w / (double) steps;
                nonZero += w != 0 ? 1 : 0;
                sum += w;
            }
            I = (I >= (int) Math.pow(steps + 1, size)) ? 0 : I + 1;
        } while (I != 0 && (sum != steps || nonZero < nZmin || nonZero > nZmax));
        r[0] = r[0] > 1 ? 1 : r[0];
        return r;
    }

    public static double[] generateRandomNormalizedArray(Random r, int size) {
        double[] w = new double[]{r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble()};
        double sum = 0;
        for (double d : w) {
            sum += d;
        }
        for (int i = 0; i < w.length; i++) {
            w[i] /= sum;
        }
        return w;
    }

    public static void iterate(int[] v, int pos, boolean incr) {

        if (v[pos] == 1) {
            //somar
            if (onlyOne(v, pos)) {
                //p("+");
                int sum = sum(v, pos);
                clear(v, pos);
                v[pos] = sum + (incr ? 1 : 0);
            } else {
                for (int i = pos; i < v.length; i++) {
                    if (v[i] != 0 && v[i] != 1) {
                        //separa
                        //p("*2");
                        iterate(v, i, false);
                        break;
                    }
                }
            }
        } else if (sum(v, pos + 1) >= 2 && one(v, pos + 1)) {
            //juntar
            //p("*");
            iterate(v, pos + 1, false);
        } else {
            //separa
            //p("%");
            shift(v);
            v[pos] = v[pos + 1] - 1;
            v[pos + 1] = 1;

        }
    }

    public static void gen(int n, ArrayList<int[]> l) {
        int[] v = new int[n];
        v[0] = n;

        while (v[0] <= n) {
            int OM = 0;
            for (int i = 0; i < v.length; i++) {
                OM += v[i] != 0 ? 1 : 0;
            }
            if (OM >= 2 && OM <= 4) {
                int w[] = Arrays.copyOf(v, 4);
                do {
                    l.add(Arrays.copyOf(w, w.length));
                    System.out.println(Arrays.toString(w));
                } while (permuteLexically(w));
            }
            if (v[0] == 1) {
                //somar
                int sum = sum(v);
                clear(v);
                v[0] = sum + 1;
            } else {
                //separar
                for (int i = v.length - 1; i >= 0; i--) {
                    if (v[i] != 0 && v[i] != 1) {
                        int sum = sum(v, i + 1);

                        if (onlyOne(v, i + 1) && sum > 0 && v[i] > 2) {
                            //duplica
                            int d = (sum + 1 <= v[i] - 1) ? sum + 1 : v[i] - 1;
                            int ex = d - 1;
                            for (int j = v.length - 1; j >= 0 && ex > 1; j--) {
                                if (v[j] == 1) {
                                    v[j] = 0;
                                    ex--;
                                }
                            }
                            v[i] = v[i] - 1;
                            v[i + 1] = d;
                            sum -= d - 1;
                            if (sum > 1) {
                                for (int j = d; j >= 1; j--) {
                                    while (sum / j > 0) {
                                        v[++i + 1] = j;
                                        sum -= j;
                                    }
                                }
                                clear(v, ++i + 1);
                            }
                        } else {
                            shift(v, i);
                            v[i] = v[i + 1] - 1;
                            v[i + 1] = 1;
                        }
                        break;
                    }
                }

            }
        }
    }

    public static boolean permuteLexically(int[] data) {
        int k = data.length - 2;
        while (data[k] <= data[k + 1]) {
            k--;
            if (k < 0) {
                return false;
            }
        }
        int l = data.length - 1;
        while (data[k] <= data[l]) {
            l--;
        }
        swap(data, k, l);
        int length = data.length - (k + 1);
        for (int i = 0; i < length / 2; i++) {
            swap(data, k + 1 + i, data.length - i - 1);
        }
        return true;
    }

    public static void per(int[] v) {
        for (int i = 0; i < v.length; i++) {
            for (int j = i; j < v.length; j++) {
                swap(v, i, j);
            }
        }
    }

    public static boolean onlyOne(int[] v, int pos) {
        for (int i = pos; i < v.length; i++) {
            if (v[i] == 0) {
                return true;
            } else if (v[i] != 1) {
                return false;
            }
        }
        return true;
    }

    public static void swap(int[] v, int a, int b) {
        if (v[a] != v[b]) {
            int t = v[a];
            v[a] = v[b];
            v[b] = t;
        }
    }

    public static boolean one(int[] v, int pos) {
        for (int i = pos; i < v.length; i++) {
            if (v[i] == 1) {
                return true;
            }
        }
        return false;
    }

    public static void shift(int[] v) {
        for (int i = v.length - 1; i > 0; i--) {
            v[i] = v[i - 1];
        }
    }

    public static void shift(int[] v, int pos) {
        for (int i = v.length - 1; i > pos; i--) {
            v[i] = v[i - 1];
        }
    }

    public static void clear(int[] v) {
        for (int i = 0; i < v.length; i++) {
            v[i] = 0;
        }
    }

    public static void clear(int[] v, int pos) {
        for (int i = pos; i < v.length; i++) {
            v[i] = 0;
        }
    }

    public static int sum(int[] v, int pos) {
        int sum = 0;
        for (int i = pos; i < v.length; i++) {
            sum += v[i];
        }
        return sum;
    }

    public static int sum(int[] v) {
        int sum = 0;
        for (int i : v) {
            sum += i;
        }
        return sum;
    }

}
