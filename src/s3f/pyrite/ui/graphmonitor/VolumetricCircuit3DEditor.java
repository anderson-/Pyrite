/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui.graphmonitor;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import s3f.core.plugin.Data;
import s3f.core.plugin.Plugabble;
import s3f.core.project.Editor;
import s3f.core.project.Element;
import s3f.core.project.editormanager.TextFile;
import s3f.core.ui.tab.TabProperty;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.types.CircuitFile;
import s3f.pyrite.types.VolumetricCircuit;
import s3f.pyrite.core.circuitsim.CircuitDOTParser;
import s3f.pyrite.ui.graphmonitor.GraphMonitor3D;

public class VolumetricCircuit3DEditor extends DockingWindowAdapter implements Editor {

    static int DEBUG = 6;
    static boolean CLOSE = false;

//    private static final ImageIcon ICON = new ImageIcon(ModularCircuitEditor.class.getResource("/resources/icons/fugue/block.png"));
    private final Data data;
    private VolumetricCircuit volumetricCircuitFile;
    private GraphMonitor3D drawingPanel;
    private String lastcircuit = "";
    private float[] eye = null;

    public VolumetricCircuit3DEditor() {
        data = new Data("editorTab", "s3f.core.code", "Editor Tab");
        drawingPanel = new GraphMonitor3D();
        TabProperty.put(data, "3DEditor", null, "Editor de código", drawingPanel.createPanel());
    }

    @Override
    public void setContent(final Element content) {
        CircuitFile c = null;
        if (content instanceof VolumetricCircuit) {
            volumetricCircuitFile = (VolumetricCircuit) content;

            for (Object o : volumetricCircuitFile.getExternalResources()) {
                if (o instanceof CircuitFile) {
                    c = (CircuitFile) o;
                }
            }
        } else if (content instanceof CircuitFile) {
            c = (CircuitFile) content;
        }

        if (c != null) {
            if (!c.getText().equals(lastcircuit)) {
                lastcircuit = c.getText();
                new Thread() {
                    @Override
                    public void run() {
                        final JDialog dlgProgress = new JDialog((Frame) null, "Please wait...", true);
                        JLabel lblStatus = new JLabel("Working..."); // this is just a label in which you can indicate the state of the processing
                        JProgressBar pbProgress = new JProgressBar(0, 100);
                        pbProgress.setIndeterminate(true); //we'll use an indeterminate progress bar
                        dlgProgress.add(BorderLayout.NORTH, lblStatus);
                        dlgProgress.add(BorderLayout.CENTER, pbProgress);
                        dlgProgress.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // prevent the user from closing the dialog
                        dlgProgress.setSize(300, 90);

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                dlgProgress.setLocationRelativeTo(null);
                                dlgProgress.setVisible(true);
                            }
                        });

                        Circuit circuit = CircuitDOTParser.parseFromFile(lastcircuit);
                        new TempTestWindow(circuit);
                        drawingPanel.setCircuit(circuit);
                        volumetricCircuitFile.setCircuit(circuit);
                        dlgProgress.dispose();
                    }
                }.start();
            }
            data.setProperty(TabProperty.TITLE, content.getName());
            data.setProperty(TabProperty.ICON, content.getIcon());
        }

    }

    @Override
    public Element getContent() {
        return volumetricCircuitFile;
    }

    @Override
    public void update() {
        for (Object o : volumetricCircuitFile.getExternalResources()) {
            if (o instanceof TextFile) {
                TextFile textFile = (TextFile) o;
                setContent(textFile);
            }
        }
    }

    @Override
    public void selected() {

    }

    @Override
    public void windowShown(DockingWindow dw) {
    }

    @Override
    public void windowHidden(DockingWindow dw) {
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
        return new VolumetricCircuit3DEditor();
    }
}
