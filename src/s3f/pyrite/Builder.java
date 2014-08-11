package s3f.pyrite;

import java.awt.Color;
import java.awt.Toolkit;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import s3f.core.plugin.PluginBuilder;
import s3f.core.ui.GUIBuilder;
import static s3f.core.ui.MainUI.createLookAndFeel;
import s3f.pyrite.types.CircuitModule;
import s3f.pyrite.types.ModularCircuit;
import s3f.pyrite.types.Position3DFile;
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

        String style = "body {color:#000; font-family:serif; margin: 4px; }"
                + "h1 {color: blue;}"
                + "h2 {color: #ff0000;}"
                + "pre {font : 10px monaco; color : black; background-color : #fafafa; }";

        String html = "<html>\n"
                + "<body>\n"
                + "<h1>Welcome!</h1>\n"
                + "<h2>This is an H2 header</h2>\n"
                + "<p>This is some sample text</p>\n"
                + "<p><a href=\"https://github.com/anderson-\">teste</a></p>\n"
                + "</body>\n";
        GUIBuilder.setWelcomePage(html, style);
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
        pm.registerFactory(ModularCircuit.FLOWCHART_FILES);
        pm.registerFactory(CircuitModule.FLOWCHART_FILES);
        pm.registerFactory(Position3DFile.FLOWCHART_FILES);
    }

}
