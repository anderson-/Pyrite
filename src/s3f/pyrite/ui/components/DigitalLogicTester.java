/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui.components;

import cern.colt.Arrays;
import com.falstad.circuit.CircuitElm;
import static com.falstad.circuit.CircuitElm.getCurrentText;
import static com.falstad.circuit.CircuitElm.getUnitText;
import static com.falstad.circuit.CircuitElm.getVoltageText;
import static com.falstad.circuit.CircuitElm.pi;
import com.falstad.circuit.EditInfo;
import com.falstad.circuit.elements.RailElm;
import static com.falstad.circuit.elements.VoltageElm.WF_AC;
import static com.falstad.circuit.elements.VoltageElm.WF_DC;
import static com.falstad.circuit.elements.VoltageElm.WF_PULSE;
import static com.falstad.circuit.elements.VoltageElm.WF_SAWTOOTH;
import static com.falstad.circuit.elements.VoltageElm.WF_SQUARE;
import static com.falstad.circuit.elements.VoltageElm.WF_TRIANGLE;
import static com.falstad.circuit.elements.VoltageElm.WF_VAR;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;

public class DigitalLogicTester extends RailElm {

    private boolean set = false;
    private boolean get = false;
    private boolean ok = true;
    private Table table = new Table();

    public DigitalLogicTester(int xx, int yy) {
        super(xx, yy, WF_SQUARE);
        frequency = 40;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public int getDumpType() {
        return 'H';
    }

    public Class getDumpClass() {
        return DigitalLogicTester.class;
    }

    public int getShortcut() {
        return 'h';
    }

    public double getVoltage() {
        double w = 2 * pi * (sim.getT() - freqTimeZero) * frequency + phaseShift;
        if (w % (2 * pi) > (2 * pi * dutyCycle)) {
            ok = true;
            if (set) {
                set = false;
                table.step();
                for (int i = 0; i < sim.elmListSize(); i++) {
                    CircuitElm elm = sim.getElm(i);
                    if (elm instanceof MyLogicInputElm) {
                        ((MyLogicInputElm) elm).setPosition(table.getValue(((MyLogicInputElm) elm).getName()) == 0 ? 0 : 1);
                    }
                }
                get = true;
            }
            return bias;
        } else {
            set = true;
            if (get && SubCircuitElm.isStable(sim)) {
                for (int i = 0; i < sim.elmListSize(); i++) {
                    CircuitElm elm = sim.getElm(i);
                    if (elm instanceof MyLogicOutputElm) {
                        table.setValue(((MyLogicOutputElm) elm).getName(), ((MyLogicOutputElm) elm).getValue().equals("L") ? 0 : 1);
                    }
                }
                ok = table.getPartialResult();
                System.out.println(ok);
            }
            get = false;
            return bias + maxVoltage;
        }
    }

    @Override
    public void draw(Graphics g) {
        int xc = point2.x;
        int yc = point2.y;
        if (!ok) {
            g.setColor(Color.red);
            g.fillOval(xc - circleSize, yc - circleSize, circleSize * 2, circleSize * 2);
        } else {
            g.setColor(Color.black);
            g.fillOval(xc - circleSize, yc - circleSize, circleSize * 2, circleSize * 2);
        }
        g.setColor(Color.white);
        drawThickCircle(g, xc, yc, circleSize);
        drawCenteredText(g, table.getStep() + "", x2, y2, true);
    }

    public void getInfo(String arr[]) {
        switch (waveform) {
            case WF_DC:
            case WF_VAR:
                arr[0] = "voltage source";
                break;
            case WF_AC:
                arr[0] = "A/C source";
                break;
            case WF_SQUARE:
                arr[0] = "square wave gen";
                break;
            case WF_PULSE:
                arr[0] = "pulse gen";
                break;
            case WF_SAWTOOTH:
                arr[0] = "sawtooth gen";
                break;
            case WF_TRIANGLE:
                arr[0] = "triangle gen";
                break;
        }
        arr[1] = "I = " + getCurrentText(getCurrent());
        arr[2] = ((this instanceof RailElm) ? "V = " : "Vd = ")
                + getVoltageText(getVoltageDiff());
        if (waveform != WF_DC && waveform != WF_VAR) {
            arr[3] = "f = " + getUnitText(frequency, "Hz");
            arr[4] = "Vmax = " + getVoltageText(maxVoltage);
            int i = 5;
            if (bias != 0) {
                arr[i++] = "Voff = " + getVoltageText(bias);
            } else if (frequency > 500) {
                arr[i++] = "wavelength = "
                        + getUnitText(2.9979e8 / frequency, "m");
            }
            arr[i++] = "P = " + getUnitText(getPower(), "W");
        }
    }

    public EditInfo getEditInfo(int n) {
        if (n == 0) {
            return new EditInfo(waveform == WF_DC ? "Voltage"
                    : "Max Voltage", maxVoltage, -20, 20);
        }
        if (n == 1) {
            EditInfo ei = new EditInfo("Waveform", waveform, -1, -1);
            ei.choice = new Choice();
            ei.choice.add("D/C");
            ei.choice.add("A/C");
            ei.choice.add("Square Wave");
            ei.choice.add("Triangle");
            ei.choice.add("Sawtooth");
            ei.choice.add("Pulse");
            ei.choice.select(waveform);
            return ei;
        }
        if (waveform == WF_DC) {
            return null;
        }
        if (n == 2) {
            return new EditInfo("Frequency (Hz)", frequency, 4, 500);
        }
        if (n == 3) {
            return new EditInfo("DC Offset (V)", bias, -20, 20);
        }
        if (n == 4) {
            return new EditInfo("Phase Offset (degrees)", phaseShift * 180 / pi,
                    -180, 180).setDimensionless();
        }
        if (n == 5 && waveform == WF_SQUARE) {
            return new EditInfo("Duty Cycle", dutyCycle * 100, 0, 100).
                    setDimensionless();
        }
        return null;
    }

    public static class Table {

        public static final float[][] AND = new float[][]{{0, 0, 0}, {0, 1, 0}, {1, 0, 0}, {1, 1, 1}};
        public static final float[][] OR = new float[][]{{0, 0, 0}, {0, 1, 1}, {1, 0, 1}, {1, 1, 1}};
        public static final float[][] XOR = new float[][]{{0, 0, 0}, {0, 1, 1}, {1, 0, 1}, {1, 1, 0}};
        public static final float[][] ULA = new float[][]{
            //a b t1 t2  s  t
            {0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0},
            {1, 1, 0, 0, 1, 1},
            //
            {0, 0, 0, 1, 0, 0},
            {1, 0, 0, 1, 1, 0},
            {0, 1, 0, 1, 1, 0},
            {1, 1, 0, 1, 1, 1},
            //
            {0, 0, 1, 0, 1, 0},
            {1, 0, 1, 0, 1, 0},
            {0, 1, 1, 0, 0, 0},
            {1, 1, 1, 0, 0, 1},
            //
            {0, 0, 1, 1, 0, 0},
            {1, 0, 1, 1, 1, 0},
            {0, 1, 1, 1, 1, 0},
            {1, 1, 1, 1, 0, 1}, //
        };
        public static final float[][] NOT = new float[][]{{0, 1}, {1, 0}};
        public static final HashMap<String, Integer> DEFAULT = new HashMap<>();
        public static float E = 0;

        static {
            for (int i = 0; i < 18; i++) {
                DEFAULT.put(Character.toString((char) (65 + i)), i);
                DEFAULT.put(Character.toString((char) (65 + i)).toLowerCase(), i);
            }
            System.out.println("-");
            for (int i = 0; i <= 7; i++) {
                DEFAULT.put(Character.toString((char) (83 + i)), i + 4);
                DEFAULT.put(Character.toString((char) (83 + i)).toLowerCase(), i + 4);
            }
        }

        private HashMap<String, Integer> references;
        private float[][] table;
        private float[][] inTest;
        private float[] currentRow;
        private int step = 0;

        public Table() {
            this(DEFAULT, ULA);
        }

        public Table(HashMap<String, Integer> references, float[][] table) {
            this.references = references;
            this.table = table;
            currentRow = new float[table[0].length];
            inTest = new float[table.length][table[0].length];
            for (int j = 0; j < table[0].length; j++) {
                currentRow[j] = 0;
                for (int i = 0; i < table.length; i++) {
                    inTest[i][j] = 0;
                }
            }
        }

        private float getValue(String col) {
            Integer i = references.get(col);
            if (i == null || i < 0 || i > table[0].length) {
                System.err.println("INVALID COLUMN! " + col);
                return -1;
            }
            return table[step][i];
        }

        private void setValue(String col, float value) {
            Integer i = references.get(col);
            if (i == null || i < 0 || i > table[0].length) {
                System.err.println("INVALID COLUMN! " + col);
                return;
            }
            inTest[step][i] = value;
            currentRow[i] = table[step][i] - value;
        }

        private boolean getPartialResult(boolean print) {
            if (print || true) {
                System.out.println(step + " " + Arrays.toString(currentRow) + " " + Arrays.toString(table[step]));
            }
            for (int i = 0; i < table[0].length; i++) {
                if (currentRow[i] < -E || currentRow[i] > +E) {
                    return false;
                }
            }
            return true;
        }

        private boolean getPartialResult() {
            return getPartialResult(false);
        }

        private void step() {
            step = (step + 1 >= table.length) ? 0 : step + 1;
        }

        private int getStep() {
            return step;
        }
    }
}
