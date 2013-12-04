/*
 * BusinessLogic.java
 *
 * Created on June 3, 2004, 3:34 PM
 */

package sortsApp;

import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.sNode.sNode;
import java.util.Collection;
import java.util.Iterator;
import java.io.Serializable;
import sortsApp.SortsTerms.SORT_TYPE;
/**
 *
 * @author  cumming
 */
public class BusinessLogic implements Serializable {
    //private GraphLayoutTopFrame topFrame;
    final static SortsTerms terms = new SortsTerms();
    
    /** Creates a new instance of BusinessLogic */
    public BusinessLogic() {
        //this.topFrame = topFrame;
    }
    
    /**
     * "Business logic" for adding new nodes
     * @since 18.Mar.2003
     * @author Michael Cumming
     * @return "ok", or an error message.
     */
    public String getParentChildMessage(sNode parent, SORT_TYPE childType) {
        //
        System.out.println("Parent child count: " + parent.getChildCount());
        SORT_TYPE parentType = parent.getSortType();
//        int parentType = -1; //parent.getSortType(); //one of:Pri/Att/Sum
//        //first possible error conditions addressed...
        if(parentType == null || childType == null) {
            return "Parent or child type not set";
        } else if (!parentType.allowsChildren()) {
            return "Primitive sorts cannot have children";     
        } else if ((parentType == SORT_TYPE.ATTRIBUTE || parentType == SORT_TYPE.ATTRIBUTE_ANON) &&
                (parent.getChildCount() >= 2)) {
            return "Attribute sorts can have a maximum of 2 children";
        } else if (parent.getChildCount() >= parent.getSortType().getMaxChildren()) {
            return "No more children can be added to parent node";
        }  
        //add additional constraints here
        return "ok"; //no errors found
    }
    
    /**Tests for attribute nodes--------------*/
    public boolean nodeIsAttributeType(Node node) {
        boolean result = true; //node.getSortType()==terms.NODE_SORT_ATTRIBUTE_TYPE;
        printBoolTest("nodeIsAttributeType", result, node.getLabel());
        return result;
    }
    public boolean nodeHasTwoChildren(Node node) {
        boolean result = node.getOutgoingEdgesCollection().size()==2;
        printBoolTest("nodeHasTwoChildren", result, node.getLabel());
        return result;
    }
    public boolean nodeIsNotDefinedType(Node node) {
        boolean result = true; //node.getSortType()==terms.SORT_NOT_DEFINED_TYPE;
        printBoolTest("nodeIsNotDefinedType", result, node.getLabel());
        return result;
    }
    public boolean nodeHasOnlyDefinedChildren(Node node) {
        Collection<Node> children = node.getChildrenCollection();
        if(children.size()<1)
            return false;
        for (Node curNode : children) {
            //for(Iterator i = children.iterator(); i.hasNext(); ) {
            //Node curNode = (Node)i.next();
            if (nodeIsNotDefinedType(curNode)) {
                return false;
            }
        }
        return true;
    }
    
//    public boolean canTurnAttributeWeightOn(Node node) {
//        /**Node must not be NOT_DEFINED type */
//        boolean result = (
//        /**node's attribute weight must be turned off */
//        !node.isAttributeWeight() &&
//        /**node cannot be NOT_DEFINED type */
//        node.getSortType() != terms.SORT_NOT_DEFINED_TYPE);
//
//        printBoolTest("canTurnAttributeWeightOn", result, node.getLabel());
//        return result;
//    }
    
    /**OK node has ONE secondAttEdge, and ONE non_secondAttEdge */
    //    public boolean nodeHasTwoGoodAttributeEdges(Node node) {
    //        Vector childEdges = node.getOutgoingEdgesCollection();
    //        /**Edges in which isSecondAttribute==true */
    //        Vector baseEdges = new Vector();
    //        /**Edges in which isSecondAttribute==false */
    //        Vector non_baseEdges = new Vector();
    //        for(Iterator i = childEdges.iterator(); i.hasNext(); ) {
    //            Edge curEdge = (Edge)i.next();
    //            if(curEdge.isBaseEdge()) {
    //                baseEdges.add(curEdge);
    //            }
    //            else {
    //                non_baseEdges.add(curEdge);
    //            }
    //        }
    //        boolean result = baseEdges.size()==1 && non_baseEdges.size()==1;
    //        printBoolTest("nodeHasTwoGoodAttributeEdges", result);
    //        return result;
    //    }
    //OK
    public boolean nodeIsWellFormedAttributeNode(Node node) {
        boolean result = (
                nodeIsAttributeType(node) &&
                nodeHasTwoChildren(node) &&
                nodeHasOnlyDefinedChildren(node));
        //nodeHasTwoGoodAttributeEdges(node));
        
        printBoolTest("nodeIsWellFormedAttributeNode", result, node.getLabel());
        return result;
    }
    
    /**Edge methods------------------------*/
    
    public void turnAttributeBaseOnIfPossible(Edge edge) {
        Node parentNode = edge.from;
        Node childNode = edge.to;
        
        /**parentNode must be attribute type */
        if(true) { //parentNode.getSortType() != terms.NODE_SORT_ATTRIBUTE_TYPE) {
            //add ERROR message here
            System.out.println("ERROR: " + parentNode.getLabel() + " not attribute type");
            return;
        }
        /**childNode must NOT be undefined type */
        if(true) { //(childNode.getSortType() == terms.SORT_NOT_DEFINED_TYPE) {
            System.out.println("ERROR: " + childNode.getLabel() + " is not defined type");
            return;
        }
        /**@since Feb 17, 2005 */
        //edge.setAttributeBase(true);
    }
    
    
    
    /**OK Parent node of an edge is Attribute type */
    public boolean parentNodeIsAttributeType(Edge edge) {
        Node parentNode = edge.getFrom();
        return nodeIsAttributeType(parentNode);
    }
    /**OK Parent node of edge has two children (including this edge) */
    public boolean parentNodeHasTwoChildren(Edge edge) {
        Node parentNode = edge.getFrom();
        return nodeHasTwoChildren(parentNode);
    }
    /**OK Other (only) sibling is SecondAttribute type */
    //    public boolean siblingEdgeIsSecondAttribute(Edge edge) {
    //        return edge.getOtherOnlySibling().isBaseEdge();
    //    }
    
    /**OK */
    //    public boolean nodeIsWellFormedAttributeNode(Node node) {
    //        boolean isAttType = node.getSortType()==terms.NODE_SORT_ATTRIBUTE_TYPE;
    //        boolean hasTwoGoodEdges = nodeHasTwoGoodAttributeEdges(node);
    //        printBoolTest("Node is attribute type", isAttType);
    //        printBoolTest("Node has two good attribute edges", hasTwoGoodEdges);
    //
    //        return(isAttType && hasTwoGoodEdges);
    //    }
    
    public void printBoolTest(String message, boolean result, String nodeName) {
        System.out.println(nodeName + ": " + message + " > " + result);
        //(nodeName + ": " + message + " > " + result);
    }
    
}
