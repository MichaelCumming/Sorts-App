/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `AssociateVisitor.java'                                   *
 * written by: Rudi Stouffs                                  *
 * last modified: 20.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.visit;

import cassis.ind.Individual;

/**
 * An <b>Associate Visitor</b> specifies an abstract visitor to associate
 * {@link cassis.ind.Individual}s. An <i>associate-visitor</i> distinguishes
 * itself from an {@link ElementVisitor} in that it traverses individuals
 * (only) upwards in the element hierarchy, from individual to associate. 
 * <p>
 * The <b>AssociateVisitor</b> class defines a {@link #visit} method for
 * individuals.
 */
public interface AssociateVisitor {
    
    /**
     * <b>Visits</b> an individual. A boolean value is returned specifying
     * whether the associate individual, if any, should also be visited.
     * @param ind the {@link cassis.ind.Individual} object being visited
     * @return a boolean value
     */
    public boolean visit(Individual ind);
}