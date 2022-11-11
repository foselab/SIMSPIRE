package charts;

import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Plot extends Composite<VerticalLayout> implements HasComponents {
	private List<String> timeline;
	double[][] yvalues;
	ChartComponent chartElm;
	ApexChartComponent myChart;
	String seriesName;
	int shownElm = 0;

	public Plot(List<String> idsList, String[] timeline, double[][] yvalues, double[][] yvaluesVent,
			String seriesName) {
		this.timeline = Arrays.asList(timeline);
		this.yvalues = yvalues;
		this.seriesName = seriesName;

		ComboBox<String> ids = new ComboBox<>();
		ids.setItems(idsList);
		ids.setAllowCustomValue(false); // custom values are not allowed
		ids.setRequired(true); // there must be a value selected
		ids.setValue(idsList.get(shownElm));
		ids.setWidth("900px");
		ids.addValueChangeListener(event -> {
			String newId = event.getValue();
			String oldId = event.getOldValue();

			if (newId != null) {
				shownElm = idsList.indexOf(newId);
				if (yvaluesVent == null) {
					updateChart(timeline, yvalues, null);
				} else {
					updateChart(timeline, yvalues, yvaluesVent);
				}
			} else {
				Notification.show("Please pick a valid option for " + seriesName + " field");
				ids.setValue(oldId);
			}

		}); // cambio grafico

		add(ids);
/*
		// settare grafico
		if (yvaluesVent == null) {
			chartElm = new ChartComponent(this.timeline, seriesName, yvalues[shownElm], null);
		} else {
			chartElm = new ChartComponent(this.timeline, seriesName, yvalues[shownElm], yvaluesVent[1]);
		}
		
		add(chartElm.getChart());*/
		
		// settare grafico
				if (yvaluesVent == null) {
					myChart = new ApexChartComponent(this.timeline, yvalues[shownElm], null, seriesName);
				} else {
					myChart = new ApexChartComponent(this.timeline, yvalues[shownElm], yvaluesVent[1], seriesName);
				}
		add(myChart);
	}

	public void updateChart(String[] timeline, double[][] yvalues, double[][] yvaluesVent) {
		/*remove(chartElm.getChart());
		if (yvaluesVent == null) {
			chartElm.updateChart(Arrays.asList(timeline), yvalues[shownElm], null);
		} else {
			chartElm.updateChart(Arrays.asList(timeline), yvalues[shownElm], yvaluesVent[1]);
		}
		add(chartElm.getChart());*/
		
		if (yvaluesVent == null) {
			myChart.updateChart(Arrays.asList(timeline), yvalues[shownElm], null);
		} else {
			myChart.updateChart(Arrays.asList(timeline), yvalues[shownElm], yvaluesVent[1]);
		}
	}
}
