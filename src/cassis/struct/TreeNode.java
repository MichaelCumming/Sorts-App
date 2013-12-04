/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `TreeNode.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 24.9.04                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.struct;

final public class TreeNode {

    // representation
    Splayable content;
    TreeNode parent, left, right, pred, succ;

    // constructors
    TreeNode(Splayable content) {
        this(content, null, null, null);
    }
    TreeNode(Splayable content, TreeNode parent, TreeNode pred, TreeNode succ){
        this.content = content;
        this.parent = parent;
        this.left = null;
        this.right = null;
        this.pred = pred;
        this.succ = succ;
    }
    
    // methods
    
    public Splayable content() {
        return this.content;
    }
    public void setContent(Splayable content) {
        this.content = content;
    }
    public TreeNode predecessor() {
        return this.pred;
    }
    public TreeNode successor() {
        return this.succ;
    }
}

