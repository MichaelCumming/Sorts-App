/*
 * GraphLayoutPage.java
 *
 * Created on September 26, 2003, 3:35 PM
 */

package sortsApp;

import cassis.sort.*;
import cassis.sort.Sort;
import cassis.sort.Sorts;
//the following from Oreilly JFC Nutshell p.6-7
//just uses awt at the moment, not swing...
import java.awt.Color;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import cassis.sort.User;
import com.touchgraph.graphlayout.*;
import sortsApp.*;

/**
 *
 * @author  cumming
 */
public class GraphLayoutPage extends JPanel {
    private GLPanel editPanel = null;
    private GLPanel viewPanel = null;
    protected GraphLayoutTopFrame topFrame;
    SortsTerms terms = new SortsTerms();
    
    
    /** Creates new form GraphLayoutFrame */
    public GraphLayoutPage(String name, boolean withDemoGraph, GraphLayoutTopFrame topFrame) {
        this.topFrame = topFrame;
        /**View panel (bottom one) shows graphs from sorts definitions; viewPanel never has a demo graph */
        viewPanel = new GLPanel("View" + name, "sortViewPanel", false, this);
        viewPanel.setPreferredSize(new Dimension(terms.GLPAGE_SCREEN_WIDTH, terms.GLPAGE_PANEL_DEPTH));
        viewPanel.getTGPanel().setBackColor(Color.lightGray);
        //NOTE constructing the edit panel LAST is necessary to allow "edit" radio button to work
        //don't know why exactly. 1.0ct.2003 MC
        
        /**Edit panel (top one) shows touchgraph graphs -- may or may not have a demo graph */
        editPanel = new GLPanel("Edit" + name, "notSortViewPanel", withDemoGraph, this);
        editPanel.setPreferredSize(new Dimension(terms.GLPAGE_SCREEN_WIDTH, terms.GLPAGE_PANEL_DEPTH));
        editPanel.getTGPanel().setBackColor(new Color(0, 204, 153));
        initComponents();
        profileScrollPanel.setPreferredSize(new Dimension(terms.GLPAGE_SCREEN_WIDTH, terms.GLPAGE_MESSAGE_AREA_DEPTH));
    }
    
    public void constructSorts(String name) {
        /**Note: only the edit panel has sorts/profile that are used... */
        TGPanel tgpEdit = getEditPanel().getTGPanel();
        //this.getEditPanel().getTGPanel().constructSorts(name);
        SortsMC newSorts = new SortsMC(new User(name), tgpEdit);
        tgpEdit.setSorts(newSorts);
    }
    
    private JPanel getEditJPanel() {
        //edit panel: a JPanel with a normal touchgraph panel
        JPanel p = new JPanel();
        GLPanel glPanelEdit = this.getEditPanel();
        p.add(glPanelEdit);
        return p;
    }
    
    private JPanel getViewJPanel() {
        //readOnly panel: a JFrame for viewing generated sorts
        JPanel p = new JPanel();
        GLPanel glPanelView = this.getViewPanel();
        p.add(glPanelView);
        return p;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelWholeEdit = new javax.swing.JPanel();
        jPanelWholeEdit.add(getEditJPanel());
        jPanelWholeView = new javax.swing.JPanel();
        jPanelProfile = new javax.swing.JPanel();
        profileScrollPanel = new javax.swing.JScrollPane();
        messageArea = new javax.swing.JTextArea();
        jPanelView = new javax.swing.JPanel();
        jPanelView.add(getViewJPanel());
        firstSortTextField = new javax.swing.JTextField();
        secondSortTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jPanelWholeEdit.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jPanelWholeEdit.setBorder(new javax.swing.border.TitledBorder(null, "Edit Sort Definitions", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 1, 12)));
        jPanelWholeEdit.setFont(new java.awt.Font("Arial", 0, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanelWholeEdit, gridBagConstraints);

        jPanelWholeView.setLayout(new java.awt.GridBagLayout());

        jPanelWholeView.setBorder(new javax.swing.border.TitledBorder(null, "View Representation of Resulting Sorts", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 1, 12)));
        jPanelWholeView.setFont(new java.awt.Font("Arial", 0, 10));
        jPanelProfile.setLayout(new java.awt.GridBagLayout());

        jPanelProfile.setFont(new java.awt.Font("Arial", 0, 12));
        profileScrollPanel.setBackground(new java.awt.Color(255, 255, 255));
        profileScrollPanel.setBorder(new javax.swing.border.TitledBorder(null, "Messages", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 0, 10)));
        profileScrollPanel.setFont(new java.awt.Font("Arial", 0, 10));
        profileScrollPanel.setName("profilePanel");
        messageArea.setEditable(false);
        messageArea.setFont(new java.awt.Font("Arial", 0, 10));
        messageArea.setLineWrap(true);
        profileScrollPanel.setViewportView(messageArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelProfile.add(profileScrollPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanelWholeView.add(jPanelProfile, gridBagConstraints);

        jPanelView.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanelWholeView.add(jPanelView, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanelWholeView, gridBagConstraints);

        firstSortTextField.setFont(new java.awt.Font("MS Sans Serif", 0, 10));
        firstSortTextField.setDisabledTextColor(new java.awt.Color(255, 204, 204));
        firstSortTextField.setPreferredSize(new java.awt.Dimension(200, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(firstSortTextField, gridBagConstraints);

        secondSortTextField.setFont(new java.awt.Font("MS Sans Serif", 0, 10));
        secondSortTextField.setDisabledTextColor(new java.awt.Color(255, 204, 204));
        secondSortTextField.setPreferredSize(new java.awt.Dimension(200, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(secondSortTextField, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 10));
        jLabel1.setText("First Sort for Comparison");
        jLabel1.setPreferredSize(new java.awt.Dimension(133, 13));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 10));
        jLabel2.setText("Second Sort for Comparison");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel2, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    /** Getter for property editPanel.
     * @return Value of property editPanel.
     *
     */
    public com.touchgraph.graphlayout.GLPanel getEditPanel() {
        return editPanel;
    }
    
    /** Getter for property viewPanel.
     * @return Value of property viewPanel.
     *
     */
    public com.touchgraph.graphlayout.GLPanel getViewPanel() {
        return viewPanel;
    }
    
    public void printMessage(String s) {
        //messageArea is the scrolling text area for messages
        messageArea.append(s + "\n");
    }
    
    /** Getter for property messageArea.
     * @return Value of property messageArea.
     *
     */
    public JTextArea getMessageArea() {
        return messageArea;
    }
    
    /** Setter for property messageArea.
     * @param messageArea New value of property messageArea.
     *
     */
    //     public void setMessageArea(javax.swing.JTextArea messageArea) {
    //         this.messageArea = messageArea;
    //     }
    
    /** Getter for property panelNum.
     * @return Value of property panelNum.
     *
     */
    //     public int getPanelNum() {
    //         return panelNum;
    //     }
    
    /** Setter for property panelNum.
     * @param panelNum New value of property panelNum.
     *
     */
    //     public void setPanelNum(int panelNum) {
    //         this.panelNum = panelNum;
    //     }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //below use for error-checking purposes only---use GraphLayoutTopFrame instead
        JFrame f = new JFrame("test");
        GraphLayoutTopFrame topFrame = new GraphLayoutTopFrame();
        GraphLayoutPage glp = new GraphLayoutPage("test", true, topFrame);
        f.getContentPane().add(glp) ;
        f.pack();
        f.show();
    }
    
    /** Getter for property topFrame.
     * @return Value of property topFrame.
     *
     */
    public sortsApp.GraphLayoutTopFrame getTopFrame() {
        return topFrame;
    }
    
    /** Setter for property topFrame.
     * @param topFrame New value of property topFrame.
     *
     */
    public void setTopFrame(sortsApp.GraphLayoutTopFrame topFrame) {
        this.topFrame = topFrame;
    }
    
    /** Setter for property messageArea.
     * @param messageArea New value of property messageArea.
     *
     */
    public void setMessageArea(javax.swing.JTextArea messageArea) {
        this.messageArea = messageArea;
    }
    
    public void setFirstSort(Sort s) {
        firstSortTextField.setText("x");
        //topFrame.set
    }
    
    /**
     * Getter for property firstSortTextField.
     * @return Value of property firstSortTextField.
     */
    public javax.swing.JTextField getFirstSortTextField() {
        return firstSortTextField;
    }
    
    /**
     * Setter for property firstSortTextField.
     * @param firstSortTextField New value of property firstSortTextField.
     */
    public void setFirstSortTextField(javax.swing.JTextField firstSortTextField) {
        this.firstSortTextField = firstSortTextField;
    }
    
    /**
     * Getter for property secondSortTextField.
     * @return Value of property secondSortTextField.
     */
    public javax.swing.JTextField getSecondSortTextField() {
        return secondSortTextField;
    }
    
    /**
     * Setter for property secondSortTextField.
     * @param secondSortTextField New value of property secondSortTextField.
     */
    public void setSecondSortTextField(javax.swing.JTextField secondSortTextField) {
        this.secondSortTextField = secondSortTextField;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField firstSortTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanelProfile;
    private javax.swing.JPanel jPanelView;
    private javax.swing.JPanel jPanelWholeEdit;
    private javax.swing.JPanel jPanelWholeView;
    private javax.swing.JTextArea messageArea;
    private javax.swing.JScrollPane profileScrollPanel;
    private javax.swing.JTextField secondSortTextField;
    // End of variables declaration//GEN-END:variables
    
}
