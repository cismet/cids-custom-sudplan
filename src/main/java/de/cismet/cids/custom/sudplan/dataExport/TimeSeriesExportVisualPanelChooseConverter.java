/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * TimeSeriesImportFileChoosePanel.java
 *
 * Created on 07.12.2011, 14:37:26
 */
package de.cismet.cids.custom.sudplan.dataExport;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import de.cismet.cids.custom.sudplan.converter.LinzNetcdfConverter;
import de.cismet.cids.custom.sudplan.converter.LinzTimeseriesConverter;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;
import de.cismet.cids.custom.sudplan.converter.WuppertalTimeseriesConverter;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class TimeSeriesExportVisualPanelChooseConverter extends javax.swing.JPanel {

    //~ Instance fields --------------------------------------------------------

    private final transient TimeSeriesExportWizardPanelChooseConverter model;

    private final transient ItemListener chooseL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboConverterChooser;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form TimeSeriesImportFileChoosePanel.
     *
     * @param   model  DOCUMENT ME!
     *
     * @throws  IllegalStateException  NullPointerException DOCUMENT ME!
     */
    public TimeSeriesExportVisualPanelChooseConverter(final TimeSeriesExportWizardPanelChooseConverter model) {
        if (model == null) {
            throw new IllegalStateException("model instance must not be null"); // NOI18N
        }

        this.chooseL = new ItemListenerImpl();

        initComponents();

        this.setName(NbBundle.getMessage(
                TimeSeriesExportVisualPanelChooseConverter.class,
                "TimeSeriesExportVisualPanelChooseConverter.this.name")); // NOI18N
        this.model = model;

        cboConverterChooser.addItemListener(WeakListeners.create(ItemListener.class, chooseL, cboConverterChooser));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void init() {
        // TODO: use lookup
        this.cboConverterChooser.removeAllItems();
        this.cboConverterChooser.addItem(new WuppertalTimeseriesConverter());
        this.cboConverterChooser.addItem(new LinzTimeseriesConverter());
        this.cboConverterChooser.addItem(new LinzNetcdfConverter());
        this.cboConverterChooser.setSelectedIndex(0);

        final TimeseriesConverter converter = model.getTimeseriesConverter();
        if (converter == null) {
            model.setTimeseriesConverter((TimeseriesConverter)cboConverterChooser.getSelectedItem());
        } else {
            cboConverterChooser.setSelectedItem(converter);
        }
    }

    /**
     * DOCUMENT ME!
     */

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        cboConverterChooser = new javax.swing.JComboBox();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        cboConverterChooser.setMinimumSize(new java.awt.Dimension(300, 27));
        cboConverterChooser.setPreferredSize(new java.awt.Dimension(300, 27));
        jPanel1.add(cboConverterChooser, new java.awt.GridBagConstraints());

        add(jPanel1, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ItemListenerImpl implements ItemListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void itemStateChanged(final ItemEvent e) {
            model.setTimeseriesConverter((TimeseriesConverter)cboConverterChooser.getSelectedItem());
        }
    }
}
