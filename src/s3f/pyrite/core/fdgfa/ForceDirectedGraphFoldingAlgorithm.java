/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core.fdgfa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.FoldingAlgorithm;
import s3f.pyrite.core.fdgfa.fdgs.ForceDirectedGraphSimulation;
import s3f.pyrite.core.fdgfa.honeycomb.ConvexUniformHoneycomb;
import s3f.pyrite.core.fdgfa.honeycomb.SimpleCubicHoneycomb;
import s3f.pyrite.core.fdgfa.strategy.GreedyStrategy;
import s3f.pyrite.util.ArrayGen;
import s3f.pyrite.util.Vector;

/**
 *
 * @author andy
 */
public class ForceDirectedGraphFoldingAlgorithm implements FoldingAlgorithm {

    private static final int edgeLen = 16;
    public static final ConvexUniformHoneycomb H = new SimpleCubicHoneycomb(edgeLen, true);

    @Override
    public void fold(Circuit circuit) {
//        while (GraphUtils.countUnsolvedConnections(circuit, H) >1) {

        H.reset();
        synchronized (circuit) {
            for (Component n : new ArrayList<>(circuit.getComponents())) {
                n.setPos(new Vector(100));
                n.setFixed(false);
                if (n.getName().equals("asdw")) {
                    //cunsume
                    n.getConnections().get(0).getOtherComponent(n).appendAndConsume(n);
                }
                circuit.clean();
            }
        }
        ForceDirectedGraphSimulation sim = new ForceDirectedGraphSimulation(circuit);
        sim.runSimulation();
        GraphFolder.waitForEqui(sim);
        double[] weights = new double[]{0.05, 0.15, 0.1, 0.7, 0.0};

        GraphFolder.fold(circuit, sim, new GreedyStrategy(H, weights), H);
        sim.kill();
//        }
    }

    public void fold2(Circuit circuit) {
        final ArrayList<double[]> samples = new ArrayList<>();
        //gen(size, l);

        samples.add(new double[]{0.05, 0.15, 0.1, 0.7, 0.0});
        int K = 0;
        do {
            K++;
            samples.add(ArrayGen.generateNormalizedArrayBruteForce(5, 20, 5, 5));
        } while (ArrayGen.I != 0);

        ForceDirectedGraphSimulation sim = new ForceDirectedGraphSimulation(circuit);
        sim.runSimulation();

        GraphFolder.delay(2000);

//        double[][] ws = new double[][]{
//            //            {0.21225307386983303, 0.1542428154480527, 0.09589267912804442, 0.5376114315540699},perfect
//            //            {0.3058354963976461, 0.47165121504512453, 0.046779258140756896, 0.17573403041647245},bad
//            //            {0.5095929936731121, 0.20329068085253152, 0.027174216649733015, 0.2599421088246235},nhe fez uma vez
//            {0.061696687944460406, 0.07464901214536089, 0.24846007207225282, 0.6151942278379258},
//            {0.24330223724764902, 0.4439675682586369, 0.023253488352502964, 0.2894767061412112},
//            {0.45684452030035916, 0.13016212108076308, 0.03387604357386717, 0.37911731504501067},
//            {0.17627388236388428, 0.254710506510437, 0.09785698544800356, 0.4711586256776752}
//        };
        final HashMap<Double, ArrayList<double[]>> hashMap = new HashMap<>();

        int times = 50;
        long start = System.currentTimeMillis();
        println(String.format("size: %d time: %.2f\n", samples.size(), (samples.size() * times * 2000 / 1000f / 60)));
        int descarted = 0;
        int lsize = samples.size();
        for (int x = 0; x < lsize; x++) {
            double[] weights = samples.get(x);

            double percent = x / (float) samples.size() * 100f;
            double elapsed = (System.currentTimeMillis() - start) / 1000f;
            double remaining = ((100 - percent) * elapsed) / percent;
            println(String.format("%.2f%% Elapsed: %.2fmin Remaining: ~%.2fmin Total: ~%.2fmin X: %05d/%05d", percent, elapsed / 60, remaining / 60, (elapsed + remaining) / 60, x, lsize));
            double missingAvr = 0;
            for (int i = 0; i < times; i++) {
                for (Component n : circuit.getComponents()) {
                    n.setPos(new Vector(100));
                    n.setFixed(false);
                }
                GraphFolder.delay(100);
//                GraphFolder.waitForEqui(g);

                ConvexUniformHoneycomb h = new SimpleCubicHoneycomb(edgeLen, true);
                double score = 0;
                try {
                    score = 100f * GraphFolder.fold(circuit, sim, new GreedyStrategy(h, weights), h);
                } catch (Exception e) {
                    descarted++;
                    missingAvr = 1000;
                    println("descarted: " + descarted);
                    break;
                }
                double sat = 100f * GraphUtils.countSatisfiedConnections(circuit, h) / circuit.getConnections().size();
                double mis = GraphUtils.countUnsolvedConnections(circuit, h) / 2;
                double vol = GraphUtils.getVolume(circuit, h);
                missingAvr += mis;
                if (mis <= 1) {
//                    if (q != null) {
//                        GraphFolder.delay(1000);
//                        q.saveScreenshot(String.format(" s%.2f-c%.0f%%", score, GraphUtils.countSatisfiedConnections(circuit, h) * 100f / circuit.getEdges().size()));
//                    }
                    println("Array: " + Arrays.toString(weights));
                    println(String.format("Score: %.2f Satisfied: %.2f%% Missing: %.0f Volume: %.4f\n", score, sat, mis, vol));
                    javax.swing.JOptionPane.showMessageDialog(null, "Continue?");
                }
                GraphFolder.delay(5000);
            }
            missingAvr /= times;
            if (missingAvr <= 6) {
                addValues(hashMap, missingAvr, weights);
                println("Array: " + Arrays.toString(weights));
                println("Average missing:" + missingAvr);
            }
        }

        Iterator<Double> it = hashMap.keySet().iterator();
        ArrayList<double[]> tempList;
        while (it.hasNext()) {
            Double key = it.next();
            tempList = hashMap.get(key);
            if (tempList != null) {
                for (double[] value : tempList) {
                    println(key + " : " + Arrays.toString(value));
                }
            }
        }

        println("Done.");

        sim.kill();
    }

    private static void addValues(HashMap<Double, ArrayList<double[]>> hashMap, double key, double[] value) {
        ArrayList tempList = null;
        if (hashMap.containsKey(key)) {
            tempList = hashMap.get(key);
            if (tempList == null) {
                tempList = new ArrayList();
            }
            tempList.add(value);
        } else {
            tempList = new ArrayList();
            tempList.add(value);
        }
        hashMap.put(key, tempList);
    }

    public static void println(String s) {
//        if (logger != null) {
//            logger.log(Level.INFO, s);
//        } else {
        System.out.println(s);
//        }
    }
}
