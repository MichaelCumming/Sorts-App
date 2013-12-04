/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `ElementVisitor.java'                                     *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.visit;

import cassis.Element;
import cassis.ind.Individual;
import cassis.form.Form;

/**
 * An <b>Element Visitor</b> specifies an abstract hierarchical visitor to an
 * {@link cassis.Element}.
 * <p>
 * The <b>ElementVisitor</b> class defines visiting methods for individuals
 * and forms. Corresponding to the hierarchical visitor pattern, this class
 * defines both a <b>visitEnter</b> and a <b>visitLeave</b> method. The
 * <b>visitEnter</b> method returns a boolean value specifying whether any
 * dependent elements (such as an attribute form or individuals contained in
 * a form) should also be visited.
 */
public abstract class ElementVisitor {

    // methods

    /**
     * <b>Enters (visits)</b> an element wrt an associate element.
     * If the associate element is an {@link cassis.ind.Individual} object then
     * it is an associate individual to the element being visited. Otherwise,
     * it is a {@link cassis.form.Form} object that contains the element being
     * visited. A boolean value is returned specifying whether any dependent
     * elements (such as an attribute form or individuals contained in a form)
     * should also be visited.
     * @param element the {@link cassis.Element} object being visited
     * @param assoc an {@link cassis.Element} object that specifies an associate
     * to the element being visited
     * @return a boolean value
     * @see cassis.Element#associate()
     */
    public boolean visitEnter(Element element, Individual assoc) {
        if (element instanceof Individual)
            return this.visitEnter((Individual) element, (Individual) assoc);
        return this.visitEnter((Form) element);
    }
    /**
     * <b>Enters (visits)</b> an individual wrt an associate individual.
     * An associate individual to another individual is any individual that
     * has an attribute form containing the other individual.
     * A boolean value is returned specifying whether the individual's
     * attribute form, if any, should also be visited.
     * @param ind the {@link cassis.ind.Individual} object being visited
     * @param assoc an {@link cassis.ind.Individual} object that is an associate
     * to the individual being visited
     * @return a boolean value
     */
    public abstract boolean visitEnter(Individual ind, Individual assoc);
    /**
     * <b>Enters (visits)</b> a form.
     * A boolean value is returned specifying whether the form's individuals
     * should also be visited.
     * @param form the {@link cassis.form.Form} object being visited
     * @return a boolean value
     */
    public abstract boolean visitEnter(Form form);

    /**
     * <b>Leaves (visits)</b> an element.
     * @param element the {@link cassis.Element} object being visited
     */
    public void visitLeave(Element element) {
        if (element instanceof Individual)
            this.visitLeave((Individual) element);
        else this.visitLeave((Form) element);
    }
    /**
     * <b>Leaves (visits)</b> an individual.
     * @param ind the {@link cassis.ind.Individual} object being visited
     */
    public abstract void visitLeave(Individual ind);
    /**
     * <b>Leaves (visits)</b> a form.
     * @param form the {@link cassis.form.Form} object being visited
     */
    public abstract void visitLeave(Form form);
}
