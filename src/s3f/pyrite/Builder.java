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
import s3f.pyrite.types.CircuitFile;
import s3f.pyrite.types.Position3DFile;
import s3f.util.ColorUtils;
import s3f.util.RandomColor;
import s3f.util.splashscreen.SimpleSplashScreen;
import s3f.util.splashscreen.SplashScreen;

public class Builder extends PluginBuilder {

    static {
        SplashScreen splash = new SimpleSplashScreen(Toolkit.getDefaultToolkit().getImage(Builder.class.getResource("/resources/pyriteLogo.png")));

        GUIBuilder.setSplashScreen(splash);

        GUIBuilder.setLookAndFeel(createLookAndFeel(Color.decode("#20629F")));
        GUIBuilder.setIcon(Toolkit.getDefaultToolkit().getImage(Builder.class
                .getResource("/resources/g6603-6.png")));

        String style = "body {color:#000; font-family:monospaced; margin: 4px; }"
                + "h1 {color: blue;}"
                + "h2 {color: #ff0000;}"
                + "pre {font : 10px monaco; color : black; background-color : #fafafa; }";

        String logosrc = Builder.class.getClassLoader().getResource("resources/g6603-6.png").toString();

        String html = "<html>\n"
                + "<body bgcolor=\"#E6E6FA\">\n"
                //+ "<img src=\"" + logosrc + "\" alt=\"some_text\" align=\"float:left\" >\n"
                + "<h1>Welcome to the pre-released version of Pyrite! - by Anderson Antunes</h1>\n"
                + "<h2>Remember: This is an unfisished build with lots of bugs, just for the curious ones (∩_∩)</h2>\n"
                + "<h3>2D Schematic editor shortcuts</h3>\n"
                + "<p>[;] - Sub-Circuit (edit for choosing the circuit)\n"
                + "<p>[+] - Input\n"
                + "<p>[-] - Output\n"
                + "<h3>3D schematic editor shortcuts</h3>\n"
                + "<p>[1]~[5] - change grid\n"
                + "<p>[r] - place source and ground in positions {4, 2, 2} and {2, 2, 2}, respectively\n"
                + "<p>[ENTER] - start fitting\n"
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
        pm.registerFactory(CircuitFile.FLOWCHART_FILES);
        pm.registerFactory(Position3DFile.FLOWCHART_FILES);
    }

}
