package s3f.pyrite.ui.components;

import com.falstad.circuit.CircuitElm;
import com.falstad.circuit.EditInfo;
import com.falstad.circuit.elements.LogicInputElm;
import java.awt.*;
import java.util.StringTokenizer;

public class MyLogicInputElm extends LogicInputElm {

    String name;

    public MyLogicInputElm(int xx, int yy) {
        super(xx, yy);
        int n = 0;
        if (sim != null) {
            for (int i = 0; i < sim.elmListSize(); i++) {
                CircuitElm c = sim.getElm(i);
                if (c instanceof MyLogicInputElm) {
                    n++;
                }
            }
        }
        name = Character.toString((char) (65 + n));//'A'
    }

    public MyLogicInputElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        name = st.nextToken();
    }

    @Override
    public int getDumpType() {
        return '+';
    }

    @Override
    public String dump() {
        return super.dump() + " " + name;
    }

    @Override
    public void draw(Graphics g) {
        Font f = new Font("SansSerif", Font.BOLD, 15);
        g.setFont(f);
        g.setColor(needsHighlight() ? selectColor : whiteColor);
        String s = name + ":" + ((position == 0) ? "L" : "H");
        if (isNumeric()) {
            s = "" + position;
        }
        setBbox(point1, lead1, 0);
        drawCenteredText(g, s, x2, y2, true);
        setVoltageColor(g, volts[0]);
        drawThickLine(g, point1, lead1);
        updateDotCount();
        drawDots(g, point1, lead1, curcount);
        drawPosts(g);
    }

    @Override
    public void getInfo(String arr[]) {
        arr[0] = "my logic input";
        arr[1] = (position == 0) ? "low" : "high";
        if (isNumeric()) {
            arr[1] = "" + position;
        }
        arr[1] += " (" + getVoltageText(volts[0]) + ")";
        arr[2] = "I = " + getCurrentText(getCurrent());
    }

    @Override
    public EditInfo getEditInfo(int n) {
        EditInfo editInfo = super.getEditInfo(n);
        if (editInfo == null) {
            if (n == 3) {
                EditInfo ei = new EditInfo("Name", 0, -1, -1);
                ei.text = name;
                return ei;
            }
            return null;
        } else {
            return editInfo;
        }
    }

    @Override
    public void setEditValue(int n, EditInfo ei) {
        super.setEditValue(n, ei);
        if (n == 3) {
            name = ei.textf.getText();
        }
    }

    @Override
    public int getShortcut() {
        return '+';
    }

    public String getName() {
        return name;
    }
}
