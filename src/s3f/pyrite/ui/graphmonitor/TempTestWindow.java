/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui.graphmonitor;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.fdgfa.ForceDirectedGraphFoldingAlgorithm;
import s3f.pyrite.core.fdgfa.fdgs.ForceDirectedGraphSimulation;
import s3f.pyrite.ui.ConfigurationTab;
import s3f.pyrite.ui.ConfigurationTab.*;
import s3f.pyrite.util.Vector;

/**
 *
 * @author andy
 */
public class TempTestWindow {

    @Spinner(name = "1: ", max = 100, min = 0, step = 1)
    int p1 = 5;
    @Spinner(name = "2: ", max = 100, min = 0, step = 1)
    int p2 = 15;
    @Spinner(name = "3: ", max = 100, min = 0, step = 1)
    int p3 = 10;
    @Spinner(name = "4: ", max = 100, min = 0, step = 1)
    int p4 = 70;
    @Spinner(name = "5: ", max = 100, min = 0, step = 1)
    int p5 = 0;
    @DontBreakLine
    @CustomComponent(method = "buildAsd")
    public String s = "sjadh";
    @DontBreakLine
    @CustomComponent(method = "buildAsd2")
    public String s2 = "sjadh";

    @BreakLine
    @DontBreakLine
    @Spinner(name = "Sec: ", max = 100, min = 0, step = 1)
    int sec = 1;
    @DontBreakLine
    @Spinner(name = "Delay: ", max = 1000, min = 0, step = 1)
    int delay = 10;
    @BreakLine

    @DontBreakLine
    @Checkbox(name = "Flat")
    boolean flatmode;
    @DontBreakLine
    @CustomComponent(method = "buildAsd3")
    public String s3 = "sjadh";

    @TrackValueLabel(interval = 100, name = "Is busy: ")
    Thread t = null;

    public JButton buildAsd() {
        return new JButton(new AbstractAction("Fold") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (t == null || !t.isAlive()) {
                    circuit.setSatisfied(false);
                    t = new Thread("Fold Thread") {
                        @Override
                        public void run() {
                            ForceDirectedGraphFoldingAlgorithm.weights[0] = p1 / 100.0;
                            ForceDirectedGraphFoldingAlgorithm.weights[1] = p2 / 100.0;
                            ForceDirectedGraphFoldingAlgorithm.weights[2] = p3 / 100.0;
                            ForceDirectedGraphFoldingAlgorithm.weights[3] = p4 / 100.0;
                            ForceDirectedGraphFoldingAlgorithm.weights[4] = p5 / 100.0;
                            new ForceDirectedGraphFoldingAlgorithm().fold(circuit);
                        }

                    };
                    t.start();
                }
            }
        });
    }

    public JButton buildAsd2() {
        return new JButton(new AbstractAction("Force Stop") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (t != null && t.isAlive()) {
                    t.stop();
                    if (!t.isAlive()) {
                        System.err.println("Stop fail");
                    }
                }
            }
        });
    }

    public JButton buildAsd3() {
        return new JButton(new AbstractAction("Reset layout") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (t == null || !t.isAlive()) {
                    synchronized (circuit) {
                        for (Component n : new ArrayList<>(circuit.getComponents())) {
                            n.setFixed(false);
                        }
                    }
                    final ForceDirectedGraphSimulation s = new ForceDirectedGraphSimulation(circuit);
                    s.setFlatMode(flatmode);
                    s.runSimulation(sec * 1000, delay);
                    t = new Thread() {
                        @Override
                        public void run() {
                            s.waitSim();
                            circuit.setSatisfied(true);
                            synchronized (circuit) {
                                for (Component n : new ArrayList<>(circuit.getComponents())) {
                                    n.setFixed(true);
                                }
                            }
                        }
                    };
                    t.start();
                }
            }
        }
        );
    }

    Circuit circuit;

    public TempTestWindow(Circuit circuit) {
        this.circuit = circuit;
        new ConfigurationTab(this);
    }
}
