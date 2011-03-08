/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingVisualPanelDatabase extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String SEP = " -> "; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final transient AirqualityDownscalingWizardPanelDatabase model;
    private final transient DocumentListener docL;
    private final transient ListSelectionListener listL;
    private final transient ActionListener chooseYearL;
    private final transient ActionListener removeL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChoose;
    private javax.swing.JButton btnRemove;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAvailableDbs;
    private javax.swing.JLabel lblChosenDbs;
    private javax.swing.JLabel lblYear;
    private javax.swing.JList lstAvailableDbs;
    private javax.swing.JList lstChosenDbs;
    private javax.swing.JTextField txtYear;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AirqualityDownscalingVisualPanelScenarios.
     *
     * @param  model  DOCUMENT ME!
     */
    public AirqualityDownscalingVisualPanelDatabase(final AirqualityDownscalingWizardPanelDatabase model) {
        this.model = model;
        this.docL = new DocumentListenerImpl();
        this.listL = new ListSelectionListenerImpl();
        this.chooseYearL = new ChooseYearActionListener();
        this.removeL = new RemoveActionListener();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelDatabase.class,
                "AirqualityDownscalingVisualPanelDatabase.this.name")); // NOI18N

        initComponents();

        txtYear.getDocument().addDocumentListener(WeakListeners.document(docL, txtYear.getDocument()));
        lstChosenDbs.addListSelectionListener(WeakListeners.create(ListSelectionListener.class, listL, lstChosenDbs));
        lstAvailableDbs.addListSelectionListener(WeakListeners.create(
                ListSelectionListener.class,
                listL,
                lblAvailableDbs));
        btnChoose.addActionListener(WeakListeners.create(ActionListener.class, chooseYearL, btnChoose));
        btnRemove.addActionListener(WeakListeners.create(ActionListener.class, removeL, btnRemove));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        final Map<String, Set<Integer>> databases = model.getDatabases();
        if (databases != null) {
            final DefaultListModel dlm = (DefaultListModel)lstChosenDbs.getModel();
            dlm.clear();

            for (final String key : databases.keySet()) {
                final Set<Integer> values = databases.get(key);
                for (final Integer value : values.toArray(new Integer[values.size()])) {
                    dlm.addElement(key + SEP + value);
                }
            }
        }

        final String[] availableDBs = model.getAvailableDatabases();

        assert availableDBs != null : "available DBs must not be null"; // NOI18N

        Arrays.sort(availableDBs);
        final DefaultListModel dlm = (DefaultListModel)lstAvailableDbs.getModel();
        dlm.clear();
        for (final String avaliable : availableDBs) {
            dlm.addElement(avaliable);
        }
        lstAvailableDbs.setSelectedIndex(0);

        final Integer year = ((model.getEndYear() - model.getStartYear()) / 2) + model.getStartYear();
        txtYear.setText(year.toString());

        btnChoose.setEnabled(buttonEnable());

        model.fireChangeEvent();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Map<String, Set<Integer>> getChosenDatabases() {
        final Map<String, Set<Integer>> map = new HashMap<String, Set<Integer>>();

        final DefaultListModel lstModel = (DefaultListModel)lstChosenDbs.getModel();
        final Enumeration<?> elements = lstModel.elements();

        while (elements.hasMoreElements()) {
            final String element = (String)elements.nextElement();
            final String[] split = element.split(SEP);
            final String dbname = split[0];
            final Integer year = Integer.valueOf(split[1]);

            Set<Integer> years = map.get(dbname);

            if (years == null) {
                years = new HashSet<Integer>();
                map.put(dbname, years);
            }

            years.add(year);
        }

        return map;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getChosenYear() {
        return txtYear.getText();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblAvailableDbs = new javax.swing.JLabel();
        lblChosenDbs = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstAvailableDbs = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstChosenDbs = new javax.swing.JList();
        btnChoose = new javax.swing.JButton();
        lblYear = new javax.swing.JLabel();
        txtYear = new javax.swing.JTextField();
        btnRemove = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(200, 150));
        setPreferredSize(new java.awt.Dimension(450, 300));
        setLayout(new java.awt.GridBagLayout());

        lblAvailableDbs.setText(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelDatabase.class,
                "AirqualityDownscalingVisualPanelDatabase.lblAvailableDbs.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblAvailableDbs, gridBagConstraints);

        lblChosenDbs.setText(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelDatabase.class,
                "AirqualityDownscalingVisualPanelDatabase.lblChosenDbs.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblChosenDbs, gridBagConstraints);

        lstAvailableDbs.setModel(new DefaultListModel());
        lstAvailableDbs.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(lstAvailableDbs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane1, gridBagConstraints);

        lstChosenDbs.setModel(new DefaultListModel());
        jScrollPane2.setViewportView(lstChosenDbs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane2, gridBagConstraints);

        btnChoose.setText(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelDatabase.class,
                "AirqualityDownscalingVisualPanelDatabase.btnChoose.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(btnChoose, gridBagConstraints);

        lblYear.setText(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelDatabase.class,
                "AirqualityDownscalingVisualPanelDatabase.lblYear.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 1, 6);
        add(lblYear, gridBagConstraints);

        txtYear.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtYear.setText(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelDatabase.class,
                "AirqualityDownscalingVisualPanelDatabase.txtYear.text")); // NOI18N
        txtYear.setMaximumSize(new java.awt.Dimension(50, 28));
        txtYear.setMinimumSize(new java.awt.Dimension(50, 28));
        txtYear.setPreferredSize(new java.awt.Dimension(50, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        add(txtYear, gridBagConstraints);

        btnRemove.setText(NbBundle.getMessage(
                AirqualityDownscalingVisualPanelDatabase.class,
                "AirqualityDownscalingVisualPanelDatabase.btnRemove.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(13, 13, 13, 13);
        add(btnRemove, gridBagConstraints);
    }                                                                        // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean buttonEnable() {
        boolean enable;
        if (yearEnable()) {
            final String selected = (String)lstAvailableDbs.getSelectedValue();
            if (selected == null) {
                enable = false;
            } else {
                final Map<String, Set<Integer>> chosen = getChosenDatabases();
                final Set<Integer> years = chosen.get(selected);
                if (years == null) {
                    enable = true;
                } else {
                    final Integer year = Integer.parseInt(txtYear.getText());
                    if (years.contains(year)) {
                        enable = false;
                    } else {
                        enable = true;
                    }
                }
            }
        } else {
            enable = false;
        }

        return enable;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean yearEnable() {
        final String year = txtYear.getText();

        if ((year == null) || year.isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(year);

            return true;
        } catch (final NumberFormatException ex) {
            return false;
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class RemoveActionListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            final int selected = lstChosenDbs.getSelectedIndex();
            if (selected > -1) {
                ((DefaultListModel)lstChosenDbs.getModel()).remove(selected);

                model.fireChangeEvent();
                btnChoose.setEnabled(buttonEnable());
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ListSelectionListenerImpl implements ListSelectionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void valueChanged(final ListSelectionEvent e) {
            model.fireChangeEvent();

            btnChoose.setEnabled(buttonEnable());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class DocumentListenerImpl implements DocumentListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void changedUpdate(final DocumentEvent e) {
            model.fireChangeEvent();

            btnChoose.setEnabled(buttonEnable());
        }

        @Override
        public void insertUpdate(final DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
            changedUpdate(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ChooseYearActionListener implements ActionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            final String selectedDb = (String)lstAvailableDbs.getSelectedValue();
            final String year = txtYear.getText();

            ((DefaultListModel)lstChosenDbs.getModel()).addElement(selectedDb + SEP + year);

            btnChoose.setEnabled(buttonEnable());
        }
    }
}
