/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.types;

import javax.swing.ImageIcon;
import s3f.core.plugin.Plugabble;
import s3f.core.project.ComplexElement;
import s3f.core.project.Editor;
import s3f.core.project.Element;
import s3f.core.project.Resource;
import s3f.core.project.editormanager.TextFile;
import s3f.pyrite.ui.CircuitEditor;
import s3f.pyrite.ui.Editor3D;

/**
 *
 * @author anderson
 */
public class ModularCircuit extends ComplexElement implements TextFile {

    public static final Element.CategoryData FLOWCHART_FILES = new Element.CategoryData("Modular Circuit", "mc", new ImageIcon(ModularCircuit.class.getResource("/resources/icons/fugue/cube.png")), new ModularCircuit());

    private String text = "";

    public ModularCircuit() {
        super("circuit", "/resources/icons/fugue/modular.png", FLOWCHART_FILES, new Class[]{CircuitEditor.class});
    }

    @Override
    public Plugabble createInstance() {
        return new ModularCircuit();
    }

    @Override
    public final void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void addResource(Resource resource) {
        super.addResource(resource);
//        if (resource.getPrimary() instanceof ModularCircuit) {
//            ModularCircuit flowchart = (ModularCircuit) resource.getPrimary();
//            flowchart.addExternalResource(resource.getSecondary());
//            Editor currentEditor = getCurrentEditor();
//            if (currentEditor != null) {
//                currentEditor.update();
//            }
//        }
    }

    @Override
    public void removeResource(Resource resource) {
        super.removeResource(resource);
//        if (resource.getPrimary() instanceof ModularCircuit) {
//            ModularCircuit flowchart = (ModularCircuit) resource.getPrimary();
//            flowchart.removeExternalResource(resource.getSecondary());
//            Editor currentEditor = getCurrentEditor();
//            if (currentEditor != null) {
//                currentEditor.update();
//            }
//        }
    }
}
