/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.multiply;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.cismet.cids.dynamics.CidsBean;
import org.junit.Ignore;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class MultiplyOutputManagerUITest {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MultiplyOutputManagerUITest object.
     */
    public MultiplyOutputManagerUITest() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * DOCUMENT ME!
     */
    @Before
    public void setUp() {
    }

    /**
     * DOCUMENT ME!
     */
    @After
    public void tearDown() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getCurrentMethodName() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Ignore
    @Test
    public void testRequest() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());
        final MultiplyOutputManager model = new MultiplyOutputManager();
        final CidsBean bean = new CidsBean();
        bean.setProperty("uri", "file:/Users/mscholl/Desktop/results.txt");
        model.setCidsBean(bean);
        final MultiplyOutputManagerUI ui = new MultiplyOutputManagerUI(model);
        ui.init();
        final JOptionPane pane = new JOptionPane();
        pane.add(ui);
        final JDialog dialog = pane.createDialog("test");
        dialog.setVisible(true);
    }
}
