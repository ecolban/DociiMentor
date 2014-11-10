package com.drawmetry.dociimentor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.drawmetry.dociimentor.database.DataAccessObject;

/**
 * This is the main class of the application. It instantiates a JFrame and all
 * its children components.
 *
 * @author Erik Colban &copy; 2012 <br> All Rights Reserved Worldwide
 */
@SuppressWarnings("serial")
public class UI extends JFrame implements Runnable, ClipboardOwner {

    private static final String revision = "Revision 1.4.0 (2014-11-12)";
    private static final ResourceBundle messageBundle =
            ResourceBundle.getBundle("com/drawmetry/dociimentor/resources/MessageBundle");
    private static final String DATABASE_LOCATION = messageBundle.getString("DATABASE LOCATION");
    private static final String ENTRY_NOT_FOUND_IN_DATABASE = messageBundle.getString("ENTRY NOT FOUND IN DATABASE");
    private static final String NO_ENTRY_SELECTED = messageBundle.getString("NO ENTRY SELECTED");
    private static final String ENTRIES_EXPORTED_TO = messageBundle.getString("ENTRIES EXPORTED TO");
    private static final String EXCEL = messageBundle.getString("EXCEL");
    private static final String DOES_NOT_EXIST = messageBundle.getString("DOES NOT EXIST");
    private static final String DATABASE_URL = messageBundle.getString("DATABASE URL");
    private static final String FILTER = messageBundle.getString("FILTER");
    private static final String FILTER_TOOL_TIP_TEXT = messageBundle.getString("FILTER TOOL TIP TEXT");
    private static final String LATEST = messageBundle.getString("LATEST");
    private static final String SYNCHRONIZE = messageBundle.getString("SYNCHRONIZE");
    private static final String SYNCHRONIZE_TOOL_TIP_TEXT = messageBundle.getString("SYNCHRONIZE TOOL TIP TEXT");
    private static final String DOWNLOAD = messageBundle.getString("DOWNLOAD");
    private static final String DOWNLOAD_TOOL_TIP_TEXT = messageBundle.getString("DOWNLOAD TOOL TIP TEXT");
    private static final String EXPORT = messageBundle.getString("EXPORT");
    private static final String EXPORT_TOOL_TIP_TEXT = messageBundle.getString("EXPORT TOOL TIP TEXT");
    private static final String ALL_YEARS = messageBundle.getString("ALL YEARS");
    private static final String ALL_GROUPS = messageBundle.getString("ALL GROUPS");
    private static final String DOWNLOAD_NOW_Q = messageBundle.getString("DOWNLOAD NOW Q");
    private static final String ABOUT = messageBundle.getString("ABOUT");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String[] workingGroups = Configuration.getWorkingGroupNames();
    public static final Logger LOGGER = Logger.getLogger("com.drawmetry.dociimentor");

    private class PopupListener extends MouseAdapter {

        JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
    private DataAccessObject db;
    /* State*/
    /* Working Group */
    private Configuration.WorkingGroup workingGroup;
    /* Year */
    private String[] yearArray;
    private String yearStr = ALL_YEARS;
    /* Group  */
    private String[] groupArray;
    private String groupStr = ALL_GROUPS; // group name
    private boolean changed = false; // true when entry needs to be saved
    private boolean latest = false; // true when only the latest revision is to appear in the entry list
    private DocumentListener documentChangeListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            changed = true;
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changed = true;
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            changed = true;
        }
    };

    private class DocEntryRenderer extends DefaultListCellRenderer {

        /**
         * Creates a new instance of DocEntryRenderer
         */
        public DocEntryRenderer() {
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            DocEntry entry = (DocEntry) value;
            String fn = entry.getFileName();
            if (new File(Configuration.getDirectory(fn), fn).exists()) {
                this.setForeground(Color.BLACK);
            } else {
                this.setForeground(Color.GRAY);
            }
            this.setText(entry.getFileName());
            return this;
        }
    }
    private DocEntry currentEntry = null;
    private String dateString;
    private boolean syncLock = false;
    private FilterDialog filterDialog;
    private List<DocEntry> allEntries;
    private List<DocEntry> latestEntries;
    private Downloader downLoader;
    private Synchronizer synchronizer;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() {

        popupMenu = new JPopupMenu();
        copyDocId = new JMenuItem();
        copyURL = new JMenuItem();
        deleteEntry = new JMenuItem();
        deleteFile = new JMenuItem();
        jToolBar1 = new JToolBar();
        workingGroupComboBox = new JComboBox<String>();
        jSeparator7 = new JToolBar.Separator();
        yearComboBox = new JComboBox<String>();
        jSeparator4 = new JToolBar.Separator();
        groupComboBox = new JComboBox<String>();
        jSeparator1 = new JToolBar.Separator();
        jToolBar2 = new JToolBar();
        latestCheckBox = new JCheckBox();
        jSeparator2 = new JToolBar.Separator();
        filterButton = new JButton();
        jSeparator3 = new JToolBar.Separator();
        synchronizeButton = new JButton();
        jSeparator5 = new JToolBar.Separator();
        downloadButton = new JButton();
        jSeparator6 = new JToolBar.Separator();
        exportButton = new JButton();
        stopButton = new JButton();
        jSplitPane1 = new JSplitPane();
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        documentList = new JList<DocEntry>();
        jSplitPane2 = new JSplitPane();
        docTabPane = new JTabbedPane();
        jPanel2 = new JPanel();
        dateTextField = new JFormattedTextField();
        jScrollPane2 = new JScrollPane();
        fileNameArea = new JTextArea();
        jScrollPane3 = new JScrollPane();
        authorsArea = new JTextArea();
        jScrollPane5 = new JScrollPane();
        notesArea = new JTextArea();
        jScrollPane6 = new JScrollPane();
        outputArea = new JTextArea();
        downloadProgressBar = new JProgressBar();
        jMenuBar1 = new JMenuBar();
        fileMenu = new JMenu();
        openMenuItem = new JMenuItem();
        exitMenuItem = new JMenuItem();
        editMenu = new JMenu();
        copyFileNameMenuItem = new JMenuItem();
        copyUrlMenuItem = new JMenuItem();
        deleteEntryMenuItem = new JMenuItem();
        deleteFileMenuItem = new JMenuItem();
        helpMenu = new JMenu();
        helpMenuItem = new JMenuItem();
        aboutMenuItem = new JMenuItem();

        ResourceBundle bundle = ResourceBundle.getBundle("com/drawmetry/dociimentor/resources/MessageBundle"); // NOI18N
        copyDocId.setText(bundle.getString("COPY DOC ID")); // NOI18N
        copyDocId.setToolTipText("");
        copyDocId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyDocIdActionPerformed(evt);
            }
        });
        popupMenu.add(copyDocId);

        copyURL.setText(bundle.getString("COPY URL")); // NOI18N
        copyURL.setToolTipText(bundle.getString("COPY THE LOCATION OF THE FILE ON THE MENTOR SERVER")); // NOI18N
        copyURL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyURLActionPerformed(evt);
            }
        });
        popupMenu.add(copyURL);

        deleteEntry.setText(bundle.getString("DELETE ENTRY")); // NOI18N
        deleteEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEntryActionPerformed(evt);
            }
        });
        popupMenu.add(deleteEntry);

        deleteFile.setText(bundle.getString("DELETE FILE")); // NOI18N
        deleteFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteFileActionPerformed(evt);
            }
        });
        popupMenu.add(deleteFile);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Docii Mentor Edition");
        setMinimumSize(new java.awt.Dimension(1020, 530));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setMaximumSize(new java.awt.Dimension(400, 22));
        jToolBar1.setPreferredSize(new java.awt.Dimension(400, 22));

        workingGroupComboBox.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        workingGroupComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        workingGroupComboBox.setMaximumSize(new java.awt.Dimension(200, 20));
        workingGroupComboBox.setMinimumSize(new java.awt.Dimension(100, 20));
        workingGroupComboBox.setPreferredSize(new java.awt.Dimension(150, 20));
        workingGroupComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workingGroupComboBoxActionPerformed(evt);
            }
        });
        jToolBar1.add(workingGroupComboBox);
        jToolBar1.add(jSeparator7);

        yearComboBox.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        yearComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        yearComboBox.setMaximumSize(new java.awt.Dimension(300, 20));
        yearComboBox.setMinimumSize(new java.awt.Dimension(100, 20));
        yearComboBox.setPreferredSize(new java.awt.Dimension(150, 20));
        yearComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yearComboBoxActionPerformed(evt);
            }
        });
        jToolBar1.add(yearComboBox);
        jToolBar1.add(jSeparator4);

        groupComboBox.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        groupComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        groupComboBox.setMaximumSize(new java.awt.Dimension(300, 20));
        groupComboBox.setMinimumSize(new java.awt.Dimension(100, 20));
        groupComboBox.setPreferredSize(new java.awt.Dimension(150, 20));
        groupComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groupComboBoxActionPerformed(evt);
            }
        });
        jToolBar1.add(groupComboBox);
        jToolBar1.add(jSeparator1);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        latestCheckBox.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        latestCheckBox.setText(LATEST);
        latestCheckBox.setFocusable(false);
        latestCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
        latestCheckBox.setVerticalTextPosition(SwingConstants.BOTTOM);
        latestCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                latestCheckBoxActionPerformed(evt);
            }
        });
        jToolBar2.add(latestCheckBox);
        jToolBar2.add(jSeparator2);

        filterButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        filterButton.setText(FILTER + "...");
        filterButton.setToolTipText(FILTER_TOOL_TIP_TEXT);
        filterButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        filterButton.setFocusable(false);
        filterButton.setHorizontalTextPosition(SwingConstants.CENTER);
        filterButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        filterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(filterButton);
        jToolBar2.add(jSeparator3);

        synchronizeButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        synchronizeButton.setText(SYNCHRONIZE);
        synchronizeButton.setToolTipText(SYNCHRONIZE_TOOL_TIP_TEXT);
        synchronizeButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        synchronizeButton.setFocusable(false);
        synchronizeButton.setHorizontalTextPosition(SwingConstants.CENTER);
        synchronizeButton.setMaximumSize(new java.awt.Dimension(200, 21));
        synchronizeButton.setMinimumSize(new java.awt.Dimension(80, 21));
        synchronizeButton.setPreferredSize(new java.awt.Dimension(80, 21));
        synchronizeButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        synchronizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                synchronizeButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(synchronizeButton);
        jToolBar2.add(jSeparator5);

        downloadButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        downloadButton.setText(DOWNLOAD);
        downloadButton.setToolTipText(DOWNLOAD_TOOL_TIP_TEXT);
        downloadButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        downloadButton.setFocusable(false);
        downloadButton.setHorizontalTextPosition(SwingConstants.CENTER);
        downloadButton.setMaximumSize(new java.awt.Dimension(200, 21));
        downloadButton.setMinimumSize(new java.awt.Dimension(80, 21));
        downloadButton.setPreferredSize(new java.awt.Dimension(80, 21));
        downloadButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(downloadButton);
        jToolBar2.add(jSeparator6);

        exportButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        exportButton.setText(EXPORT);
        exportButton.setToolTipText(EXPORT_TOOL_TIP_TEXT);
        exportButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        exportButton.setFocusable(false);
        exportButton.setHorizontalTextPosition(SwingConstants.CENTER);
        exportButton.setMaximumSize(new java.awt.Dimension(200, 21));
        exportButton.setMinimumSize(new java.awt.Dimension(80, 21));
        exportButton.setPreferredSize(new java.awt.Dimension(80, 21));
        exportButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(exportButton);

        stopButton.setIcon(new ImageIcon(getClass().getResource("/com/drawmetry/dociimentor/images/stop2_32.png"))); // NOI18N
        stopButton.setBorder(null);
        stopButton.setIconTextGap(0);
        stopButton.setMaximumSize(new java.awt.Dimension(32, 22));
        stopButton.setMinimumSize(new java.awt.Dimension(32, 22));
        stopButton.setPreferredSize(new java.awt.Dimension(32, 22));
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(stopButton);

        jSplitPane1.setPreferredSize(new java.awt.Dimension(900, 405));

        jPanel1.setMinimumSize(new java.awt.Dimension(200, 100));
        jPanel1.setPreferredSize(new java.awt.Dimension(200, 357));

        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 200));

        documentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        documentList.setToolTipText(bundle.getString("DOUBLE-CLICK ON DOCUMENT")); // NOI18N
        documentList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                documentListMouseClicked(evt);
            }
        });
        documentList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                documentListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(documentList);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel1);

        jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);

        docTabPane.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        docTabPane.setMinimumSize(new java.awt.Dimension(400, 300));
        docTabPane.setPreferredSize(new java.awt.Dimension(600, 300));

        dateTextField.setEditable(false);
        dateTextField.setHorizontalAlignment(JTextField.RIGHT);
        dateTextField.setText("2011-02-01 23:58:30");
        dateTextField.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N

        jScrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(23, 40));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(600, 40));

        fileNameArea.setColumns(20);
        fileNameArea.setEditable(false);
        fileNameArea.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        fileNameArea.setLineWrap(true);
        fileNameArea.setRows(2);
        fileNameArea.setTabSize(10);
        fileNameArea.setToolTipText(bundle.getString("TITLE")); // NOI18N
        fileNameArea.setWrapStyleWord(true);
        fileNameArea.setMinimumSize(new java.awt.Dimension(4, 49));
        jScrollPane2.setViewportView(fileNameArea);

        jScrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setMinimumSize(new java.awt.Dimension(23, 40));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(100, 40));

        authorsArea.setColumns(20);
        authorsArea.setEditable(false);
        authorsArea.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        authorsArea.setLineWrap(true);
        authorsArea.setRows(5);
        authorsArea.setTabSize(10);
        authorsArea.setToolTipText(bundle.getString("AUTHORS")); // NOI18N
        authorsArea.setWrapStyleWord(true);
        authorsArea.setMinimumSize(new java.awt.Dimension(4, 25));
        jScrollPane3.setViewportView(authorsArea);

        jScrollPane5.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane5.setMinimumSize(new java.awt.Dimension(23, 100));

        notesArea.setColumns(20);
        notesArea.setLineWrap(true);
        notesArea.setRows(5);
        notesArea.setToolTipText(bundle.getString("NOTES")); // NOI18N
        notesArea.setWrapStyleWord(true);
        jScrollPane5.setViewportView(notesArea);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(645, Short.MAX_VALUE)
                .addComponent(dateTextField, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane5, GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
            .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
            .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 791, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(dateTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
        );

        docTabPane.addTab("", jPanel2);

        jSplitPane2.setTopComponent(docTabPane);
        docTabPane.getAccessibleContext().setAccessibleName("");

        jScrollPane6.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane6.setMaximumSize(new java.awt.Dimension(32767, 50));
        jScrollPane6.setMinimumSize(new java.awt.Dimension(23, 50));

        outputArea.setColumns(20);
        outputArea.setEditable(false);
        outputArea.setRows(5);
        outputArea.setToolTipText(bundle.getString("MESSAGES")); // NOI18N
        jScrollPane6.setViewportView(outputArea);

        jSplitPane2.setRightComponent(jScrollPane6);

        jSplitPane1.setRightComponent(jSplitPane2);

        fileMenu.setText(bundle.getString("FILE MENU")); // NOI18N
        fileMenu.setActionCommand(bundle.getString("FILE MENU")); // NOI18N

        openMenuItem.setText(bundle.getString("OPEN DOC")); // NOI18N
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        exitMenuItem.setText(bundle.getString("EXIT")); // NOI18N
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        jMenuBar1.add(fileMenu);

        editMenu.setText(bundle.getString("EDIT MENU")); // NOI18N

        copyFileNameMenuItem.setText(bundle.getString("COPY DOC ID")); // NOI18N
        copyFileNameMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyDocIdActionPerformed(evt);
            }
        });
        editMenu.add(copyFileNameMenuItem);

        copyUrlMenuItem.setText(bundle.getString("COPY URL")); // NOI18N
        copyUrlMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyURLActionPerformed(evt);
            }
        });
        editMenu.add(copyUrlMenuItem);

        deleteEntryMenuItem.setText(bundle.getString("DELETE ENTRY")); // NOI18N
        deleteEntryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEntryActionPerformed(evt);
            }
        });
        editMenu.add(deleteEntryMenuItem);

        deleteFileMenuItem.setText(bundle.getString("DELETE FILE")); // NOI18N
        deleteFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteFileMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(deleteFileMenuItem);

        jMenuBar1.add(editMenu);

        helpMenu.setText(bundle.getString("HELP MENU")); // NOI18N

        helpMenuItem.setText(bundle.getString("HELP")); // NOI18N
        helpMenuItem.setEnabled(false);
        helpMenu.add(helpMenuItem);

        aboutMenuItem.setText(bundle.getString("ABOUT")); // NOI18N
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(downloadProgressBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, 503, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addComponent(jToolBar2, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downloadProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        SwingUtilities.invokeLater(new UI());
    }

    @Override
    public void run() {
        initComponents();
        LOGGER.addHandler(new LogHandler(outputArea));
        db = new DataAccessObject();
        LOGGER.log(Level.INFO, "{0}: {1}\n", new Object[]{
                    DATABASE_LOCATION, getDb().getDatabaseLocation()});
        LOGGER.log(Level.INFO, "{0}: {1}\n", new Object[]{
                    DATABASE_URL, getDb().getDatabaseUrl()});
        if (db.connect()) {
            initComponentsContinued();
            setVisible(true);
        } else {
            System.exit(1);
        }
    }

    private void initComponentsContinued() {
        Image icon = null;
        try {
            Class<? extends UI> c = getClass();
            URL iconURL = c.getResource("images/consensii.png");
            if (iconURL != null) {
                icon = ImageIO.read(iconURL);
            } else {
                System.err.println("Cannot load logo.");
            }
        } catch (IOException ex) {
            System.err.println("Cannot load logo.");
        }
        if (icon != null) {
            setIconImage(icon);
        }
        stopButton.setEnabled(false);

        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        workingGroupComboBox.setModel(new DefaultComboBoxModel<String>(workingGroups));
        workingGroupComboBox.setSelectedIndex(0);
        documentList.setCellRenderer(new DocEntryRenderer());
        documentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        documentList.addMouseListener(new PopupListener(popupMenu));
        fileNameArea.getDocument().addDocumentListener(documentChangeListener);
        authorsArea.getDocument().addDocumentListener(documentChangeListener);
        notesArea.getDocument().addDocumentListener(documentChangeListener);
    }

    public Configuration.WorkingGroup getWorkingGroup() {
        return workingGroup;
    }

    public String getYear() {
        return yearStr;
    }

    public String getGroup() {
        return groupStr;
    }

    private void selectWorkingGroup(int i) {
        assert 0 <= i && i < workingGroups.length;

        workingGroup = Configuration.getWorkingGroup(workingGroups[i]);

        yearArray = workingGroup.getYears();
        yearComboBox.setModel(new DefaultComboBoxModel<String>(yearArray));
        yearComboBox.setSelectedIndex(0);
        yearStr = (String) yearComboBox.getSelectedItem();

        groupArray = workingGroup.getGroupNames();
        groupComboBox.setModel(new DefaultComboBoxModel<String>(groupArray));
        groupComboBox.setSelectedIndex(0);
        groupStr = (String) groupComboBox.getSelectedItem();

        filterDialog = new FilterDialog(this, true);
        filter();
        updateDocumentList();
//        documentList.setListData(allEntries.toArray(new DocEntry[0]));
    }

    private void documentListValueChanged(ListSelectionEvent evt) {
        if (evt.getValueIsAdjusting()) {
            return;
        }
        DocEntry entry = documentList.getSelectedValue();
        if (changed && currentEntry != null) {
            save(currentEntry);
        }
        changed = false;
        currentEntry = entry;
        if (entry != null) {
            int id = entry.getId();
            DocumentObject docObj = getDb().getDocumentOject(workingGroup.getTable(), id);
            if (docObj != null) {
                fillDocumentFields(docObj);
            } else {
                clearDocumentFields();
            }
        } else {
            clearDocumentFields();
        }
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        closeDown();
    }

    private void closeDown() {
        if (changed && currentEntry != null) {
            save(currentEntry);
        }
        getDb().disconnect();
    }
    private void documentListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_documentListMouseClicked
        if (evt.getClickCount() != 2) {
            return;
        }
        openCurrentDocument();
    }//GEN-LAST:event_documentListMouseClicked

    private void openCurrentDocument() {
        if (currentEntry == null) {
            return;
        }
        DocumentObject docObj = getDb().getDocumentOject(
                workingGroup.getTable(), currentEntry.getId());
        assert docObj != null;
        String fileName = docObj.getFileName();
        assert fileName != null;
        File file = new File(Configuration.getDirectory(fileName), fileName);
        if (!file.exists()) {
            int answer = JOptionPane.showConfirmDialog(this,
                    DOWNLOAD_NOW_Q, DOWNLOAD, JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                Downloader dl = new Downloader(this);
                dl.downloadNow(docObj);
                documentList.repaint();
            }
        }
        if (file.exists()) {
            try {
                file = file.getCanonicalFile();
                Desktop.getDesktop().open(file);
            } catch (NullPointerException ex) {
                LOGGER.log(Level.SEVERE, "{0}\n", ex.getMessage());
            } catch (UnsupportedOperationException ex) {
                LOGGER.log(Level.SEVERE, "{0}\n", ex.getMessage());
            } catch (SecurityException ex) {
                LOGGER.log(Level.SEVERE, "{0}\n", ex.getMessage());
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "{0}\n", ex.getMessage());
            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.SEVERE,
                        "{0} {1}.\n", new String[]{fileName, DOES_NOT_EXIST});
            }
        }
    }

    private void filter() {
        String yearCode = null;
        if (!yearStr.equals(ALL_YEARS)) {
            yearCode = yearStr;
        }
        String groupCode = null;
        if (!groupStr.equals(ALL_GROUPS)) {
            groupCode = workingGroup.getGroupNameToCodeMap().get(groupStr);
        }
        allEntries = db.findEntries(workingGroup.getTable(), yearCode, groupCode);
    }

    private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterButtonActionPerformed
        filterDialog.setVisible(true);
        allEntries = filterDialog.getEntries();
        updateDocumentList();
    }//GEN-LAST:event_filterButtonActionPerformed

    private void yearComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yearComboBoxActionPerformed
        String newYear = (String) yearComboBox.getSelectedItem();
        if (newYear != null && !newYear.equals(yearStr)) {
            yearStr = newYear;
            filter();
            updateDocumentList();
        }
    }//GEN-LAST:event_yearComboBoxActionPerformed

    private void groupComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupComboBoxActionPerformed
        String newGroup = (String) groupComboBox.getSelectedItem();
        if (newGroup != null && !newGroup.equals(groupStr)) {
            groupStr = newGroup;
            filter();
            updateDocumentList();
        }
    }//GEN-LAST:event_groupComboBoxActionPerformed

    private void synchronizeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_synchronizeButtonActionPerformed
        if (!isSyncLock()) {
            setSyncLock(true);
            synchronizer = new Synchronizer(this,
                    workingGroup.getHostUrl(), workingGroup.getPath(), buildQuery());
            Thread syncThread = new Thread(synchronizer);
            syncThread.start();
        }
        filter();
        updateDocumentList();
    }//GEN-LAST:event_synchronizeButtonActionPerformed

    private void latestCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_latestCheckBoxActionPerformed
        latest = latestCheckBox.isSelected();
        DocEntry entry = getSelectedListEntry();
        int id = -1;
        if (entry != null) {
            id = entry.getId();
        }
        refreshListEntries();
        List<DocEntry> entries = latest ? latestEntries : allEntries;
        if (entries.isEmpty()) {
            return;
        }
        if (id != -1) {

            for (int index = 0; index < entries.size(); index++) {
                if (entries.get(index).getId() == id) {
                    setSelectedIndex(index);
                    return;
                }
            }
        }
        setSelectedIndex(0);
    }//GEN-LAST:event_latestCheckBoxActionPerformed

    private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed

        if (!isSyncLock()) {
            setSyncLock(true);
            downLoader = new Downloader(this);
            Thread downloadThread = new Thread(downLoader);
            downloadThread.start();
            stopButton.setEnabled(true);
        }
    }//GEN-LAST:event_downloadButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed

        FileOutputStream fileOut = null;
        try {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    EXCEL, "xls");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                String fileName = file.getName();
                String path = file.getParent();
                int pos = fileName.lastIndexOf(".");
                if (!"xls".equals(fileName.substring(pos + 1))) {
                    fileName = fileName + ".xls";
                    file = new File(path, fileName);
                }
                fileOut = new FileOutputStream(file);
                new Exporter(this).write(fileOut);
                LOGGER.log(Level.INFO, "{0} {1}.\n",
                        new Object[]{ENTRIES_EXPORTED_TO, file.getAbsolutePath()});
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "{0}\n", ex.getMessage());
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException ex) {
                }
            }
        }
    }//GEN-LAST:event_exportButtonActionPerformed

    private void workingGroupComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workingGroupComboBoxActionPerformed
        if (changed && currentEntry != null) {
            save(currentEntry);
        }
        documentList.clearSelection();
        int i = workingGroupComboBox.getSelectedIndex();
        selectWorkingGroup(i);
    }//GEN-LAST:event_workingGroupComboBoxActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        if (isSyncLock()) {
            stopButton.setEnabled(false);
            if (downLoader != null) {
                downLoader.setAbort(true);
                downloadButton.setEnabled(false);
            }
            if (synchronizer != null) {
                synchronizer.setAbort(true);
                synchronizeButton.setEnabled(false);
            }
        }
    }//GEN-LAST:event_stopButtonActionPerformed

private void deleteEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEntryActionPerformed
    int selectedIndex = documentList.getSelectedIndex();
    if (selectedIndex >= 0) {
        ListModel<DocEntry> model = documentList.getModel();
        currentEntry = (DocEntry) model.getElementAt(selectedIndex);
        if (currentEntry != null) {
            db.deleteRecord(getTable(), currentEntry.getId());
            allEntries.remove(currentEntry);
            if (latestEntries != null) {
                latestEntries.remove(currentEntry);
            }
            updateDocumentList();
        }
    }
}//GEN-LAST:event_deleteEntryActionPerformed

private void copyURLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyURLActionPerformed
    Clipboard clipboard = getToolkit().getSystemClipboard();

    if (currentEntry != null) {
        int id = currentEntry.getId();
        DocumentObject obj = db.getDocumentOject(getTable(), id);
        if (obj != null) {
            StringSelection selection = new StringSelection(obj.getURL().toString());
            clipboard.setContents(selection, this);
        }
    }
}//GEN-LAST:event_copyURLActionPerformed

private void copyDocIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyDocIdActionPerformed
    Clipboard clipboard = getToolkit().getSystemClipboard();

    if (currentEntry != null) {
        int id = currentEntry.getId();
        DocumentObject obj = db.getDocumentOject(getTable(), id);
        if (obj != null) {
            StringSelection selection = new StringSelection(obj.getDocumentId());
            clipboard.setContents(selection, this);
        }
    }
}//GEN-LAST:event_copyDocIdActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        System.setProperty("awt.useSystemAAFontSettings", "on");
        final JEditorPane editorPane = new JEditorPane();

        // Enable use of custom set fonts
        editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        editorPane.setFont(new Font("Arial", Font.BOLD, 13));

        editorPane.setPreferredSize(new Dimension(520, 180));
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.setText(
                "<html>"
                + "<body>"
                + "<table border='0px' cxellpadding='10px' height='100%'>"
                + "<tr>"
                + "<td valign='center'>"
                + "<img src=\""
                + UI.class.getResource("images/consensii.png").toExternalForm()
                + "\">"
                + "</td>"
                + "<td align=center>"
                + "Docii&trade; Document Manager &ndash; "
                + "Mentor Edition<br>"
                + revision
                + "<br/>"
                + "<br/>"
                + "Copyright &copy; 2012 <a href=\"mailto:support@drawmetry.com\">Erik Colban</a><br>"
                + "All Rights Reserved Worldwide<p>"
                + "<br/>"
                + "Docii is a trademark of Consensii LLC.<br>"
                + "<a href=\"http://consensii.com\"><b>consensii.com</b></a><br>"
                + "</td>"
                + "</tr>"
                + "</table>"
                + "</body>"
                + "</html>");

//        // TIP: Make the JOptionPane resizable using the HierarchyListener
//        editorPane.addHierarchyListener(new HierarchyListener() {
//
//            public void hierarchyChanged(HierarchyEvent e) {
//                Window window = SwingUtilities.getWindowAncestor(editorPane);
//                if (window instanceof Dialog) {
//                    Dialog dialog = (Dialog) window;
//                    if (!dialog.isResizable()) {
//                        dialog.setResizable(true);
//                    }
//                }
//            }
//        });

        // TIP: Add Hyperlink listener to process hyperlinks
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(final HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // TIP: Show hand cursor
                            SwingUtilities.getWindowAncestor(editorPane).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            // TIP: Show URL as the tooltip
                            editorPane.setToolTipText(e.getURL().toExternalForm());
                        }
                    });
                } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            // Show default cursor
                            SwingUtilities.getWindowAncestor(editorPane).setCursor(Cursor.getDefaultCursor());

                            // Reset tooltip
                            editorPane.setToolTipText(null);
                        }
                    });
                } else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    // TIP: Starting with JDK6 you can show the URL in desktop browser
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (Exception ex) {
                            LOGGER.log(Level.SEVERE, "Cannot find the file browser");
                        }
                    }
                }
            }
        });
        JOptionPane.showMessageDialog(null,
                new JScrollPane(editorPane),
                ABOUT,
                JOptionPane.PLAIN_MESSAGE);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        openCurrentDocument();
    }//GEN-LAST:event_openMenuItemActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        closeDown();
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void deleteFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteFileActionPerformed
        deleteCurrentFile();
    }//GEN-LAST:event_deleteFileActionPerformed

    private void deleteCurrentFile() {
        if (currentEntry == null) {
            return;
        }
        DocumentObject docObj = getDb().getDocumentOject(
                workingGroup.getTable(), currentEntry.getId());
        assert docObj != null;
        String fileName = docObj.getFileName();
        assert fileName != null;
        File file = new File(Configuration.getDirectory(fileName), fileName);
        if (file.exists()) {
            file.delete();
            documentList.repaint();
        }
    }
    private void deleteFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteFileMenuItemActionPerformed
        deleteCurrentFile();
    }

    private String buildQuery() {
        String query = "";
        yearStr = (String) yearComboBox.getSelectedItem();
        if (!yearStr.equals(ALL_YEARS)) {
            query = query + "&is_year=" + yearStr;
        }
        groupStr = (String) groupComboBox.getSelectedItem();
        if (!groupStr.equals(ALL_GROUPS)) {
            query = query + "&is_group="
                    + workingGroup.getGroupNameToCodeMap().get(groupStr);
        }
        return query;
    }

    private void updateDocumentList() {
        DocEntry entry = (DocEntry) documentList.getSelectedValue();
        int id = -1;
        if (entry != null) {
            id = entry.getId();
        }
        if (allEntries == null) {
            return;
        }
        refreshListEntries();
        List<DocEntry> modelEntries = latest ? latestEntries : allEntries;
        if (id != -1) {
            for (int index = 0; index < modelEntries.size(); index++) {
                if (modelEntries.get(index).getId() == id) {
                    setSelectedIndex(index);
                    return;
                }
            }
        }
        setSelectedIndex(0);
    }

    private void refreshListEntries() {
        if (allEntries == null) {
            System.out.println("Stop!");
        }
        if (!latest) {
            DocEntry[] data = allEntries.toArray(new DocEntry[0]);
            documentList.setListData(data);
            return;
        }
        latestEntries = new ArrayList<DocEntry>(allEntries.size());
        DocEntry prevEntry = null;
        for (DocEntry entry : allEntries) {
            int i = entry.compare(prevEntry);
            if (prevEntry == null || i > 0) {
                prevEntry = entry;
            } else if (i == 0) {
                latestEntries.add(prevEntry);
                prevEntry = entry;
            }
        }
        if (prevEntry != null) {
            latestEntries.add(prevEntry);
        }
        DocEntry[] data = latestEntries.toArray(new DocEntry[0]);
        documentList.setListData(data);

    }

    public int getSelectedIndex() {
        return documentList.getSelectedIndex();
    }

    private int setSelectedIndex(int index) {
        assert index >= -1;
        ListModel<DocEntry> model = documentList.getModel();
        int size = model.getSize();
        if (index < size) {
            documentList.setSelectedIndex(index);
        } else {
            documentList.setSelectedIndex(size - 1);
            index = size - 1;
        }
        if (changed && currentEntry != null) {
            save(currentEntry);
        }
        if (model.getSize() > 0) {
            currentEntry = (DocEntry) model.getElementAt(index);
        }
        changed = false;
        return index;
    }

    public DocEntry getSelectedListEntry() {
        DocEntry entry = (DocEntry) documentList.getSelectedValue();
        return entry;
    }
    private JMenuItem aboutMenuItem;
    private JTextArea authorsArea;
    private JMenuItem copyDocId;
    private JMenuItem copyFileNameMenuItem;
    private JMenuItem copyURL;
    private JMenuItem copyUrlMenuItem;
    private JFormattedTextField dateTextField;
    private JMenuItem deleteEntry;
    private JMenuItem deleteEntryMenuItem;
    private JMenuItem deleteFile;
    private JMenuItem deleteFileMenuItem;
    private JTabbedPane docTabPane;
    private JList<DocEntry> documentList;
    private JButton downloadButton;
    private JProgressBar downloadProgressBar;
    private JMenu editMenu;
    private JMenuItem exitMenuItem;
    private JButton exportButton;
    private JMenu fileMenu;
    private JTextArea fileNameArea;
    private JButton filterButton;
    private JComboBox<String> groupComboBox;
    private JMenu helpMenu;
    private JMenuItem helpMenuItem;
    private JMenuBar jMenuBar1;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPane3;
    private JScrollPane jScrollPane5;
    private JScrollPane jScrollPane6;
    private JToolBar.Separator jSeparator1;
    private JToolBar.Separator jSeparator2;
    private JToolBar.Separator jSeparator3;
    private JToolBar.Separator jSeparator4;
    private JToolBar.Separator jSeparator5;
    private JToolBar.Separator jSeparator6;
    private JToolBar.Separator jSeparator7;
    private JSplitPane jSplitPane1;
    private JSplitPane jSplitPane2;
    private JToolBar jToolBar1;
    private JToolBar jToolBar2;
    private JCheckBox latestCheckBox;
    private JTextArea notesArea;
    private JMenuItem openMenuItem;
    private JTextArea outputArea;
    private JPopupMenu popupMenu;
    private JButton stopButton;
    private JButton synchronizeButton;
    private JComboBox<String> workingGroupComboBox;
    private JComboBox<String> yearComboBox;

    private void fillDocumentFields(DocumentObject fo) {

        dateTextField.setText(fo.getUploadDateString());
        fileNameArea.setText(fo.getTitle());
        authorsArea.setText(fo.getAuthors());
        notesArea.setText(fo.getNotes());
        docTabPane.setTitleAt(0, fo.getDocumentId().substring(12));
        changed = false;
    }

    private void clearDocumentFields() {
        dateTextField.setText("");
        fileNameArea.setText("");
        authorsArea.setText("");
        notesArea.setText("");
        docTabPane.setTitleAt(0, "");
    }

    private void save(DocEntry entry) {
        if (entry != null) {
            int id = entry.getId();
            DocumentObject oldFo = getDb().getDocumentOject(getTable(), id);
            if (oldFo == null) {
                LOGGER.log(Level.WARNING, "{0}.\n", ENTRY_NOT_FOUND_IN_DATABASE);
                return;
            }
            try {
                DocumentObject newFo = new DocumentObject(id,
                        oldFo.getYear(),
                        oldFo.getDcn(),
                        oldFo.getRev(),
                        oldFo.getGroupCode(),
                        oldFo.getTitle(),
                        oldFo.getAuthors(),
                        oldFo.getUploadDate(),
                        oldFo.getURL().toString(),
                        notesArea.getText());
                getDb().editRecord(getTable(), id, newFo);
            } catch (ParseException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        } else {
            LOGGER.log(Level.WARNING, "{0}.\n", NO_ENTRY_SELECTED);
        }
    }

    /**
     * @return the db
     */
    public DataAccessObject getDb() {
        return db;
    }

    /**
     * @return the toDateString
     */
    public String getToDateString() {
        return dateString;
    }

    /**
     * @return the syncLock
     */
    public boolean isSyncLock() {
        return syncLock;
    }

    /**
     * @param lock the syncLock to set
     */
    public void setSyncLock(boolean lock) {

        downloadButton.setEnabled(!lock);
        synchronizeButton.setEnabled(!lock);
        stopButton.setEnabled(lock);
        this.syncLock = lock;
        if (!lock) {
            filter();
            updateDocumentList();
        }
    }

    public List<DocumentObject> getDocsToDownload() {
        List<DocEntry> entries = latest ? latestEntries : allEntries;
        List<DocumentObject> docs = new ArrayList<DocumentObject>(entries.size());
        for (DocEntry e : entries) {
            docs.add(db.getDocumentOject(getTable(), e.getId()));
        }
        return docs;
    }

    public Map<String, String> getGroupNameToCodeMap() {
        return workingGroup.getGroupNameToCodeMap();
    }

    public String getTable() {
        return workingGroup.getTable();
    }

    public DocEntry[] getEntries() {
        return (latest ? latestEntries : allEntries).toArray(new DocEntry[0]);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    public void setDownloadProgress(final int progress) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                downloadProgressBar.setValue(progress);
            }
        });
    }
}
