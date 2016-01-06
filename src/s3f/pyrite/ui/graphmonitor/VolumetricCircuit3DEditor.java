/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui.graphmonitor;

import javax.swing.JRootPane;
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
            final CircuitFile C = c;
            new Thread() {
                @Override
                public void run() {
                    TextFile textFile = C;
                    Circuit circuit;
                    if (!textFile.getText().isEmpty()) {
                        circuit = CircuitDOTParser.parseFromFile(textFile.getText());
                        new TempTestWindow(circuit);
                    } else {
                        circuit = new Circuit();
                    }
                    drawingPanel.setCircuit(circuit);
                    volumetricCircuitFile.setCircuit(circuit);
                }
            }.start();
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
//        for (Object o : volumetricCircuitFile.getExternalResources()) {
//            if (o instanceof TextFile) {
//                TextFile textFile = (TextFile) o;
////                setContent(textFile);
//            }
//        }
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
