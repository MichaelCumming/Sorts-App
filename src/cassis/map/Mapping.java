/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Mapping.java'                                            *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.map;

/**
 * A <b>mapping</b> specifies the conditions under which an {@link cassis.ind.Individual}
 * can map onto another individual of the same {@link cassis.sort.Sort}. For instance,
 * a label maps onto another label if it has the same string value; a line segment
 * maps onto another line segment if it is embedded in the other segment.
 * Various mappings can apply to the same sort. Each characteristic
 * {@link cassis.ind.Individual} defines a default mapping.
 * <p>
 * The <b>Mapping</b> class defines the constants specifying the different types
 * of mapping.
 */
public final class Mapping {

    // constants

    /**
     * A constant for specifying an <b>exact</b> mapping. Under an exact mapping,
     * the individuals must have equal values.
     */
    public static final Mapping EXACT = new Mapping();
    /**
     * A constant for specifying an <b>euclidean</b> mapping. An euclidean mapping
     * specifies a similarity transformation (translation, rotation and scaling)
     * for the mapping of all geometric individuals.
     */
    public static final Mapping EUCLIDEAN = new Mapping();

    private Mapping() {
    }
}