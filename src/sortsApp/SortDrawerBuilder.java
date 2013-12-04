/*
 * SortBuilder.java
 *
 * Created on June 3, 2004, 2:07 PM
 */

package sortsApp;

import cassis.*;
import cassis.form.*;
import cassis.ind.*;
import cassis.parse.*;
import cassis.parse.ParseException;
import cassis.sort.*;
import cassis.struct.*;
import cassis.visit.*;
import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGException;
import com.touchgraph.graphlayout.TGPanel;
import com.touchgraph.graphlayout.sNode.*;
import java.awt.Color;
import java.awt.List;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import javax.swing.tree.DefaultMutableTreeNode;
import sortsApp.SortsTerms.SORT_TYPE;

/**
 *
 * @author  cumming
 */
public class SortDrawerBuilder {
    
    final static SortsTerms terms = new SortsTerms();
    
    /** Creates a new instance of SortBuilder */
    public SortDrawerBuilder() {
    }
    
    /** Recursive method: TouchGraph Nodes -> SortsMC makes sorts from a touchgraph root in postorder = root node last
     * = top half of .sdl adds them to the profile:User of the TGPanel
     *  @since 26.Aug.2003 */
    public void defineSortTreePostOrder(TGPanel tgPanel, sNode node, int depth, HashSet pEdges)
    throws ParseException {
        User profile = tgPanel.getProfile();
        depth++; //starts at 1, indicating root
        
        if (pEdges == null) pEdges = new HashSet();
        if (node != null) {
            /**use only outgoing edges */
            for(Edge e : node.getOutgoingEdgesCollection() ) {
                if (e.isPLabelled()) { //an edge containing a aspects sort def
                    pEdges.add(e); //add it to the edges set (duplicates eliminated)
                } else if (!e.isPropertyEdge()) { //not a property edge, recursively traverse the tree
                    defineSortTreePostOrder(tgPanel, (sNode)e.getTo(), depth, pEdges);
                }
            }
            Sort newSort = null;
            /**Where the actual definition of sorts of the rootN takes place */
            if (depth == 1) { //either top sort or property sort -- right at the end
                String topFormName = node.getLabel();
                newSort = tgPanel.getSorts().define(node.getSortDef()); //do the top root
                /**finally, define all the property edges linking existing sorts. After top sort */
                definePropertySortsList(tgPanel, pEdges);
            } else if (depth > 1) { //at the beginning = deep in a tree -- due to the recursion
                newSort = tgPanel.getSorts().define(node.getSortDef());
            }
            defineForm(newSort, node, profile, tgPanel);
        }
    }
    
    /**@since 21.July 2005 */
    public void defineForm(Sort sort, sNode node, User profile, TGPanel tgPanel)
    throws ParseException {
        Form newForm = null;
        if(sort != null) {
            newForm = sort.newForm();
        }
        /**If primitive, data-holding node */
        if(!node.getSortType().allowsChildren() && sort != null) {
            newForm = defineIndividualsForPrimNode(sort, newForm, node, profile);
        } else {
            /**Else, a composite (non-primitive) node */
            newForm = defineComposite(sort, newForm, node, profile, tgPanel);
        }
        /**Deposit the form, whether a primitive or a composite node */
        if(newForm != null) {
            /**If prim node then node label, else a bracketed def of its children */
            String formName = node.getFormName();
            profile.deposit(formName, newForm);
            printExistingForm(formName, profile);
        }
    }
    
    /**@since 21.July 2005 */
    public Form defineComposite(Sort sort, Form form, sNode node, User profile, TGPanel tgPanel) {
        if(sort instanceof DisjunctiveSort) {
            java.util.List <String> childNames = node.getChildrenNames();
            /**Add all the sum elements */
            for(String sortName : childNames) {
                Element elem = profile.retrieve(sortName);
                form.add(elem);
            }
        }
        /**Can this be done automatically without a table? */
        else if(sort instanceof AttributeSort) {
            //TGPanel tgpEdit = getCurrentGraphLayoutPage().getEditPanel().getTGPanel();
            //sNode selNode = (sNode)tgPanel.getSelect();
            node.makeAttDataTable(node, profile, form);
        } else {
            return null;
        }
        return form;
    }
    
    /**Makes individuals from string data contained in an existing sNode
     * @since 24.May 2005 */
    public Form defineIndividualsForPrimNode(Sort sort, Form form, sNode node, User profile)
    throws ParseException {
        for(String s : node.getDataStrings()) {
            Individual ind = defineOneIndividual(node.getSortType(), s, sort, profile);
            if(ind != null) {
                //System.out.println("newInd: " + ind.toString());
                //System.out.println("New individual of sort: " + ind.ofSort().toString());
                form.add(ind);
            }
        }
        return form;
    }
    
    /**@since 9.June 2005 */
    public void printExistingForm(String formName, User profile) {
        //System.out.println("Form name: " + formName);
        Element data = profile.retrieve(formName);
        if(data == null) {
            System.out.println("form: " + formName + " cannot be found");
            return;
        }
        /**get the sorts from the profile */
        Sorts sorts = profile.sorts();
        SdlVisitor sdlV = new SdlVisitor();
        data.ofSort().accept(sdlV);
        sdlV.defineVariable(formName, data);
        System.out.println(sdlV.functionsToString());
        System.out.println(sdlV.toString());
    }
    
    /**Includes a lot of trial and error-derived information.
     * @since 24.May 2005 */
    public Individual defineOneIndividual(SORT_TYPE sortType, String def, Sort sort, User profile)
    throws ParseException {
        Individual newInd = null;
        //System.out.println("About to define an individual for sort: " + sort.toString());
        switch(sortType) {
            case LABEL://ok e.g. "wall"
                newInd = new Label(sort, def);
                break;
            case WEIGHT://ok e.g. "0.75"
                newInd = new Weight(sort, def);
                break;
            case POINT://ok e.g. "((30,20,0)" //why two brackets are required is not clear
                Point point = new Point();
                point.setSort(sort);
                point.parse(new ParseReader(new StringReader(def)));
                newInd = point;
                break;
            case LINESEGMENT://ok e.g. "<<(10,10,0), (50,10,0)>" //why two square brackets are required is not clear
                LineSegment ls = new LineSegment();
                ls.setSort(sort);
                ls.parse(new ParseReader(new StringReader(def)));
                newInd = ls;
                break;
            case FUNCTION: // not yet tested...
                Function f = new Function(sort, null, null, null);
                //f.setSort(sort);
                f.parse(new ParseReader(new StringReader(def)));
                newInd = f;
                break;
            case DATE://probably ok
                /**Assumes def is a string rep of a long value - e.g. millseconds */
                newInd = new Date(sort, new Long(def));
                break;
            case NUMERIC://probably ok
                newInd = new Numeric(sort, new Double(def));
                break;
                
            case PROPERTY:
                
            default:
//                newInd = null;
//                break;
        }
        return newInd;
    }
    
    /**@since 24.May 2005 */
    public void parseForm(sNode node, User profile, TGPanel tgPanel) {
        //System.out.println("Attempting to parse form def string (see 'sortBuilder', line 83)");
        if(!node.dataExists()) {
            tgPanel.getTopFrame().printMessage("No data in node: " + node.getLabel());
            return;
        }
        try {
            /**only if data is defined in the node */
            /**first, enter the sort definition */
            profile.parse(node.getSortDefString()); //this works
            /**next, enter the data */
            profile.parse(node.getFormDefString()); //unclear how to make data strings for disjunctive sorts
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * SortsMC -> TouchGraph Nodes. Makes nodes in a touchgraph TGPanel from a sort root.
     * Works top-down [pre-order = node first, then its children].
     * @author Michael Cumming
     * @since 28.May.2003 */
    public void drawSortGraphPreOrder(Sort rootS, TGPanel tgPanel, sNode parentN, String edgeLabel) {
        if ((rootS instanceof PrimitiveSort) && !(rootS instanceof AspectsSort)) { //AspectMC is not a subclass
            /**Aspects are handled where this method is first called: currently GraphLayoutPage */
            drawPrimitiveSort((PrimitiveSort)rootS, tgPanel, parentN, edgeLabel); //when first used, parentN is null
        } else if (rootS instanceof AttributeSort) {
            drawAttributeSort((AttributeSort)rootS, tgPanel, parentN, edgeLabel);
        } else if (rootS instanceof DisjunctiveSort) {
            drawDisjunctiveSort((DisjunctiveSort)rootS, tgPanel, parentN, edgeLabel);
        } else {
            System.out.println("Sort type not yet handled");
        }// remaining types needed...
        tgPanel.resetDamper();
    }
    
    /**
     * SortsMC -> TouchGraph Nodes Draws a sort as a Node. Draws an edge from a node to its parent (if applicable).
     * @author Michael Cumming
     * @since 28.May.2003
     * Non-recursive since a primitive sort */
    public void drawPrimitiveSort(PrimitiveSort rootS, TGPanel tgPanel, sNode parentN, String edgeLabel) {
        if (rootS != null) {
            /**Assume only used on primitive sorts; see usage in Node.drawSortGraphPreOrder */
            try { //draw the rootS node
                String primSuffix = rootS.definition();
                SORT_TYPE primType = terms.getTypeFromSuffix(primSuffix);
                //Node rN = tgPanel.addNodeSorts(terms.getSortTypeUsingSort(rootS), rootS.toString(), rootS.definition());
                sNode rootN = tgPanel.addNodeSorts(terms.getSortTypeUsingSort(rootS), rootS.toString());
                //rN.setSortTypeAllAttributesUsingType(primType);
                //rN.setAttributeWeight(rootS.isAttributeWeight());
                //System.out.println("TEST prim rootS def: " + rootS.definition());
                if (parentN != null) { //null when initially used on tree root
                    /**if there is a parentN, draw an edge to it */
                    Edge e = tgPanel.addEdge(parentN, rootN, terms.EDGE_DEFAULT_LENGTH, edgeLabel); //draw edge from parentN to root
                }
                /**select first node to be drawn */
                else {
                    tgPanel.setSelect(rootN);
                }
                //no children to recurse on, since PrimitiveSorts are always leaves
            } catch (TGException tge) {
                System.err.println(tge.getMessage());
                tge.printStackTrace(System.err);
            }
        }
    }
    
    /**
     * SortsMC -> TouchGraph Nodes Draws a sort as a Node. Draws an edge from a node to its parentN (if applicable).
     * Recurses on a sort's components.
     * @author Michael Cumming
     * @since 28.May.2003 */
    public void drawAttributeSort(AttributeSort rootSort, TGPanel tgPanel, Node parentN, String edgeLabel) {
        if (rootSort != null) {
            Sort baseSort = rootSort.base();
            Sort weightSort = rootSort.weight();
            try {
                /**draw the root node */
                sNode rootN = tgPanel.addNodeSorts(terms.getSortTypeUsingSort(rootSort), rootSort.toString());
                /**only null when initially used on tree root */
                if (parentN != null) {
                    /**draw edge from parentN to root */
                    Edge e = tgPanel.addEdge(parentN, rootN, terms.EDGE_DEFAULT_LENGTH, edgeLabel);
                }
                /**select first node to be drawn */
                else {
                    tgPanel.setSelect(rootN);
                }
                /**recurse on the two children: send back to the top */
                drawSortGraphPreOrder(baseSort, tgPanel, rootN, terms.EDGE_ATTRIBUTE_BASE);
                drawSortGraphPreOrder(weightSort, tgPanel, rootN, terms.EDGE_ATTRIBUTE_WEIGHT);
            } catch (TGException tge) {
                System.err.println(tge.getMessage());
                tge.printStackTrace(System.err);
            }
        }
    }
    
    /**
     * SortsMC -> TouchGraph Nodes Draws a sort as a TouchGraph Node. Draws an edge from a node to its parentN (if applicable).
     * Recurses on a sort's components.
     * @author Michael Cumming
     * @since 28.May.2003 */
    public void drawDisjunctiveSort(DisjunctiveSort rootS, TGPanel tgPanel, Node parentN, String edgeLabel) {
        if (rootS != null) {
            try {
                /**draw the root node */
                sNode rootN = tgPanel.addNodeSorts(terms.getSortTypeUsingSort(rootS), rootS.toString());
                //rN.setSortTypeAllAttributesUsingType(terms.NODE_SORT_SUM_TYPE);
                //rN.setAttributeWeight(rootS.isAttributeWeight());
                if (parentN != null) { //only null when initially used on tree root
                    Edge e = tgPanel.addEdge(parentN, rootN, terms.EDGE_DEFAULT_LENGTH, edgeLabel); //draw edge from parentN to root
                }
                /**select first node to be drawn */
                else {
                    tgPanel.setSelect(rootN);
                }
                /**getComponents() returns a normal Java list.
                 * @since 20.July 2005 */
                for(Sort s : rootS.getComponents()) {
                    drawSortGraphPreOrder(s, tgPanel, rootN, "");
                }
            } catch (TGException tge) {
                System.err.println(tge.getMessage());
                tge.printStackTrace(System.err);
            }
        }
    }
    
    /**@since 27.Aug.2003
     * Defines sequentially a list of Property sort definitions */
    public void definePropertySortsList(TGPanel tgPanel, HashSet pEdges) { //HashSet assures no duplicates
        if (pEdges != null && pEdges.size() > 0) {
            for (Iterator i = pEdges.iterator(); i.hasNext(); ) {
                Edge currentE = (Edge)i.next();
                if (currentE != null && currentE.isPLabelled()) { //only PLabelled edges should make it into pEdges
                    Sort s = tgPanel.getSorts().define(currentE.getSortDefProp());//eg.(A_has_B, B_has_A) :[Property] (A, B)
                    System.out.println("976.Real Psort after def: " + s.toString());
                }
            }
        }
    }
    
    /**@since 29.Aug.2003 MC */
    public void drawAllAspects(SortsMC allS, TGPanel tgPanel) {
        HashSet allA = allS.getAllAspects(); //this method eliminates duplicate aspects
        for (Iterator i = allA.iterator(); i.hasNext(); ) {
            AspectMC curA = (AspectMC)i.next();
            drawOneAspect(curA, tgPanel); //eg. A_has_B (!= B_has_A)
        }
        //redraw the screen
        tgPanel.resetDamper();
    }
    
    /** @since 29.Aug.2003
     * not all possible attributes are set for pEdges here...probably doesn't matter for now...
     * one aspect = one edge between two nodes
     * [Property] (a, b) other arc has same first and second parts, therefore draw both arcs at once */
    public void drawOneAspect(AspectMC aS, TGPanel tgPanel) {
        String srcNS = aS.getFirstPartDef(); //eg. p1
        //System.out.println("First part: " + srcNS);
        String destNS = aS.getSecondPartDef(); //eg. p2
        //System.out.println("Second part: " + destNS); first place and second part can be the same
        String edgeS = aS.toString(); //eg. p1_has_p2
        //
        Collection srcC = tgPanel.findNodesByLabel(srcNS); //should always retrieve only one node
        Collection destC = tgPanel.findNodesByLabel(destNS); //substring matching might fail
        if (srcC.size() != 1) System.out.println("srcC is not == 1");
        if (destC.size() != 1) System.out.println("destC is not == 1");
        //need actual nodes to draw to
        Node srcN = (Node)srcC.iterator().next();
        Node destN = (Node)destC.iterator().next();
        //draw edges with this information
        Edge src2destE = tgPanel.addEdge(srcN, destN, terms.EDGE_DEFAULT_LENGTH);
        Edge dest2srcE = tgPanel.addEdge(destN, srcN, terms.EDGE_DEFAULT_LENGTH);
        src2destE.setIsPropertyEdge(true);
        dest2srcE.setIsPropertyEdge(true);
        src2destE.setPropLabel_StoD(edgeS); //DtoS could also be set
        src2destE.setPLabelled(true); //this means 'get prop info here'. dest2srcE remains false
        //src2destE.setColor(terms.EDGE_SORT_PROPERTY_COLOR);
        //dest2srcE.setColor(Color.white);
    }
    
    
    
    /**
     * Pick a root node to make a TreeModel. Not used at the moment...
     * @author Michael Cumming
     * @since 21.Mar.2003 */
    public void makeTreePreOrder(DefaultMutableTreeNode tree, Node root) {
        if (root != null) {
            tree.insert(root, 0); //first insert the root, then deal with the children
            for (int i = 0; i < root.getChildCount(); i++) {
                makeTreePreOrder(tree, (Node)root.getChildAt(i));
            }
        }
    }
    
    /**
     * Prints out a touchgraph from a root in preorder. sim. to bottom half of .sdl
     * @author Michael Cumming
     * @since 11.Apr.2003 */
//this doesn't work with property edges...
    public static String getSortDefTreePreOrder(sNode root) {
        if (root != null) {
            String s = new String(root.getSortDef() + "\n");
            for (int i = 0; i < root.getChildCount(); i++) {
                s += getSortDefTreePreOrder((sNode)root.getChildAt(i));
            }
            return s;
        }
        return null;
    }
    
    
    /**
     * TouchGraph Nodes -> SortsMC makes sorts from a touchgraph root in postorder = root node last
     * = top half of .sdl adds them to the profile:User of the TGPanel
     * @author Michael Cumming
     * @since 20.May.2003
     */
//    public static void defineSortTreePostOrderOld(TGPanel tgPanel, Node root) { //recursive method
//        if (root != null) {
//            //first do the children, hence the name 'PostOrder'
//            for (int i = 0; i < root.getChildCount(); i++) {
//                defineSortTreePostOrderOld(tgPanel, (Node)root.getChildAt(i));
//            }
//            //where the actual sorts defining of the root takes place
//            Sort s = tgPanel.getSorts().define(root.getSortDef()); //sort returned is not used...
//            //first attribute distinguishes the base part of an attribute sort...
//            if (s != null) {
//                s.setSecondAttribute(root.isSecondAttribute());
//                System.out.println(root.getSortDef() + " defined: 2ndAtt = " + s.isSecondAttribute());
//            }
//        }
//    }
    
}
