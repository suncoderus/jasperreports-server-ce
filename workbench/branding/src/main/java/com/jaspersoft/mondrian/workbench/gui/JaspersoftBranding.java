/**
 * Add Jaspersoft branding to the Schema Workbench
 *
 * @author swood
 */

package com.jaspersoft.mondrian.workbench.gui;

import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import mondrian.gui.Workbench;
import mondrian.gui.WorkbenchMenubarPlugin;
import org.apache.log4j.Logger;

public class JaspersoftBranding implements WorkbenchMenubarPlugin {

    private static final Logger LOGGER
            = Logger.getLogger(JaspersoftBranding.class);

    public static final String GUIResourceName =
        "com.jaspersoft.mondrian.workbench.gui.resources.gui";
    public static final String TextResourceName =
        "com.jaspersoft.mondrian.workbench.gui.resources.text";

    public JaspersoftBranding() {
    }

    /*
     * Not needed
     */
    public void addItemsToMenubar(JMenuBar menubar) {
    }

    public void setWorkbench(Workbench workbench) {
        workbench.addGuiResourceBundle(GUIResourceName);
        workbench.addLanguageResourceBundle(TextResourceName);

        String backgroundImagePath = workbench.getResourceConverter()
                        .getGUIReference("workbenchBackground", true);

        LOGGER.debug("backgroundImagePath: " + backgroundImagePath);

        if (backgroundImagePath != null) {
            ClassLoader myClassLoader = this.getClass().getClassLoader();

            URL imageUrl = myClassLoader.getResource(backgroundImagePath);
            LOGGER.debug("imageUrl: " + imageUrl);

            ImageIcon img = new ImageIcon(imageUrl);

            workbench.setBackgroundImage(img.getImage());
        }
    }

}
