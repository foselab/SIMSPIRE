package view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;

import lungsimulator.LungSimulator;
import models.ChooseModelForm;

/**
 * The main view contains a text field for getting the user name and a button
 * that shows a greeting message in a notification.
 */
@Route("")
public class SelectModelView extends Composite<Component> {

	private LungSimulator lungSimulator = new LungSimulator();
	VerticalLayout mvv = new VerticalLayout();

	@Override
	protected Component initContent() {
		mvv.add(showChooseModelForm(lungSimulator));
		return new VerticalLayout(mvv);
	}

	private Dialog showChooseModelForm(LungSimulator lungSimulator) {
		Dialog initDialog = new Dialog();
		initDialog.setModal(true);
		initDialog.setCloseOnOutsideClick(false);
		initDialog.open();

		ChooseModelForm cmf = new ChooseModelForm(lungSimulator, () -> {
			lungSimulator.modelValidation();
			initDialog.close();

			VaadinSession.getCurrent().setAttribute("lungSimulator", lungSimulator);
			// l'utente può vedere il componente SimulationView solo per questa sessione
			RouteConfiguration.forSessionScope().setRoute("simulation", SimulationView.class);
			// accedi alla nuova view
			UI.getCurrent().navigate(SimulationView.class);
		});

		initDialog.add(cmf);

		return initDialog;
	}
}
