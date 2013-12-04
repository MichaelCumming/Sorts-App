/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `SortVisitor.java'                                        *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.visit;

import cassis.sort.Sort;

/**
 * A <b>Sort Visitor</b> specifies an abstract hierarchical visitor to a
 * {@link cassis.sort.Sort}.
 * <p>
 * The <b>SortVisitor</b> class defines visiting methods for sorts.
 * Corresponding to the hierarchical visitor pattern, this class defines
 * both a {@link #visitEnter} and a {@link #visitLeave} method. The former
 * method returns a boolean value specifying whether any dependent elements
 * (such as a weight or disjunctive component sorts) should also be visited.
 */
public interface SortVisitor {
    
    /**
     * <b>Enters (visits)</b> a sort. A boolean value is returned specifying
     * whether any dependent sorts (such as a weight or disjunctive component
     * sorts) should also be visited.
     * @param sort the {@link cassis.sort.Sort} object being visited
     * @return a boolean value
     */
    public boolean visitEnter(Sort sort);
    /**
     * <b>Leaves (visits)</b> a sort.
     * @param sort the {@link cassis.sort.Sort} object being visited
     */
    public void visitLeave(Sort sort);
}