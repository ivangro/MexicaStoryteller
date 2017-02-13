package mexica.reflection;

import mexica.story.Story;
import mexica.story.analyzer.AvatarTensions;
import mexica.story.guidelines.StoryTension;
import mexica.story.guidelines.TensionCurveAnalyzer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Ivan Guerrero (UNAM, Mexico)
 */
public class TensionGraph {
    public static ChartPanel getChart(AvatarTensions tensions, Story story) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(0, "Tension", "0");
        int year = story.getActions().size();
        for (int i=1; i<=year; i++) {
            dataset.addValue(tensions.getNumberOfTensions(i), "Tension", i+"");
        }
        JFreeChart chart = ChartFactory.createLineChart(tensions.getAvatar().toString(), "Timeline", "Tension value", 
                                             dataset, PlotOrientation.VERTICAL, false, true, false);
        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        // customise the range axis...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        ChartUtilities.applyCurrentTheme(chart);
        
        ChartPanel panel = new ChartPanel(chart);
        return panel;
    }
    
    public static ChartPanel getChart(StoryTension story) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(0, "Tension", "0");
        int year = story.getTensions().size();
        for (int i=0; i<year; i++) {
            int tensions = story.getTension(i);
            dataset.addValue(tensions, "Tension", i+"");
        }
        JFreeChart chart = ChartFactory.createLineChart("Story", "Timeline", "Tension value", 
                                             dataset, PlotOrientation.VERTICAL, false, true, false);
        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        // customise the range axis...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        ChartUtilities.applyCurrentTheme(chart);
        
        ChartPanel panel = new ChartPanel(chart);
        return panel;
    }
    
    public static ChartPanel getChart(Story story) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(0, "Tension", "0");
        int year = story.getActions().size();
        for (int i=1; i<=year; i++) {
            int tensions = TensionCurveAnalyzer.calculateNumberOfTensions(story, i);
            dataset.addValue(tensions, "Tension", i+"");
        }
        JFreeChart chart = ChartFactory.createLineChart("Story", "Timeline", "Tension value", 
                                             dataset, PlotOrientation.VERTICAL, false, true, false);
        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        // customise the range axis...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        ChartUtilities.applyCurrentTheme(chart);
        
        ChartPanel panel = new ChartPanel(chart);
        return panel;
    }
}