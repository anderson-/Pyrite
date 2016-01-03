/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui.circuitsim;

import com.falstad.circuit.CircuitElm;
import com.falstad.circuit.CircuitNode;
import com.falstad.circuit.CircuitNodeLink;
import com.falstad.circuit.CircuitSimulator;
import com.falstad.circuit.elements.GroundElm;
import com.falstad.circuit.elements.RailElm;
import com.falstad.circuit.elements.WireElm;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JOptionPane;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.Connection;
import s3f.pyrite.ui.VolimetricCircuitEditor;
import s3f.pyrite.ui.circuitsim.SimBuilder;
import s3f.pyrite.ui.components.SubCircuitElm;
import s3f.util.Parser;

/**
 *
 * @author andy
 */
public class CircuitDOTParser implements Parser<String, String> {

    @Override
    public String parse(String inputCircuitFile) {
        Circuit cir = parseFromFile(inputCircuitFile);

        StringBuilder sb = new StringBuilder();
        for (Component c : cir.getComponents()) {
            for (Connection con : c.getConnections()) {
                sb.append(c.getID()).append(";").append(con.getOtherComponent(c).getID()).append(";").append(con.getSubComponent()).append("\n");
            }
        }

        for (Component c : cir.getComponents()) {
            sb.append(".").append(c.getID()).append(";").append(c.getName()).append("\n");
        }

        return sb.toString();
    }

    public static Circuit parseFromFile(String file) {
        return parse(SimBuilder.newHiddenSim(file, false));
    }

    public static Circuit parse(CircuitSimulator cs) {
        Circuit cir = new Circuit();

        if (cs.elmListSize() == 0) {
            return cir;
        } else {
            cs.analyzeCircuit();//nodeListSize
        }

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

        cir.setStatus("parsing and inserting sub-circuits");
        for (Map.Entry<CircuitElm, Component> entry : nodes.entrySet()) {
            CircuitElm e = entry.getKey();
            if (e instanceof SubCircuitElm) {
                SubCircuitElm subCircuitElm = (SubCircuitElm) e;
                Component c = entry.getValue();
                long sleep = SLEEP;
                SLEEP = 0;
                Circuit sub = parse(SimBuilder.newHiddenSim(subCircuitElm.getCircuit(), false));
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

        cir.setStatus("consuming redundant nodes");
        boolean mod = true;
        while (mod) {
            mod = false;
            for (Component a : cir.getComponents()) {
                if (!a.isConsumed() && a.isCoupler()) {
                    ArrayList<Connection> newCons = new ArrayList<>();
                    for (Iterator<Connection> aConIt = a.getConnections().iterator(); aConIt.hasNext();) {
                        Connection c = aConIt.next();
                        if (!c.isConsumed() && c.isShort()) {
                            Component b = c.getOtherComponent(a);
                            if (!b.isConsumed() && b.isCoupler() && b.whut == null) {
                                b.removeConnection(c);
                                int ter = c.getTerminal(a);
                                c.softConsume();
                                for (Connection con : b.getConnections()) {
                                    con.replace(b, a);
                                    con.setTerminal(a, ter);
                                    newCons.add(con);
                                }
                                b.setConsumed(true);
                                aConIt.remove();
                                sleep();
                                mod = true;
                                break;
                            }
                        }
                    }
                    for (Connection con : newCons) {
                        a.addConnection(con);
                    }
                }
            }
        }

        cir.clean();
        return cir;
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
        String circuitString = hard;
        boolean anim = false;
        if (anim) {
            new Thread() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, "Close this dialog to speed thing up.");
                    SLEEP = 0;
                }
            }.start();
            SLEEP = 500;
        }
        CircuitSimulator sim = SimBuilder.newWindowSim(circuitString);
        Circuit cir = parse(sim);
        SimBuilder.newWindowSim(cir);
    }
}
