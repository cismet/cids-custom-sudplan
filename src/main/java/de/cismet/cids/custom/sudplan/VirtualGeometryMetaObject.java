/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan;

import Sirius.server.localserver.attribute.Attribute;
import Sirius.server.localserver.attribute.ObjectAttribute;
import Sirius.server.localserver.object.Object;
import Sirius.server.middleware.types.DefaultMetaObject;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.TypeVisitor;
import Sirius.server.newuser.UserGroup;

import Sirius.util.Mapable;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.fromstring.FromStringCreator;

import de.cismet.cismap.commons.features.Feature;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class VirtualGeometryMetaObject implements MetaObject, Sirius.server.localserver.object.Object {

    //~ Instance fields --------------------------------------------------------

    private final DefaultMetaObject delegate;
    private Feature virtualFeature;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VirtualGeometryMetaObject object.
     *
     * @param  original  DOCUMENT ME!
     */
    public VirtualGeometryMetaObject(final DefaultMetaObject original) {
        this.delegate = original;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  virtualFeature  DOCUMENT ME!
     */
    public void setVirtualFeature(final Feature virtualFeature) {
        this.virtualFeature = virtualFeature;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Feature getVirtualFeature() {
        return virtualFeature;
    }

    @Override
    public void setValuesNull() {
        delegate.setValuesNull();
    }

    @Override
    public void setStatus(final int status) {
        delegate.setStatus(status);
    }

    @Override
    public void setReferencingObjectAttribute(final ObjectAttribute referencingObjectAttribute) {
        delegate.setReferencingObjectAttribute(referencingObjectAttribute);
    }

    @Override
    public void setPrimaryKeysNull() {
        delegate.setPrimaryKeysNull();
    }

    @Override
    public void setPersistent(final boolean persistent) {
        delegate.setPersistent(persistent);
    }

    @Override
    public void setID(final int objectID) {
        delegate.setID(objectID);
    }

    @Override
    public void setDummy(final boolean dummy) {
        delegate.setDummy(dummy);
    }

    @Override
    public void removeAttribute(final ObjectAttribute anyAttribute) {
        delegate.removeAttribute(anyAttribute);
    }

    @Override
    public boolean isStringCreateable() {
        return delegate.isStringCreateable();
    }

    @Override
    public boolean isPersistent() {
        return delegate.isPersistent();
    }

    @Override
    public boolean isDummy() {
        return delegate.isDummy();
    }

    @Override
    public Collection getTraversedAttributesByType(final Class c) {
        return delegate.getTraversedAttributesByType(c);
    }

    @Override
    public String getStatusDebugString() {
        return delegate.getStatusDebugString();
    }

    @Override
    public int getStatus() {
        return delegate.getStatus();
    }

    @Override
    public ObjectAttribute getReferencingObjectAttribute() {
        return delegate.getReferencingObjectAttribute();
    }

    @Override
    public Attribute getPrimaryKey() {
        return delegate.getPrimaryKey();
    }

    @Override
    public FromStringCreator getObjectCreator() {
        return delegate.getObjectCreator();
    }

    @Override
    public java.lang.Object getKey() {
        return delegate.getKey();
    }

    @Override
    public int getID() {
        return delegate.getID();
    }

    @Override
    public int getClassID() {
        return delegate.getClassID();
    }

    @Override
    public Collection getAttributesByType(final Class c) {
        return getAttributesByType(c, 0);
    }

    @Override
    public Collection getAttributesByType(final Class c, final int recursionDepth) {
        final Collection attr;
        if (Geometry.class.isAssignableFrom(c) && (virtualFeature != null)) {
            attr = new ArrayList<Geometry>(1);
            attr.add(virtualFeature.getGeometry());
        } else {
            attr = delegate.getAttributesByType(c, recursionDepth);
        }

        return attr;
    }

    @Override
    public Collection getAttributesByName(final Collection names) {
        return delegate.getAttributesByName(names);
    }

    @Override
    public HashMap getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<Attribute> getAttributeByName(final String name, final int maxResult) {
        return delegate.getAttributeByName(name, maxResult);
    }

    @Override
    public ObjectAttribute getAttributeByFieldName(final String fieldname) {
        return delegate.getAttributeByFieldName(fieldname);
    }

    @Override
    public java.lang.Object getAttribute(final java.lang.Object key) {
        return delegate.getAttribute(key);
    }

    @Override
    public ObjectAttribute[] getAttribs() {
        return delegate.getAttribs();
    }

    @Override
    public java.lang.Object fromString(final String objectRepresentation, final java.lang.Object mo) throws Exception {
        return delegate.fromString(objectRepresentation, mo);
    }

    @Override
    public Object filter(final UserGroup ug) throws Exception {
        return delegate.filter(ug);
    }

    @Override
    public java.lang.Object constructKey(final Mapable m) {
        return delegate.constructKey(m);
    }

    @Override
    public void addAttribute(final ObjectAttribute anyAttribute) throws Exception {
        delegate.addAttribute(anyAttribute);
    }

    @Override
    public void addAllAttributes(final ObjectAttribute[] objectAttributes) {
        delegate.addAllAttributes(objectAttributes);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public String toString(final HashMap classes) {
        return delegate.toString(classes);
    }

    @Override
    public void setRenderer(final String renderer) {
        delegate.setRenderer(renderer);
    }

    @Override
    public boolean setPrimaryKey(final java.lang.Object key) {
        return delegate.setPrimaryKey(key);
    }

    @Override
    public void setMetaClass(final MetaClass metaClass) {
        delegate.setMetaClass(metaClass);
    }

    @Override
    public void setEditor(final String editor) {
        delegate.setEditor(editor);
    }

    @Override
    public void setChanged(final boolean changed) {
        delegate.setChanged(changed);
    }

    @Override
    public void setArrayKey2PrimaryKey() {
        delegate.setArrayKey2PrimaryKey();
    }

    @Override
    public void setAllStatus(final int status) {
        delegate.setAllStatus(status);
    }

    @Override
    public void setAllClasses() {
        delegate.setAllClasses();
    }

    @Override
    public void setAllClasses(final HashMap classes) {
        delegate.setAllClasses(classes);
    }

    @Override
    public boolean propertyEquals(final MetaObject tester) {
        return delegate.propertyEquals(tester);
    }

    @Override
    public boolean isChanged() {
        return delegate.isChanged();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public Collection getURLsByName(final Collection classKeys, final Collection urlNames) {
        return delegate.getURLsByName(classKeys, urlNames);
    }

    @Override
    public Collection getURLs(final Collection classKeys) {
        return delegate.getURLs(classKeys);
    }

    @Override
    public String getSimpleEditor() {
        return delegate.getSimpleEditor();
    }

    @Override
    public String getRenderer() {
        return delegate.getRenderer();
    }

    @Override
    public String getPropertyString() {
        return delegate.getPropertyString();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public MetaClass getMetaClass() {
        return delegate.getMetaClass();
    }

    @Override
    public Logger getLogger() {
        return delegate.getLogger();
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public String getGroup() {
        return delegate.getGroup();
    }

    @Override
    public String getEditor() {
        return delegate.getEditor();
    }

    @Override
    public String getDomain() {
        return delegate.getDomain();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public String getDebugString() {
        return delegate.getDebugString();
    }

    @Override
    public String getComplexEditor() {
        return delegate.getComplexEditor();
    }

    @Override
    public String getClassKey() {
        return delegate.getClassKey();
    }

    @Override
    public CidsBean getBean() {
        return delegate.getBean();
    }

    @Override
    public HashMap getAllClasses() {
        return delegate.getAllClasses();
    }

    @Override
    public boolean equals(final java.lang.Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public java.lang.Object accept(final TypeVisitor mov, final java.lang.Object o) {
        return delegate.accept(mov, o);
    }
}
