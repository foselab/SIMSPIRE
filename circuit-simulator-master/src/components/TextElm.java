package components;

import java.awt.Checkbox;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.StringTokenizer;
import java.util.Vector;

import utils.EditInfo;

public class TextElm extends GraphicElm {
	String text;
	Vector<String> lines;
	int size;
	final int FLAG_CENTER = 1;
	final int FLAG_BAR = 2;

	public TextElm(int xx, int yy) {
		super(xx, yy);
		text = "hello";
		lines = new Vector<String>();
		lines.add(text);
		size = 24;
	}

	public TextElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		size = new Integer(st.nextToken()).intValue();
		text = st.nextToken();
		while (st.hasMoreTokens())
			text += ' ' + st.nextToken();
		split();
	}

	void split() {
		int i;
		lines = new Vector<String>();
		StringBuffer sb = new StringBuffer(text);
		for (i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (c == '\\') {
				sb.deleteCharAt(i);
				c = sb.charAt(i);
				if (c == 'n') {
					lines.add(sb.substring(0, i));
					sb.delete(0, i + 1);
					i = -1;
					continue;
				}
			}
		}
		lines.add(sb.toString());
	}

	@Override
	public String dump() {
		return super.dump() + " " + size + " " + text;
	}

	@Override
	public int getDumpType() {
		return 'x';
	}

	@Override
	public void drag(int xx, int yy) {
		setX(xx);
		setY(yy);
		setX2(xx + 16);
		setY2(yy);
	}

	@Override
	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			EditInfo ei = new EditInfo("Text", 0, -1, -1);
			ei.setText(text);
			return ei;
		}
		if (n == 1)
			return new EditInfo("Size", size, 5, 100);
		if (n == 2) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Center", (flags & FLAG_CENTER) != 0);
			return ei;
		}
		if (n == 3) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Draw Bar On Top", (flags & FLAG_BAR) != 0);
			return ei;
		}
		return null;
	}

	@Override
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			text = ei.getTextf().getText();
			split();
		}
		if (n == 1)
			size = (int) ei.getValue();
		if (n == 3) {
			if (ei.checkbox.getState())
				flags |= FLAG_BAR;
			else
				flags &= ~FLAG_BAR;
		}
		if (n == 2) {
			if (ei.checkbox.getState())
				flags |= FLAG_CENTER;
			else
				flags &= ~FLAG_CENTER;
		}
	}

	@Override
	public boolean isCenteredText() {
		return (flags & FLAG_CENTER) != 0;
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = text;
	}

	@Override
	public int getShortcut() {
		return 't';
	}
}
