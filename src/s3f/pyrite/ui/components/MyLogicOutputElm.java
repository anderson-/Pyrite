package s3f.pyrite.ui.components;

import com.falstad.circuit.EditInfo;
import com.falstad.circuit.elements.LogicOutputElm;
import java.awt.*;
import java.util.StringTokenizer;

public class MyLogicOutputElm extends LogicOutputElm {

    String name;

    public MyLogicOutputElm(int xx, int yy) {
        super(xx, yy);
        name = "s";
    }

    public MyLogicOutputElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        name = st.nextToken();
    }

    @Override
    public int getDumpType() {
        return '-';
    }

    @Override
    public String dump() {
        return super.dump() + " " + name;
    }

    @Override
    public void draw(Graphics g) {
        Font f = new Font("SansSerif", Font.BOLD, 15);
        g.setFont(f);
        //g.setColor(needsHighlight() ? selectColor : lightGrayColor);
        g.setColor(lightGrayColor);
        String s = name + ":" + ((volts[0] < threshold) ? "L" : "H");
        if (isTernary()) {
            if (volts[0] > 3.75) {
                s = "2";
            } else if (volts[0] > 1.25) {
                s = "1";
            } else {
                s = "0";
            }
        } else if (isNumeric()) {
            s = (volts[0] < threshold) ? "0" : "1";
        }
        value = s;
        setBbox(point1, lead1, 0);
        drawCenteredText(g, s, x2, y2, true);
        setVoltageColor(g, volts[0]);
        drawThickLine(g, point1, lead1);
        drawPosts(g);
    }

    @Override
    public void getInfo(String arr[]) {
        arr[0] = "my logic output";
        arr[1] = (volts[0] < threshold) ? "low" : "high";
        if (isNumeric()) {
            arr[1] = value;
        }
        arr[2] = "V = " + getVoltageText(volts[0]);
    }

    @Override
    public EditInfo getEditInfo(int n) {
        EditInfo editInfo = super.getEditInfo(n);
        if (editInfo == null) {
            if (n == 2) {
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
        if (n == 2) {
            name = ei.textf.getText();
        }
    }

    @Override
    public int getShortcut() {
        return '-';
    }
}
