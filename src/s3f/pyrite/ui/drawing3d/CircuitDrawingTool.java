///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package s3f.pyrite.ui.drawing3d;
//
//import java.util.ArrayList;
//import org.apache.commons.math3.linear.RealVector;
//import processing.core.PGraphics;
//import s3f.pyrite.core.Circuit;
//import s3f.pyrite.core.Component;
//
///**
// *
// * @author antunes
// */
//public class CircuitDrawingTool {
//
//    private Circuit circuit = null;
//
//    public void setCircuit(Circuit circuit) {
//        this.circuit = circuit;
//    }
//
//    public void drawNode(Component c, PGraphics g3d) {
//        if (c.getPos() == null) {
//            return;
//        }
//        g3d.pushMatrix();
//        g3d.translate(c.getPos()[0], c.getPos()[1], c.getPos()[2]);
//        g3d.box((c.isCoupler() && (c.getName() == null || (c.getName() != null && c.getName().contains("ex")))) ? .10f : .20f);
//        g3d.popMatrix();
//    }
//
//    public void drawAll(PGraphics g3d) {
//        if (circuit != null) {
//            for (Component c : new ArrayList<>(circuit.getComponents())) {
//                if (c.getPos() != null) {
//                    drawNode(c, g3d);
//                }
//            }
//        }
//    }
//
//    /*  OLD  */
//    public void drawVector(RealVector v, PGraphics g3d) {
//        if (!g3d.stroke) {
//            g3d.stroke(255);
//        }
//        g3d.line(0, 0, 0, (float) v.getEntry(0),
//                (float) v.getEntry(1),
//                (float) v.getEntry(2));
//    }
//
//    public void drawVector(RealVector v1, RealVector v2, PGraphics g3d) {
//        if (!g3d.stroke) {
//            g3d.stroke(255);
//        }
//        g3d.line((float) v1.getEntry(0),
//                (float) v1.getEntry(1),
//                (float) v1.getEntry(2),
//                (float) v2.getEntry(0),
//                (float) -v2.getEntry(1),
//                (float) v2.getEntry(2));
//    }
//}
