package org.vaadin.example;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.github.appreciated.apexcharts.ApexCharts;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import lungsimulator.LungSimulator;

/**
 * The main view contains a text field for getting the user name and a button
 * that shows a greeting message in a notification.
 */
@Route("")
public class MainView extends Composite<Component> {

	private LungSimulator lungSimulator = new LungSimulator();
	CircuitComponents cc;
	VerticalLayout mvv = new VerticalLayout();
	VerticalLayout mvvd = new VerticalLayout();

	@Override
	protected Component initContent() {
		H3 title = new H3("INSPIRE");
		mvv.add(title, showChooseModelForm(lungSimulator));
		return new HorizontalLayout(mvv, mvvd);
	}

	private Dialog showChooseModelForm(LungSimulator lungSimulator) {
		Dialog initDialog = new Dialog();
		initDialog.setModal(true);
		initDialog.setCloseOnOutsideClick(false);
		initDialog.open();

		ChooseModelForm cmf = new ChooseModelForm(lungSimulator, () -> {
			initDialog.close();
			lungSimulator.modelValidation();
			while (true) {
				lungSimulator.mini();
				cc = new CircuitComponents(lungSimulator);
				mvv.add(cc);
				mvvd.add(new RightVerticalLayout(lungSimulator));
			}
		});

		initDialog.add(cmf);

		return initDialog;
	}
}
