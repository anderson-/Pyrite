///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package s3f.pyrite.ui;
//
//import com.jogamp.newt.event.KeyEvent;
//import java.awt.Color;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.swing.JRootPane;
//import processing.core.PApplet;
//import processing.core.PConstants;
//import s3f.core.plugin.Data;
//import s3f.core.plugin.Plugabble;
//import s3f.core.project.Editor;
//import s3f.core.project.Element;
//import s3f.core.project.editormanager.TextFile;
//import s3f.core.ui.tab.TabProperty;
//import s3f.pyrite.core.Circuit;
//import s3f.pyrite.core.Component;
//import s3f.pyrite.types.Blueprint;
//import s3f.pyrite.types.VolumetricCircuit;
//import s3f.pyrite.util.Vector;
//
///**
// *
// * @author anderson
// */
//public class BlueprintEditor extends PApplet implements Editor {
//
//    private final Data data;
//    private Blueprint bp;
//    private Circuit circuit = null;
//
//    JRootPane p = new JRootPane();
//    private int page;
//
//    public BlueprintEditor() {
//        data = new Data("editorTab2", "s3f.core.code2", "Editor Tab2");
//        init();
//        p.getContentPane().setBackground(Color.red);
//        p.getContentPane().add((PApplet) this);
//        TabProperty.put(data, "Editor", null, "Editor de códigs", p);
//        new Thread() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(100);
//                        redraw();
//                        System.out.println("*");
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//
//                }
//            }
//        }.start();
//    }
//
//    @Override
//    public void setContent(final Element content) {
//        if (content instanceof Blueprint) {
//            bp = (Blueprint) content;
//            data.setProperty(TabProperty.TITLE, content.getName());
//            data.setProperty(TabProperty.ICON, content.getIcon());
//        }
//    }
//
//    @Override
//    public Element getContent() {
//        return bp;
//    }
//
//    @Override
//    public void update() {
//        for (Object o : bp.getExternalResources()) {
//            if (o instanceof VolumetricCircuit) {
//                VolumetricCircuit position3DFile = (VolumetricCircuit) o;
//                circuit = position3DFile.getCircuit();
//                System.out.println(circuit);
//            }
//        }
//    }
//
//    @Override
//    public void selected() {
//
//    }
//
//    @Override
//    public Data getData() {
//        return data;
//    }
//
//    @Override
//    public Plugabble createInstance() {
//        return new BlueprintEditor();
//    }
//
//    @Override
//    public synchronized void setup() {
//        size(500, 500, JAVA2D);
//        noLoop();
//    }
//
//    int x = 0;
//    boolean i = true;
//
//    @Override
//    public void draw() {
//        int k = 70;
//        background(i ? x++ : x--, 211, 40);
//        translate(100, 100);
//        if (circuit != null) {
//            color(0);
//            for (int j = -1; j <= 1; j++) {
//                for (Component c : circuit.getComponents()) {
//                    Vector pos = c.getPos();
//                    if (pos != null && pos.getZ() == page + j) {
//                        pushMatrix();
//
//                        translate((float) pos.getX() * k + (float) pos.getZ() * 20, (float) pos.getY() * k + (float) pos.getZ() * 20);
//                        ellipse(-13, -13, 26, 26);
//                        popMatrix();
//                    }
//                }
//            }
//        } else if (x == 255 || x == 0) {
//            i = !i;
//        }
//    }
//
//    @Override
//    public void mousePressed() {
//
//    }
//
//    @Override
//    public void keyPressed() {
//        switch (keyCode) {
//
//            case KeyEvent.VK_PAGE_UP:
//                page++;
//                break;
//            case KeyEvent.VK_PAGE_DOWN:
//                page--;
//                break;
//
//        }
//    }
//
//}
