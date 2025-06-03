package simspire.web.charts;

import java.util.ArrayList;
import java.util.List;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.GridBuilder;
import com.github.appreciated.apexcharts.config.builder.StrokeBuilder;
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder;
import com.github.appreciated.apexcharts.config.builder.YAxisBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.animations.builder.DynamicAnimationBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.AnimationsBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.grid.builder.RowBuilder;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.xaxis.XAxisType;
import com.github.appreciated.apexcharts.helper.Coordinate;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Chart set up
 */
public class ApexChartComponent extends Composite<VerticalLayout> implements HasComponents {
	/**
	 * Final chart object
	 */
	private final transient ApexCharts myChart;

	/**
	 * Name of the series that has to be shown
	 */
	private final transient String seriesName;

	/**
	 * Coordinates of series values
	 */
	private transient List<Coordinate<Double, Double>> seriesCoord;

	/**
	 * Coordinates of ventilator values
	 */
	private transient List<Coordinate<Double, Double>> ventCoord;

	/**
	 * Builds the chart
	 * 
	 * @param timeline   x-axis values
	 * @param yvalues    y-axis values
	 * @param seriesName name of the series that has to be shown
	 */
	public ApexChartComponent(final List<Double> timeline, final List<Double> yvalues, final String seriesName) {
		this.seriesName = seriesName;
		buildSeries(timeline, yvalues, false);
		myChart = ApexChartsBuilder.get()
				.withChart(ChartBuilder.get()
						.withAnimations(AnimationsBuilder.get().withEnabled(false)
								.withDynamicAnimation(DynamicAnimationBuilder.get().withEnabled(false).build()).build())
						.withType(Type.LINE).withZoom(ZoomBuilder.get().withEnabled(false).build()).build())
				.withStroke(StrokeBuilder.get().withCurve(Curve.SMOOTH).build())
				.withGrid(GridBuilder.get()
						.withRow(RowBuilder.get().withColors("#f3f3f3", "transparent").withOpacity(0.5).build())
						.build())
				.withXaxis(XAxisBuilder.get()
						.withTitle(com.github.appreciated.apexcharts.config.xaxis.builder.TitleBuilder.get()
								.withText("Time [s]").build())
						.withType(XAxisType.NUMERIC).build())
				.withYaxis(YAxisBuilder.get()
						.withTitle(com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder.get()
								.withText(seriesName).build())
						.build())
				.withSeries(new Series<>(seriesName, seriesCoord.toArray())).build();

		add(myChart);
	}

	/**
	 * Updates the chart
	 * 
	 * @param timeline    x-axis values
	 * @param yvalues     y-axis values
	 * @param yvaluesVent y-axis ventilator values
	 */
	public void updateChart(final List<Double> timeline, final List<Double> yvalues, final List<Double> yvaluesVent) {
		buildSeries(timeline, yvalues, false);
		if (yvaluesVent == null) {
			myChart.updateSeries(new Series<>(seriesName, seriesCoord.toArray()));
		} else {
			buildSeries(timeline, yvaluesVent, true);
			myChart.updateSeries(new Series<>(seriesName, seriesCoord.toArray()),
					new Series<>("Ventilator", ventCoord.toArray()));
		}
	}

	private void buildSeries(final List<Double> timeline, final List<Double> yvalues, final boolean hasVent) {
		if (hasVent) {
			ventCoord = new ArrayList<>();

			for (int i = 0; i < yvalues.size(); i++) {
				ventCoord.add(new Coordinate<Double, Double>(timeline.get(i), yvalues.get(i)));
			}
		} else {
			seriesCoord = new ArrayList<>();

			for (int i = 0; i < yvalues.size(); i++) {
				seriesCoord.add(new Coordinate<Double, Double>(timeline.get(i), yvalues.get(i)));
			}
		}

	}
}
