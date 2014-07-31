/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s3f.pyrite.core;

import java.util.Collection;

/**
 *
 * @author antunes
 */
public interface Grid {
    
    public Collection<int[]> getNeighborhood(int... pos);
    
}
