/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import s3f.pyrite.core.intervaltree.HDIntervalTree;
import s3f.pyrite.ui.ConfigurationTab;
import s3f.pyrite.ui.ConfigurationTab.Checkbox;
import s3f.pyrite.ui.ConfigurationTab.CustomComponent;
import s3f.pyrite.ui.ConfigurationTab.Panel;
import s3f.pyrite.ui.ConfigurationTab.TrackValue;

/**
 *
 * @author antunes
 */
public class DefaultGridFittingTool implements GridFittingTool {

    private static class Parameters {

        public Parameters() {
//            new Thread() {
//                @Override
//                public void run() {
//                    while (true) {
//                        System.out.println(optimize);
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }
//            }.start();
        }

        private static class SubP0 {

            @Checkbox(name = "test1")
            public boolean t1 = true;

            @Checkbox(name = "asdasd")
            public boolean t2 = true;
        }

        @Checkbox(name = "optimize")
        public boolean optimize2 = true;

        @Panel(name = "caixa pra p0")
        public SubP0 p0 = new SubP0();

        @Panel(name = "p1")
        public SubP0 p1 = new SubP0();

        @CustomComponent(method = "buildAsd")
        public String asd = "Testasdasdasdse UHUL!";

        private JComponent buildAsd() {
            return new JLabel(asd);
        }

        //---x---
        public int sleep = 100;
        public boolean chain = false;
        public long seed = 0;
        public boolean shuffleNg = false;
        public boolean optimize = false;

    }

    private final Parameters parameters;
    private final Random rand;

    public static void main(String[] args) {
        new ConfigurationTab(new Parameters());
    }

    public DefaultGridFittingTool() {
        parameters = new Parameters();
        rand = new Random(0);
    }

    @Override
    public void fit(Circuit circuit, Grid grid) {
        if (parameters.shuffleNg) {
            rand.setSeed(parameters.seed);
        }

        HDIntervalTree tree = new HDIntervalTree(3);
        //##cria e atualiza a arvore
        Queue<Component> s = new ArrayDeque<>(circuit.getComponents());
        int size = 0, count = 0;
        while (!s.isEmpty()) {
            Component v = s.remove();
            if (s.size() != size) {
                size = s.size();
                count = 0;
            } else {
                count++;
                if (count > size) {
                    break;
                }
            }
//            System.out.println(s.size());
            if (v.getPos() != null) {
                for (Connection c : v.getConnections()) {
                    Component j = c.getOtherComponent(v);
                    if (j.getPos() == null) {
                        /*
                         coloca j no lugar mais proximo, com expansão 
                         de caminhos se necessario;
                         */
                        //###arvore - resetCube(cube);
                        placeNg(v, j, tree, grid);
                        count = 0;
                        //makePathAndPlace(v, j, cube, t);
                        sleep();
                        if (!parameters.chain) {
                            break;
                        }
                    } else {
                        if (!c.isSatisfied()) {
                            /*
                             procura o no mais proximo* para satisfazer a 
                             conexão;
                             */
                            if (makePathTo(v, j, grid)) {
                                count = 0;
                                sleep();
                            } else {
                                if (!s.contains(v)) {
                                    s.offer(v);
                                }
//                                System.out.println(v.getUID() + " -/-> " + j.getUID());
                            }
                            if (!parameters.chain) {
                                if (!s.contains(v)) {
                                    s.offer(v);
                                }
                                break;
                            }
                        }
                    }
                }
            } else {
                s.offer(v);
            }
        }
    }

    public void sleep() {
        sleep(parameters.sleep);
    }

    public void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException ex) {

        }
    }

    private int countNg(HDIntervalTree tree, int[] pos, Grid grid) {
        int i = 0;
        for (int[] l : grid.getNeighborhood(pos)) {
            List nh = tree.get(l[0], l[1], l[2]);
            i += nh.size();
        }
        return i;
    }

    private void placeNg(Component v, Component j, HDIntervalTree tree, Grid grid) {

    }

    private List<int[]> minPathTo(Component v, Component j, Grid grid) {
        return null;
    }

    private boolean makePathTo(Component v, Component j, Grid grid) {
        return false;
    }

}
