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
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
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
import s3f.pyrite.types.Position3DFile;
import s3f.pyrite.ui.components.DigitalLogicTester;
import s3f.pyrite.ui.components.MyLogicInputElm;
import s3f.pyrite.ui.components.MyLogicOutputElm;
import s3f.pyrite.ui.components.SubCircuitElm;
import s3f.pyrite.ui.drawing3d.Circuit3DEditPanel;

/**
 *
 * @author anderson
 */
public class Editor3D extends DockingWindowAdapter implements Editor {

    static int DEBUG = 6;
    static boolean CLOSE = false;

//    private static final ImageIcon ICON = new ImageIcon(ModularCircuitEditor.class.getResource("/resources/icons/fugue/block.png"));
    private final Data data;
    private Position3DFile textFile;
    private Circuit3DEditPanel drawingPanel;
    private Scene3D applet;
    private float[] eye = null;

    JRootPane p = new JRootPane();

    public Editor3D() {
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
        applet.init();
        p.getContentPane().add(applet);
    }

    @Override
    public void setContent(Element content) {
        if (content instanceof Position3DFile) {
            textFile = (Position3DFile) content;
        }

        if (content instanceof TextFile) {
            TextFile textFile = (TextFile) content;
            if (!textFile.getText().isEmpty()) {
                Circuit circuit = parseString(textFile.getText());
                drawingPanel.setCircuit(circuit);
//                showGraph(createGraph(circuit), true);
            } else {
                drawingPanel.setCircuit(new Circuit());
            }
            data.setProperty(TabProperty.TITLE, content.getName());
            data.setProperty(TabProperty.ICON, content.getIcon());
        }
    }

    private static CircuitSimulator createDummyCS(String text) {
        JApplet window = new JApplet();
        CircuitSimulator cs = new CircuitSimulator();
        cs.setContainer(window.getContentPane());
        cs.startCircuitText = text;
        {//TODO
            cs.register(MyLogicInputElm.class);
            cs.register(MyLogicOutputElm.class);
            cs.register(SubCircuitElm.class);
            cs.register(DigitalLogicTester.class);
        }
        cs.init();
        window.setJMenuBar(cs.getGUI().createGUI(true));
        cs.posInit();
        cs.analyzeCircuit();
        for (int i = 0; i < 30; i++) {
            cs.updateCircuit(null);
        }
        return cs;
    }

    public static CircuitSimulator createCS(String text) {
        JApplet window = new JApplet();
        CircuitSimulator cs = new CircuitSimulator();
        cs.setContainer(window.getContentPane());
        cs.startCircuitText = text;
        {//TODO
            cs.register(MyLogicInputElm.class);
            cs.register(MyLogicOutputElm.class);
            cs.register(SubCircuitElm.class);
            cs.register(DigitalLogicTester.class);
        }
        cs.init();
        window.setJMenuBar(cs.getGUI().createGUI(true));
        cs.posInit();
        cs.analyzeCircuit();
        for (int i = 0; i < 30; i++) {
            cs.updateCircuit(null);
        }
        JFrame f = new JFrame();
        f.setContentPane(window);
        f.setSize(new Dimension(400, 400));
        f.pack();
        if (CLOSE) {
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else {
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        f.setVisible(true);
        return cs;
    }

    public static CircuitSimulator createCS2(String text) {
        JApplet window = new JApplet();
        final CircuitSimulator cs = new CircuitSimulator();
        cs.setStopped(false);
        cs.setContainer(window.getContentPane());
        cs.startCircuitText = text;
        {//TODO
            cs.register(MyLogicInputElm.class);
            cs.register(MyLogicOutputElm.class);
            cs.register(SubCircuitElm.class);
            cs.register(DigitalLogicTester.class);
        }
        cs.init();
        window.setJMenuBar(cs.getGUI().createGUI(true));
        cs.posInit();
//        cs.analyzeCircuit();
//        cs.updateCircuit(null);
//        cs.updateCircuit(null);
//        JFrame f = new JFrame();
//        f.setContentPane(window);
//        f.setSize(new Dimension(400, 400));
//        f.pack();
//        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        f.setVisible(true);
        return cs;
    }

    public static void main(String[] args) {
        String hard = "$ 1 5.0E-6 10.20027730826997 54.0 5.0 50.0\n"
                + "t 80 176 80 256 0 1 0.587480887764175 -3.7727338911607884 100.0\n"
                + "t 96 256 144 256 0 1 0.6027189712514504 0.6397852210750364 100.0\n"
                + "L 64 256 0 256 0 1 false 5.0 0.0\n"
                + "r 80 176 80 96 0 4700.0\n"
                + "R 80 96 0 96 0 0 40.0 5.0 0.0 0.0 0.5\n"
                + "w 80 96 144 96 0\n"
                + "t 240 176 240 256 0 1 0.5908812846401537 0.5911300429119931 100.0\n"
                + "t 256 256 304 256 0 1 -0.03681749155174671 2.4875827183941954E-4 100.0\n"
                + "r 240 96 240 176 0 4700.0\n"
                + "w 144 96 240 96 0\n"
                + "w 240 96 304 96 0\n"
                + "r 304 96 304 176 0 1000.0\n"
                + "w 304 176 368 176 0\n"
                + "M 368 176 416 176 0 2.5\n"
                + "L 224 256 176 256 0 0 false 5.0 0.0\n"
                + "r 144 96 144 176 0 1000.0\n"
                + "w 144 176 144 208 0\n"
                + "w 304 176 304 240 0\n"
                + "w 144 208 368 208 0\n"
                + "w 368 208 368 176 0\n"
                + "w 144 208 144 240 0\n"
                + "g 144 272 144 304 0\n"
                + "g 304 272 304 304 0\n";

        String def = "$ 1 5.0E-6 10.20027730826997 52 5.0 50.0\n"
                + "t 80 208 128 208 0 1 0.6381941100809847 0.6478969866591134 100.0\n"
                + "t 176 208 224 208 0 1 -0.009702876570569624 7.55914423854251E-12 100.0\n"
                + "t 272 208 320 208 0 1 -0.009702876570569624 7.55914423854251E-12 100.0\n"
                + "w 128 160 128 192 0\n"
                + "w 224 160 224 192 0\n"
                + "w 128 160 224 160 0\n"
                + "w 224 160 320 160 0\n"
                + "w 320 160 320 192 0\n"
                + "r 80 208 80 272 0 470.0\n"
                + "r 176 208 176 272 0 470.0\n"
                + "r 272 208 272 272 0 470.0\n"
                + "+ 80 272 80 304 0 1 false 3.6 0.0 a\n"
                + "+ 176 272 176 304 0 0 false 3.6 0.0 b\n"
                + "+ 272 272 272 304 0 0 false 3.6 0.0 c\n"
                + "w 128 224 128 336 0\n"
                + "w 224 224 224 336 0\n"
                + "w 320 224 320 336 0\n"
                + "w 320 336 224 336 0\n"
                + "w 224 336 128 336 0\n"
                + "r 320 160 320 64 0 640.0\n"
                + "g 320 336 320 368 0\n"
                + "R 320 64 288 64 0 0 40.0 3.6 0.0 0.0 0.5\n"
                + "- 352 160 400 160 0 2.5 s\n"
                + "w 320 160 352 160 0\n";

        String simple = "$ 1 5.0E-6 10.20027730826997 52.0 5.0 50.0\n"
                + "t 224 192 272 192 0 1 -3.5999999999125003 2.3499999999562566E-11 100.0\n"
                + "w 272 144 272 176 0\n"
                + "r 224 192 224 256 0 470.0\n"
                + "+ 224 256 224 288 0 0 false 3.6 0.0 c\n"
                + "w 272 208 272 320 0\n"
                + "r 272 144 272 48 0 640.0\n"
                + "g 272 320 272 352 0\n"
                + "R 272 48 240 48 0 0 40.0 3.6 0.0 0.0 0.5\n"
                + "- 304 144 352 144 0 2.5 s\n"
                + "w 272 144 304 144 0\n";

        String alg = "$ 0 5.0E-6 1.0312258501325766 50.0 5.0 50.0\n"
                + "t 224 176 272 176 0 1 0.0 0.0 100.0\n"
                + "r 272 160 288 96 0 100.0\n"
                + "t 272 192 272 240 0 1 0.0 0.0 100.0\n"
                + "t 224 176 176 176 0 1 0.0 0.0 100.0\n";

        String alg2 = "$ 0 5.0E-6 1.0312258501325766 50.0 5.0 50.0\n"
                + "t 192 208 240 208 0 1 0.0 0.0 100.0\n"
                + "r 240 192 256 128 0 100.0\n"
                + "t 288 240 240 240 0 1 0.0 0.0 100.0\n"
                + "t 192 208 144 208 0 1 0.0 0.0 100.0\n";

        String filter = "$ 1 5.0E-6 1.0312258501325766 50.0 5.0 50.0\n"
                + "d 176 176 240 112 1 0.805904783\n"
                + "d 304 176 240 112 1 0.805904783\n"
                + "d 240 240 304 176 1 0.805904783\n"
                + "d 240 240 176 176 1 0.805904783\n"
                + "+ 176 176 144 176 0 1 false 5.0 0.0 A\n"
                + "+ 304 176 336 176 0 0 false 5.0 0.0 B\n"
                + "- 240 112 240 80 0 2.5 S\n"
                + "- 240 240 240 272 0 2.5 T\n";

//        Circuit c = createCircuit2(createDummyCS(alg2));
        //showGraph(c, "");
        //String s = dumpCircuit(c, createDummyCS(""));
        //createCS(s);
        Editor3D.DEBUG = 5;
        Editor3D.SLEEP = 0;
        Editor3D.CLOSE = true;

        String circuitString = hard;
        boolean anim = false;
        if (anim) {
            new Thread() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, "Close this dialog to speed thing up.");
                    Editor3D.SLEEP = 0;
                }
            }.start();
            Editor3D.SLEEP = 500;
        }
        Editor3D.createCS(circuitString);
        Circuit cir = Editor3D.parseString(circuitString, anim);
        Editor3D.createCS(Editor3D.dumpCircuit(cir));
    }

    private static void testJoin() {
        Circuit cir = new Circuit();

        Component a = new Component();
        Component b = new Component();
        Component c = new Component();
        Component d = new Component();

        Connection x = a.createConnection(b);
        Connection y = a.createConnection(c);
        Connection z = a.createConnection(d);

        x.setTerminalA(0);
        y.setTerminalA(1);
        z.setTerminalA(2);

        cir.addComponent(a);
        cir.addComponent(b);
        cir.addComponent(c);
        cir.addComponent(d);

        cir.addConnection(x);
        cir.addConnection(y);
        cir.addConnection(z);

        Component b2 = new Component();
        Component c2 = new Component();
        Component d2 = new Component();

        Connection x2 = b2.createConnection(b);
        Connection y2 = c2.createConnection(c);
        Connection z2 = d2.createConnection(d);

        cir.addComponent(b2);
        cir.addComponent(c2);
        cir.addComponent(d2);

        cir.addConnection(x2);
        cir.addConnection(y2);
        cir.addConnection(z2);

        cir.removeConnection(a.appendAndConsume(b));
        cir.removeComponent(b);

        showGraph(cir, "");
    }

    private static void joinAll(Circuit cir, Class type) {
        Component c = null;
        for (Iterator<Component> it = cir.getComponents().iterator(); it.hasNext();) {
            Component t = it.next();
            if (type.isInstance(t.whut)) {
                t.setCoupler(true);
                if (c == null) {
                    c = t;
                } else {
                    c.appendAndConsume(t);
                    it.remove();
                }
            }
        }
    }

    public static long SLEEP = 0;

    private static void sleep() {
        if (SLEEP > 0) {
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException ex) {
            }
        }
    }

    private static Circuit createCircuit2(CircuitSimulator cs) {
        return createCircuit2(cs, new Circuit());
    }

    private static Circuit createCircuit2(CircuitSimulator cs, Circuit cir) {

        {
            if (cs.elmListSize() == 0) {
                return cir;
            } else {

//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                }
                cs.analyzeCircuit();//nodeListSize
            }
//            showGraph(cir, "www", true);

            /* place every component as an node */
            cir.setStatus("placing components as nodes");
            HashMap<CircuitElm, Component> nodes = new HashMap<>();
            for (int i = 0; i < cs.elmListSize(); i++) {
                CircuitElm elm = cs.getElm(i);
                Component c = new Component();
                c.whut = elm;
                c.setData(elm.dump());
                c.setName(elm.getDumpClass().getSimpleName());
                cir.addComponent(c);
                nodes.put(elm, c);
                sleep();
            }

            /* conects all the nodes with empty terminal-riented connections */
            cir.setStatus("connecting nodes");
            Point p = new Point();
            for (int i = 0; i < cs.nodeListSize(); i++) {
                CircuitNode node = cs.getCircuitNode(i);
                p.setLocation(node.x, node.y);
                if (node.internal) {
                    continue;
                }
                ArrayList<CircuitNodeLink> w = new ArrayList<>(node.links);
                for (Iterator<CircuitNodeLink> it = w.iterator(); it.hasNext();) {
                    CircuitNodeLink linkA = it.next();
                    CircuitElm a = linkA.elm;
                    for (CircuitNodeLink linkB : w) {
                        CircuitElm b = linkB.elm;
                        if (a != b) {
                            int ta;
                            for (ta = 0; ta < a.getPostCount(); ta++) {
                                if (a.getPost(ta).equals(p)) {
                                    break;
                                }
                            }
                            int tb;
                            for (tb = 0; tb < b.getPostCount(); tb++) {
                                if (b.getPost(tb).equals(p)) {
                                    break;
                                }
                            }
                            Component ca = nodes.get(a);
                            Component cb = nodes.get(b);
                            Connection con = new Connection(ca, ta, cb, tb, "");
//                            con.whut = a;
                            cir.addConnection(con);
                            sleep();
                        }
                    }
                    it.remove();
                }
            }

            cir.setStatus("done 1");
            sleep();
            if (DEBUG == 1) {
                showGraph(cir, "1 - Conected nodes");
                return cir;
            }

            /*
             remove cliques
             */
            cir.setStatus("removing cliques");
            for (int i = 0; i < cs.nodeListSize(); i++) {
                CircuitNode cn = cs.getCircuitNode(i);
                if (!cn.internal && cn.links.size() >= 3) {//triangular clique
                    Component coupler = new Component("k");
                    coupler.setCoupler(true);
                    for (CircuitNodeLink linkA : cn.links) {
                        CircuitElm cea = linkA.elm;
                        Component a = nodes.get(cea);
                        for (CircuitNodeLink linkB : cn.links) {
                            CircuitElm ceb = linkB.elm;
                            if (cea != ceb) {
                                Component b = nodes.get(ceb);
                                Connection con = a.getConnection(b);
                                if (con != null) {
                                    con = con.shiftA(coupler);
                                    cir.addConnection(con);
                                    sleep();
                                }
                            }
                        }
                    }
                    cir.addComponent(coupler);
                    sleep();
                }
            }

            cir.clean();
            cir.setStatus("done 2");
            sleep();

            if (DEBUG == 2) {
                showGraph(cir, "2 - remove cliques");
                return cir;
            }
            /*
             for each component placed before; do
             ___if component have only 2 terminals
             ______if component have only 2 connections
             ________realoc as connection
             ______end if
             ___end if
             end for
             */
            cir.setStatus("reallocating 2-terminal components");
            for (Map.Entry<CircuitElm, Component> entry : nodes.entrySet()) {
                CircuitElm e = entry.getKey();
                if (e.getPostCount() == 2 && !(e instanceof SubCircuitElm)) {
                    Component c = entry.getValue();
                    if (c.getConnections().size() == 2) {
                        /*
                         a -> [0] c [1] -> b
                         ca: a -> c
                         cb: c -> b
                         a -> [1] c [0] -> b
                         ca: a <- c
                         cb: c <- b
                         */
                        Connection ca = c.getConnections().get(0);
                        Connection cb = c.getConnections().get(1);

                        if (ca.whut == null || ca.whut instanceof WireElm) {
                            //realoc c in the connection ca
                            ca.setSubComponent(e.dump());
                            ca.whut = e;

                            if (ca.getTerminal(c) == 0 && ca.getA() == c) {
                                ca.swap();
                            } else if (ca.getTerminal(c) == 1 && ca.getB() == c) {
                                ca.swap();
                            }
                            //set c as an coupler
                            c.whut = null;
                            c.setData(null);
                            c.setName("*");
                            c.setCoupler(true);
                        } else if (cb.whut == null || cb.whut instanceof WireElm) {
                            //realoc c in the connection cb
                            cb.setSubComponent(e.dump());
                            cb.whut = e;

                            if (cb.getTerminal(c) == 0 && cb.getA() == c) {
                                cb.swap();
                            } else if (cb.getTerminal(c) == 1 && cb.getB() == c) {
                                cb.swap();
                            }
                            //set c as an coupler
                            c.whut = null;
                            c.setData(null);
                            c.setName("*");
                            c.setCoupler(true);
                        } else {
                            //create a new coupler component
                            Component coupler = new Component("k");
                            coupler.setCoupler(true);
                            //split conection and configure the new connection
                            Connection con = ca.shiftA(coupler);
                            con.setSubComponent(e.dump());
                            con.whut = e;

//                            if (con.getTerminal(c) == 1 && con.getB() == c){
//                                con.swap();
//                            } else if (cb.getTerminal(c) == 0 && con.getA() == c){
//                                con.swap();
//                            }
                            //add new elements
                            cir.addComponent(coupler);
                            cir.addConnection(con);
                            //set c as an coupler
                            c.whut = null;
                            c.setData(null);
                            c.setName("*");
                            c.setCoupler(true);
                        }
                        sleep();
                    }
                }
            }

            cir.setStatus("done 3");
            sleep();
            if (DEBUG == 3) {
                showGraph(cir, "3 - 2-terminal realoc");
                return cir;
            }

//            cir.setStatus("consuming redundant nodes");
//            ArrayList<Component[]> consume = new ArrayList<>();
//            for (Component a : cir.getComponents()) {
//                if (a.isCoupler()) {
//                    for (Connection c : a.getConnections()) {
//                        Component b = c.getOtherComponent(a);
//                        if (c.isShort() && (b.whut == null || (((CircuitElm) b.whut).getPostCount() == b.getConnections().size() && a.getConnections().size() == 2))) {
//                            //if (c.isShort() && (b.whut == null || ((CircuitElm) b.whut).getPostCount() == 1)) {
//                            consume.add(new Component[]{b, a});
//                        }
//                    }
//                }
//            }
//            for (Component[] pair : consume) {
//                Component a = pair[0];
//                Component b = pair[1];
//                if (!a.isConsumed() && !b.isConsumed()) {
//                    cir.removeConnection(a.appendAndConsume(b));
//                    cir.removeComponent(b);
//                    sleep();
//                }
//            }
//
//            cir.setStatus("done 4");
//            sleep();
//            if (DEBUG == 4) {
//                showGraph(cir, "4 - append and consume");
//                return cir;
//            }
            cir.setStatus("parsing and inserting sub-circuits");
            for (Map.Entry<CircuitElm, Component> entry : nodes.entrySet()) {
                CircuitElm e = entry.getKey();
                if (e instanceof SubCircuitElm) {
                    SubCircuitElm subCircuitElm = (SubCircuitElm) e;
                    Component c = entry.getValue();
                    long sleep = SLEEP;
                    SLEEP = 0;
                    Circuit sub = parseString(subCircuitElm.getCircuit());
                    SLEEP = sleep;
                    cir.insert(sub, c);
                    sleep();
                }
            }

            cir.setStatus("connecting positive and negative terminals");
            joinAll(cir, RailElm.class);
            sleep();
            joinAll(cir, GroundElm.class);
            sleep();

            cir.setStatus("done 5");
            sleep();
            if (DEBUG == 5) {
                showGraph(cir, "5 - SubCircuits");
                return cir;
            }

            cir.setStatus("consuming redundant nodes");
            //ArrayList<Component[]> consume = new ArrayList<>();
            boolean mod = true;
            while (mod) {
                System.out.println(";");
                mod = false;
                int x, y = 0;
                for (Component a : cir.getComponents()) {
                    x = a.getConnections().size();
                    if (!a.isConsumed() ) {
                        ArrayList<Connection> newCons = new ArrayList<>();
                        for (Iterator<Connection> aConIt = a.getConnections().iterator(); aConIt.hasNext();) {
                            Connection c = aConIt.next();
                            boolean ahh = (c.getSubComponent().isEmpty() || c.getSubComponent().startsWith("w")) && (c.whut == null || c.whut instanceof WireElm);
                            if (ahh && !c.isConsumed()) {
                                Component b = c.getOtherComponent(a);
                                y = b.getConnections().size();
                                if (!b.isConsumed() && b.isCoupler() && b.whut == null) {
                                    {
                                        System.out.println(".." + b);
                                        b.removeConnection(c);
//                                        aConIt.remove(); //a.removeConnection(c);
                                        int ter = c.getTerminal(a);
                                        c.softConsume();
                                        for (Connection con : b.getConnections()) {
                                            con.replace(b, a);
                                            con.setTerminal(a, ter);
                                            newCons.add(con);
//                                            System.out.println("pre " + a + " " + con + " " + con.getOtherComponent(a));
                                        }
//                                        b.getConnections().clear();
                                        b.setConsumed(true);
                                    }
                                    sleep();
                                    mod = true;
                                    break;
                                }
                            }
                        }
                        for (Connection con : newCons) {
//                            System.out.println("add " + a + " " + con + " " + con.getOtherComponent(a));
                            a.addConnection(con);
                        }
                        if (mod) {
//                            System.out.println(x + " " + y + " : " + (x + y) + " ==  " + a.getConnections().size());
                        }
                    }
                }
            }
            cir.clean();

//            for (Component[] pair : consume) {
//                Component b = pair[0];
//                Component a = pair[1];
//                if (!b.isConsumed() && !a.isConsumed()) {
//                    cir.removeConnection(b.appendAndConsume(a));
//                    cir.removeComponent(a);
//                    sleep();
//                    System.out.println(a + " ++ " + b);
//                } else {
//                    System.out.println(a + " -- " + b);
//                }
//            }
            cir.setStatus("done 4");
            sleep();
            if (DEBUG == 4) {
                showGraph(cir, "4 - append and consume");
                return cir;
            }
        }

        if (true) {
            return cir;
        }

        HashMap<Point, Component> allNodes = new HashMap<>();
        HashMap<CircuitElm, Integer> asd = new HashMap<>();
        ArrayList<ArrayList<Component>> w = new ArrayList<>();

        //create one node for each terminal of each component
        for (int i = 0; i < cs.elmListSize(); i++) {
            CircuitElm elm = cs.getElm(i);
            ArrayList<Component> s = new ArrayList<>();
            for (int j = 0; j < elm.getPostCount(); j++) {
                Point p = elm.getPost(j);
                if (!allNodes.containsKey(p)) {
                    Component c = new Component();
                    c.whut = elm;
                    s.add(c);
                    cir.addComponent(c);
                    allNodes.put(p, c);
                    if (!asd.containsKey(elm)) {
                        asd.put(elm, j);
                    }
                } else {
                    System.out.println("*");
                }
            }
            w.add(s);
        }
        showGraph(cir, "1");
        if (true) {
            return cir;
        }

        //wire everthing up
        for (int i = 0; i < cs.elmListSize(); i++) {
            CircuitElm elm = cs.getElm(i);
            for (int j = 0; j < elm.getPostCount(); j++) {
                Point p = elm.getPost(j);
                Component c = allNodes.get(p);
                Component c2 = allNodes.get(elm.getPost(0));
                if (c2 != null && c2 != c) {
                    Connection con = c2.createConnection(c);
                    if (elm.getPostCount() > 2) {
                        con.setTerminalA(j);
                    } else {
                        con.setSubComponent(elm.dump());
                        con.setTerminalA(0);
                    }
                    con.whut = elm;
                    cir.addConnection(con);
                }
            }
        }
        showGraph(cir, "2");

//        //defines what's node and what's edge
//        ArrayList<CircuitElm> nodes = new ArrayList<>();
//        ArrayList<CircuitElm> edges = new ArrayList<>();
//        for (int i = 0; i < cs.elmListSize(); i++) {
//            CircuitElm elm = cs.getElm(i);
//            if (elm.getPostCount() == 2) {
//                edges.add(elm);
//            } else {
//                nodes.add(elm);
//            }
//        }
//
//        //join all the terminal-nodes created before of each component into a single component 
//        for (CircuitElm elm : nodes) {
//            Component c = null;
//            for (int i = 0; i < elm.getPostCount(); i++) {
//                Component t = allNodes.get(elm.getPost(i));
//                if (c == null) {
//                    c = t;
//                    System.out.println(">" + c);
//                } else {
//                    Connection con = t.getConnection(c);
//                    if (con != null && con.whut == t.whut && c != t) {
//                        cir.removeConnection(c.appendAndConsume(t));
//                        cir.removeComponent(t);
//                        System.out.println(t + " con");
//                    } else {
//                        System.out.println(t + " ig");
//                    }
//                }
//            }
////            c.whut = elm;
////            c.setData(elm.dump());
////            c.setName(elm.getClass().getSimpleName());
//        }
////        for (CircuitElm elm : nodes) {
////            for (int i = 0; i < elm.getPostCount(); i++) {
////                Component c = allNodes.get(elm.getPost(i));
////                if (!c.isConsumed()) {
////                    c.whut = elm;
////                    c.setData(elm.dump());
////                    c.setName(elm.getClass().getSimpleName());
////                    break;
////                }
////            }
//        }
        for (Map.Entry<CircuitElm, Integer> e : asd.entrySet()) {
            CircuitElm elm = e.getKey();
            Component c = allNodes.get(elm.getPost(e.getValue()));
            System.out.println(">" + c);
            for (int i = 0; i < elm.getPostCount(); i++) {
                Component t = allNodes.get(elm.getPost(i));
                if (c != t) {
                    Connection con = t.getConnection(c);
                    if (con != null && con.whut == t.whut) {
                        cir.removeConnection(c.appendAndConsume(t));
                        cir.removeComponent(t);
                        System.out.println(t + " con");
                    } else {
                        System.out.println(t + " ig");
                    }
                }
            }
        }

        showGraph(cir, "3");

        return cir;

//        System.out.println(allNodes.size());
//
//        for (int i = 0; i < cs.elmListSize(); i++) {
//            CircuitElm elm = cs.getElm(i);
//            for (int j = 0; j < elm.getPostCount(); j++) {
//                Point p = elm.getPost(j);
//                Component c = allNodes.get(p);
//                for (int k = 0; k < ((CircuitElm) c.getData()).getPostCount(); k++) {
//                    Component c2 = allNodes.get(elm.getPost(k));
//                    if (c2 != null) {
//                        Connection con = c2.createConnection(c);
//                        cir.addConnection(con);
//                    }
//                }
//            }
//        }
//        ArrayList<CircuitElm> nodes = new ArrayList<>();
//        ArrayList<CircuitElm> edges = new ArrayList<>();
//        for (int i = 0; i < cs.elmListSize(); i++) {
//            CircuitElm elm = cs.getElm(i);
//            if (elm.getPostCount() == 2) {
//                edges.add(elm);
//            } else {
//                nodes.add(elm);
//            }
//        }
//        for (ArrayList<Component> s : w) {
//            Component c = null;
//            for (Component t : s) {
//                if (c == null) {
//                    c = t;
//                } else {
//                    c.appendAndConsume(t);
//                    cir.removeComponent(t);
//                }
//            }
//            //c.setData(elm.dump());
//        }
//        for (CircuitElm elm : nodes) {
//            Component c = null;
//            for (int i = 0; i < elm.getPostCount(); i++) {
//                Component t = allNodes.get(elm.getPost(i));
//                if (c == null) {
//                    c = t;
//                } else {
//                    c.appendAndConsume(t);
//                    cir.removeComponent(t);
//                }
//            }
//            c.setData(elm.dump());
//        }
//
//        n:
//        for (int i = 0; i < cs.nodeListSize(); i++) {
//            CircuitNode cn = cs.getCircuitNode(i);
//            if (cn.internal) {
//                continue;
//            }
//
//            for (CircuitNodeLink link : cn.links) {
//                CircuitElm elm = link.elm;
//                if (elm.getPostCount() != 2) {
//                    continue n;
//                }
//            }
//
//            Component c = null;
//            for (CircuitNodeLink link : cn.links) {
//                CircuitElm elm = link.elm;
//                Component t = s.get(w.indexOf(elm));
//                if (t == null) {
//                    System.out.println(elm);
//                    continue;
//                }
//
//                if (c == null) {
//                    c = t;
//                } else {
//                    c.appendAndConsume(t);
//                    cir.removeComponent(t);
//                }
//            }
//        }
//        n:
//        for (int i = 0; i < cs.nodeListSize(); i++) {
//            CircuitNode cn = cs.getCircuitNode(i);
//            if (cn.internal) {
//                continue;
//            }
//
//            for (CircuitNodeLink link : cn.links) {
//                CircuitElm elm = link.elm;
//                if (elm.getPostCount() != 2){
//                    continue n;
//                }
//            }
//            
//            for (CircuitNodeLink link : cn.links) {
//                CircuitElm elm = link.elm;
//                
//            }
//        }
//        if (true) {
//            return cir;
//        }
//
//        HashMap<Point, Object[]> nodeComponents = new HashMap<>();
//        for (CircuitElm elm : nodes) {
//            Component c = new Component();
//            c.setData(elm.dump());
//            cir.addComponent(c);
//            for (int i = 0; i < elm.getPostCount(); i++) {
//                nodeComponents.put(elm.getPost(i), new Object[]{c, elm, i});
//            }
//        }
//
//        for (int i = 0; i < cs.nodeListSize(); i++) {
//            CircuitNode circuitNode = cs.getCircuitNode(i);
//            if (circuitNode.internal) {
//                continue;
//            }
//            Component c = new Component();
//            for (int j = 0; j < circuitNode.links.size(); j++) {
//                CircuitElm elm = circuitNode.links.get(j).elm;
//                if (elm.getPostCount() != 2) {
//                    for (int k = 0; k < elm.getPostCount(); k++) {
//                        Object[] v = nodeComponents.get(elm.getPost(k));
//                        if (v != null) {
//                            Component c2 = (Component) v[0];
//                            String t2 = ((Integer) v[2]).toString();
//                            Connection con = new Connection(c, "", c2, t2, "");
//                            cir.addConnection(con);
//                            cir.addComponent(c);
//                        }
//                    }
//
//                }
//            }
//        }
//
//        for (CircuitElm edge : edges) {
//            Object[] v1 = nodeComponents.get(edge.getPost(0));
//            Object[] v2 = nodeComponents.get(edge.getPost(1));
//
////            if (v1 == null) {
////                ArrayList<CircuitElm> list = map.get(edge.getPost(0));
////                if (list != null) {
////                    for (CircuitElm elm : list) {
////                        if (elm.getPostCount() != 2) {
////                            Component c = new Component();
////                            c.setData(elm.dump());
////                            cir.addComponent(c);
////                            for (int i = 0; i < elm.getPostCount(); i++) {
////                                Point p = elm.getPost(i);
////                                Object[] o = new Object[]{c, elm, i};
////                                nodeComponents.put(p, o);
////                                if (edge.getPost(0) == p) {
////                                    v1 = o;
////                                }
////                            }
////                        }
////                    }
////                }
////            }
////
////            if (v2 == null) {
////                ArrayList<CircuitElm> list = map.get(edge.getPost(1));
////                if (list != null) {
////                    for (CircuitElm elm : list) {
////                        if (elm.getPostCount() != 2) {
////                            Component c = new Component();
////                            c.setData(elm.dump());
////                            cir.addComponent(c);
////                            for (int i = 0; i < elm.getPostCount(); i++) {
////                                Point p = elm.getPost(i);
////                                Object[] o = new Object[]{c, elm, i};
////                                nodeComponents.put(p, o);
////                                if (edge.getPost(0) == p) {
////                                    v2 = o;
////                                }
////                            }
////                        }
////                    }
////                }
////            }
//            if (v1 != null && v2 != null) {
//                Component c1 = (Component) v1[0];
//                Component c2 = (Component) v2[0];
//
//                String t1 = ((Integer) v1[2]).toString();
//                String t2 = ((Integer) v1[2]).toString();
//
//                Connection c = new Connection(c1, t1, c2, t2, edge.dump());
//                cir.addConnection(c);
//            } else {
//
//            }
//        }
//
//        return cir;
    }

    private static Circuit createCircuit(CircuitSimulator cs) {
        Circuit cir = new Circuit();

        HashMap<Point, Component> jointMap = new HashMap<>();
        HashMap<CircuitElm, Component> compMap = new HashMap<>();
        HashMap<CircuitElm, ArrayList<CircuitNode>> kMap = new HashMap<>();

        for (int i = 0; i < cs.nodeListSize(); i++) {
            CircuitNode node = cs.getCircuitNode(i);
            if (node.internal || node.links.isEmpty()) {
                System.out.println("*");
                continue;
            }
//            
            ArrayList<CircuitElm> ts = new ArrayList<>();
            for (CircuitNodeLink link : node.links) {
                //if (link.elm.getPostCount() > 2) {
                if (link.elm.getPostCount() != 2) {
                    CircuitElm t = link.elm;
                    ts.add(t);
                    if (!kMap.containsKey(t)) {
                        kMap.put(t, new ArrayList<CircuitNode>());
                    }
                    if (!kMap.get(t).contains(node)) {
                        kMap.get(t).add(node);
                    }
//                    break;
                }
            }

            if (ts.isEmpty()) {
                //junta simples
                Component j = new Component();
                //j.name = "j";

                cir.addComponent(j);
                jointMap.put(new Point(node.x, node.y), j);
                for (CircuitNodeLink link : node.links) {
                    CircuitElm e = link.elm;
                    if (e.getPostCount() == 2) {
                        Component c1 = jointMap.get(e.getPost(0));
                        Component c2 = jointMap.get(e.getPost(1));
                        System.out.println("***" + e.dump());
                        if (c1 != null && c2 != null) {
                            Connection con = c1.createConnection(c2);
                            con.whut = e;
                            con.setSubComponent(e.dump());
                            cir.addConnection(con);
                        }
                    }
                }
            } else {
                for (CircuitElm t : ts) {
                    if (!compMap.containsKey(t)) {
                        //transistor e outros
                        Component j = new Component();
                        j.whut = t;
                        String n = t.dump();
                        j.setName(t.dump());
                        j.setData(t.dump());
                        if (n.startsWith("-")) {
                            String name = t.dump();
                            for (int k = 0; k < 7; k++) {
                                name = name.substring(name.indexOf(' ') + 1);
                            }
                            j.setName(name);
                            cir.addComponent(j, Circuit.OUTPUT);
                        } else if (n.startsWith("+")) {
                            String name = t.dump();
                            for (int k = 0; k < 10; k++) {
                                name = name.substring(name.indexOf(' ') + 1);
                            }
                            j.setName(name);
                            cir.addComponent(j, Circuit.INPUT);
                        } else {
                            cir.addComponent(j);
                        }
                        compMap.put(t, j);
                        if (t.getPostCount() == 1 && !jointMap.containsValue(j)) {
                            jointMap.put(new Point(node.x, node.y), j);
                        }
                    }
                }
            }
        }

        for (Map.Entry<CircuitElm, Component> entry : compMap.entrySet()) {
            CircuitElm t = entry.getKey();
            for (int i = 0; i < t.getPostCount(); i++) {
                Point p = t.getPost(i);
                boolean ok = false;
                for (CircuitNode node : kMap.get(t)) {
                    for (CircuitNodeLink link : node.links) {
                        CircuitElm e = link.elm;
                        if (e.getPostCount() == 2) {
                            Component c = null;
                            if (p.equals(e.getPost(0))) {
                                c = jointMap.get(e.getPost(1));
                            } else if (p.equals(e.getPost(1))) {
                                c = jointMap.get(e.getPost(0));
                            } else {
//                                System.out.println("Hfail: " + t + " " + i);
                                continue;
                            }
                            if (c != null) {
                                Connection con = entry.getValue().createConnection(c);
                                con.whut = e;
                                con.setTerminalA(i);
                                con.setSubComponent(e.dump());
                                cir.addComponent(entry.getValue());
                                cir.addComponent(c);
                                cir.addConnection(con);
                                System.out.println(entry.getValue().getName());
                                System.out.println(c.getName());
                                System.out.println("=");
                                entry.getValue().setName(t.getClass().getSimpleName());
                                ok = true;
                            } else {
//                                System.out.println("fail: " + e);
                            }
                        }
                    }
                }
                if (!ok) {
                    System.out.println("Hfail: " + t + " " + i + " " + kMap.get(t).size());
                }
            }
        }
        return cir;
    }

    public static Circuit parseString(String text, boolean animate) {
        Circuit cir = new Circuit();
        if (animate) {
            showGraph(cir, "www", true);
        }
        cir = createCircuit2(createDummyCS(text), cir);
        if (!animate) {
            showGraph(cir, "www", false);
        }
        return cir;
    }

    public static Circuit parseString(String text) {
        return createCircuit2(createDummyCS(text));
    }

    public static Graph<Component, Connection> createGraph(Circuit circuit) {
        SparseMultigraph<Component, Connection> graph = new SparseMultigraph<>();

        //adiciona vertices
        for (Component v : circuit.getComponents()) {
            graph.addVertex(v);
        }

        //adciona arestas
        for (Connection c : circuit.getConnections()) {
            graph.addEdge(c, c.getA(), c.getB());
        }

        return graph;
    }

    public static void showGraph(Circuit circuit, String title) {
        showGraph(circuit, title, false);
    }

    public static void showGraph(final Circuit circuit, final String title, boolean track) {
        showGraph(circuit, null, title, track);
    }

    public static void showGraph(final Circuit circuit, Layout<Component, Connection> layout, final String title, boolean track) {
        int w, h;
        w = h = 500;

        final SparseMultigraph<Component, Connection> graph = new SparseMultigraph<>();

        //adiciona vertices
        for (Component v : circuit.getComponents()) {
            graph.addVertex(v);
        }

        //adciona arestas
        for (Connection c : circuit.getConnections()) {
            graph.addEdge(c, c.getA(), c.getB());
        }

        if (layout == null) {
            if (track) {
                SpringLayout<Component, Connection> springLayout = new SpringLayout(graph);
                springLayout.setSize(new Dimension(w, h));
//            springLayout.setRepulsionRange(200);
//            springLayout.setForceMultiplier(.1);
                layout = springLayout;
            } else {
                KKLayout<Component, Connection> kkLayout = new KKLayout(graph);//new FRLayout(graph);
                kkLayout.setSize(new Dimension(w, h));
                layout = kkLayout;
            }
        }

        VisualizationViewer<Component, Connection> vv = new VisualizationViewer<>(layout);
        vv.setPreferredSize(new Dimension(w, h));

        vv.getRenderContext().setVertexLabelTransformer(new Transformer<Component, String>() {

            @Override
            public String transform(Component c) {
                return c.getUID();
            }
        });
//        vv.getRenderContext().setEdgeLabelTransformer(new Transformer<Connection, String>() {
//
//            @Override
//            public String transform(Connection c) {
//                return c.getUID();
//            }
//        });

        vv.setEdgeToolTipTransformer(new Transformer<Connection, String>() {

            @Override
            public String transform(Connection c) {
                return c.toString();
            }
        });

        vv.setVertexToolTipTransformer(new Transformer<Component, String>() {

            @Override
            public String transform(Component c) {
                return c.toString();
            }
        });

        vv.getRenderContext().setVertexDrawPaintTransformer(new Transformer<Component, Paint>() {
            @Override
            public Paint transform(Component c) {
                if (c.isConsumed()) {
                    return Color.LIGHT_GRAY;
                } else {
                    return Color.BLACK;
                }
            }
        });

        vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<Component, Paint>() {
            @Override
            public Paint transform(Component c) {
                if (c.whut == null || c.whut instanceof WireElm) {
                    return Color.GRAY;
                } else if (c.whut instanceof GroundElm) {
                    return Color.BLUE;
                } else if (c.whut instanceof RailElm) {
                    return Color.YELLOW;
                } else if (c.whut instanceof MyLogicInputElm) {
                    return Color.GREEN;
                } else if (c.whut instanceof MyLogicOutputElm) {
                    return Color.MAGENTA;
                } else {
                    return Color.RED;
                }
            }
        });

        vv.getRenderContext().setEdgeStrokeTransformer(new Transformer<Connection, Stroke>() {
            BasicStroke simple = new BasicStroke(1);
            BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5, new float[]{5}, 0);

            @Override
            public Stroke transform(Connection c) {
                if (c.isConsumed()) {
                    return dashed;
                } else {
                    return simple;
                }
            }
        });

        vv.getRenderContext().setEdgeDrawPaintTransformer(new Transformer<Connection, Paint>() {
            @Override
            public Paint transform(Connection c) {
                if (c.isConsumed()) {
                    return Color.LIGHT_GRAY;
                } else {
                    if (c.whut == null) {
                        return Color.BLACK;
                    } else if (c.whut instanceof WireElm) {
                        return Color.BLUE;
                    } else {
                        return Color.CYAN.darker();
                    }
                }
            }
        });

        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm);
        final JFrame frame = new JFrame(title + " V:" + graph.getVertexCount() + " E:" + graph.getEdgeCount());
        JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add(gm.getModeMenu());
        frame.setJMenuBar(jMenuBar);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        if (track) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            try {
                                Thread.sleep(50);
                                for (Component v : circuit.getComponents()) {
                                    if (!graph.getVertices().contains(v)) {
                                        graph.addVertex(v);
                                    }
                                }
                                for (Connection c : circuit.getConnections()) {
                                    if (!graph.getEdges().contains(c)) {
                                        graph.addEdge(c, c.getA(), c.getB());
                                    } else {
                                        Component a = graph.getEndpoints(c).getFirst();
                                        Component b = graph.getEndpoints(c).getFirst();
                                        if ((a != c.getA() || b != c.getB()) && (a != c.getB() || b != c.getA())) {
                                            graph.removeEdge(c);
                                            graph.addEdge(c, c.getA(), c.getB());
                                        }
                                    }
                                }
                                for (Component v : graph.getVertices()) {
                                    if (!circuit.contains(v)) {
                                        graph.removeVertex(v);
                                    }
                                }
                                for (Connection v : graph.getEdges()) {
                                    if (!circuit.contains(v)) {
                                        graph.removeEdge(v);
                                    }
                                }
                            } catch (Exception ex) {
//                                ex.printStackTrace();
                            }
                            frame.setTitle(title + " V:" + graph.getVertexCount() + " E:" + graph.getEdgeCount() + " - " + circuit.getStatus());
//                            vv.repaint();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }.start();
        }

    }

//    public static void main(String[] args) {
//        String s = dumpCircuit(parseString(Position3DFile.DUMMY), createDummyCS(""));
//        System.out.println(">>\n" + s);
//        createCS(s);
//    }
    public static String dumpCircuit(Circuit circuit) {
        return dumpCircuit(circuit, createDummyCS(""));
    }

    private static String dumpCircuit(Circuit circuit, CircuitSimulator cs) {
        int w, h;
        w = h = 900;

        StringBuilder sb = new StringBuilder();

        Graph<Component, Connection> graph = createGraph(circuit);
        KKLayout<Component, Connection> layout = new KKLayout(graph);//new FRLayout(graph);
        layout.setSize(new Dimension(w, h));

        layout.initialize();
        int i = 0;
        while (!layout.done()) {
            layout.step();
            i++;
            if (i > 20000) {
                break;
            }
        }
        System.out.println(i);

        if (true) {
            showGraph(circuit, layout, "result", false);
        }

        HashMap<Component, Point> pointMap = new HashMap<>();

        for (Component v : circuit.getComponents()) {
            Point2D p = layout.transform(v);
            int x = ((int) p.getX() / 1) * 1;
            int y = ((int) p.getY() / 1) * 1;
            pointMap.put(v, new Point(x, y));
        }

        sb.append("$ 1 5.0E-6 10 54 5.0\n");
        HashMap<Connection, Point> postA = new HashMap<>();
        HashMap<Connection, Point> postB = new HashMap<>();

//        System.out.println("nodes: " + circuit.getComponents().size() + " " + graph.getVertexCount());
//        System.out.println("edges: " + circuit.getConnections().size() + " " + graph.getEdgeCount());
        for (Component a : circuit.getComponents()) {
            Point p = pointMap.get(a);
            if (a.getData() != null) {
                CircuitElm comp = CircuitSimulator.createElm(dumpString(p.x, p.y, p.x + 32, p.y, "" + a.getData()), cs);

                sb.append(comp.dump());
                sb.append('\n');

                for (Connection c : a.getConnections()) {
                    int post = c.getTerminal(a);

                    if (c.getA() == a) {
                        postA.put(c, comp.getPost(post));
                    } else {
                        postB.put(c, comp.getPost(post));
                    }
                }
            } else {
                for (Connection c : a.getConnections()) {
                    if (c.getA() == a) {
                        postA.put(c, p);
                    } else {
                        postB.put(c, p);
                    }
                }
            }
        }

        for (Connection c : circuit.getConnections()) {
            Point p1 = postA.get(c);
            Point p2 = postB.get(c);

            if (p1 == null || p2 == null) {
                continue;
            }

            sb.append(dumpString(p1.x, p1.y, p2.x, p2.y, c.getSubComponent()));
            sb.append('\n');
        }

        System.out.println(sb.toString());
        return sb.toString();
    }

    private static String dumpString(int x1, int y1, int x2, int y2, String data) {
        StringBuilder sb = new StringBuilder();
        String type;
        String flags;
        if (data != null && data.length() > 8) {
//            System.out.println(data);
            type = data.substring(0, data.indexOf(' '));
            flags = data;
            for (int i = 0; i < 5; i++) {
                flags = flags.substring(flags.indexOf(' ') + 1);
            }
        } else {
//            System.out.println(data);
            type = "w";
            flags = "0";
        }
        sb.append(type).append(" ");
        sb.append(x1).append(" ");
        sb.append(y1).append(" ");
        sb.append(x2).append(" ");
        sb.append(y2).append(" ");
        sb.append(flags);
        return sb.toString();
    }

    @Override
    public Element getContent() {
        return textFile;
    }

    @Override
    public void update() {
        for (Object o : textFile.getExternalResources()) {
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
        return new Editor3D();
    }
}
