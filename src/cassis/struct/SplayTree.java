/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `SplayTree.java'                                          *
 * written by: Rudi Stouffs                                  *
 * last modified: 10.10.04                                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.struct;

import cassis.Thing;

public class SplayTree {

    // representation
    private TreeNode root;
    private int size;

    // constructor

    public SplayTree() {
        this.root = null;
        this.size = 0;
    }

    // methods

    /**
     * Returns the <b>size</b> of the tree
     */
    public int size(){
        return this.size;
    }
  
    /**
     * Returns the <b>root</b> node of the tree
     */
    public TreeNode root(){
        return this.root;
    }
  
    /* Returns true if the tree is empty, false otherwise */
    public boolean empty(){
        return (this.root == null);
    }

    /* Empties the tree */
    public void purge(){
      this.root = null;
      this.size = 0;
    }

    /**
     * <b>Rotates</b> the edge joining node y to its right child.
     */
    private static void RotateLeft(TreeNode y) {
        TreeNode x = y.right, z = y.parent;

        if (z != null) {
            if (y == z.left)
                z.left = x;
            else z.right = x;
        }
        y.right = x.left;
        x.left = y;
        x.parent = z;
        y.parent = x;
        if (y.right != null)
            y.right.parent = y;
    }

    /**
     * <b>Rotates</b> the edge joining node y to its left child.
     */
    private static void RotateRight(TreeNode y) {
        TreeNode x = y.left, z = y.parent;

        if (z != null) {
            if (y == z.left)
                z.left = x;
            else z.right = x;
        }
        y.left = x.right;
        x.right = y;
        x.parent = z;
        y.parent = x;
        if (y.left != null)
            y.left.parent = y;
    }

    /**
     * <b>Splays</b> (moves) the node x to the root using pairwise rotations.
     */
    private void splay(TreeNode x) {
        while (x.parent != null) {
            if (x == x.parent.left) {
                if (x.parent.parent == null) {
                    /* terminating single rotation "zig" */
                    this.RotateRight(x.parent);
                } else if (x.parent == x.parent.parent.left) {
                    /* two single rotations "zig-zig" */
                    this.RotateRight(x.parent.parent);
                    this.RotateRight(x.parent);
                } else {
                    /* double rotation "zig-zag" */
                    this.RotateRight(x.parent);
                    this.RotateLeft(x.parent);
                }
            } else {
                /* symmetric cases */
                if (x.parent.parent == null) {
                    this.RotateLeft(x.parent);
                } else if (x.parent == x.parent.parent.right) {
                    this.RotateLeft(x.parent.parent);
                    this.RotateLeft(x.parent);
                } else {
                    this.RotateLeft(x.parent);
                    this.RotateRight(x.parent);
                }
            }
        }
    }
	

    /**
     * <b>Inserts</b> an object in the tree, creating a node for it.
     * @param object a splayable object
     */
    public void insert(Splayable object) {
        TreeNode node;
        int side;

        if (this.empty()) {
            this.root = new TreeNode(object, null, null, null);
            this.size = 1;
        }
        for (node = this.root; ; ) {
            side = object.splayCompare(node.content);
            if ((side < 0) && (node.left != null))
                node = node.left;
            else if ((side >= 0) && (node.right != null))
                node = node.right;
            else if (side < 0) {
                this.root = node.left = new TreeNode(object, node, node.pred, node);
                if (node.pred != null) node.pred.succ = node.left;
                node.pred = node.left;
                break;
            } else {
                this.root = node.right = new TreeNode(object, node, node, node.succ);
                if (node.succ != null) node.succ.pred = node.right;
                node.succ = node.right;
                break;
            }
        }
        this.size++;
        splay(this.root);
    }

    /**
     * <b>Finds</b> the left-most node in the tree of which the content is closed
     * to the specified object
     * @param object a splayable object
     * @return a {@link TreeNode} object
     */
    public TreeNode find(Splayable object) {
        TreeNode node;
        int side;

        if (this.empty()) return null;
        for (node = this.root; ; ) {
            side = object.splayCompare(node.content);
            if ((side < 0) && (node.left != null))
                node = node.left;
            else if ((side > 0) && (node.right != null))
                node = node.right;
            else if ((side == 0) && (node.pred != null) && (object.compare(node.pred.content) == 0))
                node = node.pred;
            else {
                this.root = node;
                break;
            }
        }
        this.splay(this.root);
        return this.root;
    }

    /**
     * <b>Finds</b> the <b>minimal</b> (left-most) node from the tree and
     * returns its content.
     * @return a {@link Splayable} object
     */
    public Splayable findMin() {
        TreeNode node;
      
        if (this.empty()) return null;
        for (node = this.root; node.left != null; node = node.left);
            return node.content;
    }

    /**
     * <b>Extracts</b> a node from the tree and returns its content.
     * First, the node is splayed, then it is replaced by the join of its left
     * and right subtrees
     * @return a {@link Splayable} object
     */
    public Splayable extract(TreeNode node) {
        TreeNode lt, rt;

        this.splay(node);
        /* disconnect node and left subtree */
        lt = node.pred;	/* right most node in left tree */
        if (lt != null) {
            node.left.parent = null;
            this.splay(lt);
            lt.succ = null;
        }
        /* disconnect node and right subtree */
        rt = node.succ;	/* left most node in right tree */
        if (rt != null) {
            node.right.parent = null;
            /* make lt the left child of rt */
            rt.pred = rt.left = lt;
            if (lt != null) { lt.succ = lt.parent = rt; }
            this.root = node.right;
        } else this.root = lt;
        this.size--;
        return node.content;
    }

    /**
     * <b>Extracts</b> the <b>minimal</b> (left-most) node from the tree and
     * returns its content.
     * @return a {@link Splayable} object
     */
    public Splayable extractMin() {
        TreeNode node;

        if (this.empty()) return null;
        for (node = this.root; node.left != null; node = node.left);
        return this.extract(node);
    }

    /**
     * <b>Deletes</b> the node containing the specified object.
     * @param object a splayable object
     * @return <tt>true</tt> if successful; <tt>false</tt> otherwise.
     */
    boolean delete(Splayable object) {
        TreeNode node;
        int side;

        if (this.empty()) return false;
        for (node = this.root; ; ) {
            if (node.content == object) break;
            side = object.compare(node.content);
            if ((side < 0) && (node.left != null))
                node = node.left;
            else if ((side > 0) && (node.right != null))
                node = node.right;
            else return false;
        }
        this.extract(node);
        return true;
    }

    /* print the tree in sorted order */
    public void print() {
        this.printTree(this.root);
    }
    private void printTree(TreeNode node) {
        if (node != null) {
            printTree(node.left);
            System.out.println(node.content.toString());
            printTree(node.right);
        }
    }

    /**
     * <b>Initializes the tree with sentinels<b>.
     * Creates and links the nodes for both sentinels, bypassing the insert
     * procedure. The tree must be empty
     * @param tail
     * @param head
     */
    public void initSentinelTree(Splayable tail, Splayable head) {
        this.root = new TreeNode(tail, null, null, null);
        this.root.succ = this.root.right = new TreeNode(head, this.root, this.root, null);
        this.size = 2;
    }
    
    private void swap(TreeNode node) {
        TreeNode temp = node.left;
        node.left = node.right;
        node.right = temp;
        if (node.left != null) swap(node.left);
        if (node.right != null) swap(node.right);
    }
    
    /**
     * <b>Reverses</b> the subset of the tree between x and y, Initially,
     * x must be a predecessor of y.
     */
    public void reverse(TreeNode x, TreeNode y) {
        this.root = y;
        this.splay(this.root);
        if (x == y) return;
        
        // disconnect x and predecessor
        TreeNode left = x.pred; // right most node in left tree
        if (left != null) {
            left.succ = null;
            x.left.parent = null;
            splay(left);
            x.pred = x.left = null;
        }
        // disconnect y and successor
        TreeNode right = y.succ; // left most node in right tree
        if (right != null) {
            right.pred = null;
            y.right.parent = null;
            splay(right);
            y.succ = y.right = null;
        }
        // reverse pred and succ links between x and y
        for (TreeNode node = x; node != null; node = node.pred) {
            TreeNode temp = node.pred;
            node.pred = node.succ;
            node.succ = temp;
        }
        // reverse left and right links between x and y recursively,
        // starting at y, which is the current root
        swap(y);
        // make left the left child of y
        y.pred = y.left = left;
        if (left != null) left.succ = left.parent = y;
        if (right != null) right.pred = right.parent = x;
    }
}
