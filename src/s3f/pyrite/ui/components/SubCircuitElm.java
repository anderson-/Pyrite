/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui.components;

import com.falstad.circuit.CircuitController;
import com.falstad.circuit.CircuitElm;
import com.falstad.circuit.CircuitSimulator;
import com.falstad.circuit.EditInfo;
import com.falstad.circuit.elements.ChipElm;
import com.falstad.circuit.elements.GateElm;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import s3f.core.plugin.EntityManager;
import s3f.core.plugin.PluginManager;
import s3f.core.plugin.SimulableElement;
import s3f.core.project.Element;
import s3f.core.project.Project;
import s3f.pyrite.types.CircuitFile;
import s3f.pyrite.ui.VolimetricCircuitEditor;

/**
 *
 * @author anderson
 */
public class SubCircuitElm extends ChipElm {

    private CircuitSimulator cs;
    private int postCount = 0;
    private ArrayList<MyLogicInputElm> inputs = new ArrayList<>();
    private ArrayList<MyLogicOutputElm> outputs = new ArrayList<>();
    private String name = "";
    private String circuit = "";
    private SubCircuitElm parent = null;

    boolean hasReset() {
        return false;
    }

    public SubCircuitElm(int xx, int yy) {
        super(xx, yy);
    }

    public SubCircuitElm(int xa, int ya, int xb, int yb, int f,
            StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        if (st.hasMoreTokens()) {
            name = st.nextToken("\0").trim();
            reload();
        }
    }

    public static boolean isStable(CircuitSimulator cs) {
        if (cs != null) {
            for (int i = 0; i < cs.elmListSize(); i++) {
                CircuitElm elm = cs.getElm(i);
                if (elm instanceof SubCircuitElm) {
                    if (!isStable(((SubCircuitElm) elm).cs)) {
                        return false;
                    }
                }
            }
        }
        return !cs.getAnalyzeFlag();
    }

    @Override
    public void draw(Graphics g) {
        int xc = x;
        int yc = y;
        int circleSize = 5;
        super.draw(g);

        SubCircuitElm i = this;
        boolean s = true;
        while (s && i != null) {
            s &= isStable(i.cs);
            i = i.parent;
        }
        if (this.cs.isStopped()) {
            g.setColor(Color.ORANGE);
            g.fillOval(xc - circleSize, yc - circleSize, circleSize * 2, circleSize * 2);
        } else if (this.cs != null && s) {
            g.setColor(Color.green);
            g.fillOval(xc - circleSize, yc - circleSize, circleSize * 2, circleSize * 2);
        } else {
            g.setColor(Color.red);
            g.fillOval(xc - circleSize, yc - circleSize, circleSize * 2, circleSize * 2);
        }
    }

    public void setInternalCircuitSimulator(CircuitSimulator cs) {
        if (this.cs != null && !this.cs.isStopped()) {
            CircuitController.reset(this.cs);
        }
        this.cs = cs;
    }

    @Override
    public String dump() {
        return super.dump() + " " + name;
    }

    public String getChipName() {
        return name;
    }

    public void setupPins() {
        sizeX = 2;
        if (cs == null) {
            sizeY = 2;
            postCount = 0;
            pins = new ChipElm.Pin[0];
        } else {
            inputs.clear();
            outputs.clear();
            for (int i = 0; i < cs.elmListSize(); i++) {
                CircuitElm elm = cs.getElm(i);
                if (elm instanceof MyLogicInputElm) {
                    inputs.add((MyLogicInputElm) elm);
                } else if (elm instanceof MyLogicOutputElm) {
                    outputs.add((MyLogicOutputElm) elm);
                }
            }

            Collections.sort(inputs, new Comparator<MyLogicInputElm>() {
                @Override
                public int compare(MyLogicInputElm o1, MyLogicInputElm o2) {
                    return o1.name.compareTo(o2.name);
                }
            });

            Collections.sort(outputs, new Comparator<MyLogicOutputElm>() {
                @Override
                public int compare(MyLogicOutputElm o1, MyLogicOutputElm o2) {
                    return o1.name.compareTo(o2.name);
                }
            });

            if (inputs.size() > outputs.size()) {
                sizeY = inputs.size();
            } else {
                sizeY = outputs.size();
            }
            int pc = inputs.size() + outputs.size();
            pins = new ChipElm.Pin[pc];
            int p = 0;
            for (int i = 0; i < inputs.size(); i++) {
                pins[p] = new ChipElm.Pin(i, SIDE_W, Character.toString((char) (65 + i)));//A
                p++;
            }

            for (int i = 0; i < outputs.size(); i++) {
                pins[p] = new ChipElm.Pin(i, SIDE_E, Character.toString((char) (83 + i)));//S
                pins[p].output = true;
                p++;
            }
            postCount = pc;
        }
    }

    public int getPostCount() {
        return postCount;
    }

    public int getVoltageSourceCount() {
        return outputs.size();
    }

    public void execute() {

        if (postCount == pins.length) {

            int k = 0;

            for (MyLogicInputElm i : inputs) {
                if (i.getPosition() != (pins[k].value ? 1 : 0)) {
                    i.setPosition(pins[k].value ? 1 : 0);
                    i.setSubCir(true);
                    i.setVInput(volts[k]);
                }
                k++;
            }
            for (MyLogicOutputElm o : outputs) {
//                pins[k].value = "H".equals(o.getValue());
                sim.updateVoltageSource(0, nodes[k], pins[k].voltSource, o.getVoltageDiff());
                k++;
            }

        }

//        if (parent == null) {
//            System.out.println(currentSim.isStopped() + " " + cs.isStopped() + " " + this.hashCode());
//        }
//        pins[0].value = (pins[2].value ^ pins[3].value) ^ pins[4].value;
//        pins[1].value = (pins[2].value && pins[3].value) || (pins[2].value && pins[4].value)
//                || (pins[3].value && pins[4].value);
    }

    public int getDumpType() {
        return 220;
    }

    @Override
    public int getShortcut() {
        return ';';
    }

    @Override
    public EditInfo getEditInfo(int n) {
        EditInfo editInfo = super.getEditInfo(n);
        if (editInfo == null) {
            if (n == 2) {
                EditInfo ei = new EditInfo("Sub circuit", 0, -1, -1);
                ei.choice = new Choice();
                EntityManager em = PluginManager.getInstance().createFactoryManager(null);
                Project project = (Project) em.getProperty("s3f.core.project.tmp", "project");
                int i = 0;
                for (s3f.core.project.Element e : project.getElements()) {
                    if (e instanceof CircuitFile) {
                        ei.choice.add(e.getName());
                        if (e.getName().equals(name)) {
                            ei.choice.select(i);
                        }
                        i++;
                    }
                }
                return ei;
            }
            return null;
        } else {
            return editInfo;
        }
    }

    @Override
    public void setEditValue(int n, EditInfo ei) {
        super.setEditValue(n, ei);
        if (n == 2) {
            name = ei.choice.getSelectedItem();
            reload();
        }
    }

    public String getCircuit() {
        return circuit;
    }

    private void reload() {
        EntityManager em = PluginManager.getInstance().createFactoryManager(null);
        Project project = (Project) em.getProperty("s3f.core.project.tmp", "project");
        for (s3f.core.project.Element e : project.getElements()) {
            if (e instanceof CircuitFile) {
                CircuitFile circuitModule = (CircuitFile) e;
                if (circuitModule.getName().equals(name)) {
                    circuit = circuitModule.getText();
                    CircuitSimulator cs = VolimetricCircuitEditor.createCS2(circuit);

                    for (int i = 0; i < cs.elmListSize(); i++) {
                        CircuitElm elm = cs.getElm(i);
                        if (elm instanceof SubCircuitElm) {
                            SubCircuitElm subCircuitElm = (SubCircuitElm) elm;
                            subCircuitElm.parent = this;
                        }
                    }

                    cs.setStopped(false);
                    setInternalCircuitSimulator(cs);
                    setupPins();
                    allocNodes();
                    setPoints();

                    new Thread("ThreadThatWaitForCurrentSimToStart" + Thread.activeCount() + 1) {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    Thread.sleep(200);
                                    if (true) {
                                        //CircuitController.reset(SubCircuitElm.this.cs);
                                        SubCircuitElm.this.cs.setStopped(!SubCircuitElm.this.cs.isUnstable());
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }.start();

//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException ex) {
//                    }
                    break;
                }
            }
        }
    }
}
