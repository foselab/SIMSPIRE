package org.vaadin.example;

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
import com.github.appreciated.apexcharts.config.yaxis.builder.AxisBorderBuilder;
import com.github.appreciated.apexcharts.helper.Series;

public class ChartComponent extends ApexChartsBuilder{
	private ApexCharts chart;
	
	public ChartComponent(List<String> timeline, String seriesName, double[] yvalues) {
		setChart(withChart(ChartBuilder.get()
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
                        .withCategories(timeline) //time
                        .build())
                .withYaxis(YAxisBuilder.get()
                        .withTitle(com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder.get().withText("Desktops").build())
                        .withAxisBorder(AxisBorderBuilder.get().withShow(true).build())
                        .build()
                )
                .withSeries(new Series<>(seriesName, yvalues))
                .build());
		
		chart.setHeight("200px");
		chart.setWidth("900px");
        /*withChart(ChartBuilder.get()
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
                        .withCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep")
                        .withTickPlacement(TickPlacement.BETWEEN)
                        .build())
                .withSeries(new Series<>("Desktops", 10.0, 41.0, 35.0, 51.0, 49.0, 62.0, 69.0, 91.0, 148.0));
    */}

	public ApexCharts getChart() {
		return chart;
	}

	public void setChart(ApexCharts chart) {
		this.chart = chart;
	}
}
