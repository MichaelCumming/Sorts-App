/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `SwapTop2.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.proc;

import cassis.form.Form;
import cassis.ind.Individual;

public final class SwapTop2 extends Function {
    // constructors
    public SwapTop2() {
        super();
    }

    public SwapTop2(String title) {
        super(title);
    }

    // methods
    public Form apply(Form form) {
        Form result = null, temp;
        Individual first, second;
        form.toBegin();
        while (!form.beyond()) {
            temp = ((Individual)form.current()).attribute();
            temp.toBegin();
            while (!temp.beyond()) {
                first = (Individual)form.current().duplicate();
                if (((Individual)temp.current()).attrDefined())
                    first.addAttribute(((Individual)temp.current()).attribute());
                second = (Individual)temp.current().duplicate();
                second.addAttribute(first);
                temp.toNext();
                if (result == null) result = second.ofSort().newForm();
                result.add(second);
            }
            temp.purge();
            form.toNext();
        }
        form.purge();
        result.maximalize();
        return result;
    }
}
