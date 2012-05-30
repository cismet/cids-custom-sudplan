/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.airquality;

import java.util.Collection;

import de.cismet.cids.custom.sudplan.Resolution;
import de.cismet.cids.custom.sudplan.Variable;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class AirqualityDownscalingOutput {

    //~ Instance fields --------------------------------------------------------

    private transient int modelInputId;
    private transient int modelRunId;
    private transient Collection<Result> results;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AirqualityDownscalingOutput object.
     */
    public AirqualityDownscalingOutput() {
    }

    /**
     * Creates a new AirqualityDownscalingOutput object.
     *
     * @param  modelInputId  DOCUMENT ME!
     * @param  modelRunId    DOCUMENT ME!
     */
    public AirqualityDownscalingOutput(final int modelInputId,
            final int modelRunId) {
        this.modelInputId = modelInputId;
        this.modelRunId = modelRunId;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getModelInputId() {
        return modelInputId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  modelInputId  DOCUMENT ME!
     */
    public void setModelInputId(final int modelInputId) {
        this.modelInputId = modelInputId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getModelRunId() {
        return modelRunId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  modelRunId  DOCUMENT ME!
     */
    public void setModelRunId(final int modelRunId) {
        this.modelRunId = modelRunId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<Result> getResults() {
        return results;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  results  DOCUMENT ME!
     */
    public void setResults(final Collection<Result> results) {
        this.results = results;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class Result {

        //~ Instance fields ----------------------------------------------------

        private String url;
        private String type;
        private String description;
        private String offering;
        private Variable variable;
        private Resolution resolution;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new Result object.
         */
        public Result() {
        }

        /**
         * Creates a new Result object.
         *
         * @param  url          DOCUMENT ME!
         * @param  type         DOCUMENT ME!
         * @param  description  DOCUMENT ME!
         * @param  offering     DOCUMENT ME!
         */
        public Result(final String url, final String type, final String description, final String offering) {
            this.url = url;
            this.type = type;
            this.description = description;
            this.offering = offering;

            extractResolution(offering);
            extractVariable(offering);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  offering  DOCUMENT ME!
         */
        protected void extractVariable(final String offering) {
            final String offeringWithoutResolution = offering.substring(0, offering.lastIndexOf('_'));
            final String variableFromOffering = offeringWithoutResolution.substring(
                    offeringWithoutResolution.lastIndexOf('_')
                            + 1);
            if ("NO2".equalsIgnoreCase(variableFromOffering)) {          // NOI18N
                this.variable = Variable.NO2;
            } else if ("NOX".equalsIgnoreCase(variableFromOffering)) {   // NOI18N
                this.variable = Variable.NOX;
            } else if ("OZONE".equalsIgnoreCase(variableFromOffering)) { // NOI18N
                this.variable = Variable.O3;
            } else if ("PM10".equalsIgnoreCase(variableFromOffering)) {  // NOI18N
                this.variable = Variable.PM10;
            } else if ("SO2".equalsIgnoreCase(variableFromOffering)) {   // NOI18N
                this.variable = Variable.SO2;
            } else {
                this.variable = null;
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  offering  DOCUMENT ME!
         */
        protected void extractResolution(final String offering) {
            final String resolutionFromOffering = offering.substring(offering.lastIndexOf('_') + 1);
            for (final Resolution resolution : Resolution.values()) {
                if (resolution.getOfferingSuffix().equalsIgnoreCase(resolutionFromOffering)) {
                    this.resolution = resolution;
                    break;
                }
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getDescription() {
            return description;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getOffering() {
            return offering;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getType() {
            return type;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getUrl() {
            return url;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Resolution getResolution() {
            if (resolution == null) {
                extractResolution(offering);
            }

            return resolution;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Variable getVariable() {
            if (variable == null) {
                extractVariable(offering);
            }

            return variable;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  description  DOCUMENT ME!
         */
        public void setDescription(final String description) {
            this.description = description;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  offering  DOCUMENT ME!
         */
        public void setOffering(final String offering) {
            this.offering = offering;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  type  DOCUMENT ME!
         */
        public void setType(final String type) {
            this.type = type;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  url  DOCUMENT ME!
         */
        public void setUrl(final String url) {
            this.url = url;
        }
    }
}
