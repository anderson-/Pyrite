//package s3f.pyrite.core;
//
//import s3f.pyrite.core.fdgfa.fdgs.ParticleProperty;
//import s3f.pyrite.util.Vector;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Random;
//import java.util.concurrent.TimeUnit;
//import java.util.logging.FileHandler;
//import java.util.logging.Formatter;
//import java.util.logging.Level;
//import java.util.logging.LogRecord;
//import java.util.logging.Logger;
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.LineUnavailableException;
//import javax.sound.sampled.SourceDataLine;
//import s3f.pyrite.core.fdgfa.honeycomb.ConvexUniformHoneycomb;
//import s3f.pyrite.ui.graphmonitor.GraphMonitor3D;
//import s3f.pyrite.core.fdgfa.GraphFolder;
//import s3f.pyrite.core.fdgfa.GraphUtils;
//import s3f.pyrite.core.fdgfa.strategy.GreedyStrategy;
//import s3f.pyrite.core.fdgfa.honeycomb.SimpleCubicHoneycomb;
//import quick3d.simplegraphics.Console;
//import s3f.pyrite.core.fdgfa.fdgs.ForceDirectedGraphSimulation;
//
//public class Main {
//
//    private static Console console = null;
//    private static Circuit g;
//    private static GraphMonitor3D q;
//
//    private static int edgeLen = 16;
//    private static int N = 1;
//
//    public static void print(String str) {
//        if (console != null) {
//            console.put(str);
//        }
//    }
//
//    public static void main(String[] args) throws LineUnavailableException {
//
//        new Thread("log temp thread") {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(8000);
//                    } catch (InterruptedException ex) {
//                    }
//                    println("temp:");
//                    logCPUTemp("/sys/class/thermal/thermal_zone0/temp");
//                }
//            }
//        }.start();
//
//        final ArrayList<double[]> l = new ArrayList<>();
//        //gen(size, l);
//
//        l.add(new double[]{0.05, 0.15, 0.1, 0.7, 0.0});
//        int K = 0;
//        do {
//            K++;
//            l.add(generateNormalizedArrayBruteForce(5, 20, 5, 5));
//        } while (I != 0);
//
//        final int half = l.size() / 2;
//
//        Thread t0 = new Thread("t0") {
//            @Override
//            public void run() {
//                createSim(true, l.subList(0, half));
//            }
//        };
//
////        Thread t1 = new Thread("t1") {
////            @Override
////            public void run() {
////                createSim(true, l.subList(half, l.size()));
////            }
////        };
//        t0.start();
////        t1.start();
//
//        try {
//            t0.join();
////            t1.join();
//        } catch (InterruptedException ex) {
//        }
//
////        tryEvolve(100, 20);
////        long k = showTimeStats(4, 5, 1);
////        int K = 0;
////        do {
////            K++;
////            System.out.println(Arrays.toString(generateBruteForce(4, 20, 4, 4)));
////        } while (I != 0);
////        System.out.println(K - 1);
//        System.exit(0);
//    }
//
//    public static void promptBeforeQuit() {
//        javax.swing.JOptionPane.showMessageDialog(null, "Quit");
//        System.exit(0);
//    }
//    public static float SAMPLE_RATE = 8000f;
//
//    public static void tone(int hz, int msecs) {
//        tone(hz, msecs, 1.0);
//    }
//
//    public static void tone(int hz, int msecs, double vol) {
//        byte[] buf = new byte[1];
//        AudioFormat af
//                = new AudioFormat(
//                        SAMPLE_RATE, // sampleRate
//                        8, // sampleSizeInBits
//                        1, // channels
//                        true, // signed
//                        false);      // bigEndian
//        try {
//            SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
//            sdl.open(af);
//            sdl.start();
//            for (int i = 0; i < msecs * 8; i++) {
//                double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
//                buf[0] = (byte) (Math.sin(angle) * 127.0 * vol);
//                sdl.write(buf, 0, 1);
//            }
//            sdl.drain();
//            sdl.stop();
//            sdl.close();
//        } catch (Exception e) {
//        }
//    }
//
//    private static void addValues(HashMap<Double, ArrayList<double[]>> hashMap, double key, double[] value) {
//        ArrayList tempList = null;
//        if (hashMap.containsKey(key)) {
//            tempList = hashMap.get(key);
//            if (tempList == null) {
//                tempList = new ArrayList();
//            }
//            tempList.add(value);
//        } else {
//            tempList = new ArrayList();
//            tempList.add(value);
//        }
//        hashMap.put(key, tempList);
//    }
//
//    public static void createSim(boolean show, List<double[]> samples) {
//        Graph g = new Graph();
//        Importer.importfile(g, "cir3.txt");
//        ForceDirectedGraphSimulation sim = null;
//        if (show) {
//            throw new RuntimeException("arruma aqui!");
////            q = new Quick3DGraphMonitor(new SimpleCubicHoneycomb(edgeLen, true));
////            console = q.getConsole();
////            q.show(g);
//        } else {
//            q = null;
//            sim = new ForceDirectedGraphSimulation(g);
//            sim.runSimulation();
//        }
//
//        GraphFolder.delay(2000);
//
////        double[][] ws = new double[][]{
////            //            {0.21225307386983303, 0.1542428154480527, 0.09589267912804442, 0.5376114315540699},perfect
////            //            {0.3058354963976461, 0.47165121504512453, 0.046779258140756896, 0.17573403041647245},bad
////            //            {0.5095929936731121, 0.20329068085253152, 0.027174216649733015, 0.2599421088246235},nhe fez uma vez
////            {0.061696687944460406, 0.07464901214536089, 0.24846007207225282, 0.6151942278379258},
////            {0.24330223724764902, 0.4439675682586369, 0.023253488352502964, 0.2894767061412112},
////            {0.45684452030035916, 0.13016212108076308, 0.03387604357386717, 0.37911731504501067},
////            {0.17627388236388428, 0.254710506510437, 0.09785698544800356, 0.4711586256776752}
////        };
//        final HashMap<Double, ArrayList<double[]>> hashMap = new HashMap<>();
//
//        new Thread("backup thread") {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(60000);
//                    } catch (InterruptedException ex) {
//                    }
//                    Iterator<Double> it = hashMap.keySet().iterator();
//                    ArrayList<double[]> tempList = null;
//                    boolean p = false;
//                    while (it.hasNext()) {
//                        Double key = it.next();
//                        tempList = hashMap.get(key);
//                        if (tempList != null) {
//                            for (double[] value : tempList) {
//                                println(key + " : " + Arrays.toString(value));
//                                p = true;
//                            }
//                        }
//                    }
//                    if (p) {
//                        println("BackUp done.");
//                    }
//                }
//            }
//        }.start();
//
//        int times = 50;
//        long start = System.currentTimeMillis();
//        println(String.format("size: %d time: %.2f\n", samples.size(), (samples.size() * times * 2000 / 1000f / 60)));
//        int descarted = 0;
//        int lsize = samples.size();
//        for (int x = 0; x < lsize; x++) {
//            double[] weights = samples.get(x);
//
//            double percent = x / (float) samples.size() * 100f;
//            double elapsed = (System.currentTimeMillis() - start) / 1000f;
//            double remaining = ((100 - percent) * elapsed) / percent;
//            println(String.format("%.2f%% Elapsed: %.2fmin Remaining: ~%.2fmin Total: ~%.2fmin X: %05d/%05d", percent, elapsed / 60, remaining / 60, (elapsed + remaining) / 60, x, lsize));
////            Toolkit.getDefaultToolkit().beep();
//            double missingAvr = 0;
//            for (int i = 0; i < times; i++) {
//                for (ParticleProperty n : g.getNodes()) {
//                    n.setPos(new Vector(100));
//                    n.setFixed(false);
//                }
//                GraphFolder.delay(100);
////                GraphFolder.waitForEqui(g);
//
//                ConvexUniformHoneycomb h = new SimpleCubicHoneycomb(edgeLen, true);
//                double score = 0;
//                try {
//                    score = 100f * GraphFolder.fold(g, new GreedyStrategy(h, weights), h);
//                } catch (Exception e) {
//                    descarted++;
//                    missingAvr = 1000;
//                    println("descarted: " + descarted);
//                    break;
//                }
//                double sat = 100f * GraphUtils.countSatisfiedConnections(g, h) / g.getEdges().size();
//                double mis = GraphUtils.countUnsolvedConnections(g, h) / 2;
//                double vol = GraphUtils.getVolume(g, h);
//                missingAvr += mis;
//                if (mis <= 2) {
//                    if (q != null) {
//                        Main.print(Arrays.toString(weights));
//                        tone(1000, 100, .2);
//                        GraphFolder.delay(1000);
//                        q.saveScreenshot(String.format(" s%.2f-c%.0f%%", score, GraphUtils.countSatisfiedConnections(g, h) * 100f / g.getEdges().size()));
//                    }
//                    println("Array: " + Arrays.toString(weights));
//                    println(String.format("Score: %.2f Satisfied: %.2f%% Missing: %.0f Volume: %.4f\n", score, sat, mis, vol));
////                    javax.swing.JOptionPane.showMessageDialog(null, "Continue?");
//                }
//                GraphFolder.delay(5000);
//            }
//            missingAvr /= times;
//            if (missingAvr <= 6) {
//                addValues(hashMap, missingAvr, weights);
//                println("Array: " + Arrays.toString(weights));
//                println("Average missing:" + missingAvr);
//            }
//        }
//
//        Iterator<Double> it = hashMap.keySet().iterator();
//        ArrayList<double[]> tempList = null;
//        while (it.hasNext()) {
//            Double key = it.next();
//            tempList = hashMap.get(key);
//            if (tempList != null) {
//                for (double[] value : tempList) {
//                    println(key + " : " + Arrays.toString(value));
//                }
//            }
//        }
//
//        println("Done.");
//
//        if (sim != null) {
//            sim.kill();
//        }
//
////        if (show) {
////            promptBeforeQuit();
////        } else {
////            System.exit(0);
////        }
//    }
//
//    static Logger logger = null;
//
//    static {
//        logger = Logger.getLogger("MyLog");
//        FileHandler fh;
//
//        try {
//
//            // This block configure the logger with handler and formatter  
//            SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
//            fh = new FileHandler("log_" + format.format(Calendar.getInstance().getTime()) + ".log");
//            logger.addHandler(fh);
//
//            fh.setFormatter(new Formatter() {
//                @Override
//                public String format(LogRecord record) {
//                    SimpleDateFormat logTime = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
//                    Calendar cal = new GregorianCalendar();
//                    cal.setTimeInMillis(record.getMillis());
//                    return record.getLevel()
//                            + logTime.format(cal.getTime())
//                            + " || "
//                            + record.getSourceClassName().substring(
//                                    record.getSourceClassName().lastIndexOf(".") + 1,
//                                    record.getSourceClassName().length())
//                            + "."
//                            + record.getSourceMethodName()
//                            + "() : "
//                            + record.getMessage() + (record.getMessage().endsWith("\n") ? "" : "\n");
//                }
//            });
//            // the following statement is used to log any messages  
//            logger.info("My first log");
//
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void println(String s) {
//        if (logger != null) {
//            logger.log(Level.INFO, s);
//        } else {
//            System.out.println(s);
//        }
//    }
//
//    public static void logCPUTemp(String path) {
//        try {
//            Runtime rt = Runtime.getRuntime();
//            String[] commands = {"cat", path};
//            Process proc = rt.exec(commands);
//
//            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//
//            // read the output from the command
//            String s = null;
//            StringBuilder sb = new StringBuilder();
//            while ((s = stdInput.readLine()) != null) {
//                System.out.println(s);
//                sb.append(s).append("\n");
//            }
//            println(sb.toString());
//        } catch (IOException ex) {
//        }
//    }
//
//    public static int computeAvrgTimeToProcess(Circuit g, int times, int maxDelay, double max) {
//        ForceDirectedGraphSimulation sim = new ForceDirectedGraphSimulation(g);
//        sim.runSimulation();
//        int ret = maxDelay;
//        for (int delay = 1; delay < maxDelay; delay++) {
//            int t;
//            double ke = 0;
//            for (t = 0; t < times; t++) {
//                for (ParticleProperty n : g.getNodes()) {
//                    n.setPos(new Vector(100));
//                    n.setFixed(false);
//                }
//                GraphFolder.delay(delay);
//                ke += g.getKE();
//            }
//            double avrgKE = ke / t;
//            System.out.println(delay + " ms: Avrg KE = " + avrgKE);
//            if (avrgKE <= max) {
//                ret = delay;
//                break;
//            }
//        }
//        if (sim != null) {
//            sim.kill();
//        }
//        return ret;
//    }
//
//    static int I = 0;
//
//    public static double[] generateNormalizedArrayBruteForce(int size, int steps, int nZmin, int nZmax) {
//        double[] r = new double[size];
//        int sum;
//        int nonZero;
//        do {
//            sum = 0;
//            nonZero = 0;
//            int i = I;
//            for (int k = 0; k < size; k++) {
//                int p = (int) Math.pow(steps + 1, size - k - 1);
//                int w = i / p;
//                i -= w * p;
//                r[k] = w / (double) steps;
//                nonZero += w != 0 ? 1 : 0;
//                sum += w;
//            }
//            I = (I >= (int) Math.pow(steps + 1, size)) ? 0 : I + 1;
//        } while (I != 0 && (sum != steps || nonZero < nZmin || nonZero > nZmax));
//        r[0] = r[0] > 1 ? 1 : r[0];
//        return r;
//    }
//
//    public static long showTimeStats(int size, int steps, long ms) {
//        long millis = (long) Math.pow(steps, size);
//        millis *= ms;
//        ms = millis;
//        long h = TimeUnit.MILLISECONDS.toHours(millis);
//        millis -= TimeUnit.HOURS.toMillis(h);
//        long m = TimeUnit.MILLISECONDS.toMinutes(millis);
//        millis -= TimeUnit.MINUTES.toMillis(m);
//        long s = TimeUnit.MILLISECONDS.toSeconds(millis);
//        millis -= TimeUnit.SECONDS.toMillis(s);
//        System.out.printf("%d:%02d:%02d.%04d:%d\n", h, m, s, millis, ms);
//        return ms;
//    }
//
//    public static double[] generateRandomNormalizedArray(Random r, int size) {
//        double[] w = new double[]{r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble()};
//        double sum = 0;
//        for (double d : w) {
//            sum += d;
//        }
//        for (int i = 0; i < w.length; i++) {
//            w[i] /= sum;
//        }
//        return w;
//    }
//
//    public static void gen(int n, ArrayList<int[]> l) {
//        int[] v = new int[n];
//        v[0] = n;
//
//        DEBUG = false;
//        while (v[0] <= n) {
////            if (v[0] == sum(v)) {
////                System.out.println();
////            }
//            int OM = 0;
//            for (int i = 0; i < v.length; i++) {
//                OM += v[i] != 0 ? 1 : 0;
//            }
//            if (OM >= 2 && OM <= 4) {
//                int w[] = Arrays.copyOf(v, 4);
//                do {
//                    l.add(Arrays.copyOf(w, w.length));
//                    System.out.println(Arrays.toString(w));
//                } while (permuteLexically(w));
//            }
////            System.out.println(Arrays.toString(v));
//            if (v[0] == 1) {
//                p("+");
//                //somar
//                int sum = sum(v);
//                clear(v);
//                v[0] = sum + 1;
//            } else {
//                //separar
//                for (int i = v.length - 1; i >= 0; i--) {
//                    if (v[i] != 0 && v[i] != 1) {
//                        int sum = sum(v, i + 1);
//
//                        if (onlyOne(v, i + 1) && sum > 0 && v[i] > 2) {
//                            //duplica
//                            p("%" + i + " 1");
//                            int d = (sum + 1 <= v[i] - 1) ? sum + 1 : v[i] - 1;
////                            System.out.println(">" + Arrays.toString(v));
//                            int ex = d - 1;
//                            for (int j = v.length - 1; j >= 0 && ex > 1; j--) {
//                                if (v[j] == 1) {
//                                    v[j] = 0;
//                                    ex--;
//                                }
//                            }
////                            System.out.println(">" + Arrays.toString(v));
////                            shift(v, i);
//                            v[i] = v[i] - 1;
//                            v[i + 1] = d;
//                            sum -= d - 1;
//                            if (sum > 1) {
//                                for (int j = d; j >= 1; j--) {
//                                    while (sum / j > 0) {
//                                        v[++i + 1] = j;
//                                        sum -= j;
//                                    }
//                                }
//                                clear(v, ++i + 1);
//                            }
//                        } else {
//                            p("%" + i + " 2");
//                            shift(v, i);
//                            v[i] = v[i + 1] - 1;
//                            v[i + 1] = 1;
//                        }
//                        break;
//                    }
//                }
//
//            }
//        }
//    }
//
//    public static boolean permuteLexically(int[] data) {
//        int k = data.length - 2;
//        while (data[k] <= data[k + 1]) {
//            k--;
//            if (k < 0) {
//                return false;
//            }
//        }
//        int l = data.length - 1;
//        while (data[k] <= data[l]) {
//            l--;
//        }
//        swap(data, k, l);
//        int length = data.length - (k + 1);
//        for (int i = 0; i < length / 2; i++) {
//            swap(data, k + 1 + i, data.length - i - 1);
//        }
//        return true;
//    }
//
//    public static void per(int[] v) {
//        for (int i = 0; i < v.length; i++) {
//            for (int j = i; j < v.length; j++) {
//                swap(v, i, j);
//            }
//        }
//    }
//
//    public static void swap(int[] v, int a, int b) {
//        if (v[a] != v[b]) {
//            int t = v[a];
//            v[a] = v[b];
//            v[b] = t;
//        }
//    }
//
//    public static void iterate(int[] v, int pos, boolean incr) {
//
//        if (v[pos] == 1) {
//            //somar
//            if (onlyOne(v, pos)) {
//                p("+");
//                int sum = sum(v, pos);
//                clear(v, pos);
//                v[pos] = sum + (incr ? 1 : 0);
//            } else {
//                for (int i = pos; i < v.length; i++) {
//                    if (v[i] != 0 && v[i] != 1) {
//                        //separa
//                        p("*2");
//                        iterate(v, i, false);
//                        break;
//                    }
//                }
//            }
//        } else if (sum(v, pos + 1) >= 2 && one(v, pos + 1)) {
//            //juntar
//            p("*");
//            iterate(v, pos + 1, false);
//        } else {
//            //separa
//            p("%");
//            shift(v);
//            v[pos] = v[pos + 1] - 1;
//            v[pos + 1] = 1;
//
//        }
//    }
//
//    static boolean DEBUG = true;
//
//    public static void p(String s) {
//        if (DEBUG) {
//            System.out.println(s);
//        }
//    }
//
//    public static boolean onlyOne(int[] v, int pos) {
//        for (int i = pos; i < v.length; i++) {
//            if (v[i] == 0) {
//                return true;
//            } else if (v[i] != 1) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public static boolean one(int[] v, int pos) {
//        for (int i = pos; i < v.length; i++) {
//            if (v[i] == 1) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static void shift(int[] v) {
//        for (int i = v.length - 1; i > 0; i--) {
//            v[i] = v[i - 1];
//        }
//    }
//
//    public static void shift(int[] v, int pos) {
//        for (int i = v.length - 1; i > pos; i--) {
//            v[i] = v[i - 1];
//        }
//    }
//
//    public static void clear(int[] v) {
//        for (int i = 0; i < v.length; i++) {
//            v[i] = 0;
//        }
//    }
//
//    public static void clear(int[] v, int pos) {
//        for (int i = pos; i < v.length; i++) {
//            v[i] = 0;
//        }
//    }
//
//    public static int sum(int[] v, int pos) {
//        int sum = 0;
//        for (int i = pos; i < v.length; i++) {
//            sum += v[i];
//        }
//        return sum;
//    }
//
//    public static int sum(int[] v) {
//        int sum = 0;
//        for (int i : v) {
//            sum += i;
//        }
//        return sum;
//    }
//
//}
