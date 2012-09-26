/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.local.wupp;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import java.util.MissingResourceException;

import javax.swing.Icon;
import javax.swing.JComponent;

import de.cismet.cismap.navigatorplugin.CismapPlugin;

import de.cismet.tools.gui.BasicGuiComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   jlauter
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = BasicGuiComponentProvider.class)
public class DeltaConfigurationListWidgedProvider implements BasicGuiComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(DeltaConfigurationListWidgedProvider.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getId() {
        return "sudplan.configselection";
    }

    @Override
    public String getDescription() {
        String description = "Configuration list widged";

        try {
            description = org.openide.util.NbBundle.getMessage(
                    DeltaConfigurationListWidgedProvider.class,
                    "DeltaConfigurationListWidgedProvider.getDescription().description");
        } catch (final MissingResourceException ex) {
            LOG.info("I18Nized message for 'ConfigurationSelectionWidgedProvider.description' not found.", ex);
        }

        return description;
    }

    @Override
    public Icon getIcon() {
        return new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/local/wupp/geocpm_delta_16.png"));
    }

    @Override
    public JComponent getComponent() {
        return DeltaConfigurationListWidged.getInstance();
    }

    @Override
    public GuiType getType() {
        return BasicGuiComponentProvider.GuiType.GUICOMPONENT;
    }

    @Override
    public Object getPositionHint() {
        return CismapPlugin.ViewSection.LAYER_INFO;
    }

    @Override
    public void setLinkObject(final Object link) {
    }

    @Override
    public String getName() {
        String name = "Configuration selection";
        try {
            name = org.openide.util.NbBundle.getMessage(
                    DeltaConfigurationListWidgedProvider.class,
                    "DeltaConfigurationListWidgedProvider.getName().name");
        } catch (final MissingResourceException ex) {
            LOG.info("I18Nized message for 'DeltaConfigurationListWidgedProvider.getName().name' not found.", ex);
        }
        return name;
    }
}
