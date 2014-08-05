/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui.drawing3d;

import com.jogamp.newt.event.KeyEvent;
import java.awt.Color;
import static java.lang.Math.PI;
import java.util.ArrayList;
import java.util.Arrays;
import processing.core.PApplet;
import static processing.core.PConstants.QUAD_STRIP;
import static processing.core.PConstants.TRIANGLES;
import static processing.core.PConstants.TRIANGLE_FAN;
import processing.core.PGraphics;
import processing.opengl.PGraphics3D;
import quickp3d.DrawingPanel3D;
import quickp3d.simplegraphics.Axis;
import quickp3d.tools.ObjectPicker;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.Connection;
import s3f.pyrite.core.DefaultGridFittingTool;
import s3f.pyrite.core.Grid;

/**
 *
 * @author antunes
 */
public class Circuit3DEditPanel extends DrawingPanel3D {

    private CircuitDrawingTool defaultDrawer;
    private ObjectPicker<Component> picker;
    private CircuitDrawingTool pickerBufferDrawer;
    private PGraphics3D pickerBuffer;
    private ObjectPicker.Selectable<Component> pickerSource;
    private Circuit circuit;
    private Thread cubThread = null;
    private ArrayList<String> messages = new ArrayList<>();
    private Thread placeThread = null;

    public int pop = 5;
    public int gen = 5;

    public Circuit3DEditPanel(Circuit circuit) {
        super(800, 600, true);

        eyeZ = 1150;
        atX = 0;
        atY = -200;
        atZ = 648;
        upX = 0;
        upY = 1;
        posX = -400;
        posY = -620;
        theta = 500;
        this.circuit = circuit;

        defaultDrawer = new CircuitDrawingTool() {
            @Override
            public void drawNode(Component c, PGraphics g3d) {

                boolean printNodeInfo = false;
                for (Component bn : picker) {
                    if (bn.equals(c)) {
                        printNodeInfo = true;
                        break;
                    }
                }

                if (printNodeInfo || true) {
                    g3d.pushMatrix();
                    g3d.translate(c.getPos()[0], c.getPos()[1], c.getPos()[2]);
                    g3d.fill(0);
                    g3d.scale(DrawingPanel3D.RESET_SCALE / 10);
                    g3d.textSize(100);
                    g3d.text(c.getUID() + Arrays.toString(c.getPos()), 120, 0, 0);
                    g3d.popMatrix();
                }

//                if (c.type != null) {
//                    switch (c.type) {
//                        case "transistor":
//                            g3d.fill(0);
//                            break;
//                        case "jj":
//                            g3d.fill(Color.magenta.getRGB());
//                            break;
//                        case "j":
//                            g3d.fill(Color.WHITE.getRGB());
//                            break;
//                        case "&":
//                            if (c.name == null) {
//                                g3d.fill(Color.orange.getRGB());
//                            } else {
//                                if (c.name.equals("vcc")) {
//                                    g3d.fill(Color.yellow.getRGB());
//                                } else if (c.name.equals("gnd")) {
//                                    g3d.fill(Color.blue.getRGB());
//                                } else {
//                                    g3d.fill(Color.orange.getRGB());
//                                }
//                            }
//                            break;
//                        case "ex":
//                            g3d.fill(Color.LIGHT_GRAY.getRGB());
//                            break;
//                        case "w":
//                            g3d.fill(Color.CYAN.getRGB());
//                            break;
//                        default:
//                            g3d.fill(Color.PINK.getRGB());
//                            break;
//                    }
//                } else {
//                    g3d.fill(Color.PINK.getRGB());
//                    //g3d.fill(1f, 1, (c.uid * 2 / 360f));
//                }
                int i = 0;
                for (Connection con : c.getConnections()) {
                    Component v = con.getOtherComponent(c);
                    if (!con.isSatisfied()) {
                        g3d.stroke(255, 0, 0);
                        if (v.getPos() != null) {
                            g3d.pushStyle();
                            g3d.strokeWeight(3);
                            drawDashedLine(g3d, c.getPos()[0], c.getPos()[1], c.getPos()[2], v.getPos()[0], v.getPos()[1], v.getPos()[2]);
                            g3d.popStyle();
                        }
                    } else {
                        g3d.stroke(0, 255, 0);
                        if (v.getPos() != null) {
                            g3d.line(c.getPos()[0], c.getPos()[1], c.getPos()[2], v.getPos()[0], v.getPos()[1], v.getPos()[2]);
//                            {
//                                g3d.pushStyle();
//                                g3d.fill(0, 255, 0);
//                                //g3d.stroke(0);
//                                g3d.noStroke();
//                                drawCylinderBetweenPoints(g3d, c.getPos()[0], c.getPos()[1], c.getPos()[2], v.getPos()[0], v.getPos()[1], v.getPos()[2], .02f, 5);
//                                g3d.popStyle();
//                            }
                            String subComponent = con.getSubComponent();
                            if (!subComponent.isEmpty()) {
                                g3d.pushStyle();
                                g3d.stroke(0);
                                if (subComponent.contains("res")) {
                                    if (subComponent.contains("10k")) {
                                        g3d.fill(180, 180, 0);
                                    } else if (subComponent.contains("1k")) {
                                        g3d.fill(255, 255, 0);
                                    } else {
                                        g3d.fill(Color.orange.getRGB());
                                    }

                                } else if (subComponent.equals("(->^^|-)")) {
                                    g3d.fill(0, 0, 255);
                                    drawCylinderBetweenPoints(g3d, c.getPos()[0], c.getPos()[1], c.getPos()[2], v.getPos()[0], v.getPos()[1], v.getPos()[2], .08f, 7, 1 - .35f, 1 - .4f);
                                } else if (subComponent.contains("bat")) {
                                    g3d.fill(0);
                                } else if (subComponent.contains("btn")) {
                                    g3d.fill(0, 255, 255);
                                } else {
                                    g3d.fill(180, 0, 0);
                                    if (subComponent.equals("(->|-)")) {
                                        drawCylinderBetweenPoints(g3d, c.getPos()[0], c.getPos()[1], c.getPos()[2], v.getPos()[0], v.getPos()[1], v.getPos()[2], .08f, 7, 1 - .35f, 1 - .4f);
                                    } else if (subComponent.equals("(-|<-)")) {
                                        drawCylinderBetweenPoints(g3d, c.getPos()[0], c.getPos()[1], c.getPos()[2], v.getPos()[0], v.getPos()[1], v.getPos()[2], .08f, 7, .35f, .4f);
                                    }
                                }
                                drawCylinderBetweenPoints(g3d, c.getPos()[0], c.getPos()[1], c.getPos()[2], v.getPos()[0], v.getPos()[1], v.getPos()[2], .06f, 5, .3f);
                                g3d.popStyle();
                            }
                        }
                    }
                    i++;
                }

                for (Connection con : c.getConnections()) {
                    Component v = con.getOtherComponent(c);
                    if (v.getPos() == null) {
                        g3d.stroke(0, 255, 0);
                        break;
                    }
                }

                for (Connection con : c.getConnections()) {
                    if (!con.isSatisfied()) {
                        g3d.stroke(255, 0, 0);
                        break;
                    }
                }

                //g3d.stroke(0);
                for (Component bn : picker) {
                    if (bn.equals(c)) {
                        g3d.stroke(Color.blue.getRGB());
                    }
                }

//                g3d.fill(Color.HSBtoRGB((Circuit3DEditPanel.this.circuit.vertices.indexOf(c) / (float) Circuit3DEditPanel.this.circuit.vertices.size()), 1, 1));
//                g3d.noStroke();
                super.drawNode(c, g3d);
            }

        };

        pickerBufferDrawer = new CircuitDrawingTool() {
            @Override
            public void drawNode(Component c, PGraphics g3d) {
                g3d.fill(picker.getColor(Circuit3DEditPanel.this.circuit.getComponents().indexOf(c)));
                g3d.noStroke();
                super.drawNode(c, g3d);
            }
        };

        pickerSource = new ObjectPicker.Selectable<Component>() {
            @Override
            public Component select(int index) {
                if (index < Circuit3DEditPanel.this.circuit.getComponents().size()) {
                    return Circuit3DEditPanel.this.circuit.getComponents().get(index);
                } else {
                    return null;
                }
            }
        };
        
        defaultDrawer.setCircuit(circuit);
        pickerBufferDrawer.setCircuit(circuit);
    }

    public Circuit getCircuit() {
        return circuit;
    }

    public void setCircuit(Circuit cir) {
        circuit = cir;
        defaultDrawer.setCircuit(circuit);
        pickerBufferDrawer.setCircuit(circuit);
    }

    public ObjectPicker<Component> getPicker() {
        return picker;
    }

    @Override
    public void setup(Scene3D scene3D) {
        super.setup(scene3D);
        scene3D.textFont(scene3D.createFont("Ubuntu Light", 50, true));
        pickerBuffer = (PGraphics3D) scene3D.createGraphics(scene3D.width, scene3D.height, PApplet.P3D);
        picker = new ObjectPicker<>(pickerBuffer, pickerSource);
        append(new Axis());
    }

    @Override
    public void draw(PGraphics3D g3d) {
        //g3d.background(140, 170, 255);
        //g3d.background(18,97,128);
        //g3d.background(150,200,169);
        g3d.background(246, 217, 159);
        //g3d.background(200);
        defaultDrawer.drawAll(g3d);
//        cylinder(g3d,.02f,2f,10);

//        if (picker.getSize() == 2) {
//            Iterator<Component> iterator = picker.iterator();
//            Component c1 = iterator.next();
//            Component c2 = iterator.next();
//            printPath(g3d, c1, c2);
//        }
    }

    @Override
    public void draw(PGraphics g2d) {
        int plc = 0;
        int n = 0;
        for (Component c : circuit.getComponents()) {
            plc += (c.getPos() == null) ? 1 : 0;
            n += (c.isFixed()) ? 1 : 0;
        }
        g2d.clear();
        g2d.fill(0);
        g2d.textSize(15);
        //top info
        g2d.textAlign = PApplet.LEFT;
        int x = 10;
        int y = 15;
        g2d.text("Connections missing: " + circuit.getDisconectedConnections(), x, y);
        y += 15;
        g2d.text("Nodes placed: " + (circuit.getComponents().size() - plc), x, y);
        y += 15;
        g2d.text("Nodes unplaced: " + plc, x, y);
        y += 15;
        g2d.text("Nodes total: " + circuit.getComponents().size(), x, y);
        y += 15;
        g2d.text("Volume: " + circuit.calcVolume(), x, y);
        y += 15;
//        int p = Circuit.fitnessFunction(n, circuit.getComponents().size(), circuit.getDisconectedConnections(), circuit.calcVolume());
//        g2d.text("Points: " + p, x, y);
//        y += 15;
        g2d.text("P: " + eyeZ + " " + atX + " " + atY + " " + atZ + " " + upX + " " + upY + " " + posX + " " + posY + " " + theta, x, y);
        y += 15;
        g2d.text("fps: " + (int) scene3D.frameRate, x, y);
        //bottom info
        g2d.textAlign = PApplet.RIGHT;
        y = 25;
        for (String m : messages) {
            y += 15;
            g2d.text(m, g2d.width - x, g2d.height - y);
        }
    }

    public int print(String m) {
        messages.add(m);
        while (messages.size() > 8) {
            messages.remove(0);
        }
        return messages.size() - 1;
    }

    public int print(int i, String m) {
        messages.set(i, m);
        return i;
    }

    @Override
    public void mouseClicked(PApplet applet) {
        pickerBuffer.beginDraw();
        pickerBuffer.background(Color.black.getRGB());
        scene3D.applyCameraTransform(pickerBuffer);
        pickerBuffer.scale(scale);
        pickerBufferDrawer.drawAll(pickerBuffer);
        pickerBuffer.endDraw();
        picker.select(applet.mouseX, applet.mouseY);
    }

    @Override
    public void keyTyped(PApplet applet) {
        for (final Component c : picker) {
            if (c.getPos() == null) {
                continue;
            }
            switch (applet.keyCode) {
                case KeyEvent.VK_J:
                    c.getPos()[0] += left().x;
                    c.getPos()[1] += left().y;
                    break;
                case KeyEvent.VK_L:
                    c.getPos()[0] += right().x;
                    c.getPos()[1] += right().y;
                    break;
                case KeyEvent.VK_I:
                    c.getPos()[2]++;
                    break;
                case KeyEvent.VK_K:
                    c.getPos()[2]--;
                    break;
            }
        }

        if (applet.keyCode == KeyEvent.VK_BACK_SPACE) {
//            circuit.reset();
        } else if (applet.keyCode == KeyEvent.VK_ENTER) {
            if (cubThread == null || !cubThread.isAlive()) {
                cubThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            int i = print("cubeficating...");
                            DefaultGridFittingTool defaultGridFittingTool = new s3f.pyrite.core.DefaultGridFittingTool();
                            defaultGridFittingTool.fit(circuit, Grid.SIMPLE3);
                            print(i, "cubeficating... done.");
                        } catch (ThreadDeath e) {
                            print("break");
                        }
                    }
                };
                cubThread.start();
            } else {
                //print("cubefication not done!");
            }
        } else if (applet.keyCode == KeyEvent.VK_DELETE) {
//            new Thread() {
//                @Override
//                public void run() {
//                    for (Component c : circuit.vertices) {
//                        boolean remove = true;
//                        for (Component j : picker) {
//                            if (c == j) {
//                                remove = false;
//                            }
//                        }
//                        if (remove) {
//                            c.pos = null;
//                        }
//                    }
//                }
//            }.start();
        } else if (applet.keyCode == KeyEvent.VK_G) {
//            circuit.show2D();
        } else if (applet.keyCode == KeyEvent.VK_X) {
            if (cubThread != null && cubThread.isAlive()) {
                cubThread.stop();
            }
            if (placeThread != null && placeThread.isAlive()) {
                placeThread.stop();
            }
        } else if (applet.keyCode == KeyEvent.VK_R) {
//            circuit.decubeficate();
            circuit.getComponent("RailElm").setPos(new int[]{5, 2, 2});
            circuit.getComponent("GroundElm").setPos(new int[]{2, 2, 2});
        } else if (applet.keyCode == KeyEvent.VK_P) {
//            if (placeThread == null || !placeThread.isAlive()) {
//                placeThread = new Thread() {
//                    @Override
//                    public void run() {
//                        try {
//                            final int i = print("placing componecnts...");
//                            final int tpop = pop;
//                            final int tgen = gen;
//                            circuit.geneticPlaceComponents(tpop, tgen, topology, circuitBuilder);
//                            //                                    new CircuitBuilder() {
//                            //                                int x = 0;
//                            //                                int w = 0;
//                            //
//                            //                                @Override
//                            //                                public Circuit build() {
//                            //                                    x++;
//                            //                                    if (x > w) {
//                            //                                        w = x;
//                            //                                        print(i, "placing components... " + (tpop * tgen - x));
//                            //                                    }
//                            //
//                            //                                    return FULL_ADDER();
//                            //                                }
//                            //                            }
//                            print(i, "placing components... done.");
//                        } catch (ThreadDeath e) {
//                            print("break");
//                        }
//                    }
//                };
//                placeThread.start();
//            }
        } else if (applet.keyCode == KeyEvent.VK_T) {
//            if (Circuit.sleep == 0) {
//                Circuit.sleep = 10;
//            } else {
//                Circuit.sleep = 0;
//            }
//            print("Set timestep = " + Circuit.sleep + " ms");
        } else if (applet.keyCode == KeyEvent.VK_1) {
//            print("SET: Topology.SIMPLE");
//            topology = Topology.SIMPLE;
        } else if (applet.keyCode == KeyEvent.VK_2) {
//            print("SET: Topology.SIMPLE2");
//            topology = Topology.SIMPLE2;
        } else if (applet.keyCode == KeyEvent.VK_3) {
//            print("SET: Topology.SIMPLE3");
//            topology = Topology.SIMPLE3;
        } else if (applet.keyCode == KeyEvent.VK_4) {
//            print("SET: Topology.SIMPLE4");
//            topology = Topology.SIMPLE4;
        } else if (applet.keyCode == KeyEvent.VK_5) {
//            print("SET: Topology.HEX");
//            topology = Topology.HEX;
        }

    }

    public static void drawDotedLine(PGraphics g3d, float x0, float y0, float z0, float x1, float y1, float z1) {
        float d = (float) Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1) + (z0 - z1) * (z0 - z1));
        int s = 100;
        float x, y, z, t = 0;
        for (; t <= 1;) {
            x = (1 - t) * x0 + t * x1;
            y = (1 - t) * y0 + t * y1;
            z = (1 - t) * z0 + t * z1;
            g3d.point(x, y, z);
            t += 1f / s;
        }
    }

    public static void drawDashedLine(PGraphics g3d, float x0, float y0, float z0, float x1, float y1, float z1) {
        float d = (float) Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1) + (z0 - z1) * (z0 - z1));
        float s = d / 0.1f; //[0.01-0.12]
        float x, y, z, t = 0;
        float a, b, c;
        for (; t <= 1;) {
            x = (1 - t) * x0 + t * x1;
            y = (1 - t) * y0 + t * y1;
            z = (1 - t) * z0 + t * z1;
            t += 1f / s;
            a = (1 - t) * x0 + t * x1;
            b = (1 - t) * y0 + t * y1;
            c = (1 - t) * z0 + t * z1;
            t += 2f / s;
            g3d.line(x, y, z, a, b, c);
        }
    }

    public static void drawCylinderBetweenPoints(PGraphics g3d, float x0, float y0, float z0, float x1, float y1, float z1, float thickness, int sides, float wut) {
        drawCylinderBetweenPoints(g3d, x0, y0, z0, x1, y1, z1, thickness, sides, wut, 1 - wut);
    }

    public static void drawCylinderBetweenPoints(PGraphics g3d, float x0, float y0, float z0, float x1, float y1, float z1, float thickness, int sides, float start, float end) {
        float x, y, z, t = start;
        float a, b, c;
        x = (1 - t) * x0 + t * x1;
        y = (1 - t) * y0 + t * y1;
        z = (1 - t) * z0 + t * z1;
        t = end;
        a = (1 - t) * x0 + t * x1;
        b = (1 - t) * y0 + t * y1;
        c = (1 - t) * z0 + t * z1;
        drawCylinderBetweenPoints(g3d, x, y, z, a, b, c, thickness, sides);
    }

    public static void drawCylinderBetweenPoints(PGraphics g3d, float x0, float y0, float z0, float x1, float y1, float z1, float thickness, int sides) {
        float x = (x0 - x1);
        float y = (y0 - y1);
        float z = (z0 - z1);
        float d = (float) Math.sqrt(x * x + y * y + z * z);

        /**
         * http://processing.org/discourse/beta/num_1159148295.html
         * (samuel_bruce)
         *
         * Given a 3d vector, I wanted to calculate the angles of x and y
         * rotation to make the Z axis line up along that vector. Also (as it
         * turns out) I want the X axis to stay 'flat', i.e. no Z rotation.
         *
         * Turns out this is reasonably simple so long as you rotate in Y first.
         * Here's the solution..
         *
         * float rx = asin(-vector.y); float ry = atan2(vector.x, vector.z);
         * rotateY(ry); rotateX(rx);
         *
         * Works like a charm.
         *
         * NOTE: normalize before taking the asin
         *
         */
        float rx = (float) Math.asin(-y / d);
        float ry = (float) Math.atan2(x, z);

        g3d.pushMatrix();
        g3d.translate(x0, y0, z0);
        g3d.rotateY(ry);
        g3d.rotateX(rx);
        g3d.translate(0, 0, -d / 2);
        g3d.rotateX((float) (Math.PI / 2));
        drawCylinder(g3d, thickness, d, sides);
        g3d.popMatrix();
    }

    public static void drawCylinder(PGraphics g3d, float w, float h, int sides) {
        float angle;
        float[] x = new float[sides + 1];
        float[] z = new float[sides + 1];

        //get the x and z position on a circle for all the sides
        for (int i = 0; i < x.length; i++) {
            angle = PGraphics.TWO_PI / (sides) * i;
            x[i] = (float) Math.sin(angle) * w;
            z[i] = (float) Math.cos(angle) * w;
        }

        //draw the top of the cylinder
        g3d.beginShape(TRIANGLE_FAN);

        g3d.vertex(0, -h / 2, 0);

        for (int i = 0; i < x.length; i++) {
            g3d.vertex(x[i], -h / 2, z[i]);
        }

        g3d.endShape();

        //draw the center of the cylinder
        g3d.beginShape(QUAD_STRIP);

        for (int i = 0; i < x.length; i++) {
            g3d.vertex(x[i], -h / 2, z[i]);
            g3d.vertex(x[i], h / 2, z[i]);
        }

        g3d.endShape();

        //draw the bottom of the cylinder
        g3d.beginShape(TRIANGLE_FAN);

        g3d.vertex(0, h / 2, 0);

        for (int i = 0; i < x.length; i++) {
            g3d.vertex(x[i], h / 2, z[i]);
        }

        g3d.endShape();
    }

    public static float[] cx, cz, sphereX, sphereY, sphereZ;
    public static int currRes = 0;

    public static void drawSphere(PGraphics g3d, float radius, int res) {
        float delta = 360f / res;
        float rad = (float) (PI / 180);
        if (res != currRes) {
            cx = new float[res];
            cz = new float[res];
            // calc unit circle
            for (int i = 0; i < res; i++) {
                cx[i] = (float) Math.cos(i * delta * rad);
                cz[i] = (float) Math.sin(i * delta * rad);
            }
            // computing vertexlist
            // vertexlist starts at south pole
            int vertCount = res * (res - 1) + 2;
            int currVert = 0;

            sphereX = new float[vertCount];
            sphereY = new float[vertCount];
            sphereZ = new float[vertCount];

            // radius angle is used for scaling circle
            float angle_step = 180.0f / res;
            float angle = angle_step;
//            float[] ringVertsX = new float[res];
//            float[] ringVertsZ = new float[res];

            for (int i = 1; i < res; i++) {
                float curradius = (float) Math.sin(angle * rad);
                float currY = (float) -Math.cos(angle * rad);
                for (int j = 0; j < res; j++) {
                    sphereX[currVert] = cx[j] * curradius;
                    sphereY[currVert] = currY;
                    sphereZ[currVert++] = cz[j] * curradius;
                }
                angle += angle_step;
            }
            currRes = res;
        }

        int v1, v2, v3, v4;
        g3d.pushMatrix();
        g3d.scale(radius);
        g3d.beginShape(TRIANGLES);
        // 1st ring from south pole
        for (int i = 1; i <= res; i++) {
            v2 = i % res;
            v3 = (i + 1) % res;
            g3d.vertex(0, -1, 0);
            g3d.vertex(sphereX[v2], sphereY[v2], sphereZ[v2]);
            g3d.vertex(sphereX[v3], sphereY[v3], sphereZ[v3]);
        }

        // middle rings
        int voff = res;
        for (int i = 1; i < res - 1; i++) {
            for (int j = 0; j < res; j++) {
                v1 = voff - res + j;
                v2 = voff + j;
                v3 = (j + 1) % res + voff;
                v4 = voff - res + (j + 1) % res;
                g3d.vertex(sphereX[v1], sphereY[v1], sphereZ[v1]);
                g3d.vertex(sphereX[v2], sphereY[v2], sphereZ[v2]);
                g3d.vertex(sphereX[v3], sphereY[v3], sphereZ[v3]);
                // 2nd part of quad
                g3d.vertex(sphereX[v1], sphereY[v1], sphereZ[v1]);
                g3d.vertex(sphereX[v3], sphereY[v3], sphereZ[v3]);
                g3d.vertex(sphereX[v4], sphereY[v4], sphereZ[v4]);
            }
            voff += res;
        }
        // undo the last update
        voff -= res;
        // add the northern cap
        for (int i = 1; i <= res; i++) {
            v2 = voff + i % res;
            v3 = voff + (i + 1) % res;
            g3d.vertex(0, 1, 0);
            g3d.vertex(sphereX[v2], sphereY[v2], sphereZ[v2]);
            g3d.vertex(sphereX[v3], sphereY[v3], sphereZ[v3]);
        }
        g3d.endShape();
        g3d.popMatrix();
    }
}
