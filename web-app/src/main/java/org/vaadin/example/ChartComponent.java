package org.vaadin.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class ChartComponent extends ApexChartsBuilder{
	private ApexCharts myChart;
	String seriesName;
	
	public ChartComponent(List<String> timeline, String seriesName, double[] yvalues) {
		List<Double> myvalues = new ArrayList<>();
		for(int i=0; i<yvalues.length; i++) {
			myvalues.add(yvalues[i]);
		}
		
		//double maxValueX = Double.parseDouble(timeline.get(timeline.size()-1));
		this.seriesName = seriesName;
		myChart = ApexChartsBuilder.get()
				.withChart(ChartBuilder.get()
                .withType(Type.LINE)
                .withZoom(ZoomBuilder.get()
                        .withEnabled(false)
                        .build())
                .build())
                .withStroke(StrokeBuilder.get()
                        .withCurve(Curve.STRAIGHT)
                        .build())
                .withGrid(GridBuilder.get()
                        .withRow(RowBuilder.get()
                                .withColors("#f3f3f3", "transparent")
                                .withOpacity(0.5).build()
                        ).build())
                .withXaxis(XAxisBuilder.get()
                		.withTitle(com.github.appreciated.apexcharts.config.xaxis.builder.TitleBuilder.get().withText("Time [s]").build())
                        //.withMin(maxValueX > 12 ?  maxValueX - 12 : 0.0)
                		//.withMax(maxValueX)
                		.withCategories(timeline)
                		.build())
                .withYaxis(YAxisBuilder.get()
                        .withTitle(com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder.get().withText(seriesName).build())
                        .withMin(-5.0)
                        .withMax(5.0)
                        .build()
                )
                .withSeries(new Series<>(seriesName, myvalues.toArray()))
                .build();
		
	}
	
	public void updateChart(List<String> timeline, double[] yvalues) {
		List<Double> myvalues = new ArrayList<>();
		for(int i=0; i<yvalues.length; i++) {
			myvalues.add(yvalues[i]);
		}
		
		myChart.setXaxis(XAxisBuilder.get()
                		.withTitle(com.github.appreciated.apexcharts.config.xaxis.builder.TitleBuilder.get().withText("Time [s]").build())
                        .withCategories(timeline) //time
                        .build());

		myChart.updateSeries(new Series<>(myvalues.toArray()));
	}

	public ApexCharts getChart() {
		return myChart;
	}

	public void setChart(ApexCharts chart) {
		this.myChart = chart;
	}
}
