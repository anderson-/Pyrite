/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import s3f.pyrite.ui.ConfigurationTab;
import s3f.pyrite.ui.ConfigurationTab.Checkbox;
import s3f.pyrite.ui.ConfigurationTab.CustomComponent;
import s3f.pyrite.ui.ConfigurationTab.Panel;
import s3f.pyrite.ui.ConfigurationTab.TrackValue;

/**
 *
 * @author antunes
 */
public class DefaultGridFittingTool implements GridFittingTool {

    private static class Parameters {

        public Parameters() {
//            new Thread() {
//                @Override
//                public void run() {
//                    while (true) {
//                        System.out.println(optimize);
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }
//            }.start();
        }

        private static class SubP0 {

            @Checkbox(name = "test1")
            public boolean t1 = true;

            @Checkbox(name = "asdasd")
            public boolean t2 = true;
        }

        @Checkbox(name = "optimize")
        public boolean optimize = true;

        @Panel(name = "caixa pra p0")
        public SubP0 p0 = new SubP0();

        @Panel(name = "p1")
        public SubP0 p1 = new SubP0();

        @CustomComponent(method = "buildAsd")
        public String asd = "Testasdasdasdse UHUL!";

        private JComponent buildAsd() {
            return new JLabel(asd);
        }
    }

    @Override
    public void fit(Circuit circuit, Grid grid) {

    }

    public static void main(String[] args) {
        new ConfigurationTab(new Parameters());
    }

}
