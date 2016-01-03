/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui;

import com.falstad.circuit.CircuitElm;
import com.falstad.circuit.CircuitNode;
import com.falstad.circuit.CircuitNodeLink;
import com.falstad.circuit.CircuitSimulator;
import com.falstad.circuit.elements.GroundElm;
import com.falstad.circuit.elements.RailElm;
import com.falstad.circuit.elements.WireElm;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import org.apache.commons.collections15.Transformer;
import quickp3d.DrawingPanel3D.Scene3D;
import s3f.core.plugin.Data;
import s3f.core.plugin.Plugabble;
import s3f.core.project.Editor;
import s3f.core.project.Element;
import s3f.core.project.editormanager.TextFile;
import s3f.core.ui.tab.TabProperty;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.Connection;
import s3f.pyrite.types.CircuitFile;
import s3f.pyrite.types.VolumetricCircuit;
import s3f.pyrite.ui.circuitsim.CircuitDOTParser;
import s3f.pyrite.ui.components.DigitalLogicTester;
import s3f.pyrite.ui.components.MyLogicInputElm;
import s3f.pyrite.ui.components.MyLogicOutputElm;
import s3f.pyrite.ui.components.SubCircuitElm;
import s3f.pyrite.ui.drawing3d.Circuit3DEditPanel;

public class VolimetricCircuitEditor extends DockingWindowAdapter implements Editor {

    static int DEBUG = 6;
    static boolean CLOSE = false;

//    private static final ImageIcon ICON = new ImageIcon(ModularCircuitEditor.class.getResource("/resources/icons/fugue/block.png"));
    private final Data data;
    private VolumetricCircuit volumetricCircuitFile;
    private Circuit3DEditPanel drawingPanel;
    private Scene3D applet;
    private float[] eye = null;

    JRootPane p = new JRootPane();

    public VolimetricCircuitEditor() {
        data = new Data("editorTab", "s3f.core.code", "Editor Tab");
        createApplet();
        TabProperty.put(data, "Editor", null, "Editor de código", p);
    }

    private void createApplet() {
        p.getContentPane().removeAll();
        if (drawingPanel == null) {
            drawingPanel = new Circuit3DEditPanel(null);
        } else {
            eye = drawingPanel.getEye();
            drawingPanel = new Circuit3DEditPanel(drawingPanel.getCircuit());
            drawingPanel.setEye(eye);
        }
        applet = drawingPanel.getApplet();
        //p.getContentPane().add(Gears.getGears());
        p.getContentPane().add(applet);
        new Thread() {
            @Override
            public void run() {
                applet.init();
//                try {
//                    Thread.sleep(500);
//                    drawingPanel.getApplet().invalidate();
//                } catch (InterruptedException ex) {
//                }
            }
        }.start();
//        new Thread() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(400);
//                    } catch (InterruptedException ex) {
//                    }
//
//                    if (PGL.canvas != null) {
//                        p.getContentPane().removeAll();
//                        p.getContentPane().add(PGL.canvas);
//                        new Thread() {
//                            @Override
//                            public void run() {
//                                while (true) {
//                                    try {
//                                        Thread.sleep(100);
//                                        PGL.canvas.repaint();
//                                    } catch (InterruptedException ex) {
//                                    }
//                                }
//                            }
//                        }.start();
//                        return;
//                    }
////                    p3d.get
//                }
//            }
//        }.start();
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
        createApplet();
    }

    @Override
    public void windowHidden(DockingWindow dw) {
        if (applet != null) {
            applet.stop();
        }
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
        return new VolimetricCircuitEditor();
    }
}
