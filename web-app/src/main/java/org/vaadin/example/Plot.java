package org.vaadin.example;

import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Plot extends Composite<VerticalLayout> implements HasComponents {
	private List<String> idsList;
	List<String> timeline;
	double[][] yvalues;
	ChartComponent chartElm;
	String seriesName;
	VerticalLayout prova;

	public Plot(List<String> idsList, String[] timeline, double[][] yvalues, String seriesName) {
		this.idsList = idsList;
		this.timeline = Arrays.asList(timeline);
		this.yvalues = yvalues;
		this.seriesName = seriesName;

		ComboBox<String> ids = new ComboBox<>();
		ids.setItems(idsList);
		ids.setValue(idsList.get(0));
		ids.setWidth("900px");
		ids.addValueChangeListener(null); // cambio grafico

		add(ids);

		// settare grafico
		chartElm = new ChartComponent(this.timeline, seriesName, yvalues[0]);
		add(chartElm.getChart());
	}

	public ChartComponent getChart() {
		return chartElm;
	}

	public void updateFlowChart(String[] timeline, double[][] yvalues) {
		remove(chartElm.getChart());
		chartElm.updateChart(Arrays.asList(timeline), yvalues[0]);
		add(chartElm.getChart());
	}
}
