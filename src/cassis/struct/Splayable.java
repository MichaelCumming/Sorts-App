/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Splayable.java'                                          *
 * written by: Rudi Stouffs                                  *
 * last modified: 30.9.04                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.struct;

import cassis.Thing;

/**
 * The <b>Splayable</b> interface extends on the <tt>Thing</tt> interface,
 * adding abstract methods for comparing things for the purpose of
 * {@link SplayTree}s. For example, {@link cassis.ind.PlaneSegment} uses
 * a {@link SplayTree} to represent a sweepline. The sorting of edges along
 * a sweepline requires an alternative (more complex) comparison algorithm.
 */
public interface Splayable extends Thing {

    // methods

    /**
     * <b>Compares</b> this splayable thing with another splayable thing for
     * the purpose of splaying.
     * @param other the comparison thing
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER},
     * or {@link cassis.Thing#FAILED}
     */
    int splayCompare(Splayable other);
}
