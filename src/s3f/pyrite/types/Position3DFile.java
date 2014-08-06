/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s3f.pyrite.types;

import javax.swing.ImageIcon;
import s3f.core.plugin.Plugabble;
import s3f.core.project.ComplexElement;
import s3f.core.project.Element;
import s3f.core.project.Resource;
import s3f.core.project.editormanager.TextFile;
import s3f.pyrite.ui.Editor3D;

/**
 *
 * @author anderson
 */
public class Position3DFile extends ComplexElement implements TextFile {

    public static final Element.CategoryData FLOWCHART_FILES = new Element.CategoryData("Position File", "pf", new ImageIcon(ModularCircuit.class.getResource("/resources/icons/fugue/wand-hat.png")), new Position3DFile());

    public static final String DUMMY = "$ 1 5.0E-6 10.20027730826997 52 5.0 50.0\n"
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
    
    private String text = DUMMY;

    public Position3DFile() {
        super("circuit", "/resources/icons/fugue/grid3d.png", FLOWCHART_FILES, new Class[]{Editor3D.class});
    }

    @Override
    public Plugabble createInstance() {
        return new Position3DFile();
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
