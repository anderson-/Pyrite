package s3f.pyrite;

import s3f.core.plugin.PluginManager;
import s3f.core.ui.MainUI;

public class Main {

    public static void main(String[] args) {
        PluginManager pm = PluginManager.getInstance(args, Main.class);
        pm.loadSoftPlugin("s3f/pyrite/plugin.cfg", null);
        MainUI.buildAndRun();
    }
}
