/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ConfigurationDialog.java
 *
 * Created on Jan 20, 2012, 5:59:17 PM
 */
package com.drawmetry.dociimentor;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 *  @author Erik Colban &copy; 2012 <br> All Rights Reserved Worldwide
 */
@SuppressWarnings("serial")
public class ConfigurationDialog extends javax.swing.JDialog {
    
    Component parent;
    File selectedDirectory;
    File configFile;
    
    public static void main(String[] args) {
    	ConfigurationDialog dialog = new ConfigurationDialog(null, true);
        dialog.setVisible(true);
        File docDir = dialog.getSelectedDirectory();
        dialog.dispose();
        dialog = null;
        System.out.println(docDir.getAbsolutePath());
	}

    /** Creates new form ConfigurationDialog */
    public ConfigurationDialog(Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        selectedDirectory = new File(System.getProperty("user.dir"), "IEEE/docs");
        configFile = new File(System.getProperty("user.dir"), ".dociimentor/dociiconfig.xml");
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        browseButton = new javax.swing.JButton();
        selectedDirectoryLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        finishButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/drawmetry/dociimentor/resources/MessageBundle"); // NOI18N
        setTitle(bundle.getString("CUSTOMIZATION")); // NOI18N

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 11));
        jLabel1.setText(bundle.getString("DOWLOADED FILES WILL BE SAVED TO")+":"); // NOI18N

        browseButton.setFont(new java.awt.Font("SansSerif", 0, 11));
        browseButton.setText(bundle.getString("BROWSE")+"..."); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        selectedDirectoryLabel.setFont(new java.awt.Font("SansSerif", 0, 11));
        selectedDirectoryLabel.setText(selectedDirectory.getAbsolutePath());

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 11));
        jLabel3.setText(bundle.getString("CUSTOMIZABLE SETTING WILL BE SAVED TO")+":"); // NOI18N

        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 11));
        jLabel4.setText(configFile.getAbsolutePath());

        finishButton.setFont(new java.awt.Font("SansSerif", 0, 11));
        finishButton.setText(bundle.getString("FINISH")); // NOI18N
        finishButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(selectedDirectoryLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                        .addComponent(browseButton))
                    .addComponent(finishButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(selectedDirectoryLabel)
                    .addComponent(browseButton))
                .addGap(7, 7, 7)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(finishButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        JFileChooser jfch = new JFileChooser();
        jfch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = jfch.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedDirectory = jfch.getSelectedFile();
            selectedDirectoryLabel.setText(selectedDirectory.getAbsolutePath());
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void finishButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finishButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_finishButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton finishButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel selectedDirectoryLabel;
    // End of variables declaration//GEN-END:variables
    public File getSelectedDirectory() {
        return selectedDirectory;
    }

}
