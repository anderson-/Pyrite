/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core.circuitsim;

import com.falstad.circuit.CircuitElm;
import com.falstad.circuit.CircuitSimulator;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.SparseMultigraph;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;
import javax.swing.JApplet;
import javax.swing.JFrame;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.Connection;
import s3f.pyrite.ui.components.DigitalLogicTester;
import s3f.pyrite.ui.components.MyLogicInputElm;
import s3f.pyrite.ui.components.MyLogicOutputElm;
import s3f.pyrite.ui.components.SubCircuitElm;

/**
 *
 * @author andy
 */
public class SimBuilder {

    //simulador escondido
    public static CircuitSimulator newHiddenSim(String text, boolean run, boolean dummy) {
        JApplet window = new JApplet();
        final CircuitSimulator cs = new CircuitSimulator(!run);
        cs.setStopped(!run);
        cs.setContainer(window.getContentPane());
        cs.startCircuitText = text;
        {//TODO
            cs.register(MyLogicInputElm.class);
            cs.register(MyLogicOutputElm.class);
            cs.register(SubCircuitElm.class);
            cs.register(DigitalLogicTester.class);
        }
        if (dummy) {
            cs.setDisabled();
        }
        cs.init();
        window.setJMenuBar(cs.getGUI().createGUI(true));
        cs.posInit();

        if (!run && !dummy) {
            cs.analyzeCircuit();
            for (int i = 0; i < 30; i++) {
                cs.updateCircuit(null);
            }
        }

        return cs;
    }

    public static CircuitSimulator newWindowSim(String text) {
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
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setVisible(true);
        return cs;
    }

    public static CircuitSimulator newWindowSim(Circuit circuit) {
        CircuitSimulator cs = newHiddenSim("", false, false);

        HashMap<Component, Point> pointMap = new HashMap<>();

        {// set components position
            int w, h;
            w = h = 900;

            SparseMultigraph<Component, Connection> graph = new SparseMultigraph<>();

            //adiciona vertices
            for (Component v : circuit.getComponents()) {
                graph.addVertex(v);
            }

            //adciona arestas
            for (Connection c : circuit.getConnections()) {
                graph.addEdge(c, c.getA(), c.getB());
            }

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

            for (Component v : circuit.getComponents()) {
                Point2D p = layout.transform(v);
                int x = ((int) p.getX() / 1) * 1;
                int y = ((int) p.getY() / 1) * 1;
                pointMap.put(v, new Point(x, y));
            }
        }

        StringBuilder sb = new StringBuilder();
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

        return newWindowSim(sb.toString());
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

}
