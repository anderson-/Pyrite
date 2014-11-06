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
import s3f.pyrite.core.Circuit;
import s3f.pyrite.ui.VolimetricCircuitEditor;

/**
 *
 * @author anderson
 */
public class VolumetricCircuit extends ComplexElement implements TextFile {

    public static final Element.CategoryData FLOWCHART_FILES = new Element.CategoryData("Volumetric Circuit", "vc", new ImageIcon(CircuitFile.class.getResource("/resources/icons/silk/bricks.png")), new VolumetricCircuit());

    private String text = "";
    private String circuitName = "";
    private Circuit circuit;

    public VolumetricCircuit() {
        super("Empty Volumetric Circuit", "/resources/icons/silk/brick.png", FLOWCHART_FILES, new Class[]{VolimetricCircuitEditor.class});
    }

    @Override
    public Plugabble createInstance() {
        return new VolumetricCircuit();
    }

    @Override
    public final void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        if (circuit != null) {
            text = circuit.save();
        }
        return text;
    }

    @Override
    public void addResource(Resource resource) {
        super.addResource(resource);
        if (resource.getPrimary() instanceof VolumetricCircuit) {
            VolumetricCircuit flowchart = (VolumetricCircuit) resource.getPrimary();
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
        if (resource.getPrimary() instanceof VolumetricCircuit) {
            VolumetricCircuit flowchart = (VolumetricCircuit) resource.getPrimary();
            flowchart.removeExternalResource(resource.getSecondary());
            Editor currentEditor = getCurrentEditor();
            if (currentEditor != null) {
                currentEditor.update();
            }
        }
    }

    public Circuit getCircuit() {
        return circuit;
    }

    public void setCircuit(Circuit circuit) {
        this.circuit = circuit;
        circuit.load(text);
    }

}
