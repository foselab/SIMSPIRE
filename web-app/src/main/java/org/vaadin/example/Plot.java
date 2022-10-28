package org.vaadin.example;

import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Plot extends Composite<Component>{
	private List<String> idsList;
	List<String> timeline;
	double[][] yvalues;
	
	public Plot(List<String> idsList, String[] timeline) {
		this.idsList = idsList;
		this.timeline = Arrays.asList(timeline);
	}
	
	public Plot(List<String> idsList, String[] timeline, double[][] yvalues) {
		this.idsList = idsList;
		this.timeline = Arrays.asList(timeline);
		this.yvalues = yvalues;
	}
	
	@Override
	protected Component initContent() {
		ComboBox<String> ids = new ComboBox<>();
		ids.setItems(idsList);
		ids.setValue(idsList.get(0));
		ids.setWidth("900px");
		ids.addValueChangeListener(null); // cambio grafico
		
		//settare grafico
		ChartComponent chart = new ChartComponent(timeline, "Flow", yvalues[0]);
		return new VerticalLayout(ids, chart.build());
	}
}
