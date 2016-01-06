/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui.graphmonitor;

import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
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

public class VolumetricCircuit2DEditor extends DockingWindowAdapter implements Editor {

    static int DEBUG = 6;
    static boolean CLOSE = false;

//    private static final ImageIcon ICON = new ImageIcon(ModularCircuitEditor.class.getResource("/resources/icons/fugue/block.png"));
    private final Data data;
    private VolumetricCircuit volumetricCircuitFile;
    private GraphMonitor2D drawingPanel;
    private float[] eye = null;
final JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.drawOval(20, 20, 40, 40);
            }
        };
    public VolumetricCircuit2DEditor() {
        data = new Data("editorTab", "s3f.core.code", "Editor Tab");
        drawingPanel = GraphMonitor2D.createFrame();
        TabProperty.put(data, "2DEditor", null, "Editor de código", drawingPanel);
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
                    } else {
                        circuit = new Circuit();
                    }
                    drawingPanel.setCircuit(circuit);
                    new TempTestWindow(circuit);
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
//                setContent(textFile);
//            }
//        }
    }

    @Override
    public void selected() {
    }

    @Override
    public void windowShown(DockingWindow dw) {
        drawingPanel.requestFocusInWindow();
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
        return new VolumetricCircuit2DEditor();
    }
}
