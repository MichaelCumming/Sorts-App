/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Resolvable.java'                                         *
 * written by: Rudi Stouffs                                  *
 * last modified: 05.3.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

/**
 * The <b>Resolvable</b> interface applies to {@link Individual}s that are dependent
 * upon one another for their complete specification. Given two individuals that are 
 * dependent on one another, when parsing the first individual in this pair, the second
 * individual is necessarily not yet specified, resulting in an incomplete
 * specification of the first one. As a result, this individual is <i>unresolved</i>.
 * When the second individual is parsed subsequently, the first is already 
 * (incompletely) specified, resulting in a complete specification of the second one.
 * This second individual may then serve to <i>resolve</i> the first one.
 */
public interface Resolvable {

    /**
     * <b>Resolves</b> this object wrt another individual.
     * @param ind an {@link Individual} object
     */
    void resolve(Individual ind);
}
