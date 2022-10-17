package components;

import java.util.StringTokenizer;
import java.util.Vector;

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
