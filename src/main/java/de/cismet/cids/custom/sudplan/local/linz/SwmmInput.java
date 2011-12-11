/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SwmmInput {

    //~ Static fields/initializers ---------------------------------------------

    public static final String TABLENAME_SWMM_PROJECT = "SWMM_PROJECT"; // NOI18N
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final String PROP_TIMESERIES = "timeseries";
    public static final String PROP_INPFILE = "inpFile";
    public static final String PROP_STARTDATE = "startDate";
    public static final String PROP_SWMMPROJECT = "swmmProject";
    public static final String PROP_ENDDATE = "endDate";
    public static final String PROP_FORECAST = "forecast";

    //~ Instance fields --------------------------------------------------------

    protected transient List<Integer> timeseries = new ArrayList<Integer>();

    protected transient String inpFile;

    protected transient String startDate;

    protected transient int swmmProject = -1;

    protected transient String endDate;

    protected boolean forecast = false;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the value of timeseries.
     *
     * @return  the value of timeseries
     */
    public List<Integer> getTimeseries() {
        return timeseries;
    }

    /**
     * Set the value of timeseries.
     *
     * @param  timeseries  new value of timeseries
     */
    public void setTimeseries(final List<Integer> timeseries) {
        final List<Integer> oldTimeseries = this.timeseries;
        this.timeseries = timeseries;
        propertyChangeSupport.firePropertyChange(PROP_TIMESERIES, oldTimeseries, timeseries);
    }

    /**
     * Get the value of timeseries at specified index.
     *
     * @param   index  DOCUMENT ME!
     *
     * @return  the value of timeseries at specified index
     */
    public int getTimeseries(final int index) {
        return this.timeseries.get(index);
    }

    /**
     * Set the value of timeseries at specified index.
     *
     * @param  index          DOCUMENT ME!
     * @param  newTimeseries  new value of timeseries at specified index
     */
    public void setTimeseries(final int index, final int newTimeseries) {
        final int oldTimeseries = this.timeseries.get(index);
        this.timeseries.set(index, newTimeseries);
        propertyChangeSupport.fireIndexedPropertyChange(PROP_TIMESERIES, index, oldTimeseries, newTimeseries);
    }

    /**
     * Get the value of startDate.
     *
     * @return  the value of startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Get the value of swmmProject.
     *
     * @return  the value of swmmProject
     */
    public int getSwmmProject() {
        return swmmProject;
    }

    /**
     * Set the value of swmmProject.
     *
     * @param  swmmProject  new value of swmmProject
     */
    public void setSwmmProject(final int swmmProject) {
        final int oldSwmmProject = this.swmmProject;
        this.swmmProject = swmmProject;
        propertyChangeSupport.firePropertyChange(PROP_SWMMPROJECT, oldSwmmProject, swmmProject);
    }

    /**
     * Set the value of startDate.
     *
     * @param  startDate  new value of startDate
     */
    public void setStartDate(final String startDate) {
        final String oldStartDate = this.startDate;
        this.startDate = startDate;
        propertyChangeSupport.firePropertyChange(PROP_STARTDATE, oldStartDate, startDate);
    }

    /**
     * Get the value of endDate.
     *
     * @return  the value of endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Set the value of endDate.
     *
     * @param  endDate  new value of endDate
     */
    public void setEndDate(final String endDate) {
        final String oldEndDate = this.endDate;
        this.endDate = endDate;
        propertyChangeSupport.firePropertyChange(PROP_ENDDATE, oldEndDate, endDate);
    }

    /**
     * Get the value of inpFile.
     *
     * @return  the value of inpFile
     */
    public String getInpFile() {
        return inpFile;
    }

    /**
     * Set the value of inpFile.
     *
     * @param  inpFile  new value of inpFile
     */
    public void setInpFile(final String inpFile) {
        final String oldInpFile = this.inpFile;
        this.inpFile = inpFile;
        propertyChangeSupport.firePropertyChange(PROP_INPFILE, oldInpFile, inpFile);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ParseException  DOCUMENT ME!
     */
    public Date getEndDateDate() throws ParseException {
        return DATE_FORMAT.parse(this.getEndDate());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ParseException  DOCUMENT ME!
     */
    public Date getStartDateDate() throws ParseException {
        return DATE_FORMAT.parse(this.getStartDate());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> fetchTimeseries() {
        assert this.timeseries != null : "timeseries list is null";
        final List<CidsBean> timeseriesBeans = new ArrayList<CidsBean>(this.timeseries.size());
        for (final int timeseriesId : this.timeseries) {
            timeseriesBeans.add(SMSUtils.fetchCidsBean(timeseriesId, SMSUtils.TABLENAME_TIMESERIES));
        }

        return timeseriesBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchSwmmProject() {
        return SMSUtils.fetchCidsBean(this.getSwmmProject(), TABLENAME_SWMM_PROJECT);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Get the value of forecast.
     *
     * @return  the value of forecast
     */
    public boolean isForecast() {
        return forecast;
    }

    /**
     * Set the value of forecast.
     *
     * @param  forecast  new value of forecast
     */
    public void setForecast(final boolean forecast) {
        final boolean oldForecast = this.forecast;
        this.forecast = forecast;
        propertyChangeSupport.firePropertyChange(PROP_FORECAST, oldForecast, forecast);
    }
}
