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
import s3f.pyrite.ui.Editor3D;

/**
 *
 * @author anderson
 */
public class Position3DFile extends ComplexElement implements TextFile {

    public static final Element.CategoryData FLOWCHART_FILES = new Element.CategoryData("Position File", "pf", new ImageIcon(CircuitFile.class.getResource("/resources/icons/fugue/wand-hat.png")), new Position3DFile());
    
    private String text = "";

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
        if (resource.getPrimary() instanceof Position3DFile) {
            Position3DFile flowchart = (Position3DFile) resource.getPrimary();
            flowchart.addExternalResource(resource.getSecondary());
            Editor currentEditor = getCurrentEditor();
            if (currentEditor != null) {
                currentEditor.update();
            }
        }
    }

    @Override
    public void removeResource(Resource resource) {
        super.removeResource(resource);
        if (resource.getPrimary() instanceof Position3DFile) {
            Position3DFile flowchart = (Position3DFile) resource.getPrimary();
            flowchart.removeExternalResource(resource.getSecondary());
            Editor currentEditor = getCurrentEditor();
            if (currentEditor != null) {
                currentEditor.update();
            }
        }
    }
}
