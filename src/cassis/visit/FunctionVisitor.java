/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `FunctionVisitor.java'                                    *
 * written by: Rudi Stouffs                                  *
 * last modified: 28.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.visit;

import cassis.Element;
import cassis.ind.Individual;
import cassis.ind.Function;
import cassis.form.Form;
import cassis.sort.Sort;
import cassis.sort.PrimitiveSort;

/**
 * A <b>Function Visitor</b> evaluates a {@link Function}.
 * Starting from the numeric-function, it looks alternately up (using
 * the associate-visitor behavior) and down (using the element-visitor behavior)
 * the hierarchical data structure for combinations of individuals of the
 * appropriate sorts. A combination of individuals consists of individuals
 * that are found on the same path. As soon as a valid combination is found,
 * this path is no longer extended and alternative paths are considered. 
 * Each time a valid combination is found, the numeric function is applied to
 * the appropriate numerical methods of these individuals.<br>
 * The initial exploration strategy is to alternate looking up and down.
 * As soon as a valid combination is found, all exploration is restricted to
 * alternative down paths. In the case that the end of the down path is
 * encountered before the end of the up path, exploration is temporarily
 * restricted to the up path, until a valid combination is found or the end of
 * the up path is encountered.
 * <p>
 * The <b>FunctionVisitor</b> class both extends on the {@link ElementVisitor}
 * class and implements the {@link AssociateVisitor} interface.
 * A function-visitor specifies a {@link Function} to evaluate, and
 * an array of individuals corresponding to the array of target sorts of
 * the numeric-function. It also keeps track of the current level, both up and
 * down, of the number of target encountered, and whether and when to look up.
 * The current level is a measure of the distance of the current element from
 * the numeric-function.
 */
public class FunctionVisitor extends ElementVisitor implements AssociateVisitor {

    // representation

    private Function fn;
    private PrimitiveSort targets[];
    private Individual assoc, inds[];
    private int level, uplevel, order[], found;
    private boolean up;

    // constructors

    /**
     * Constructs a <b>function visitor</b> for the specified numeric function.
     * @param fn a {@link Function} object
     */
    public FunctionVisitor(Function fn) {
	super();
	this.fn = fn;
        this.targets = fn.targets();
        this.inds = new Individual[this.targets.length];
        this.order = new int[this.targets.length];
        for (int n = 0; n < this.targets.length; n++) this.order[n] = 0;
        this.level = this.uplevel = this.found = 0;
        this.assoc = fn.associate();
        this.up = true;
     }

    // methods

    /**
     * <b>Clears</b> and reinitializes this function-visitor. 
     */
    public void clear() {
        for (int n = 0; n < this.targets.length; n++) this.order[n] = 0;
        this.level = this.uplevel = this.found = 0;
        this.assoc = fn.associate();
        this.up = true;
    }

    /**
     * <b>Visits</b> an individual. If the (base) sort of the individual equals
     * one or more of the currently unencountered target sorts, then these
     * target sorts are marked as encountered at the current level and this
     * individual becomes the current target individual for these sorts.
     * If all target sorts are currently marked as encountered, then
     * the numeric function is applied to all current target individuals and
     * looking up no longer needs to be considered. In this case, <tt>false</tt>
     * is returned. Otherwise, <tt>true</tt> or <tt>false</tt> is returned
     * depending on the current strategy (resp., up only or alternate).
     * @param ind the {@link Individual} object being visited
     * @return a <tt>boolean</tt> value
     */
    public boolean visit(Individual ind) {
        this.uplevel--;
        Sort sort = ind.ofSort().base();
        for (int arg = 0; arg < this.targets.length; arg++)
            if ((this.order[arg] == 0) && sort.equals(this.targets[arg])) {
                this.inds[arg] = ind;
                this.order[arg] = this.uplevel;
                this.found++;
                if (this.found == this.targets.length) {
                    fn.apply(inds);
                    this.assoc = null;
                    return false;
                }
            }
        this.assoc = ind.associate();
        return this.up;
    }
    
    /**
     * <b>Enters (visits)</b> an individual wrt an associate individual.
     * If one or more target sorts have been encountered at the current level,
     * then this individual becomes the current target individual for these
     * sorts. If all target sorts are currently marked as encountered, then
     * the numeric function is applied to all current target individuals and
     * looking up no longer needs to be considered. In this case, <tt>false</tt>
     * is returned. Otherwise, the adopted strategy depends on whether this
     * individual has an attribute form and whether the end of the up-path
     * has been encountered. In this case, <tt>true</tt> is returned. 
     * @param ind the {@link Individual} object being visited
     * @return a <tt>boolean</tt> value
     * @see Individual#attrDefined
     */
    public boolean visitEnter(Individual ind, Individual assoc) {
        for (int arg = 0; arg < this.targets.length; arg++)
            if (this.order[arg] == this.level)
                this.inds[arg] = ind;
        if (this.found == this.targets.length) {
            fn.apply(inds);
            this.assoc = null;
            return false;
        }
        this.up = !ind.attrDefined();
        if (this.assoc != null) this.assoc.accept(this);
        return true;
    }
    /**
     * <b>Leaves (visits)</b> an individual. Does nothing.
     * @param ind the {@link cassis.ind.Individual} object being visited
     */
    public void visitLeave(Individual ind) {}

    /**
     * <b>Enters (visits)</b> a form. If the (base) sort of the form equals
     * one or more of the currently unencountered target sorts, then these
     * target sort are marked as encountered at the current level.
     * @param form the {@link Form} object being visited
     * @return <tt>true</tt>
     */
    public boolean visitEnter(Form form) {
        this.level++;
        Sort sort = form.ofSort().base();
        for (int arg = 0; arg < this.targets.length; arg++)
            if ((this.order[arg] == 0) && sort.equals(this.targets[arg])) {
                this.order[arg] = this.level;
                this.found++;
            }
	form.maximalize();
        return true;
    }
    /**
     * <b>Leaves (visits)</b> a form. If one or more target sort have been
     * encountered at the current level, then these target sorts are no longer
     * marked as encountered.
     * @param form the {@link Form} object being visited
     */
    public void visitLeave(Form form) {
        for (int arg = 0; arg < this.targets.length; arg++)
            if (this.order[arg] == this.level) {
                this.order[arg] = 0;
                this.found--;
            }
        this.level--;
    }    
}
