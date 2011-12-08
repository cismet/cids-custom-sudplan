/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.linz;

/**
 * This is the output of the ETA (efficency rates) calculation.
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
public class EtaOutput {

    //~ Instance fields --------------------------------------------------------

    /**
     * Statistische Niederschlagsintensität (in mm/12h) mit einer Dauerstufe von 720 Minuten (12h) und einer
     * Wiederkehrperiode von 1 Jahr Engl: „statistical rainfall intensity with a duration of 12 h and return period once
     * per year (r720,1)”
     */
    protected int r720;

    /**
     * Mindestwirkungsgrad (der Weiterleitung) für gelöste Stoffe (required CSO efficiency for dissolved pollutants),
     * definiert im ÖWAV Regelblatt 19.
     */
    protected float etaHydRequired;
    /**
     * Mindestwirkungsgrad (der Weiterleitung) für abfiltrierbare Stoffe (required CSO efficiency for particulate
     * pollutants), definiert im ÖWAV Regelblatt 19.
     */
    protected float etaSedRequired;
    /**
     * Vom Modell berechneter Wirkungsgrad (der Weiterleitung) für gelöste Stoffe (CSO efficiency for dissolved
     * pollutants).
     */
    protected float etaHydActual;
    /**
     * Vom Modell berechnete Wirkungsgrad (der Weiterleitung) für abfiltrierbare Stoffe (CSO efficiency for particulate
     * pollutants).
     */
    protected float etaSedActual;

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the value of r720.
     *
     * @return  the value of r720
     */
    public int getR720() {
        return r720;
    }

    /**
     * Set the value of r720.
     *
     * @param  r720  new value of r720
     */
    public void setR720(final int r720) {
        this.r720 = r720;
    }

    /**
     * Get the value of etaHydRequired.
     *
     * @return  the value of etaHydRequired
     */
    public float getEtaHydRequired() {
        return etaHydRequired;
    }

    /**
     * Set the value of etaHydRequired.
     *
     * @param  etaHydRequired  new value of etaHydRequired
     */
    public void setEtaHydRequired(final float etaHydRequired) {
        this.etaHydRequired = etaHydRequired;
    }

    /**
     * Get the value of etaSedRequired.
     *
     * @return  the value of etaSedRequired
     */
    public float getEtaSedRequired() {
        return etaSedRequired;
    }

    /**
     * Set the value of etaSedRequired.
     *
     * @param  etaSedRequired  new value of etaSedRequired
     */
    public void setEtaSedRequired(final float etaSedRequired) {
        this.etaSedRequired = etaSedRequired;
    }

    /**
     * Get the value of etaHydActual.
     *
     * @return  the value of etaHydActual
     */
    public float getEtaHydActual() {
        return etaHydActual;
    }

    /**
     * Set the value of etaHydActual.
     *
     * @param  etaHydActual  new value of etaHydActual
     */
    public void setEtaHydActual(final float etaHydActual) {
        this.etaHydActual = etaHydActual;
    }

    /**
     * Get the value of etaSedActual.
     *
     * @return  the value of etaSedActual
     */
    public float getEtaSedActual() {
        return etaSedActual;
    }

    /**
     * Set the value of etaSedActual.
     *
     * @param  etaSedActual  new value of etaSedActual
     */
    public void setEtaSedActual(final float etaSedActual) {
        this.etaSedActual = etaSedActual;
    }
}
