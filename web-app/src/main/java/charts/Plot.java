package charts;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Plot extends Composite<VerticalLayout> implements HasComponents {
	ChartComponent chartElm;
	ApexChartComponent myChart;
	String seriesName;
	String shownElm;

	public Plot(List<String> idsList, List<String> timeline, Map<String, List<Double>> yvalues, List<Double> yvaluesVent,
			String seriesName) {
		this.seriesName = seriesName;
		shownElm = idsList.get(0); //primo della lista come default

		ComboBox<String> ids = new ComboBox<>();
		ids.setItems(idsList);
		ids.setAllowCustomValue(false); // custom values are not allowed
		ids.setRequired(true); // there must be a value selected
		ids.setValue(shownElm);
		ids.setWidth("900px");
		ids.addValueChangeListener(event -> {
			String newId = event.getValue();
			String oldId = event.getOldValue();

			if (newId != null) {
				shownElm = newId;
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
		
		// settare grafico
				if (yvaluesVent == null) {
					myChart = new ApexChartComponent(timeline, yvalues.get(shownElm), null, seriesName);
				} else {
					myChart = new ApexChartComponent(timeline, yvalues.get(shownElm), yvaluesVent, seriesName);
				}
		add(myChart);
	}

	public void updateChart(List<String> timeline, Map<String, List<Double>> yvalues, List<Double> yvaluesVent) {		
		if (yvaluesVent == null) {
			myChart.updateChart(timeline, yvalues.get(shownElm), null);
		} else {
			myChart.updateChart(timeline, yvalues.get(shownElm), yvaluesVent);
		}
	}
}
