/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

import javax.swing.JComponent;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public abstract class AbstractIOManager<T extends Object> implements Manager {

    //~ Instance fields --------------------------------------------------------

    protected transient CidsBean ioBean;
    private transient volatile JComponent ui;

    //~ Methods ----------------------------------------------------------------

    @Override
    public T getUR() throws IOException {
        final String json = (String)ioBean.getProperty("ur"); // NOI18N
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, getIOClass());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract Class<T> getIOClass();

    @Override
    public void finalise() throws IOException {
        // do nothing by default
    }

    @Override
    public Feature getFeature() throws IOException {
        // do nothing by default

        return null;
    }

    @Override
    public CidsBean getCidsBean() {
        return ioBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.ioBean = cidsBean;
    }

    @Override
    public JComponent getUI() {
        if (ui == null) {
            synchronized (this) {
                if (ui == null) {
                    ui = createUI();
                }
            }
        }

        return ui;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract JComponent createUI();
}
