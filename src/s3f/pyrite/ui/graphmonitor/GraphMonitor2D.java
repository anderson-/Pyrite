/*
    Copyright (c) 2013, 2014 pachacamac
                  2015, 2016 Anderson Antunes

    This file is part of jg3d.

    jg3d is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jg3d is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package s3f.pyrite.ui.graphmonitor;

import s3f.pyrite.util.Vector;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import javax.swing.event.MouseInputListener;

import java.awt.BasicStroke;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.Connection;
import s3f.pyrite.core.fdgfa.fdgs.ParticleProperty;
import s3f.pyrite.core.fdgfa.fdgs.TpsCounter;

public class GraphMonitor2D extends JPanel implements Runnable, MouseInputListener, KeyListener, MouseWheelListener {

    ParticleProperty hit = null;
    boolean shiftIsDown = false;
    boolean ctrlIsDown = false;
    private static final long serialVersionUID = 9101840683353633974L;

    private Thread thread;

    public Circuit graph;

    private TpsCounter tick;
    int gGridSize = 10;

    private Vector totalforce = new Vector();

    private boolean showNodes = true;
    private boolean showNodeNames = false;
    private boolean showNodePosition = false;
    private boolean showEdges = true;
    private boolean showEdgeNames = false;
    private boolean showHud = true;
    private boolean showEnergyStatistics = false;
    private boolean showHelp = false;
    private boolean showEdgeWeights = false;
    private boolean showNodeWeights = false;
    private boolean showEdgeLength = false;
    private boolean flatMode = false;
    private double pseudoZoom = 6;
    int px, py, pz;
    int gx, gy;

    ArrayList<Integer> test = new ArrayList<>();

    private int threads = 1;
    private ParticleProperty WWW;
    private GuideGenerator lastGuideGen = null;

    public void setCircuit(Circuit circuit) {
        this.graph = circuit;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // System.out.println(e);
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SHIFT:
                if (!shiftIsDown) {
                    shiftIsDown = true;
                }
                break;
            case KeyEvent.VK_CONTROL:
                if (!ctrlIsDown) {
                    ctrlIsDown = true;
                }
                break;
            case KeyEvent.VK_UP:
                if (ctrlIsDown) {
                    py -= 1;
                    generateGuide(lastGuideGen, 5, gGridSize);
                } else {
                    gy -= 10;
                }
//                for (Component c : graph.getComponents()) {                        ParticleProperty n = c.getProperty();
////                    n.getPos().setY(n.getPos().getY() + -10);
////                    n.setSelfForceY(-.2);
//                }
                break;
            case KeyEvent.VK_DOWN:
                if (ctrlIsDown) {
                    py += 1;
                    generateGuide(lastGuideGen, 5, gGridSize);
                } else {
                    gy += 10;
                }
//                for (Component c : graph.getComponents()) {                        ParticleProperty n = c.getProperty();
////                    n.getPos().setY(n.getPos().getY() + 10);
////                    n.setSelfForceY(.2);
//                }
                break;
            case KeyEvent.VK_RIGHT:
                if (ctrlIsDown) {
                    px += 1;
                    generateGuide(lastGuideGen, 5, gGridSize);
                } else {
                    gx += 10;
                }
//                for (Component c : graph.getComponents()) {                        ParticleProperty n = c.getProperty();
//
////                    n.getPos().setX(n.getPos().getX() + 10);
////                    n.setSelfForceX(.2);
//                }
                break;
            case KeyEvent.VK_LEFT:
                if (ctrlIsDown) {
                    px -= 1;
                    generateGuide(lastGuideGen, 5, gGridSize);
                } else {
                    gx -= 10;
                }
//                for (Component c : graph.getComponents()) {                        ParticleProperty n = c.getProperty();
////                    n.getPos().setX(n.getPos().getX() + -10);
////                    n.setSelfForceX(-.2);
//                }
                break;
            case KeyEvent.VK_PAGE_UP:
                if (ctrlIsDown) {
                    pz += 1;
                    generateGuide(lastGuideGen, 5, gGridSize);
                } else {
                    for (Component c : graph.getComponents()) {
                        ParticleProperty n = c.getProperty();
                        n.getPos().setZ(n.getPos().getZ() + 10);
//                    n.setSelfForceZ(.2);
                    }
                }
                break;
            case KeyEvent.VK_PAGE_DOWN:
                if (ctrlIsDown) {
                    pz -= 1;
                    generateGuide(lastGuideGen, 5, gGridSize);
                } else {
                    for (Component c : graph.getComponents()) {
                        ParticleProperty n = c.getProperty();
                        n.getPos().setZ(n.getPos().getZ() + -10);
//                    n.setSelfForceZ(-.2);
                    }
                }
                break;
        }

        switch (e.getKeyChar()) {
            case '?':
                showHelp = !showHelp;
                break;
            case '1':
                threads = 1;
                break;
            case '2':
                threads = 2;
                break;
            case '3':
                threads = 3;
                break;
            case '4':
                threads = 4;
                break;
            case '6':
//                WWW = hit;
//                if (WWW == null) {
//                    WWW = graph.getNode("7");
//                    WWW.setColor(Color.red);
//                }
//                for (Edge ed : graph.getEdges()) {
//                    ed.setWeight(10);
//                }
//
//                for (Component c : graph.getComponents()) {
//                    ParticleProperty n = c.getProperty();
//                    if (n.connectedTo(WWW)) {
//                        n.setColor(Color.yellow);
//                        n.setWeight(80);
//                        Edge e1 = n.getEdgeTo(WWW);
//                        Edge e2 = WWW.getEdgeTo(n);
//                        e1.setWeight(100);
//                        e2.setWeight(100);
//                    } else {
//                        n.setColor(Color.yellow);
//                        n.setWeight(10);
//                    }
//                }
//
//                int[] gp = getClosestLatticeNode(WWW, gGridSize, 1);
//                px = gp[0];
//                py = gp[1];
//                pz = gp[2];
//                WWW.setPos(new Vector(px, py, pz).multiply(gGridSize));
//                WWW.setFixed(true);
//                generateGuide(new GuideGenerator() {
//                    @Override
//                    public boolean contains(int x, int y, int z) {
//                        double dist = Math.sqrt(x * x + y * y + z * z);
//                        return dist != 0 && dist < 2;
//                    }
//                }, 5, gGridSize);
                break;
            case '7':
                generateGuide(new GuideGenerator() {
                    @Override
                    public boolean contains(int x, int y, int z) {
                        return x == 0;
                    }
                }, 5, gGridSize);
                break;
            case '8':
                generateGuide(new GuideGenerator() {
                    @Override
                    public boolean contains(int x, int y, int z) {
                        return y == 0;
                    }
                }, 5, gGridSize);
                break;
            case '9':
                generateGuide(new GuideGenerator() {
                    @Override
                    public boolean contains(int x, int y, int z) {
                        return z == 0;
                    }
                }, 5, gGridSize);
                break;
            case '0':
//                createAxis(graph, 30);
                break;
            case '-': // pseudo-unzoom
                pseudoZoom = (pseudoZoom - 1 >= 1) ? pseudoZoom - 1 : pseudoZoom;
                for (Component c : graph.getComponents()) {
                    ParticleProperty n = c.getProperty();
                    n.setDiameter(8 * pseudoZoom / 2);
                }
                break;
            case '=':
            case '+': // pseudo-zoom
                pseudoZoom = (pseudoZoom + 1 <= 30) ? pseudoZoom + 1 : pseudoZoom;
                for (Component c : graph.getComponents()) {
                    ParticleProperty n = c.getProperty();
                    n.setDiameter(8 * pseudoZoom / 2);
                }
                break;
            case 'i': // invert all fixings
                for (Component c : graph.getComponents()) {
                    ParticleProperty n = c.getProperty();
                    n.setFixed(!n.isFixed());
                }
                break;
            case 'f': // fix all nodes
                for (Component c : graph.getComponents()) {
                    ParticleProperty n = c.getProperty();
                    n.setFixed(true);
                }
                break;
            case 'u': // unfix all nodes
                for (Component c : graph.getComponents()) {
                    ParticleProperty n = c.getProperty();
                    n.setFixed(false);
                }
                break;
            case 'r': // reduce all edge weights
                for (Component c : graph.getComponents()) {
                    ParticleProperty n = c.getProperty();
                    n.setWeight(n.getWeight() - 0.5);
                }
                break;
            case 't': // enhance all edge weights
                for (Component c : graph.getComponents()) {
                    ParticleProperty n = c.getProperty();
                    n.setWeight(n.getWeight() + 0.5);
                }
                break;
            case 'h':
                showHud = !showHud;
                break;
            case 'E':
                showEnergyStatistics = !showEnergyStatistics;
                break;
            case 'n':
                showNodes = !showNodes;
                break;
            case 'l':
                showNodeNames = !showNodeNames;
                break;
            case 'P':
                showNodePosition = !showNodePosition;
                break;
            case 'm':
                showNodeWeights = !showNodeWeights;
                break;
            case 'e':
                showEdges = !showEdges;
                break;
            case 'b':
                showEdgeNames = !showEdgeNames;
                break;
            case 'd':
                showEdgeWeights = !showEdgeWeights;
                break;
            case 'L':
                showEdgeLength = !showEdgeLength;
                break;
            case 'F':
                flatMode = !flatMode;
                break;
            case 'q':
                for (Component c : graph.getComponents()) {
                    ParticleProperty n = c.getProperty();
//                    if (!n.isFixed()) {
                    n.getPos().rotateX(0.05);
//                    }
                }
                test.add(1);
                break;
            case 'w':
                for (Component c : graph.getComponents()) {
                    ParticleProperty n = c.getProperty();
//                    if (!n.isFixed()) {
                    n.getPos().rotateX(-0.05);
//                    }
                }
                test.add(2);
                break;
            case 'a':
                for (Component c : graph.getComponents()) {
                    ParticleProperty n = c.getProperty();
//                    if (!n.isFixed()) {
                    n.getPos().rotateY(0.05);
//                    }
                }
                test.add(3);
                break;
            case 's':
                for (Component c : graph.getComponents()) {
                    ParticleProperty n = c.getProperty();
//                    if (!n.isFixed()) {
                    n.getPos().rotateY(-0.05);
//                    }
                }
                test.add(4);
                break;
            case 'y':
                for (Component c : graph.getComponents()) {
                    ParticleProperty n = c.getProperty();
//                    if (!n.isFixed()) {
                    n.getPos().rotateZ(0.05);
//                    }
                }
                test.add(5);
                break;
            case 'x':
                for (Component c : graph.getComponents()) {
                    ParticleProperty n = c.getProperty();
//                    if (!n.isFixed()) {
                    n.getPos().rotateZ(-0.05);
//                    }
                }
                test.add(6);
                break;
            case '`':
//                //Node.G += .5;
////                for (Node n : graph.getNodes()) {
////                    System.out.println("." + n.getName() + ";" + n.getPos().getX() + ";" + n.getPos().getY() + ";" + n.getPos().getZ());
////                }
//                HashSet<Edge> hs = new HashSet<>();
//                for (Edge ed : graph.getEdges()) {
//                    hs.add(ed);
//                }
//                double sizeInMM = 72;
//                double max = Double.MIN_VALUE;
//                double min = Double.MAX_VALUE;
//
//                for (Edge ed : hs) {
//                    double l = ed.getLength();
//                    if (l > max) {
//                        max = l;
//                    }
//                    if (l < min) {
//                        min = l;
//                    }
//                    System.out.printf("%s %.1f\n", ed, l);
//                }
//                System.out.printf("min %.1f, max %.1f\n", min, max);
//
//                HashMap<Integer, Integer> hm = new HashMap<>();
//                for (Edge ed : hs) {
//                    double l = ed.getLength();
//                    l = (sizeInMM * l) / max;
//                    int rl = (int) (l * 10);
//
//                    rl += (rl % 10 >= 5) ? 10 : 0;
//                    rl /= 10;
//
//                    if (hm.containsKey(rl)) {
//                        hm.put(rl, hm.get(rl) + 1);
//                    } else {
//                        hm.put(rl, 1);
//                    }
//
//                    System.out.printf("%.1f = %d\n", l, rl);
//                }
//
//                for (HashMap.Entry<Integer, Integer> es : hm.entrySet()) {
//                    System.out.printf("%d : %d\n", es.getKey(), es.getValue());
//                }
                break;
            case '#':
                for (int i = test.size() - 1; i >= 0; i--) {
                    switch (test.get(i)) {
                        case 1:
                            for (Component c : graph.getComponents()) {
                                ParticleProperty n = c.getProperty();
                                n.getPos().rotateX(-0.05);
                            }
                            break;
                        case 2:
                            for (Component c : graph.getComponents()) {
                                ParticleProperty n = c.getProperty();
                                n.getPos().rotateX(0.05);
                            }
                            break;
                        case 3:
                            for (Component c : graph.getComponents()) {
                                ParticleProperty n = c.getProperty();
                                n.getPos().rotateY(-0.05);
                            }
                            break;
                        case 4:
                            for (Component c : graph.getComponents()) {
                                ParticleProperty n = c.getProperty();
                                n.getPos().rotateY(0.05);
                            }
                            break;
                        case 5:
                            for (Component c : graph.getComponents()) {
                                ParticleProperty n = c.getProperty();
                                n.getPos().rotateZ(-0.05);
                            }
                            break;
                        case 6:
                            for (Component c : graph.getComponents()) {
                                ParticleProperty n = c.getProperty();
                                n.getPos().rotateZ(0.05);
                            }
                            break;
                    }
                }
                test.clear();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        for (Component c : graph.getComponents()) {
            ParticleProperty n = c.getProperty();
            n.setSelfForceX(0);
            n.setSelfForceY(0);
            n.setSelfForceZ(0);
        }
        if (e.getKeyCode() == KeyEvent.VK_SHIFT && shiftIsDown) {
            shiftIsDown = false;
            System.out.println("shift released");
        }
        if (e.getKeyCode() == KeyEvent.VK_CONTROL && ctrlIsDown) {
            ctrlIsDown = false;
            System.out.println("ctrl released");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
//        if (!shiftIsDown) {
//            hit.setPos(new Vector((e.getX() - 400) / pseudoZoom, (e.getY() - 300) / pseudoZoom, 0));
//        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (ctrlIsDown) {
////            if (hit != null) {
////                Node tmp = new Node(new Vector((e.getX() - 400) / pseudoZoom, (e.getY() - 300) / pseudoZoom, 0));
////                if (hit.getPos().distance(tmp.getPos()) > 20) {//gridSize??
//////                    graph.addNode(tmp);
//////                    graph.connect(hit, tmp, 10);
////                    hit = tmp;
////                }
////            } else {
//            hit = graph.hit(e.getPoint());
////            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        if (e.getClickCount() == 2) {
//            if (shiftIsDown) {
//                graph.remNode(graph.hit(e.getPoint()));
//            } else {
//                graph.addNode(new ParticleProperty(new Vector((e.getX() - 400) / pseudoZoom, (e.getY() - 300) / pseudoZoom, 0)));
//            }
//        }
//        if (e.getClickCount() == 3) {
//            if (shiftIsDown) {
//                graph.remNodeRec(graph.hit(e.getPoint()));
//            }
//        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        hit = graph.hit(e.getPoint());
//        if (hit != null && !shiftIsDown) {
//            hit.setFixed(true);
//        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        if (e.getButton() == MouseEvent.BUTTON1 && !shiftIsDown) {
//            {
//                ParticleProperty tmp = hit;
//                hit = graph.hit(e.getPoint(), 30, tmp);
//                if (tmp != null && hit != null && tmp != hit && hit.getType() == 2) {
//                    tmp.getPos().setPos(hit.getPos().getX(), hit.getPos().getY(), hit.getPos().getZ());
//                    tmp.setFixed(true);
//                }
//                if (tmp != null && hit != null && hit.getType() != 2) {
//                    tmp.setFixed(false);
//                }
//            }
//            if (hit != null && hit.getType() != 2) {
//                hit.setFixed(false);
//            }
//            hit = null;
//        } else if (e.getButton() == MouseEvent.BUTTON1 && shiftIsDown) {
//            if (hit != null) {
//                ParticleProperty tmp = hit;
//                hit = graph.hit(e.getPoint());
//                if (tmp != hit) {
//                    if (!tmp.connectedTo(hit)) {
//                        graph.connect(tmp, hit, 10);
//                    } else {
//                        graph.disconnect(tmp, hit);
//                    }
//                }
//                hit = tmp;
//            } else {
//                hit = null;
//            }
//        }
    }

//    public List<ParticleProperty> findNodesInRange(Vector pos, double radius) {
//        List<ParticleProperty> inrange = new ArrayList<>();
//        for (ParticleProperty n : circuit.getNodes()) {
//            if (pos.distance(n.getPos()) <= radius) {
//                inrange.add(n);
//            }
//        }
//        return inrange;
//    }
//
//    public ParticleProperty hit(Point p) {
//        Vector v = new Vector(p.getX(), p.getY(), 0);
//        ParticleProperty closestNode = null;
//        double closestDistance = Double.MAX_VALUE;
//        double distance;
//        for (ParticleProperty node : circuit.getNodes()) {
//            if (node.getType() != 0) {
//                continue;
//            }
//            distance = node.getProjection().distance(v);
//            if (distance < closestDistance) {
//                closestDistance = distance;
//                closestNode = node;
//            }
//        }
//        return closestNode;
//    }
//
//    public ParticleProperty hit(Point p, double maxDistance, ParticleProperty... ignore) {
//        Vector v = new Vector(p.getX(), p.getY(), 0);
//        ParticleProperty closestNode = null;
//        double closestDistance = Double.MAX_VALUE;
//        double distance;
//        hit:
//        for (ParticleProperty a : circuit.getNodes()) {
//            for (ParticleProperty i : ignore) {
//                if (a == i) {
//                    continue hit;
//                }
//            }
//            distance = a.getProjection().distance(v);
//            if (distance < closestDistance && distance < maxDistance) {
//                closestDistance = distance;
//                closestNode = a;
//            }
//        }
//        return closestNode;
//    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent we) {
        double wr = we.getWheelRotation() * .5;
        if (pseudoZoom + wr > 0.2) {
            //centralizar o no mais proximo do mouse
            gx += gx * (wr * 1);
            gy += gy * (wr * 1);

            pseudoZoom += wr;
            throw new RuntimeException("implementa isso aqui!");
        }
    }

//    public static void connectNodesToNearestNeighbours(Graph g, double radius) {
//        for (ParticleProperty n : g.getNodes()) {
//            connectNodeToNearestNeighbours(g, n, radius);
//        }
//    }
//
//    public static void connectNodeToNearestNeighbours(Graph g, ParticleProperty n, double radius) {
//        List<ParticleProperty> inrange = g.findNodesInRange(n.getPos(), radius);
//        for (ParticleProperty m : inrange) {
//            if (n != m) {
//                g.connect(n, m, 5);
//            }
//        }
//    }
//
//    public static void connectNodeGrids(Graph g, List<List<ParticleProperty>> a, List<List<ParticleProperty>> b, double ew) {
//        for (int i = 0; i < a.size(); i++) {
//            for (int j = 0; j < a.get(0).size(); j++) {
//                b.get(i).get(j).getPos().setPos(a.get(i).get(j).getPos().getX(),
//                        a.get(i).get(j).getPos().getY() + 10, a.get(i).get(j).getPos().getZ());
//                g.connect(a.get(i).get(j), b.get(i).get(j), ew);
//            }
//        }
//    }
    public void transform() {
        for (Component c : graph.getComponents()) {
            ParticleProperty node = c.getProperty();
            node.getPos().rotateX(0.008);
            node.getPos().rotateY(0.01);
            node.getPos().rotateZ(0.02);
        }
    }

    public void project() {
        for (Component c : graph.getComponents()) {
            ParticleProperty node = c.getProperty();
            node.project(800, 600, pseudoZoom);
        }
    }

//    public static void createAxis(Graph graph, int size) {
//        for (ParticleProperty n : new ArrayList<ParticleProperty>(graph.getNodes())) {
//            if (n.getType() == 3) {
//                graph.remNode(n);
//            }
//        }
//        ParticleProperty O = new ParticleProperty(new Vector(0, 0, 0));
//        O.setType(3);
//        O.setDiameter(5);
//        O.setFixed(true);
//        O.setWeight(0);
//        O.setColor(Color.gray);
//        graph.addNode(O);
//
//        Edge c[];
//
//        java.awt.Stroke s = new BasicStroke(4);
//
//        ParticleProperty X = new ParticleProperty(new Vector(size, 0, 0));
//        X.setType(3);
//        X.setDiameter(5);
//        X.setFixed(true);
//        X.setWeight(0);
//        X.setColor(Color.red);
//        graph.addNode(X);
//        c = graph.connect(O, X, 0);
//        c[0].setColor(Color.red);
//        c[1].setColor(Color.red);
//        c[0].setStroke(s);
//        c[1].setStroke(s);
//
//        ParticleProperty Y = new ParticleProperty(new Vector(0, size, 0));
//        Y.setType(3);
//        Y.setDiameter(5);
//        Y.setFixed(true);
//        Y.setWeight(0);
//        Y.setColor(Color.green);
//        graph.addNode(Y);
//        c = graph.connect(O, Y, 0);
//        c[0].setColor(Color.green);
//        c[1].setColor(Color.green);
//        c[0].setStroke(s);
//        c[1].setStroke(s);
//
//        ParticleProperty Z = new ParticleProperty(new Vector(0, 0, size));
//        Z.setType(3);
//        Z.setDiameter(5);
//        Z.setFixed(true);
//        Z.setWeight(0);
//        Z.setColor(Color.blue);
//        graph.addNode(Z);
//        c = graph.connect(O, Z, 0);
//        c[0].setColor(Color.blue);
//        c[1].setColor(Color.blue);
//        c[0].setStroke(s);
//        c[1].setStroke(s);
//    }
    public void generateGuide(GuideGenerator guideGen, int size, int d) {
//        lastGuideGen = guideGen;
//        /*synchronized (graph)*/ {
//            for (ParticleProperty n : new ArrayList<ParticleProperty>(graph.getNodes())) {
//                if (n.getType() == 2) {
//                    graph.remNode(n);
//                }
//            }
//            for (int x = -size; x <= size; x++) {
//                for (int y = -size; y <= size; y++) {
//                    for (int z = -size; z <= size; z++) {
//                        if (guideGen.contains(x, y, z)) {
//                            ParticleProperty node = new ParticleProperty(new Vector((x + px) * d, (y + py) * d, (z + pz) * d));
//                            node.setType(2);
//                            node.setColor(Color.red);
//                            graph.addNode(node);
//                            node.setDiameter(5);
//                            node.setFixed(true);
//                            node.setWeight(0);
//                        }
//                    }
//                }
//            }
//            for (ParticleProperty n : new ArrayList<ParticleProperty>(graph.getNodes())) {
//                if (n.getType() == 2) {
//                    for (int i : test) {
//                        switch (i) {
//                            case 1:
//                                n.getPos().rotateX(0.05);
//                                break;
//                            case 2:
//                                n.getPos().rotateX(-0.05);
//                                break;
//                            case 3:
//                                n.getPos().rotateY(0.05);
//                                break;
//                            case 4:
//                                n.getPos().rotateY(-0.05);
//                                break;
//                            case 5:
//                                n.getPos().rotateZ(0.05);
//                                break;
//                            case 6:
//                                n.getPos().rotateZ(-0.05);
//                                break;
//                        }
//                    }
//                }
//            }
//
//        }
    }

    Ellipse2D.Double e = new Ellipse2D.Double();

    public void drawScene(int w, int h, Graphics2D g2) {
        g2.translate(gx, gy);
        if (showEdges) {
            for (Connection edge : graph.getConnections()) {
                Color color = Color.green;
                double v = (edge.getA().getPos().getZ() + edge.getB().getPos().getZ()) / 2;
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), getAlpha(v)));
                g2.setStroke(new BasicStroke(1));

                int x1 = (int) ((ParticleProperty) edge.getA().getProperty()).getProjection().getX();
                int y1 = (int) ((ParticleProperty) edge.getA().getProperty()).getProjection().getY();
                int x2 = (int) ((ParticleProperty) edge.getB().getProperty()).getProjection().getX();
                int y2 = (int) ((ParticleProperty) edge.getB().getProperty()).getProjection().getY();

                g2.drawLine(x1, y1, x2, y2);

                if (showEdgeWeights) {
//                    g2.drawString("" + edge.getWeight(), (int) ((x1 + x2) / 2.0), (int) ((y1 + y2) / 2.0));
                }
                if (showEdgeLength) {
//                    g2.drawString("" + (int) edge.getSource().getPos().distance(edge.getDestination().getPos()), (int) (edge.getSource()
//                            .getProjection().getX() + edge.getDestination().getProjection()
//                            .getX()) / 2, (int) (edge.getSource().getProjection().getY() + edge
//                            .getDestination().getProjection().getY()) / 2);
                }

                if (showEdgeNames && edge.getSubComponent() != null) {
                    g2.drawString("" + edge.getSubComponent(), (int) ((x1 + x2) / 2.0), (int) ((y1 + y2) / 2.0));
                }
            }

        }

        if (showNodes) {
            for (Component c : graph.getComponents()) {
                ParticleProperty node = c.getProperty();
//                if (node == hit) {
//                    node.setColor(Color.green);
//                } else if (node.getAdjacencies().size() <= 1) {
//                    node.setColor(Color.red);
//                } else if (node.getAdjacencies().size() <= 2) {
//                    node.setColor(Color.yellow);
//                } else if (node.getAdjacencies().size() <= 3) {
//                    node.setColor(Color.white);
//                } else if (node.getAdjacencies().size() <= 4) {
//                    node.setColor(Color.cyan);
//                } else if (node.getAdjacencies().size() <= 5) {
//                    node.setColor(Color.blue);
//                } else if (node.getAdjacencies().size() <= 6) {
//                    node.setColor(Color.magenta);
//                } else if (node.getAdjacencies().size() <= 7) {
//                    node.setColor(Color.pink);
//                }
                Color color = node.getColor();
                double v = node.getPos().getZ();
                if (node == hit) {
                    color = Color.magenta;
                }

                if (node.isFixed()) {
                    color = Color.CYAN;
                }

                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), getAlpha(v)));
                double zoom = pseudoZoom / 5;
                e.setFrame(node.getProjection().getX() - node.getRadius() * zoom,
                        node.getProjection().getY() - node.getRadius() * zoom,
                        node.getDiameter() * zoom,
                        node.getDiameter() * zoom
                );

                g2.fill(e);
                if (showNodeWeights) {
                    g2.drawString("" + node.getWeight(), (int) node.getProjection().getX(),
                            (int) node.getProjection().getY());
                }
                if (showNodeNames) {
                    g2.drawString("" + node.getName(), (int) node.getProjection().getX(),
                            (int) node.getProjection().getY());
                }
                if (showNodePosition) {
                    g2.drawString("" + node.getPos(), (int) node.getProjection().getX(),
                            (int) node.getProjection().getY());
                }
                if (node.isFixed()) {
                    g2.setColor(new Color(0, 0, 250, getAlpha(v)));
//                    g2.draw(e);
                }
            }
        }

        g2.translate(-gx, -gy);
        if (showHud) {
            drawTextBlock(
                    g2,
                    "FPS : " + tick + ((threads > 1) ? " using " + threads + " threads" : "") + "\n"
                    //                    + "Nodes : " + graph.getNodes().size() + "\n"
                    //                    + "Edges : " + graph.getEdges().size() + "\n"
                    + "Force : " + totalforce + " (" + totalforce.sum() + ")" + "\n", 10, 10, Color.LIGHT_GRAY);
        }
        if (showEnergyStatistics) {
            drawTextBlock(
                    g2,
                    "NASDASD" //                    "Kinetic Energy : " + (int) graph.getKE() + "\n"
                    //                    + "Potential Energy : " + (int) graph.getPE() + "\n"
                    //                    + "Total Energy : " + (int) (graph.getPE() + graph.getKE()) + "\n"
                    , 10, 70, Color.LIGHT_GRAY);
        }
        if (showHelp) {
            drawTextBlock(g2, "Help:\n" + "Left mouse and drag:\n"
                    + "  move node near mouse (unfix)\n" + "Right mouse and drag:\n"
                    + "  move node near mouse and fix\n" + "Doubleclick:\n" + "  create new node\n"
                    + "Shift and doubleclick:\n" + "  delete node near mouse\n"
                    + "Drag and drop one node on another:\n" + "  connect them to each other\n"
                    + "Shift and drag/drop one node on another:\n" + "  disconnect them\n"
                    + "Ctrl and mouse-move\n" + "  create a line of nodes\n\n"
                    + "X-Rotation: q / w\n" + "Y-Rotation: a / s\n" + "Z-Rotation: y / x\n"
                    + "Pseudozoom: + / -\n" + "Toggle nodes: n\n" + "Toggle node names: l\n"
                    + "Toggle node weights: m\n" + "Toggle node positions: P\n"
                    + "Toggle edges: e\n" + "Toggle edges length: L\n" + "Toggle edge names: b\n"
                    + "Toggle edge weights: d\n" + "Toggle hud: h\n" + "Toogle help: ?\n"
                    + "Fix all nodes: f\n" + "Unfix all nodes: u\n" + "Invert node fixations: i\n"
                    + "Decrease edge weights: r\n" + "Enhance edge weights: t\n"
                    + "Toggle system energy statatistcs: E\n"
                    + "Toggle flat mode: F\n"
                    + "", 10, 100,
                    new Color(255, 0, 0, 127));
        }
    }

    private int getAlpha(double v) {
        int alpha = (int) (127 - (v));
        if (alpha < 5) {
            alpha = 5;
        } else if (alpha > 250) {
            alpha = 250;
        }
        alpha = (v < -100) ? 0 : alpha;
        return alpha;
    }

    private static void drawTextBlock(Graphics2D g2, String txt, int x, int y, Color c) {
        g2.setColor(c);
        String[] lines = txt.split("\n");
        for (String line : lines) {
            g2.drawString(line, x, y += 15);
        }
    }


    @Override
    public synchronized void paintComponent(Graphics g) {
        tick.tick();
        Dimension d = getSize();
        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(getBackground());
        g2.clearRect(0, 0, d.width, d.height);
        if (graph == null) {
            return;
        }
        for (Component c : graph.getComponents()) {
            if (c.getProperty() == null) {
                c.setProperty(new ParticleProperty(c, c.getUID(), Color.yellow));
            }
        }
        project();

        drawScene(d.width, d.height, g2);
    }

    public synchronized void stop() {
        thread = null;
    }

    @Override
    public void run() {
        Thread me = Thread.currentThread();
        while (thread == me) {
            repaint();
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                break;
            }
        }
        thread = null;
    }

    public static GraphMonitor2D createFrame() {
        final GraphMonitor2D demo = new GraphMonitor2D();
        demo.setBackground(Color.black);
        demo.setFocusable(true); // VERY IMPORTANT for making the keylistener work on linux!!!!
        demo.tick = new TpsCounter(100);
        demo.thread = new Thread(demo);
        demo.thread.setPriority(Thread.MIN_PRIORITY);
        demo.thread.start();
        demo.addMouseListener(demo);
        demo.addMouseMotionListener(demo);
        demo.addKeyListener(demo);
        demo.addMouseWheelListener(demo);
        return demo;
    }

    public static int[] getClosestLatticeNode(ParticleProperty node, int d, int a) {
        int x = (int) node.getPos().getX();
        int y = (int) node.getPos().getY();
        int z = (int) node.getPos().getZ();
        return getClosestLatticeNode(x, y, z, d, a);
    }

    public static int[] getClosestLatticeNode(int x, int y, int z, int d, int a) {
        x = (x / d) * a + (Math.abs(x) % d > d / 2 ? a * Math.abs(x) / x : 0);
        y = (y / d) * a + (Math.abs(y) % d > d / 2 ? a * Math.abs(y) / y : 0);
        z = (z / d) * a + (Math.abs(z) % d > d / 2 ? a * Math.abs(z) / z : 0);
        return new int[]{x, y, z};
    }

    public interface GuideGenerator {

        public boolean contains(int x, int y, int z);

    }

}
