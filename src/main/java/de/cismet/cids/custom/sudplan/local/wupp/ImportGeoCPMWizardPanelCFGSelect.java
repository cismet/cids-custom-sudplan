/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

import java.awt.Component;

import java.io.File;

import javax.swing.event.ChangeListener;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class ImportGeoCPMWizardPanelCFGSelect implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(ImportGeoCPMWizardPanelCFGSelect.class);

    public static final String PROP_GEOCPM_FILE = "__prop_geocpm_file__"; // NOI18N
    public static final String PROP_DYNA_FILE = "__prop_dyna_file__";     // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;

    private transient volatile ImportGeoCPMVisualPanelCFGSelect component;

    private transient File geocpmFile;
    private transient File dynaFile;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ImportGeoCPMWizardPanelCFGSelect object.
     */
    public ImportGeoCPMWizardPanelCFGSelect() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            synchronized (this) {
                if (component == null) {
                    try {
                        component = new ImportGeoCPMVisualPanelCFGSelect(this);
                    } catch (final WizardInitialisationException ex) {
                        LOG.error("cannot create wizard panel component", ex); // NOI18N
                    }
                }
            }
        }

        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;

        geocpmFile = (File)wizard.getProperty(PROP_GEOCPM_FILE);
        dynaFile = (File)wizard.getProperty(PROP_DYNA_FILE);

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;

        wizard.putProperty(PROP_GEOCPM_FILE, geocpmFile);
        wizard.putProperty(PROP_DYNA_FILE, dynaFile);
    }

    @Override
    public boolean isValid() {
        if (geocpmFile == null) {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Please choose a GeoCPM configuration file");

            return false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);

            if (geocpmFile.isFile() && geocpmFile.canRead()) {
                wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

                if (dynaFile == null) {
                    return true;
                } else {
                    if (dynaFile.isFile() && dynaFile.canRead()) {
                        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

                        return true;
                    } else {
                        wizard.putProperty(
                            WizardDescriptor.PROP_WARNING_MESSAGE,
                            "The chosen Dyna configuration file is unreadable");

                        return false;
                    }
                }
            } else {
                wizard.putProperty(
                    WizardDescriptor.PROP_WARNING_MESSAGE,
                    "The chosen GeoCPM configuration file is unreadable");

                return false;
            }
        }
    }

    @Override
    public void addChangeListener(final ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(final ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    File getDynaFile() {
        return dynaFile;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dynaFile  DOCUMENT ME!
     */
    void setDynaFile(final File dynaFile) {
        this.dynaFile = dynaFile;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    File getGeocpmFile() {
        return geocpmFile;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geocpmFile  DOCUMENT ME!
     */
    void setGeocpmFile(final File geocpmFile) {
        this.geocpmFile = geocpmFile;

        changeSupport.fireChange();
    }
}
