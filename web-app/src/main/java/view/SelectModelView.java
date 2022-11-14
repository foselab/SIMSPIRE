package view;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
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
public class SelectModelView extends Composite<VerticalLayout> implements HasComponents{

	/**
	 * Init the view for model selection
	 */
	public SelectModelView() {
		final LungSimulator lungSimulator = new LungSimulator();
		add(showChooseModelForm(lungSimulator));
	}

	private Dialog showChooseModelForm(final LungSimulator lungSimulator) {
		final Dialog initDialog = new Dialog();
		initDialog.setModal(true);
		initDialog.setCloseOnOutsideClick(false);
		initDialog.open();

		final ChooseModelForm cmf = new ChooseModelForm(lungSimulator, () -> {
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
