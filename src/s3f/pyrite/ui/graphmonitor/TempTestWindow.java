/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui.graphmonitor;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.fdgfa.ForceDirectedGraphFoldingAlgorithm;
import s3f.pyrite.core.fdgfa.fdgs.ForceDirectedGraphSimulation;
import s3f.pyrite.ui.ConfigurationTab;
import s3f.pyrite.ui.ConfigurationTab.*;

/**
 *
 * @author andy
 */
public class TempTestWindow {

    @CustomComponent(method = "buildAsd")
    public String s = "sjadh";
    Thread t = null;

    public JButton buildAsd() {
        return new JButton(new AbstractAction("Hello") {
            @Override
            public void actionPerformed(ActionEvent ae) {
//                ForceDirectedGraphSimulation s = new ForceDirectedGraphSimulation(circuit);
//                s.runSimulation(1000, 10);
                if (t == null || !t.isAlive()) {
                    t = new Thread("Fold Thread") {
                        @Override
                        public void run() {
                            new ForceDirectedGraphFoldingAlgorithm().fold(circuit);
                        }

                    };
                    t.start();
                }
            }
        });
    }

    Circuit circuit;

    public TempTestWindow(Circuit circuit) {
        this.circuit = circuit;
        new ConfigurationTab(this);
    }
}
