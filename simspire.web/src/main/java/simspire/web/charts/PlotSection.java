package simspire.web.charts;

import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Manages plot section of the simulation simspire.web.view
 */
public class PlotSection extends Composite<VerticalLayout> implements HasComponents {
	/**
	 * Final chart object
	 */
	private final transient ApexChartComponent myChart;

	/**
	 * Current value that is shown on the plot
	 */
	private transient String shownElm;

	/**
	 * Set up the plot section
	 * @param idsList ids that has to be shown in the combo box
	 * @param timeline x-axis values
	 * @param yvalues y-axis values
	 * @param yvaluesVent y-axis ventilator values 
	 * @param seriesName name of the series 
	 */
	public PlotSection(final List<String> idsList, final List<Double> timeline, final Map<String, List<Double>> yvalues, final List<Double> yvaluesVent,
			final String seriesName) {
		shownElm = idsList.get(0); //primo della lista come default

		final ComboBox<String> ids = new ComboBox<>();
		ids.setItems(idsList);
		ids.setAllowCustomValue(false); // custom values are not allowed
		ids.setRequired(true); // there must be a value selected
		ids.setValue(shownElm);
		ids.setWidth("900px");
		ids.addValueChangeListener(event -> {
			final String newId = event.getValue();
			final String oldId = event.getOldValue();

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
					myChart = new ApexChartComponent(timeline, yvalues.get(shownElm), seriesName);
				} else {
					myChart = new ApexChartComponent(timeline, yvalues.get(shownElm), seriesName);
				}
		add(myChart);
	}

	/**
	 * Update chart values
	 * @param timeline x-axis values
	 * @param yvalues y-axis values
	 * @param yvaluesVent y-axis ventilator values
	 */
	public void updateChart(final List<Double> timeline, final Map<String, List<Double>> yvalues, final List<Double> yvaluesVent) {		
		if (yvaluesVent == null) {
			myChart.updateChart(timeline, yvalues.get(shownElm), null);
		} else {
			myChart.updateChart(timeline, yvalues.get(shownElm), yvaluesVent);
		}
	}
}
