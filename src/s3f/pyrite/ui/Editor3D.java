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
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import javax.swing.JApplet;
import javax.swing.JFrame;
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
import s3f.pyrite.ui.components.MyLogicInputElm;
import s3f.pyrite.ui.components.MyLogicOutputElm;
import s3f.pyrite.ui.components.SubCircuitElm;
import s3f.pyrite.ui.drawing3d.Circuit3DEditPanel;

/**
 *
 * @author anderson
 */
public class Editor3D extends DockingWindowAdapter implements Editor {

//    private static final ImageIcon ICON = new ImageIcon(ModularCircuitEditor.class.getResource("/resources/icons/fugue/block.png"));
    private final Data data;
    private TextFile textFile;
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
        if (content instanceof TextFile) {
            textFile = (TextFile) content;
            Circuit circuit = parseString(textFile.getText());
            //---
            Circuit circuit1 = new Circuit();
            Component c1 = new Component("vcc");
            Component c2 = new Component("gnd");
            c1.createConnection(c2);
            circuit1.addComponent(c1);
            circuit1.addComponent(c2);
//            System.out.println(">>" + dumpCircuit(circuit, true));

            drawingPanel.setCircuit(circuit);

            showGraph(createGraph(circuit), true);

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
        }
        cs.init();
        window.setJMenuBar(cs.getGUI().createGUI(true));
        cs.posInit();
        cs.analyzeCircuit();
        cs.updateCircuit(null);
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
        }
        cs.init();
        window.setJMenuBar(cs.getGUI().createGUI(true));
        cs.posInit();
        cs.analyzeCircuit();
        cs.updateCircuit(null);
        JFrame f = new JFrame();
        f.setContentPane(window);
        f.setSize(new Dimension(400, 400));
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        return cs;
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
                String terminal = "";
                switch (i) {
                    case 0:
                        terminal = "b";
                        break;
                    case 1:
                        terminal = "c";
                        break;
                    case 2:
                        terminal = "e";
                        break;
                }
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
                                con.setTerminalA(terminal);
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

    private static Circuit parseString(String text) {
        return createCircuit(createCS(text));
    }

    private static Graph<String, String> createGraph(Circuit circuit) {
        SparseMultigraph<String, String> graph = new SparseMultigraph<>();

        //adiciona vertices
        for (Component v : circuit.getComponents()) {
            graph.addVertex(v.toString());
        }

        //adciona arestas
        for (Connection c : circuit.getConnections()) {
            graph.addEdge(c.toString(), c.getA().toString(), c.getB().toString());
        }
        return graph;
    }

    private static void showGraph(Graph<String, String> graph, boolean show) {

        KKLayout<String, String> layout = new KKLayout(graph);//new FRLayout(graph);
        layout.setSize(new Dimension(400, 400));

        if (show) {
            VisualizationViewer<String, String> vv = new VisualizationViewer<>(layout);
            vv.setPreferredSize(new Dimension(400, 400));

            vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
            vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller() {
//                @Override
//                public String transform(Object v) {
//                    String s = v.toString();
//                    return s.substring(0, s.indexOf('['));
//                }
            });

            vv.setEdgeToolTipTransformer(new ToStringLabeller() {
                @Override
                public String transform(Object v) {
                    String s = v.toString();
                    return s;
                }
            });

            vv.setVertexToolTipTransformer(new ToStringLabeller() {
                @Override
                public String transform(Object v) {
                    String s = v.toString();
                    return s;
                }
            });
            DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
            gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
            vv.setGraphMouse(gm);
            JFrame frame = new JFrame("Interactive Graph 2D View - DUMP");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(vv);
            frame.pack();
            frame.setVisible(true);
        }
    }

    public static void main(String[] args) {
        String s = dumpCircuit2(parseString(Position3DFile.DUMMY), createDummyCS(""));
        System.out.println(">>\n" + s);
        createCS(s);
    }

    private static String dumpCircuit2(Circuit circuit, CircuitSimulator cs) {
        StringBuilder sb = new StringBuilder();

        Graph<String, String> graph = createGraph(circuit);
        KKLayout<String, String> layout = new KKLayout(graph);//new FRLayout(graph);
        layout.setSize(new Dimension(400, 400));

        HashMap<Component, Point> pointMap = new HashMap<>();

        for (Component v : circuit.getComponents()) {
            Point2D p = layout.transform(v.toString());
            int x = ((int) p.getX() / 15) * 16;
            int y = ((int) p.getY() / 15) * 16;
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
                    int post = 0;
                    String t = c.getTerminal(a);
                    switch (t) {
                        case "":
                            break;
                        case "b":
                            post = 0;
                            break;
                        case "c":
                            post = 1;
                            break;
                        case "e":
                            post = 2;
                            break;
                        default:
                            post = Integer.parseInt(t);
                    }

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

            sb.append(dumpString(p1.x, p1.y, p2.x, p2.y, "" + c.getSubComponent()));
            sb.append('\n');
        }

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

    private static String dumpCircuit(Circuit circuit) {

        Graph<String, String> graph = createGraph(circuit);

        KKLayout<String, String> layout = new KKLayout(graph);//new FRLayout(graph);
        layout.setSize(new Dimension(400, 400));

        HashMap<String, Point2D> pointMap = new HashMap<>();
        HashMap<String, Component> compMap = new HashMap<>();

        for (Component v : circuit.getComponents()) {
            String id = v.toString();
            Point2D p = layout.transform(id);
            pointMap.put(id, p);
            compMap.put(id, v);
        }

        ArrayList<String> edges = new ArrayList<>();
        for (Component v : circuit.getComponents()) {
            for (Connection con : v.getConnections()) {
                Component e = con.getOtherComponent(v);
                for (String s : graph.findEdgeSet(v.toString(), e.toString())) {
                    if (!edges.contains(s)) {
                        edges.add(s);
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("$ 1 5.0E-6 10 54 5.0\n");
        TreeSet<Pair<String>> ok = new TreeSet<>(new Comparator<Pair<String>>() {
            @Override
            public int compare(Pair<String> o1, Pair<String> o2) {
                String f1 = o1.getFirst();
                String f2 = o2.getFirst();
                String s1 = o1.getSecond();
                String s2 = o2.getSecond();

                if (f1.equals(f2) && s1.equals(s2)) {
                    return 0;
                } else if (f1.equals(s2) && s1.equals(f2)) {
                    return 0;
                }

                return 1;
            }
        });
        ArrayList<String> okt = new ArrayList<>();
        for (String edge : edges) {
            Pair<String> endpoints = graph.getEndpoints(edge);
            String source = endpoints.getFirst();
            String dest = endpoints.getSecond();
            Component v = compMap.get(source);
            Component e = compMap.get(dest);

            Connection con = e.getConnection(v);

            if (con == null) {
                System.err.println("ERROR!");
                System.exit(0);
                continue;
            }

            String c1 = con.getA().toString();
            String c1t = con.getTerminalA();
            String comp = con.getSubComponent();
            String c2t = con.getTerminalB();
            String c2 = con.getB().toString();

            /*
            
             r w 
             w r 
             w w 
            
             */
            if (!ok.contains(endpoints)) {
                if (edge.startsWith("[")) {
                    ok.add(endpoints);
                    continue;
                }
            } else {

            }

            String type = "";
            String flags = "";

            if (comp.length() > 8) {
                type = comp.substring(0, comp.indexOf(' '));
                flags = comp;
                for (int i = 0; i < 5; i++) {
                    flags = flags.substring(flags.indexOf(' ') + 1);
                }
                System.out.println(type + " - " + flags);
            } else {

                switch (comp) {
                    case "":
                        type = "w";
                        flags = "0";
                        break;
                    case "res1k":
                        type = "r";
                        flags = "0 1000.0";
                        break;
                    case "res2k":
                        type = "r";
                        flags = "0 2000.0";
                        break;
                    case "res10k":
                        type = "r";
                        flags = "0 10000.0";
                        break;
                    case "btn":
                        type = "s";
                        flags = "0 1 false";
                        break;
                    case "(->^^|-)":
                        type = "162";
                        //x tensão r g b
                        flags = "1 2.1024259 1.0 0.0 0.0";
                        break;
                    case "(->|-)":
                        type = "d";
                        flags = "1 0.805904783";
                        break;
                    case "bat":
                        type = "v";
                        flags = "0 0 40.0 9.0 0.0 0.0 0.5";
                        break;
//                        case "?":
//                            type = "";
//                            flags = "";
//                            break;
                    default:
                        System.out.println("not def: " + comp);
                }
            }

            Point2D p1 = pointMap.get(c1);
            Point2D p2 = pointMap.get(c2);

            int x1 = ((int) p1.getX() / 10) * 16;
            int x2 = ((int) p2.getX() / 10) * 16;
            int y1 = ((int) p1.getY() / 10) * 16;
            int y2 = ((int) p2.getY() / 10) * 16;

            if (v.getData() != null && v.getData().toString().contains("ransistor") && !okt.contains(c1)) {
                okt.add(c1);
                sb.append("t " + x1 + " " + y1 + " " + (x1 + 32) + " " + y1 + "  0 1 0.0 0.0 100.0\n");
            }

            if (e.getData() != null && "transistor".equals(e.getData().toString()) && !okt.contains(c2)) {
                okt.add(c2);
                sb.append("t " + x2 + " " + y2 + " " + (x2 + 32) + " " + y2 + "  0 1 0.0 0.0 100.0\n");
            }

            switch (c1t) {
                case "b":
                    x1 = x1;
                    y1 = y1;
                    break;
                case "c":
                    x1 = x1 + 32;
                    y1 = y1 - 16;
                    break;
                case "e":
                    x1 = x1 + 32;
                    y1 = y1 + 16;
                    break;
            }

            switch (c2t) {
                case "b":
                    x2 = x2;
                    y2 = y2;
                    break;
                case "c":
                    x2 = x2 + 32;
                    y2 = y2 - 16;
                    break;
                case "e":
                    x2 = x2 + 32;
                    y2 = y2 + 16;
                    break;
            }

            if (!type.isEmpty()) {
                sb.append(type + " " + x1 + " " + y1 + " " + x2 + " " + y2 + " " + flags + "\n");
            }

            if (e.getData() != null && e.getData().toString().length() > 8) {
                comp = e.getData().toString();
                type = comp.substring(0, comp.indexOf(' '));
                flags = comp;
                for (int i = 0; i < 5; i++) {
                    flags = flags.substring(flags.indexOf(' ') + 1);
                }
                sb.append(type + " " + x2 + " " + y2 + " " + (x1 - 64) + " " + y1 + " " + flags + "\n");
            }

//            if (this.inputs.contains(e)) {
//                sb.append("+" + " " + x2 + " " + y2 + " " + (x2 - 64) + " " + y2 + " 0 0 false 3.6 0.0 " + e.name + "\n");
//            } else if (this.outputs.contains(e)) {
//                sb.append("-" + " " + x1 + " " + y1 + " " + (x1 + 64) + " " + y1 + " 0 2.5 s " + e.name + "\n");
//            } else if ("vcc".equals(e.name)) {
//            } else if ("gnd".equals(e.name)) {
//            }
        }

        return sb.toString();
    }

    @Override
    public Element getContent() {
        return textFile;
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
