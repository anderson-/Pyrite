/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core.fdgfa.fdgs.force;

import s3f.pyrite.core.fdgfa.fdgs.ParticleProperty;
import s3f.pyrite.util.Vector;

/**
 *
 * @author andy
 */
public interface ForceComputer<T extends ParticleProperty> {

    public Vector repulsiveForce(T a, T b);

    public Vector attractiveForce(T node);
}
