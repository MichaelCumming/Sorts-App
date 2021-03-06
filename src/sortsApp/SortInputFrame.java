/*
 * SortInputFrame.java
 *
 * Created on October 31, 2003, 3:08 PM
 */

package sortsApp;


import cassis.parse.ParseException;
import com.touchgraph.graphlayout.sNode.*;
import java.util.Vector;
import javax.swing.JPanel;
import com.touchgraph.graphlayout.Node;
import cassis.sort.Sort;
import cassis.sort.Sorts;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.*;
import com.touchgraph.graphlayout.*;


/**
 *
 * @author  cumming
 */
//a general container for various types of input forms
public class SortInputFrame extends javax.swing.JFrame {
    
    protected Node node; //node to which Individual gets added
    protected TGPanel tgPanel; //panel where drawing takes place
    protected Vector panels = new Vector(); //the container for various input panels (all JPanels)
    SortDrawerBuilder builder = new SortDrawerBuilder();
    
    /** Creates new form SortInputFrame */
    public SortInputFrame(Node n, TGPanel tgp) {
        this.node = n;
        this.tgPanel = tgp;
        //initComponents(); //init after you add a panel
        showAllPanels();
        Dimension frameSize = this.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.FlowLayout());

        setTitle("Input sort data");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        okButton.setFont(new java.awt.Font("Arial", 1, 12));
        okButton.setText("Ok");
        okButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                okButtonMouseClicked(evt);
            }
        });

        getContentPane().add(okButton);

        cancelButton.setFont(new java.awt.Font("Arial", 1, 12));
        cancelButton.setText("Cancel");
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelButtonMouseClicked(evt);
            }
        });

        getContentPane().add(cancelButton);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents
    
    private void cancelButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButtonMouseClicked
        // Add your handling code here:
    }//GEN-LAST:event_cancelButtonMouseClicked
    
    private void okButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_okButtonMouseClicked
        // Add your handling code here:
        //String label = textField.getText();
        //Node popupNode = getTgPanel().getMouseOverN();
        Node n = getNode();
        //
        /**clean up and make new sort from mouse over node */
        Sorts sorts = tgPanel.getSorts();
        if (sorts == null) { //if no sorts exist, make new ones
            tgPanel.getSorts().cleanup(); //get rid of old ones
            Node selected = tgPanel.getSelect();
            
            if(selected instanceof sNode) {
                sNode selectSortNode = (sNode)selected;
                try {
                /**make new sorts from selected node--not [necessarily] 'n' node */
                builder.defineSortTreePostOrder(tgPanel, selectSortNode, 0, null); //start counter at zero
                }
                catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        }
        Sort s = sorts.sortOf(n.getLabel());
        //
        //most common case: normal primitive nodes
        if (s != null) {// && label != null) {
            //Label ind = new Label(s, label);
            //n.addIndividual(s, ind); //add this new individual to the node's form
        } else {
            System.out.println("Sort in profile is null");
        }
        this.dispose();
    }//GEN-LAST:event_okButtonMouseClicked
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        //System.exit(0);
        this.dispose();
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    //    public static void main(String args[]) {
    //        new SortInputFrame().show();
    //    }
    
    public void addComponents(JPanel jPanel) {
        panels.add(jPanel);
        this.getContentPane().add(jPanel);
        initComponents();
        pack();
        showAllPanels();
    }
    
    public void showAllPanels() {
        for (Iterator i = panels.iterator(); i.hasNext(); ) {
            JPanel jp = (JPanel)i.next();
            jp.show();
        }
        this.show();
    }
    
    /** Getter for property node.
     * @return Value of property node.
     *
     */
    public com.touchgraph.graphlayout.Node getNode() {
        return node;
    }
    
    /** Setter for property node.
     * @param node New value of property node.
     *
     */
    public void setNode(com.touchgraph.graphlayout.Node node) {
        this.node = node;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
    
}
