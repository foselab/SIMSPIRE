package utils;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Scrollbar;
import java.awt.TextField;

public class EditInfo {
	public EditInfo(String n, double val, double mn, double mx) {
		name = n;
		setValue(val);
		if (mn == 0 && mx == 0 && val > 0) {
			minval = 1e10;
			while (minval > val / 100)
				minval /= 10.;
			maxval = minval * 1000;
		} else {
			minval = mn;
			maxval = mx;
		}
		forceLargeM = name.indexOf("(ohms)") > 0 || name.indexOf("(Hz)") > 0;
		dimensionless = false;
	}

	public EditInfo setDimensionless() {
		dimensionless = true;
		return this;
	}

	public Checkbox getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(Checkbox checkbox) {
		this.checkbox = checkbox;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public TextField getTextf() {
		return textf;
	}

	public TextField setTextf(TextField textf) {
		this.textf = textf;
		return textf;
	}

	public Choice getChoice() {
		return choice;
	}

	public void setChoice(Choice choice) {
		this.choice = choice;
	}

	public boolean isNewDialog() {
		return newDialog;
	}

	public void setNewDialog(boolean newDialog) {
		this.newDialog = newDialog;
	}

	String name;
	private String text;
	private double value;
	double minval;
	double maxval;
	private TextField textf;
	Scrollbar bar;
	private Choice choice;
	public Checkbox checkbox;
	private boolean newDialog;
	boolean forceLargeM;
	boolean dimensionless;
}
