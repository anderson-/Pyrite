package s3f.pyrite;

import java.awt.Color;
import java.awt.Toolkit;
import java.io.File;
import java.util.Scanner;
import s3f.core.plugin.PluginBuilder;
import s3f.core.ui.GUIBuilder;
import static s3f.core.ui.MainUI.createLookAndFeel;
import s3f.pyrite.types.Blueprint;
import s3f.pyrite.types.CircuitFile;
import s3f.pyrite.types.VolumetricCircuit;
import s3f.util.splashscreen.SimpleSplashScreen;
import s3f.util.splashscreen.SplashScreen;

public class Builder extends PluginBuilder {

    static {
        SplashScreen splash = new SimpleSplashScreen(Toolkit.getDefaultToolkit().getImage(Builder.class.getResource("/resources/pyriteLogo.png")));

        GUIBuilder.setSplashScreen(splash);

        GUIBuilder.setLookAndFeel(createLookAndFeel(Color.decode("#20629F")));
        GUIBuilder.setIcon(Toolkit.getDefaultToolkit().getImage(Builder.class
                .getResource("/resources/g6603-6.png")));

        GUIBuilder.setWelcomePage(new Scanner(Builder.class.getClassLoader().getResourceAsStream("resources/Welcome.html")).useDelimiter("\\Z").next(), "");
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
        pm.registerFactory(CircuitFile.FLOWCHART_FILES);
        pm.registerFactory(VolumetricCircuit.FLOWCHART_FILES);
        pm.registerFactory(Blueprint.FLOWCHART_FILES);
    }

}
