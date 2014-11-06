/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core.cycle;

import java.util.ArrayList;
import java.util.Iterator;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;

/**
 *
 * @author anderson
 */
public class Cycle implements Iterable<Component> {

    private final ArrayList<Component> components;

    public Cycle(Circuit circuit, int[] cycle) {
        components = new ArrayList<>(cycle.length);
        for (int i = 0; i < cycle.length; i++) {
            components.add(circuit.getComponents().get(cycle[i]));
        }
    }

    @Override
    public Iterator<Component> iterator() {
        return components.iterator();
    }

    public int getLength() {
        return components.size();
    }

}
