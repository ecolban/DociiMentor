/*
 * FilterDialog.java
 *
 */
package com.drawmetry.dociimentor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * A dialog for searches in the database
 * 
 *  @author Erik Colban &copy; 2012 <br> All Rights Reserved Worldwide
 */
@SuppressWarnings("serial")
public class FilterDialog extends javax.swing.JDialog {

    private final UI parent;
    private List<DocEntry> entries;
    private String fromDateString;
    private String toDateString;
    private ActionListener filterListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            okButtonActionPerformed(e);
        }
    };
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private boolean now = true;
    private boolean fourHoursAgo = false;
    private final Map<String, String> groupNameToCodeMap;

    /** 
     * Creates new FilterDialog
     * 
     */
    public FilterDialog(UI parent, boolean modal) {
        super(parent, "Filter", modal);
        this.parent = parent;
        initComponents();

        dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        int year = new GregorianCalendar(TimeZone.getTimeZone("America/New_York")).get(Calendar.YEAR);
        
        fromDateString = year + "-01-01 00:00:00";
        fromDateFormattedTextField.setText(fromDateString);
        
        toDateString = year + "-12-31 23:59:59";
        toDateFormattedTextField.setText(toDateString);
        
        toDateRadioButtonGroup.add(nowRadioButton);
        toDateRadioButtonGroup.add(fourHoursAgoRadioButton);
        toDateRadioButtonGroup.add(dateRadioButton);
        dateRadioButton.setSelected(true);

        fileNameTextField.addActionListener(filterListener);
        authorsTextField.addActionListener(filterListener);
        notesTextField.addActionListener(filterListener);
        groupNameToCodeMap = parent.getGroupNameToCodeMap();

    }

    /**
     * @return the entries
     */
    public List<DocEntry> getEntries() {
        return entries;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toDateRadioButtonGroup = new javax.swing.ButtonGroup();
        fileNameLabel = new javax.swing.JLabel();
        fileNameTextField = new javax.swing.JTextField();
        authorsLabel = new javax.swing.JLabel();
        authorsTextField = new javax.swing.JTextField();
        notesLabel = new javax.swing.JLabel();
        notesTextField = new javax.swing.JTextField();
        fromDateLabel = new javax.swing.JLabel();
        fromDateFormattedTextField = new javax.swing.JFormattedTextField();
        toDateLabel = new javax.swing.JLabel();
        toDateFormattedTextField = new javax.swing.JFormattedTextField();
        clearButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        nowRadioButton = new javax.swing.JRadioButton();
        fourHoursAgoRadioButton = new javax.swing.JRadioButton();
        dateRadioButton = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        fileNameLabel.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/drawmetry/dociimentor/resources/MessageBundle"); // NOI18N
        fileNameLabel.setText(bundle.getString("FILE NAME")+":"); // NOI18N
        fileNameLabel.setMaximumSize(new java.awt.Dimension(100, 15));

        authorsLabel.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        authorsLabel.setText(bundle.getString("AUTHORS")+":"); // NOI18N

        notesLabel.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        notesLabel.setText(bundle.getString("NOTES")+":"); // NOI18N

        fromDateLabel.setFont(new java.awt.Font("SansSerif", 0, 11));
        fromDateLabel.setText(bundle.getString("FROM DATE")+":"); // NOI18N

        fromDateFormattedTextField.setText("2011-12-31 00:00:00");
        fromDateFormattedTextField.setFont(new java.awt.Font("SansSerif", 0, 11));

        toDateLabel.setFont(new java.awt.Font("SansSerif", 0, 11));
        toDateLabel.setText(bundle.getString("TO DATE")+":"); // NOI18N

        toDateFormattedTextField.setText("9999-99-99 23:59:59");
        toDateFormattedTextField.setFont(new java.awt.Font("SansSerif", 0, 11));

        clearButton.setFont(new java.awt.Font("SansSerif", 0, 11));
        clearButton.setText(bundle.getString("CLEAR")); // NOI18N
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        cancelButton.setFont(new java.awt.Font("SansSerif", 0, 11));
        cancelButton.setText(bundle.getString("CANCEL")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setFont(new java.awt.Font("SansSerif", 0, 11));
        okButton.setText(bundle.getString("OK")); // NOI18N
        okButton.setMaximumSize(new java.awt.Dimension(1000, 23));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        nowRadioButton.setFont(new java.awt.Font("SansSerif", 0, 11));
        nowRadioButton.setText(bundle.getString("NOW")+":"); // NOI18N
        nowRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nowRadioButtonActionPerformed(evt);
            }
        });

        fourHoursAgoRadioButton.setFont(new java.awt.Font("SansSerif", 0, 11));
        fourHoursAgoRadioButton.setText(bundle.getString("FOUR HOURS AGO")+":"); // NOI18N
        fourHoursAgoRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fourHoursAgoRadioButtonActionPerformed(evt);
            }
        });

        dateRadioButton.setFont(new java.awt.Font("SansSerif", 0, 11));
        dateRadioButton.setText(bundle.getString("DATE")+":"); // NOI18N
        dateRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateRadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(notesLabel, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fromDateLabel, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(authorsLabel, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fileNameLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(notesTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                    .addComponent(authorsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                    .addComponent(fileNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(fromDateFormattedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(toDateLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nowRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(clearButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(fourHoursAgoRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dateRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(toDateFormattedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(authorsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(authorsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(notesLabel)
                    .addComponent(notesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fromDateLabel)
                    .addComponent(fromDateFormattedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toDateLabel)
                    .addComponent(nowRadioButton)
                    .addComponent(fourHoursAgoRadioButton)
                    .addComponent(dateRadioButton)
                    .addComponent(toDateFormattedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clearButton)
                    .addComponent(cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fourHoursAgoRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fourHoursAgoRadioButtonActionPerformed
        long toTime = System.currentTimeMillis() - 14400000;
        toDateString = dateFormat.format(new Date(toTime));
        toDateFormattedTextField.setText(toDateString);
        toDateFormattedTextField.setEditable(false);
}//GEN-LAST:event_fourHoursAgoRadioButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        now = nowRadioButton.isSelected();
        fourHoursAgo = fourHoursAgoRadioButton.isSelected();
        filter();
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void filter() {
        fromDateString = fromDateFormattedTextField.getText();
        if (now) {
            toDateString = dateFormat.format(new Date());
        } else if (fourHoursAgo) {
            long toTime = System.currentTimeMillis() - 14400000;
            toDateString = dateFormat.format(new Date(toTime));
        } else {
            toDateString = toDateFormattedTextField.getText();
        }
        String group = (String) parent.getGroup();
        String groupCode = null;
        if (!group.equals("All Groups")) {
            groupCode = groupNameToCodeMap.get(group);
        }
        try {
            entries = parent.getDb().findEntries(
                    parent.getTable(), 
                    groupCode == null ? "%" : groupCode,
                    "%" + fileNameTextField.getText() + "%",
                    "%" + authorsTextField.getText() + "%",
                    "%" + notesTextField.getText() + "%",
                    dateFormat.parse(fromDateString),
                    dateFormat.parse(toDateString));
        } catch (ParseException ex) {
            UI.LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        fileNameTextField.setText("");
        authorsTextField.setText("");
        notesTextField.setText("");
    }//GEN-LAST:event_clearButtonActionPerformed

    private void nowRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nowRadioButtonActionPerformed
        toDateString = dateFormat.format(new Date());
        toDateFormattedTextField.setText(toDateString);
        toDateFormattedTextField.setEditable(false);
    }//GEN-LAST:event_nowRadioButtonActionPerformed

    private void dateRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateRadioButtonActionPerformed
        Calendar toDate = new GregorianCalendar(TimeZone.getTimeZone("America/New_York"));
        toDateString = toDate.get(Calendar.YEAR) + "-12-31 23:59:59";
        toDateFormattedTextField.setText(toDateString);
        toDateFormattedTextField.setEditable(true);
    }//GEN-LAST:event_dateRadioButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel authorsLabel;
    private javax.swing.JTextField authorsTextField;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JRadioButton dateRadioButton;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JTextField fileNameTextField;
    private javax.swing.JRadioButton fourHoursAgoRadioButton;
    private javax.swing.JFormattedTextField fromDateFormattedTextField;
    private javax.swing.JLabel fromDateLabel;
    private javax.swing.JLabel notesLabel;
    private javax.swing.JTextField notesTextField;
    private javax.swing.JRadioButton nowRadioButton;
    private javax.swing.JButton okButton;
    private javax.swing.JFormattedTextField toDateFormattedTextField;
    private javax.swing.JLabel toDateLabel;
    private javax.swing.ButtonGroup toDateRadioButtonGroup;
    // End of variables declaration//GEN-END:variables

}
