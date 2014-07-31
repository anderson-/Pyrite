package s3f.pyrite;

import java.awt.Color;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import s3f.core.plugin.PluginBuilder;
import s3f.core.ui.GUIBuilder;
import static s3f.core.ui.MainUI.createLookAndFeel;
import s3f.util.ColorUtils;
import s3f.util.RandomColor;
import s3f.util.splashscreen.SimpleSplashScreen;
import s3f.util.splashscreen.SplashScreen;

public class Builder extends PluginBuilder {

    static {
        SplashScreen splash = new SimpleSplashScreen(Toolkit.getDefaultToolkit().getImage(Builder.class.getResource("/resources/pyriteLogo.png")));
           
        GUIBuilder.setSplashScreen(splash);

        GUIBuilder.setLookAndFeel(createLookAndFeel(ColorUtils.changeHSBA(Color.blue, 0, -.6f, -.2f, 0)));
        GUIBuilder.setIcon(Toolkit.getDefaultToolkit().getImage(Builder.class
                .getResource("/resources/g6603-6.png")));
    }

    public Builder() {
        super("Pyrite");
    }

    @Override
    public void init() {
//        ConfigurableObject o = new ConfigurableObject("s3f.jifi.cmd");
//        o.getData().setProperty("procedure", new If());
//        pm.registerFactory(o);
//        
//        pm.registerFactory(Flowchart.FLOWCHART_FILES);
    }

}
