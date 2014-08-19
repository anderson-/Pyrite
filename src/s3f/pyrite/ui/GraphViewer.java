/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui;

import javax.swing.JPanel;
import javax.swing.JRootPane;
import quickp3d.DrawingPanel3D;
import s3f.core.plugin.Data;
import s3f.core.plugin.Plugabble;
import s3f.core.project.Editor;
import s3f.core.project.Element;
import s3f.core.ui.tab.TabProperty;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.types.CircuitFile;
import s3f.pyrite.ui.drawing3d.Circuit3DEditPanel;

/**
 *
 * @author anderson
 */
public class GraphViewer implements Editor {

//    private static final ImageIcon ICON = new ImageIcon(ModularCircuitEditor.class.getResource("/resources/icons/fugue/block.png"));
    private final Data data;
    private final JRootPane pane;
    private CircuitFile circuit;

    public GraphViewer() {
        data = new Data("editorTab", "s3f.core.code", "Editor Tab");
        pane = new JRootPane();
        TabProperty.put(data, "Editor", null, "Editor de código", pane);
    }

    @Override
    public void setContent(Element content) {
        pane.removeAll();
        if (content instanceof CircuitFile) {
            circuit = (CircuitFile) content;
            data.setProperty(TabProperty.TITLE, content.getName());
            data.setProperty(TabProperty.ICON, content.getIcon());
        }
    }

    @Override
    public Element getContent() {
        return circuit;
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
    public Data getData() {
        return data;
    }

    @Override
    public void init() {

    }

    @Override
    public Plugabble createInstance() {
        return new GraphViewer();
    }
}
