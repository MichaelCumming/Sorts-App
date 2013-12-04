/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Function.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.proc;

import cassis.form.Form;

public abstract class Function {
    // representation
    private String title;

    // constructors
    Function() {
        super();
        this.title = null;
    }

    Function(String title) {
        super();
        this.title = title;
    }

    // methods
    public abstract Form apply(Form form);

    public String getTitle() { return this.title; }
}
