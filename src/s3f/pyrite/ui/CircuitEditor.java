/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui;

import com.falstad.circuit.CircuitSimulator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import s3f.core.plugin.Data;
import s3f.core.plugin.Plugabble;
import s3f.core.project.Editor;
import s3f.core.project.Element;
import s3f.core.project.editormanager.TextFile;
import s3f.core.ui.tab.TabProperty;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.ui.circuitsim.CircuitDOTParser;
import s3f.pyrite.ui.circuitsim.SimBuilder;
import s3f.pyrite.ui.components.DigitalLogicTester;
import s3f.pyrite.ui.components.MyLogicInputElm;
import s3f.pyrite.ui.components.MyLogicOutputElm;
import s3f.pyrite.ui.components.SubCircuitElm;

/**
 *
 * @author anderson
 */
public class CircuitEditor implements Editor {

//    private static final ImageIcon ICON = new ImageIcon(ModularCircuitEditor.class.getResource("/resources/icons/fugue/block.png"));
    private final Data data;
    private final CircuitSimulator circuitSimulator;
    private TextFile circuit;

    public CircuitEditor() {
        data = new Data("editorTab", "s3f.core.code", "Editor Tab");
        JApplet window = new JApplet();
        circuitSimulator = new CircuitSimulator();
        circuitSimulator.setContainer(window.getContentPane());
        circuitSimulator.startCircuitText = "";
//        circuitSimulator.register(MyLogicInputElm.class);
//        circuitSimulator.register(MyLogicOutputElm.class);
        circuitSimulator.init();

        window.setJMenuBar(circuitSimulator.getGUI().createGUI(false));
        circuitSimulator.posInit();
        final JSpinner debugLevel = new JSpinner();
        debugLevel.setModel(new SpinnerNumberModel(VolimetricCircuitEditor.DEBUG, 0, VolimetricCircuitEditor.DEBUG, 1));
        debugLevel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                VolimetricCircuitEditor.DEBUG = (int) debugLevel.getValue();
            }
        });
        JButton convertButton = new JButton("Convert!");
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                new Thread() {
                    public void run() {
                        Circuit cir = CircuitDOTParser.parse(circuitSimulator);
                        cir.printDOT();
                        
                        SimBuilder.newWindowSim(cir);
                    }
                }.start();
            }
        });
        window.getContentPane().add(debugLevel);
        window.getContentPane().add(convertButton);
//        window.pack();
//        window.setSize(new Dimension(600, 600));
        TabProperty.put(data, "Editor", null, "Editor de código", window);
    }

    @Override
    public void setContent(Element content) {
        if (content instanceof TextFile) {
            circuit = (TextFile) content;
            data.setProperty(TabProperty.TITLE, content.getName());
            data.setProperty(TabProperty.ICON, content.getIcon());
            circuitSimulator.register(MyLogicInputElm.class);
            circuitSimulator.register(MyLogicOutputElm.class);
            circuitSimulator.register(SubCircuitElm.class);
            circuitSimulator.register(DigitalLogicTester.class);
            circuitSimulator.readSetup(circuit.getText());
            circuitSimulator.addCircuitChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent pce) {
                    circuit.setText(pce.getNewValue().toString());
                }
            });

        }
    }

    @Override
    public Element getContent() {
        return circuit;
    }

    @Override
    public void update() {
//        EntityManager em = PluginManager.getInstance().createFactoryManager(null);
//        Simulator sim = (Simulator) em.getProperty("s3f.core.interpreter.tmp", "interpreter");
//        Interpreter i = new Interpreter();
//        for (Object o : flowchart.getExternalResources()) {
//            i.addResource(o);
//        }
//        i.setMainFunction(flowchartPanel.getFunction());
//        sim.clear();
//        sim.add(i);
    }

    @Override
    public void selected() {

    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void init() {

    }

    @Override
    public Plugabble createInstance() {
        return new CircuitEditor();
    }
}
