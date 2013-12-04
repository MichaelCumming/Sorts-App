/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `GraphicsVisitor.java'                                    *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.visit;

import cassis.struct.Vector;
import cassis.form.Form;
import cassis.form.MetaForm;
import cassis.ind.*;
import cassis.sort.*;
import cassis.UnresolvedReferenceException;

public abstract class GraphicsVisitor extends ElementVisitor implements SortVisitor {

    protected abstract void beginGroup(Class characteristic, int count);

    protected abstract void endGroup();

    protected abstract void link(String url, String label, String image);

    protected abstract void satellite(String s);

    protected abstract void jump(String reference);    

    protected abstract void label(String s);

    protected abstract void register(String reference);

    protected abstract void point(Vector position);

    protected abstract void lineSegment(Vector tail, Vector head);
    
    public abstract void clear();

    public boolean visitEnter(Sort sort) {
        if (sort instanceof SimpleSort) {
	    this.beginGroup(AttributeSort.class, 1);
	    this.label(sort.toString());
        } else if (sort instanceof AttributeSort) {
	    //this.satellite(sort.toString()); // if parent is DisjunctiveSort
            this.beginGroup(AttributeSort.class, 2);
            this.label(sort.base().toString());
            this.label(((AttributeSort) sort).weight().toString());
        } else if (sort instanceof DisjunctiveSort)
            this.beginGroup(DisjunctiveSort.class, ((DisjunctiveSort) sort).size());
        return true;
    }

    public void visitLeave(Sort sort) {
        this.endGroup();
    }
    
    public boolean visitEnter(Individual ind, Individual assoc) {
        if (ind.nil())
            this.label(Individual.NIL);
        else if (ind instanceof Label)
            this.label(((Label) ind).label());
        else if (ind instanceof Key)
            this.label(((Key) ind).getKey());
        else if (ind instanceof Point)
            this.point(((Point) ind).position());
        else if (ind instanceof Property) {
            if (assoc == null)
                this.label(ind.toString(assoc));
            else try {
                this.jump(((Property) ind).getAssociate(assoc).getReference());
            } catch (UnresolvedReferenceException e) {
                this.label(((Property) ind).unresolved().getKey());
            }
        } else if (ind instanceof Sign) {
            if (((Sign) ind).value() == Sign.POSITIVE)
                this.label("+");
            else if (((Sign) ind).value() == Sign.NEGATIVE)
                this.label("-");
            else this.label(Individual.NIL);
        } else if (ind instanceof Url)
            this.link(((Url) ind).address(), ((Url) ind).address(), null);
        else if (ind instanceof ImageUrl)
            this.link(((ImageUrl) ind).address(), ((ImageUrl) ind).address(), ((ImageUrl) ind).icon());
        else
            this.label(ind.toString(assoc));
	if (ind.isReferenced()) this.register(ind.getReference());
        return true;
    }
    
    public void visitLeave(Individual ind) {}
    
    public boolean visitEnter(Form form) {
        if (form.nil()) return false;
        if (form instanceof MetaForm)
            this.beginGroup(cassis.sort.DisjunctiveSort.class, form.size());
        else {
	    this.satellite(form.ofSort().toString());
            this.beginGroup(form.first().getClass(), form.size());
        }
        return true;
    }

    public void visitLeave(Form form) {
        if (form.nil()) return;
        this.endGroup();
    }
}
