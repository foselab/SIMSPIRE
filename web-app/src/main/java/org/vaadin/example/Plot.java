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
	ChartComponent chart;
	String seriesName;
	
	public Plot(List<String> idsList, String[] timeline, double[][] yvalues, String seriesName) {
		this.idsList = idsList;
		this.timeline = Arrays.asList(timeline);
		this.yvalues = yvalues;
		this.seriesName = seriesName;
	}
	
	@Override
	protected Component initContent() {
		ComboBox<String> ids = new ComboBox<>();
		ids.setItems(idsList);
		ids.setValue(idsList.get(0));
		ids.setWidth("900px");
		ids.addValueChangeListener(null); // cambio grafico
		
		//settare grafico
		chart = new ChartComponent(timeline, seriesName, yvalues[0]);
		return new VerticalLayout(ids, chart.getChart());
	}

	public ChartComponent getChart() {
		return chart;
	}
}
