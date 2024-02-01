package ru.FedorILyaCO.MLTests.application.logic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;

import org.jfree.data.category.CategoryDataset;

import org.jfree.chart.renderer.category.LineAndShapeRenderer;

public class MyGraphs {


    public class LineChart1 extends ApplicationFrame
    {
        private static final long serialVersionUID = 1L;

        public LineChart1(final String title)
        {
            super(title);
            final CategoryDataset dataset    = Dataset.createDataset1();
            final JFreeChart      chart      = createChart(dataset);
            final ChartPanel      chartPanel = new ChartPanel(chart);

            chartPanel.setPreferredSize(new Dimension(560, 480));
            setContentPane(chartPanel);
        }

        private JFreeChart createChart(final CategoryDataset dataset)
        {
            final JFreeChart chart = ChartFactory.createLineChart(
                    "Линейный график 1",       // chart title
                    null,                      // domain axis label
                    "Значение",                // range axis label
                    dataset,                   // data
                    PlotOrientation.VERTICAL,  // orientation
                    true,                      // include legend
                    true,                      // tooltips
                    false                      // urls
            );

            chart.setBackgroundPaint(Color.white);

            final CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setBackgroundPaint(Color.lightGray);
            plot.setRangeGridlinePaint(Color.white);

            // customise the range axis...
            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            rangeAxis.setAutoRangeIncludesZero(true);

            // customise the renderer...
            final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

            renderer.setSeriesStroke(
                    0, new BasicStroke(
                            2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                            1.0f, new float[] {10.0f, 6.0f}, 0.0f
                    )
            );
            renderer.setSeriesStroke(
                    1, new BasicStroke(
                            2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                            1.0f, new float[] {6.0f, 6.0f}, 0.0f
                    )
            );
            return chart;
        }

    }

    public void makeLineGraph()
    {
        final LineChart1 demo = new LineChart1("Линейный график 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
    public class Dataset
    {
        public static CategoryDataset createDataset1()
        {
            DefaultCategoryDataset dataset;
            // row keys...
            final String series1 = "Series 1" ;
            final String series2 = "Series 2" ;
            final String series3 = "Series 3" ;

            // column keys...
            final String category1 = "Январь" ;
            final String category2 = "Февраль";
            final String category3 = "Март"   ;
            final String category4 = "Апрель" ;
            final String category5 = "Май"    ;

            dataset = new DefaultCategoryDataset();

            dataset.addValue(3.1, series1, category1);
            dataset.addValue(2.2, series1, category2);
            dataset.addValue(2.3, series1, category3);
            dataset.addValue(3.4, series1, category4);
            dataset.addValue(4.5, series1, category5);

            dataset.addValue(7.2, series2, category1);
            dataset.addValue(5.4, series2, category2);
            dataset.addValue(6.2, series2, category3);
            dataset.addValue(6.3, series2, category4);
            dataset.addValue(7.5, series2, category5);

            dataset.addValue(3.5, series3, category1);
            dataset.addValue(2.9, series3, category2);
            dataset.addValue(3.3, series3, category3);
            dataset.addValue(4.2, series3, category4);
            dataset.addValue(6.4, series3, category5);

            return dataset;
        }
        public static XYDataset createDataset2(final int idx)
        {
            final XYSeries series1 = new XYSeries("Series 1");
            series1.add( 1.0, 1.0);
            series1.add( 2.0, 4.0);
            series1.add( 3.0, 3.0);
            series1.add( 4.0, 5.0);
            series1.add( 5.0, 4.0);
            series1.add( 6.0, 7.0);
            series1.add( 8.0, 8.5);
            series1.add(11.0, 9.0);

            final XYSeries series2 = new XYSeries("Series 2");
            series2.add(1.0, 5.0);
            series2.add(2.0, 7.0);
            series2.add(3.0, 6.0);
            series2.add(4.0, 8.0);
            series2.add(5.0, 4.0);
            series2.add(6.0, 4.0);
            series2.add(7.0, 2.0);
            series2.add(8.0, 1.0);

            final XYSeries series3 = new XYSeries("Series 3");
            series3.add(3.0, 4.0);
            series3.add(4.0, 3.0);
            series3.add(5.0, 2.0);
            series3.add(6.0, 3.0);
            series3.add(7.0, 6.0);
            series3.add(8.0, 3.0);
            series3.add(10.0, 4.0);
            series3.add(12.0, 3.0);

            final XYSeriesCollection dataset = new XYSeriesCollection();
            if (idx == -1) {
                dataset.addSeries(series1);
                dataset.addSeries(series2);
                dataset.addSeries(series3);
            } else if (idx == 0) {
                dataset.addSeries(series1);
            } else if (idx == 1) {
                dataset.addSeries(series2);
            } else if (idx == 2) {
                dataset.addSeries(series3);
            }
            return dataset;
        }
    }
}
