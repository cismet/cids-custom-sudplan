/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import at.ac.ait.enviro.tsapi.handler.DataHandler;
import at.ac.ait.enviro.tsapi.handler.Datapoint;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;

import net.opengis.sps.v_1_0_0.InputDescriptor;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.EventQueue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import de.cismet.cids.custom.sudplan.AbstractWizardPanel;
import de.cismet.cids.custom.sudplan.DataHandlerCache;
import de.cismet.cids.custom.sudplan.SudplanOptions;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.server.search.EmissionDatabaseSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingWizardPanelDatabase extends AbstractWizardPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(AirqualityDownscalingWizardPanelDatabase.class);

    //~ Instance fields --------------------------------------------------------

    private transient AirqualityDownscalingVisualPanelDatabase component;

    private transient String database;
    private transient List<CidsBean> databases;
    private transient Exception spsError;

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component createComponent() {
        if (component == null) {
            component = new AirqualityDownscalingVisualPanelDatabase(this);
        }

        return component;
    }

    @Override
    public void read(final WizardDescriptor wizard) {
        database = (String)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_DATABASE);
        databases = (List<CidsBean>)wizard.getProperty(AirqualityDownscalingWizardAction.PROP_DATABASES);

        if ((databases != null) && (!databases.isEmpty())) {
            component.init();
            return;
        }

        spsError = null;

        SudplanConcurrency.getSudplanGeneralPurposePool().execute(new Runnable() {

                @Override
                public void run() {
                    databases = new LinkedList<CidsBean>();
                    final List<String> databasesFromSPS = new LinkedList<String>();

                    try {
                        final Properties filter = new Properties();
                        filter.put(TimeSeries.PROCEDURE, AirqualityDownscalingModelManager.AQ_TS_DS_PROCEDURE);

                        final DataHandler dataHandler = DataHandlerCache.getInstance()
                                    .getSPSDataHandler(
                                        AirqualityDownscalingModelManager.AQ_SPS_LOOKUP,
                                        SudplanOptions.getInstance().getAqSpsUrl());
                        final Datapoint datapoint = dataHandler.createDatapoint(filter, null, DataHandler.Access.READ);
                        final InputDescriptor inputDescriptor = (InputDescriptor)datapoint.getProperties()
                                    .get("jaxb_desc:emission_scenario");              // NOI18N
                        databasesFromSPS.addAll(
                            inputDescriptor.getDefinition().getCommonData().getCategory().getConstraint()
                                        .getAllowedTokens().getValueList().get(0).getValue());
                    } catch (final Exception ex) {
                        LOG.error("Couldn't fetch emission databases from SPS.", ex); // NOI18N
                        spsError = ex;
                    }

                    try {
                        final Collection<MetaObject> databaseMetaObjectsFromSMS = SessionManager.getProxy()
                                    .customServerSearch(new EmissionDatabaseSearch());
                        final Collection<CidsBean> databasesFromSMS = new LinkedList<CidsBean>();
                        for (final MetaObject databaseMetaObjectFromSMS : databaseMetaObjectsFromSMS) {
                            databasesFromSMS.add(databaseMetaObjectFromSMS.getBean());
                        }

                        final Collection<String> databaseNamesFromSMS = new LinkedList<String>();
                        for (final CidsBean databaseCandidate : databasesFromSMS) {
                            final Object databaseName = databaseCandidate.getProperty("name"); // NOI18N

                            if (databaseName instanceof String) {
                                databaseNamesFromSMS.add((String)databaseName);

                                if (databasesFromSPS.contains((String)databaseName)) {
                                    databases.add(databaseCandidate);
                                }
                            }
                        }

                        final MetaClass metaClass = ClassCacheMultiple.getMetaClass(
                                SessionManager.getSession().getUser().getDomain(),
                                "emission_database");                                       // NOI18N
                        for (final String databaseFromSPS : databasesFromSPS) {
                            if (!databaseNamesFromSMS.contains(databaseFromSPS)) {
                                final CidsBean database = metaClass.getEmptyInstance().getBean();
                                database.setProperty("name", databaseFromSPS);              // NOI18N
                                databases.add(database);
                            }
                        }
                    } catch (final Exception ex) {
                        LOG.error("Couln't convert result from SPS/SMS to CidsBeans.", ex); // NOI18N
                        spsError = ex;
                    }

                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                component.init();

                                changeSupport.fireChange();
                            }
                        });
                }
            });

        component.init();
    }

    @Override
    public void store(final WizardDescriptor wizard) {
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_DATABASE, database);
        wizard.putProperty(AirqualityDownscalingWizardAction.PROP_DATABASES, databases);
    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);

        if (spsError != null) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelDatabase.class,
                    "AirqualityDownscalingWizardPanelDatabase.isValid().spsError")); // NOI18N
            valid = false;
        }

        if ((database == null) || (database.trim().length() <= 0)) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    AirqualityDownscalingWizardPanelDatabase.class,
                    "AirqualityDownscalingWizardPanelDatabase.isValid().noDatabase")); // NOI18N
            valid = false;
        }

        return valid;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDatabase() {
        return database;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  database  DOCUMENT ME!
     */
    public void setDatabase(final String database) {
        this.database = database;

        changeSupport.fireChange();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getDatabases() {
        return databases;
    }
}
