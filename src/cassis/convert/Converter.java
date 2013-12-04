/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Converter.java'                                          *
 * written by: Rudi Stouffs                                  *
 * last modified: 24.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.convert;

import cassis.Element;
import cassis.ind.Individual;
import cassis.form.Form;
import cassis.sort.*;
import java.util.Stack;

public class Converter {

    // methods
    
    private static Element convert(Match match, Element element) {
        if (element instanceof Individual)
            return convert(match, (Individual) element);
        return convert(match, (Form) element);
    }
    private static Element convert(Match match, Individual ind) throws IllegalArgumentException {
        //System.out.println(match);
        int operation = match.operation();
        if (match.isConcordant() && ((operation == Match.noOp) || (operation == Match.namingOp))) {
            Sort sort = match.rhs();
            if (sort == null) sort = ind.ofSort();
	    Individual result = ind.duplicate(sort);
	    if (sort instanceof AttributeSort) {
		if (ind.attrDefined()) {
                    Match submatch = Matches.lookup(((AttributeSort) ind.ofSort()).weight(), ((AttributeSort) sort).weight());
                    if (submatch == null) // submatch.isIdentical()
                        result.setAttribute((Form) ind.attribute().duplicate());
                    else result.setAttribute((Form) convert(submatch, ind.attribute()));
                } else result.setAttribute(((AttributeSort) sort).weight().newForm());
	    }
	    return result;
        } else if (match.isConcordant() && (operation == Match.rearrangementOp)) {
            Sort sort = match.getPart(0).lhs();
            Form thisForm = sort.newForm();
            for (int n = match.affectCount(); n > 0; n--) sort = ((AttributeSort) sort).weight();
            Form otherForm = sort.newForm();
            Individual result = ind.duplicate(otherForm.ofSort());
            rearrange(ind.attribute(), match.concernCount() - 1, result.attribute(), match.affectCount(), thisForm, result);
            return (Form) convert(match.getPart(0), thisForm);
        }
        return null;
    }
    private static void rearrange(Form form, int front, Form frontForm, int back, Form backForm, Individual frontInd) {
        if (front > 0) {
            form.toBegin();
            while(!form.beyond()) {
                Individual current = (Individual) form.current();
                Individual ind = current.duplicate(frontForm.ofSort());
                frontForm.add(ind);
                rearrange(current.attribute(), front - 1, ind.attribute(), back, backForm, frontInd);
                frontForm.purge();
                form.toNext();
            }
        } else if (back > 0) {
            form.toBegin();
            while(!form.beyond()) {
                Individual current = (Individual) form.current();
                Individual ind = current.duplicate(backForm.ofSort());
                if (back == 1) {
                    if (current.attrDefined()) frontForm.duplicate(current.attribute());
                    ind.addAttribute(frontInd.convert(frontInd.ofSort()));
                    if (current.attrDefined()) frontForm.purge();
                } else rearrange(current.attribute(), 0, frontForm, back - 1, ind.attribute(), frontInd);
                backForm.add(ind);
                form.toNext();
            }
        }
    }
    private static Element convert(Match match, Form form) {
        //System.out.println(match);
        Form result = null;
        int operation = match.operation();
        if (match.isIdentical()) {
            result = (Form) form.duplicate();
        } else if (match.isConcordant() && ((operation == Match.noOp) || (operation == Match.namingOp))) {
            result = match.rhs().newForm();
            form.toBegin();
            while (!form.beyond()) {
                result.add(convert(match, form.current()));
                form.toNext();
            }
        } else if (match.isConcordant() && (operation == Match.rearrangementOp)) {
            Sort sort = match.getPart(0).lhs();
            Form thisForm = sort.newForm();
            for (int n = match.affectCount(); n > 0; n--) sort = ((AttributeSort) sort).weight();
            Form otherForm = sort.newForm();
            form.toBegin();
            while(!form.beyond()) {
                Individual current = (Individual) form.current();
                Individual ind = current.duplicate(otherForm.ofSort());
                rearrange(current.attribute(), match.concernCount() - 1, ind.attribute(), match.affectCount(), thisForm, ind);
                form.toNext();
            }
            result = (Form) convert(match.getPart(0), thisForm);
        }
        //result.maximalize();
        return result;
    }

    /**
     * <b>Converts</b> an element to the specified target sort.
     * @param element an {@link cassis.Element} object
     */
    public static Element convert(Sort target, Element element) {
        //System.out.println(element.ofSort().match(target));
        return convert(element.ofSort().match(target), element);
    }
}
