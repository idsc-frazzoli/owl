// code by gjoel, jph
package ch.ethz.idsc.tensor.fig;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;

public enum TimeChart {
  ;
  public static JFreeChart of(VisualSet visualSet) {
    return of(visualSet, false);
  }

  public static JFreeChart of(VisualSet visualSet, boolean stacked) {
    JFreeChart jFreeChart = JFreeChartFactory.fromXYTable(visualSet, stacked, DatasetFactory.timeTableXYDataset(visualSet));
    DateAxis domainAxis = new DateAxis();
    domainAxis.setLabel(visualSet.getAxesLabelX());
    domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.SECOND, 1));
    domainAxis.setAutoTickUnitSelection(true);
    jFreeChart.getXYPlot().setDomainAxis(domainAxis);
    return jFreeChart;
  }
}
