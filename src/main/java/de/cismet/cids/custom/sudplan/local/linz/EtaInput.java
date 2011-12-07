/*
 * Copyright (C) 2011 cismet GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.cids.custom.sudplan.local.linz;

import java.util.HashMap;
import java.util.Map;

/**
 * Input for the ETA Calculation is the output of the SWMM Model Run!
 * 
 * @author Pascal Dihé
 */
public class EtaInput extends SwmmOutput {

    protected transient Map<String, EtaConfiguration> etaConfigurations = new HashMap<String, EtaConfiguration>();

    public Map<String, EtaConfiguration> getEtaConfigurations() {
        return this.etaConfigurations;
    }

    public void setEtaConfigurations(Map<String, EtaConfiguration> etaConfigurations) {
        this.etaConfigurations = etaConfigurations;
    }

    //TODO: don't store output 2x, provide to model output id, implement fetch operations
    public class EtaConfiguration {

        protected transient boolean enabled;
        protected transient float sedimentationEfficency;
        protected transient String name;

        /**
         * Get the value of name
         *
         * @return the value of name
         */
        public String getName() {
            return name;
        }

        /**
         * Set the value of name
         *
         * @param name new value of name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Get the value of sedimentationEfficency
         *
         * @return the value of sedimentationEfficency
         */
        public float getSedimentationEfficency() {
            return sedimentationEfficency;
        }

        /**
         * Set the value of sedimentationEfficency
         *
         * @param sedimentationEfficency new value of sedimentationEfficency
         */
        public void setSedimentationEfficency(float sedimentationEfficency) {
            this.sedimentationEfficency = sedimentationEfficency;
        }

        /**
         * Get the value of enabled
         *
         * @return the value of enabled
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Set the value of enabled
         *
         * @param enabled new value of enabled
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
