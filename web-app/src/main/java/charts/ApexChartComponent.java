package charts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.core.pattern.EqualsBaseReplacementConverter;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.GridBuilder;
import com.github.appreciated.apexcharts.config.builder.StrokeBuilder;
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder;
import com.github.appreciated.apexcharts.config.builder.YAxisBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.grid.builder.RowBuilder;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ApexChartComponent extends Composite<VerticalLayout> implements HasComponents {
	ApexChartsBuilder myChart;
	ApexCharts finalChart;
	ApexCharts oldfinalChart;
	int yLimit;
	int numberOfData;
	String seriesName;
	int counter;
	List<String> time;
	List<Double> yvaluesList;
	List<Double> yvaluesVentList;

	public ApexChartComponent(List<String> timeline, double[] yvalues, double[] yvaluesVent, String seriesName) {
		myChart = ApexChartsBuilder.get(); // init chart
		this.seriesName = seriesName;
		chartSetUpConstantParameters();
		yLimit = 0;
		numberOfData = yvalues.length;
		time = new ArrayList<>();
		yvaluesList = new ArrayList<>();
		if (yvaluesVent != null) {
			yvaluesVentList = new ArrayList<>();
		}
		counter = 0;
		updateChart(timeline, yvalues, yvaluesVent);
		add(finalChart);
	}

	/**
	 * Set up all chart's parameters that won't change during simulation
	 */
	private void chartSetUpConstantParameters() {
		myChart.withChart(
				ChartBuilder.get().withType(Type.LINE).withZoom(ZoomBuilder.get().withEnabled(false).build()).build());

		myChart.withStroke(StrokeBuilder.get().withCurve(Curve.STRAIGHT).build());

		myChart.withGrid(GridBuilder.get()
				.withRow(RowBuilder.get().withColors("#f3f3f3", "transparent").withOpacity(0.5).build()).build());
	}

	/**
	 * Set up or update modified parameters
	 */
	public void updateChart(List<String> timeline, double[] yvalues, double[] yvaluesVent) {
		if (counter < numberOfData) {
			time.add(timeline.get(numberOfData - 1));
			yvaluesList.add(yvalues[numberOfData - 1]);

			if (yvaluesVent != null) {
				yvaluesVentList.add(yvaluesVent[numberOfData - 1]);
			}
			counter++;
		} else {
			time = timeline;
			yvaluesList = Arrays.asList(ArrayUtils.toObject(yvalues));
			if (yvaluesVent != null) {
				yvaluesVentList = yvaluesVent == null ? null : Arrays.asList(ArrayUtils.toObject(yvaluesVent));
			}
		}

		// x-axis set up
		myChart.withXaxis(XAxisBuilder.get().withTitle(
				com.github.appreciated.apexcharts.config.xaxis.builder.TitleBuilder.get().withText("Time [s]").build())
				.withCategories(time).build());

		if (yLimit == 0) {
			// cerco il massimo
			yLimit = findMax(yvaluesList, yvaluesVentList);
		} else {
			if (yLimit < yvalues[numberOfData - 1]) {
				yLimit = (int) yvalues[numberOfData - 1] + 1;
			}

			if (yvaluesVent != null && yLimit < yvaluesVent[numberOfData - 1]) {
				yLimit = (int) yvaluesVent[numberOfData - 1] + 1;
			}
		}

		// y-axis set up
		myChart.withYaxis(YAxisBuilder.get().withTitle(
				com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder.get().withText(seriesName).build())
				.withMin(-yLimit).withMax(yLimit).build());
		// if (counter == 1) {
		if (yvaluesVent == null) {
			myChart.withSeries(new Series<>(seriesName, yvaluesList.toArray())).build();
		} else {
			myChart.withSeries(new Series<>(seriesName, yvaluesList.toArray()),
					new Series<>("Ventilator", yvaluesVentList.toArray())).build();
		}
		/*
		 * } else { if (yvaluesVent == null) { finalChart.updateSeries(new
		 * Series<>(seriesName, yvaluesList.toArray())); } else {
		 * finalChart.updateSeries(new Series<>(seriesName, yvaluesList.toArray()), new
		 * Series<>("Ventilator", yvaluesVentList.toArray())); } }
		 */

		if (counter > 1) {
			oldfinalChart = finalChart;
			finalChart = myChart.build();
			remove(oldfinalChart);
			add(finalChart);
		} else {
			finalChart = myChart.build();
		}
	}

	private int findMax(List<Double> yvaluesList, List<Double> yvaluesVentList) {
		double yValuesMax = Collections.max(yvaluesList);
		double yValuesVentMax = yvaluesVentList == null ? 0.0 : Collections.max(yvaluesVentList);

		int max;

		if (yValuesMax > yValuesVentMax) {
			max = (int) yValuesMax + 1;
		} else {
			max = (int) yValuesVentMax + 1;
		}

		return max;
	}

	public ApexCharts getMyChart() {
		return myChart.build();
	}
}
