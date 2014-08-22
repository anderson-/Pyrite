/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui.components;

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

public class DigitalLogicTester extends RailElm {

    private boolean set = false;

    public DigitalLogicTester(int xx, int yy) {
        super(xx, yy, WF_SQUARE);
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
            if (set) {
                set = false;
                for (int i = 0; i < sim.elmListSize(); i++) {
                    CircuitElm elm = sim.getElm(i);
                    if (elm instanceof MyLogicInputElm) {
                        ((MyLogicInputElm) elm).setPosition(Math.random() > .5 ? 0 : 1);
                    }
                }
            }
            return bias;
        } else {
            set = true;
            return bias + maxVoltage;
        }
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
}
