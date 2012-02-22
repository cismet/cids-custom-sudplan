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
import org.openide.util.NbBundle;

import java.awt.Component;

import java.io.File;
import java.io.FileFilter;

import java.util.Iterator;
import java.util.NoSuchElementException;

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

    private final transient SingleFileFilter dynaFilter;
    private final transient SingleFileFilter geocpmfFilter;
    private final transient SingleFileFilter geocpmiFilter;
    private final transient SingleFileFilter geocpmsFilter;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ImportGeoCPMWizardPanelCFGSelect object.
     */
    public ImportGeoCPMWizardPanelCFGSelect() {
        changeSupport = new ChangeSupport(this);

        dynaFilter = new SingleFileFilter("DYNA.EIN");     // NOI18N
        geocpmfFilter = new SingleFileFilter("GEOCPMF.D"); // NOI18N
        geocpmiFilter = new SingleFileFilter("GEOCPMI.D"); // NOI18N
        geocpmsFilter = new SingleFileFilter("GEOCPMS.D"); // NOI18N
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

        changeSupport.fireChange();
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
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    ImportGeoCPMWizardPanelCFGSelect.class,
                    "ImportGeoCPMWizardPanelCFGSelect.isValid().info.chooseGeocpmFile")); // NOI18N

            return false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);

            if (geocpmFile.isFile() && geocpmFile.canRead()) {
                wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

                if (dynaFile == null) {
                    wizard.putProperty(
                        WizardDescriptor.PROP_INFO_MESSAGE,
                        NbBundle.getMessage(
                            ImportGeoCPMWizardPanelCFGSelect.class,
                            "ImportGeoCPMWizardPanelCFGSelect.isValid().info.chooseDynaFolder")); // NOI18N

                    return false;
                } else {
                    wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);

                    if (dynaFile.isDirectory()) {
                        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

                        if (dynaFile.canRead()) {
                            for (final SingleFileFilter currentFilter : getFilterIterator()) {
                                final File[] files = dynaFile.listFiles(currentFilter);
                                if (files.length == 1) {
                                    wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
                                } else {
                                    wizard.putProperty(
                                        WizardDescriptor.PROP_WARNING_MESSAGE,
                                        NbBundle.getMessage(
                                            ImportGeoCPMWizardPanelCFGSelect.class,
                                            "ImportGeoCPMWizardPanelCFGSelect.isValid().warn.dynaFolderWithoutFile(file)", // NOI18N
                                            currentFilter.getFilename()));
                                    return false;
                                }
                            }

                            return true;
                        } else {
                            wizard.putProperty(
                                WizardDescriptor.PROP_WARNING_MESSAGE,
                                NbBundle.getMessage(
                                    ImportGeoCPMWizardPanelCFGSelect.class,
                                    "ImportGeoCPMWizardPanelCFGSelect.isValid().warn.dynaFolderUnreadable")); // NOI18N

                            return false;
                        }
                    } else {
                        wizard.putProperty(
                            WizardDescriptor.PROP_WARNING_MESSAGE,
                            NbBundle.getMessage(
                                ImportGeoCPMWizardPanelCFGSelect.class,
                                "ImportGeoCPMWizardPanelCFGSelect.isValid().warn.dynaPathNoFolder")); // NOI18N

                        return false;
                    }
                }
            } else {
                wizard.putProperty(
                    WizardDescriptor.PROP_WARNING_MESSAGE,
                    NbBundle.getMessage(
                        ImportGeoCPMWizardPanelCFGSelect.class,
                        "ImportGeoCPMWizardPanelCFGSelect.isValid().warn.geocpmFileUnreadable")); // NOI18N

                return false;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NoSuchElementException         DOCUMENT ME!
     * @throws  IllegalStateException          DOCUMENT ME!
     * @throws  UnsupportedOperationException  DOCUMENT ME!
     */
    private Iterable<SingleFileFilter> getFilterIterator() {
        return new Iterable<SingleFileFilter>() {

                @Override
                public Iterator<SingleFileFilter> iterator() {
                    return new Iterator<SingleFileFilter>() {

                            private transient SingleFileFilter next = dynaFilter;

                            @Override
                            public boolean hasNext() {
                                return next != null;
                            }

                            @Override
                            public SingleFileFilter next() {
                                if (next == null) {
                                    throw new NoSuchElementException("end of iteration"); // NOI18N
                                }

                                final SingleFileFilter ret = next;

                                if (dynaFilter.getFilename().equals(next.getFilename())) {
                                    next = geocpmfFilter;
                                } else if (geocpmfFilter.getFilename().equals(next.getFilename())) {
                                    next = geocpmiFilter;
                                } else if (geocpmiFilter.getFilename().equals(next.getFilename())) {
                                    next = geocpmsFilter;
                                } else if (geocpmsFilter.getFilename().equals(next.getFilename())) {
                                    next = null;
                                } else {
                                    throw new IllegalStateException("unsupported filter: " + next); // NOI18N
                                }

                                return ret;
                            }

                            @Override
                            public void remove() {
                                throw new UnsupportedOperationException("Not supported"); // NOI18N
                            }
                        };
                }
            };
    }

    @Override
    public void addChangeListener(final ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(final ChangeListener l) {
        changeSupport.removeChangeListener(l);
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

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class SingleFileFilter implements FileFilter {

        //~ Instance fields ----------------------------------------------------

        private final transient String filename;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new SingleFileFilter object.
         *
         * @param  filename  DOCUMENT ME!
         */
        public SingleFileFilter(final String filename) {
            this.filename = filename;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean accept(final File pathname) {
            if (pathname == null) {
                return false;
            } else {
                return pathname.getName().equals(filename);
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getFilename() {
            return filename;
        }
    }
}
