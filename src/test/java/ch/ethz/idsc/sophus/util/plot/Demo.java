// code by gjoel, jph
package ch.ethz.idsc.sophus.util.plot;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ enum Demo {
  ;
  static void demoPlots(File folder, boolean labels) throws IOException {
    folder.mkdirs();
    Tensor values1 = RandomVariate.of(UniformDistribution.unit(), 5);
    Tensor values2 = RandomVariate.of(UniformDistribution.unit(), 15);
    Tensor values3 = RandomVariate.of(UniformDistribution.unit(), 10);
    VisualSet visualSet = new VisualSet(ColorDataLists._250.cyclic());
    VisualRow row0 = visualSet.add(Range.of(0, values1.length()), values1);
    // VisualRow row1 =
    visualSet.add(Range.of(0, values2.length()), values2);
    VisualRow row2 = visualSet.add(Range.of(3, 3 + values3.length()), values3);
    if (labels) {
      row0.setLabel("row 0");
      // row2.setLabel("row 2");
      row2.setLabel("row 2");
      visualSet.setAxesLabelX("x axis");
      visualSet.setAxesLabelY("y axis");
      // visualSet.setDomainAxisLabel("x axis");
      // visualSet.setRangeAxisLabel("y axis");
    }
    /* amodeus specific */
    // ChartFactory.setChartTheme(ChartTheme.STANDARD);
    {
      visualSet.setPlotLabel(StackedHistogram.class.getSimpleName());
      export(folder, StackedHistogram.of(visualSet));
    }
    {
      visualSet.setPlotLabel(Histogram.class.getSimpleName());
      export(folder, Histogram.of(visualSet));
    }
    {
      visualSet.setPlotLabel(TimeChart.class.getSimpleName());
      export(folder, TimeChart.of(visualSet));
    }
    {
      visualSet.setPlotLabel(StackedTimeChart.class.getSimpleName());
      export(folder, StackedTimeChart.of(visualSet));
    }
    {
      visualSet.setPlotLabel(ListPlot.class.getSimpleName());
      export(folder, ListPlot.of(visualSet));
    }
    {
      visualSet.setPlotLabel(StackedTablePlot.class.getSimpleName());
      export(folder, StackedTablePlot.of(visualSet));
    }
  }

  private static void export(File folder, JFreeChart jFreeChart) throws IOException {
    File file = new File(folder, jFreeChart.getTitle().getText() + ".png");
    jFreeChart.setBackgroundPaint(Color.WHITE);
    ChartUtils.saveChartAsPNG(file, jFreeChart, 500, 300);
  }

  public static void main(String[] args) throws IOException {
    demoPlots(HomeDirectory.Pictures("subare", "labels_0"), false);
    demoPlots(HomeDirectory.Pictures("subare", "labels_1"), true);
  }
}
