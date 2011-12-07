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

/*
 * EtaOutputManagerUI.java
 *
 * Created on 07.12.2011, 19:05:30
 */
package de.cismet.cids.custom.sudplan.local.linz;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author pd
 */
public class EtaOutputManagerUI extends javax.swing.JPanel {

    private static final transient Logger LOG = Logger.getLogger(EtaInputManagerUI.class);
    
   private final transient EtaOutputManager outputManager;
    
    /** Creates new form EtaOutputManagerUI */
    public EtaOutputManagerUI(EtaOutputManager outputManager) {
        this.outputManager = outputManager;
        initComponents();
        init();
    }
    
    private void init()
    {
        
    }
    
    public EtaOutput getEtaOutput()
    {
        try {
            return this.outputManager.getUR();
        } catch (IOException ex) {
            LOG.error("could not load eta output", ex);
        }
        
        return null;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        lbl_r720 = new javax.swing.JLabel();
        lbl_etaHydRequired = new javax.swing.JLabel();
        lbl_etaSedRequired = new javax.swing.JLabel();
        lbl_etaHydActual = new javax.swing.JLabel();
        lbl_etaSedActual = new javax.swing.JLabel();
        fld_720 = new javax.swing.JTextField();
        fld_etaHydRequired = new javax.swing.JTextField();
        fld_etaSedRequired = new javax.swing.JTextField();
        fld_etaHydActual = new javax.swing.JTextField();
        fld_etaSedActual = new javax.swing.JTextField();

        lbl_r720.setText(org.openide.util.NbBundle.getMessage(EtaOutputManagerUI.class, "EtaOutputManagerUI.lbl_r720.text")); // NOI18N
        lbl_r720.setToolTipText(org.openide.util.NbBundle.getMessage(EtaOutputManagerUI.class, "EtaOutputManagerUI.lbl_r720.toolTipText")); // NOI18N

        lbl_etaHydRequired.setText(org.openide.util.NbBundle.getMessage(EtaOutputManagerUI.class, "EtaOutputManagerUI.lbl_etaHydRequired.text")); // NOI18N
        lbl_etaHydRequired.setToolTipText(org.openide.util.NbBundle.getMessage(EtaOutputManagerUI.class, "EtaOutputManagerUI.lbl_etaHydRequired.toolTipText")); // NOI18N

        lbl_etaSedRequired.setText(org.openide.util.NbBundle.getMessage(EtaOutputManagerUI.class, "EtaOutputManagerUI.lbl_etaSedRequired.text")); // NOI18N
        lbl_etaSedRequired.setToolTipText(org.openide.util.NbBundle.getMessage(EtaOutputManagerUI.class, "EtaOutputManagerUI.lbl_etaSedRequired.toolTipText")); // NOI18N

        lbl_etaHydActual.setText(org.openide.util.NbBundle.getMessage(EtaOutputManagerUI.class, "EtaOutputManagerUI.lbl_etaHydActual.text")); // NOI18N
        lbl_etaHydActual.setToolTipText(org.openide.util.NbBundle.getMessage(EtaOutputManagerUI.class, "EtaOutputManagerUI.lbl_etaHydActual.toolTipText")); // NOI18N

        lbl_etaSedActual.setText(org.openide.util.NbBundle.getMessage(EtaOutputManagerUI.class, "EtaOutputManagerUI.lbl_etaSedActual.text")); // NOI18N
        lbl_etaSedActual.setToolTipText(org.openide.util.NbBundle.getMessage(EtaOutputManagerUI.class, "EtaOutputManagerUI.lbl_etaSedActual.toolTipText")); // NOI18N

        fld_720.setColumns(4);
        fld_720.setEnabled(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${etaOutput.r720}"), fld_720, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("0.0");
        binding.setSourceUnreadableValue("0.0");
        bindingGroup.addBinding(binding);

        fld_etaHydRequired.setColumns(4);
        fld_etaHydRequired.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${etaOutput.etaHydRequired}"), fld_etaHydRequired, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("0.0");
        binding.setSourceUnreadableValue("0.0");
        bindingGroup.addBinding(binding);

        fld_etaSedRequired.setColumns(4);
        fld_etaSedRequired.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${etaOutput.etaSedRequired}"), fld_etaSedRequired, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("0.0");
        binding.setSourceUnreadableValue("0.0");
        bindingGroup.addBinding(binding);

        fld_etaHydActual.setColumns(4);
        fld_etaHydActual.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_ONCE, this, org.jdesktop.beansbinding.ELProperty.create("${etaOutput.etaHydActual}"), fld_etaHydActual, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("0.0");
        binding.setSourceUnreadableValue("0.0");
        bindingGroup.addBinding(binding);

        fld_etaSedActual.setColumns(4);
        fld_etaSedActual.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_ONCE, this, org.jdesktop.beansbinding.ELProperty.create("${etaOutput.etaSedActual}"), fld_etaSedActual, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("0.0");
        binding.setSourceUnreadableValue("0.0");
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_etaHydActual)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fld_etaHydActual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_etaSedActual)
                            .addComponent(lbl_r720)
                            .addComponent(lbl_etaHydRequired)
                            .addComponent(lbl_etaSedRequired))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(fld_etaHydRequired, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(fld_etaSedActual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(fld_720, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(fld_etaSedRequired, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(101, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(97, 97, 97)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_r720)
                    .addComponent(fld_720, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_etaHydRequired)
                    .addComponent(fld_etaHydRequired, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_etaSedRequired)
                    .addComponent(fld_etaSedRequired, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_etaHydActual)
                    .addComponent(fld_etaHydActual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fld_etaSedActual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_etaSedActual))
                .addContainerGap(79, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField fld_720;
    private javax.swing.JTextField fld_etaHydActual;
    private javax.swing.JTextField fld_etaHydRequired;
    private javax.swing.JTextField fld_etaSedActual;
    private javax.swing.JTextField fld_etaSedRequired;
    private javax.swing.JLabel lbl_etaHydActual;
    private javax.swing.JLabel lbl_etaHydRequired;
    private javax.swing.JLabel lbl_etaSedActual;
    private javax.swing.JLabel lbl_etaSedRequired;
    private javax.swing.JLabel lbl_r720;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}