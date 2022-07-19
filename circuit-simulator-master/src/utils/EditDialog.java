package utils;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Label;
import java.awt.Point;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;

import simulator.CirSim;

public class EditDialog extends Dialog implements AdjustmentListener, ActionListener, ItemListener {
	private Editable elm;
	CirSim cframe;
	Button applyButton, okButton;
	EditInfo einfos[];
	int einfocount;
	final int barmax = 1000;
	NumberFormat noCommaFormat;

	public EditDialog(Editable ce, CirSim f) {
		super(f, "Edit Component", false);
		cframe = f;
		setElm(ce);
		setLayout(new EditDialogLayout());
		einfos = new EditInfo[10];
		noCommaFormat = NumberFormat.getInstance();
		noCommaFormat.setMaximumFractionDigits(10);
		noCommaFormat.setGroupingUsed(false);
		int i;
		for (i = 0;; i++) {
			einfos[i] = getElm().getEditInfo(i);
			if (einfos[i] == null)
				break;
			EditInfo ei = einfos[i];
			add(new Label(ei.name));
			if (ei.getChoice() != null) {
				add(ei.getChoice());
				ei.getChoice().addItemListener(this);
			} else if (ei.getCheckbox() != null) {
				add(ei.getCheckbox());
				ei.getCheckbox().addItemListener(this);
			} else {
				add(ei.setTextf(new TextField(unitString(ei), 10)));
				if (ei.getText() != null)
					ei.getTextf().setText(ei.getText());
				ei.getTextf().addActionListener(this);
				if (ei.getText() == null) {
					add(ei.bar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 10, 0, barmax + 2));
					setBar(ei);
					ei.bar.addAdjustmentListener(this);
				}
			}
		}
		einfocount = i;
		add(applyButton = new Button("Apply"));
		applyButton.addActionListener(this);
		add(okButton = new Button("OK"));
		okButton.addActionListener(this);
		Point x = CirSim.getMain().getLocationOnScreen();
		Dimension d = getSize();
		setLocation(x.x + (cframe.getWinSize().width - d.width) / 2, x.y + (cframe.getWinSize().height - d.height) / 2);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				closeDialog();
			}
		});
	}

	String unitString(EditInfo ei) {
		double v = ei.getValue();
		double va = Math.abs(v);
		if (ei.dimensionless)
			return noCommaFormat.format(v);
		if (v == 0)
			return "0";
		if (va < 1e-9)
			return noCommaFormat.format(v * 1e12) + "p";
		if (va < 1e-6)
			return noCommaFormat.format(v * 1e9) + "n";
		if (va < 1e-3)
			return noCommaFormat.format(v * 1e6) + "u";
		if (va < 1 && !ei.forceLargeM)
			return noCommaFormat.format(v * 1e3) + "m";
		if (va < 1e3)
			return noCommaFormat.format(v);
		if (va < 1e6)
			return noCommaFormat.format(v * 1e-3) + "k";
		if (va < 1e9)
			return noCommaFormat.format(v * 1e-6) + "M";
		return noCommaFormat.format(v * 1e-9) + "G";
	}

	double parseUnits(EditInfo ei) throws java.text.ParseException {
		String s = ei.getTextf().getText();
		s = s.trim();
		int len = s.length();
		char uc = s.charAt(len - 1);
		double mult = 1;
		switch (uc) {
		case 'p':
		case 'P':
			mult = 1e-12;
			break;
		case 'n':
		case 'N':
			mult = 1e-9;
			break;
		case 'u':
		case 'U':
			mult = 1e-6;
			break;

		// for ohm values, we assume mega for lowercase m, otherwise milli
		case 'm':
			mult = (ei.forceLargeM) ? 1e6 : 1e-3;
			break;

		case 'k':
		case 'K':
			mult = 1e3;
			break;
		case 'M':
			mult = 1e6;
			break;
		case 'G':
		case 'g':
			mult = 1e9;
			break;
		}
		if (mult != 1)
			s = s.substring(0, len - 1).trim();
		return noCommaFormat.parse(s).doubleValue() * mult;
	}

	void apply() {
		int i;
		for (i = 0; i != einfocount; i++) {
			EditInfo ei = einfos[i];
			if (ei.getTextf() == null)
				continue;
			if (ei.getText() == null) {
				try {
					double d = parseUnits(ei);
					ei.setValue(d);
				} catch (Exception ex) {
					/* ignored */ }
			}
			getElm().setEditValue(i, ei);
			if (ei.getText() == null)
				setBar(ei);
		}
		cframe.needAnalyze();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i;
		Object src = e.getSource();
		for (i = 0; i != einfocount; i++) {
			EditInfo ei = einfos[i];
			if (src == ei.getTextf()) {
				if (ei.getText() == null) {
					try {
						double d = parseUnits(ei);
						ei.setValue(d);
					} catch (Exception ex) {
						/* ignored */ }
				}
				getElm().setEditValue(i, ei);
				if (ei.getText() == null)
					setBar(ei);
				cframe.needAnalyze();
			}
		}
		if (e.getSource() == okButton) {
			apply();
			closeDialog();
		}
		if (e.getSource() == applyButton)
			apply();
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		Object src = e.getSource();
		int i;
		for (i = 0; i != einfocount; i++) {
			EditInfo ei = einfos[i];
			if (ei.bar == src) {
				double v = ei.bar.getValue() / 1000.;
				if (v < 0)
					v = 0;
				if (v > 1)
					v = 1;
				ei.setValue((ei.maxval - ei.minval) * v + ei.minval);
				/*
				 * if (ei.maxval-ei.minval > 100) ei.value = Math.round(ei.value); else ei.value
				 * = Math.round(ei.value*100)/100.;
				 */
				ei.setValue(Math.round(ei.getValue() / ei.minval) * ei.minval);
				getElm().setEditValue(i, ei);
				ei.getTextf().setText(unitString(ei));
				cframe.needAnalyze();
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object src = e.getItemSelectable();
		int i;
		boolean changed = false;
		for (i = 0; i != einfocount; i++) {
			EditInfo ei = einfos[i];
			if (ei.getChoice() == src || ei.getCheckbox() == src) {
				getElm().setEditValue(i, ei);
				if (ei.isNewDialog())
					changed = true;
				cframe.needAnalyze();
			}
		}
		if (changed) {
			setVisible(false);
			CirSim.setEditDialog(new EditDialog(getElm(), cframe));
			CirSim.getEditDialog().show();
		}
	}

	@Override
	public boolean handleEvent(Event ev) {
		if (ev.id == Event.WINDOW_DESTROY) {
			closeDialog();
			return true;
		}
		return super.handleEvent(ev);
	}

	void setBar(EditInfo ei) {
		int x = (int) (barmax * (ei.getValue() - ei.minval) / (ei.maxval - ei.minval));
		ei.bar.setValue(x);
	}

	protected void closeDialog() {
		CirSim.getMain().requestFocus();
		setVisible(false);
		CirSim.setEditDialog(null);
	}

	public Editable getElm() {
		return elm;
	}

	public void setElm(Editable elm) {
		this.elm = elm;
	}
}
