package charts;

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
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ApexChartComponent extends Composite<VerticalLayout> implements HasComponents {
	ApexChartsBuilder myChart;
	ApexCharts finalChart;
	ApexCharts oldfinalChart;
	int yLimit;
	String seriesName;

	public ApexChartComponent(List<String> timeline, List<Double> yvalues, List<Double> yvaluesVent,
			String seriesName) {
		myChart = ApexChartsBuilder.get(); // init chart
		this.seriesName = seriesName;
		chartSetUpConstantParameters();
		yLimit = 2;
	}

	/**
	 * Set up all chart's parameters that won't change during simulation
	 */
	private void chartSetUpConstantParameters() {
		myChart.withChart(ChartBuilder.get().withType(Type.LINE).withZoom(ZoomBuilder.get().withEnabled(false).build())
				.withAnimations(AnimationsBuilder.get().withEnabled(false)
						.withDynamicAnimation(DynamicAnimationBuilder.get().withEnabled(false).build()).build())
				.build());

		myChart.withStroke(StrokeBuilder.get().withCurve(Curve.STRAIGHT).build());

		myChart.withGrid(GridBuilder.get()
				.withRow(RowBuilder.get().withColors("#f3f3f3", "transparent").withOpacity(0.5).build()).build());
	}

	/**
	 * Set up or update modified parameters
	 * 
	 * @param init
	 */
	public void updateChart(List<String> timeline, List<Double> yvalues, List<Double> yvaluesVent) {
		// x-axis set up
		myChart.withXaxis(XAxisBuilder.get().withTitle(
				com.github.appreciated.apexcharts.config.xaxis.builder.TitleBuilder.get().withText("Time [s]").build())
				.withCategories(timeline).build());

		int lastIndex = yvalues.size() - 1;

		if (yLimit < yvalues.get(lastIndex)) {
			yLimit = (int) (yvalues.get(lastIndex) + 1);
		}

		if (yvaluesVent != null && yLimit < yvaluesVent.get(lastIndex)) {
			yLimit = (int) (yvaluesVent.get(lastIndex) + 1);
		}

		// y-axis set up
		myChart.withYaxis(YAxisBuilder.get()
				.withTitle(com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder.get()
						.withText(seriesName).build())
				.withMin(yvaluesVent == null ? -yLimit : 0.0).withMax(yLimit).build());

		if (yvalues.size() > 1) {
			oldfinalChart = finalChart;
			finalChart = myChart.build();
			if (yvaluesVent == null) {
				finalChart.updateSeries(new Series<>(seriesName, yvalues.toArray()));
			} else {
				finalChart.updateSeries(new Series<>(seriesName, yvalues.toArray()),
						new Series<>("Ventilator", yvaluesVent.toArray()));
			}
			remove(oldfinalChart);
			add(finalChart);
		} else {
			if (yvaluesVent == null) {
				myChart.withSeries(new Series<>(seriesName, yvalues.toArray())).build();
			} else {
				myChart.withSeries(new Series<>(seriesName, yvalues.toArray()),
						new Series<>("Ventilator", yvaluesVent.toArray())).build();
			}
			finalChart = myChart.build();
			add(finalChart);
		}
	}

	public ApexCharts getMyChart() {
		return myChart.build();
	}
}
