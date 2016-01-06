/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author antunes
 */
public class ConfigurationTab {

    public static final boolean TRACKING_VALUES_ENABLED = true;

    @Retention(RetentionPolicy.RUNTIME)
    public @interface DontBreakLine {

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface CustomComponent {

        public String method();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface TrackValue {

        public long interval();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Panel {

        public String name();

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Checkbox {

        public String name();

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Spinner {

        public String name();

        public int min();

        public int max();

        public int step();
    }

    public ConfigurationTab(Object parameters) {
        createFrame(parameters);
    }

    public void createFrame(Object parameters) {
        JFrame window = new JFrame();
        build(parameters, window.getContentPane());
        window.getContentPane().setLayout(new BoxLayout(window.getContentPane(), BoxLayout.PAGE_AXIS));
//        window.setSize(new Dimension(300, 300));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }

    public void build(Object parameters, Container panel) {
        for (Field field : parameters.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                try {
                    JComponent c = createJComponent(annotation, field, parameters);
                    if (c != null) {
                        panel.add(c);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private JComponent createJComponent(Annotation annotation, final Field field, final Object obj) throws Exception {
        if (TRACKING_VALUES_ENABLED && annotation instanceof TrackValue) {
            final TrackValue trackValue = (TrackValue) annotation;
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            System.out.println("tracking: " + field.get(obj));
                            Thread.sleep(trackValue.interval());
                        } catch (Exception ex) {
                        }
                    }
                }
            }.start();
        } else if (annotation instanceof CustomComponent) {
            CustomComponent customComponent = (CustomComponent) annotation;
            for (Method method : obj.getClass().getDeclaredMethods()) {
                method.setAccessible(true);
                if (JComponent.class.isAssignableFrom(method.getReturnType()) && method.getName().equals(customComponent.method())) {
                    JComponent c = (JComponent) method.invoke(obj);
                    return c;
                }
            }
            throw new Error("Method " + customComponent.method() + "() not found in field:\n (ノಠ益ಠ)ノ " + field);
        } else if (annotation instanceof Panel) {
            Panel panel = (Panel) annotation;
            JPanel p = new JPanel();
            p.setBorder(BorderFactory.createTitledBorder(panel.name()));
            build(field.get(obj), p);
            return p;
        } else if (annotation instanceof Checkbox) {
            Checkbox checkbox = (Checkbox) annotation;
            final JCheckBox c = new JCheckBox(checkbox.name(), null, field.getBoolean(obj));
            c.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        field.setBoolean(obj, c.isSelected());
                    } catch (Exception ex) {

                    }
                }
            });
            return c;
        } else if (annotation instanceof Spinner) {
            Spinner spinner = (Spinner) annotation;
            JPanel p = new JPanel();
            final JSpinner s = new JSpinner();
            s.setModel(new SpinnerNumberModel(field.getLong(obj), spinner.min(), spinner.max(), spinner.step()));
            s.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                    try {
                        field.setInt(obj, ((Number) s.getValue()).intValue());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
            p.add(new JLabel(spinner.name()));
            p.add(s);
            return p;
        }
        return createJComponent(annotation);
    }

    public JComponent createJComponent(Annotation a) {
        return null;
    }

}
