/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * TimeseriesAggregationRenderer.java
 *
 * Created on 17.10.2011, 14:17:11
 */
package de.cismet.cids.custom.objectrenderer.sudplan;

import org.apache.log4j.Logger;

import java.awt.BorderLayout;

import java.net.MalformedURLException;

import java.util.Collection;
import java.util.HashMap;

import javax.swing.JPanel;

import de.cismet.cids.custom.sudplan.Resolution;
import de.cismet.cids.custom.sudplan.SMSUtils;
import de.cismet.cids.custom.sudplan.TimeseriesChartPanel;
import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanAggregationRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeseriesAggregationRenderer extends JPanel implements CidsBeanAggregationRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(TimeseriesAggregationRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private Collection<CidsBean> cidsBeans;
    private transient TimeseriesChartPanel panel;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form TimeseriesAggregationRenderer.
     */
    public TimeseriesAggregationRenderer() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setOpaque(false);
        setLayout(new java.awt.BorderLayout());
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public Collection<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    @Override
    public void setCidsBeans(final Collection<CidsBean> beans) {
        if (beans != null) {
            this.cidsBeans = beans;
        }
        final HashMap<TimeseriesRetrieverConfig, TimeseriesConverter> beanConfigs =
            new HashMap<TimeseriesRetrieverConfig, TimeseriesConverter>();
        for (final CidsBean cidsBean : cidsBeans) {
            try {
                final String uri = (String)cidsBean.getProperty("uri"); // NOI18N
                final TimeseriesConverter converter = SMSUtils.loadConverter(cidsBean);

                final TimeseriesRetrieverConfig config = TimeseriesRetrieverConfig.fromUrl(uri);
                final Resolution previewResolution = TimeSeriesRendererUtil.getPreviewResolution(config);

                beanConfigs.put(config.changeResolution(previewResolution), converter);
            } catch (MalformedURLException ex) {
                final String message = "cidsbean contains invalid uri"; // NOI18N
                LOG.error(message, ex);
                throw new IllegalStateException(message, ex);
            }
        }
        panel = new TimeseriesChartPanel(beanConfigs, false, null);
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void dispose() {
        if (panel != null) {
            panel.dispose();
        }
    }

    @Override
    public String getTitle() {
        if (cidsBeans == null) {
            return "Comparison of 0 Time series";                        // NOI18N
        } else {
            return "Comparison of " + cidsBeans.size() + " Time series"; // NOI18N
        }
    }

    @Override
    public void setTitle(final String title) {
        // noop
    }
}
