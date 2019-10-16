// code by jph
package ch.ethz.idsc.tensor.fig;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ enum TestHelper {
  ;
  public static void draw(JFreeChart jFreeChart) {
    BufferedImage bufferedImage = new BufferedImage(400, 200, BufferedImage.TYPE_INT_ARGB);
    jFreeChart.draw(bufferedImage.createGraphics(), new Rectangle2D.Double(0, 0, 400, 200));
  }

  public static void cascade(File folder, boolean labels) throws IOException {
    folder.mkdirs();
    Tensor values1 = RandomVariate.of(UniformDistribution.unit(), 5);
    Tensor values2 = RandomVariate.of(UniformDistribution.unit(), 15);
    Tensor values3 = RandomVariate.of(UniformDistribution.unit(), 10);
    VisualSet visualSet = new VisualSet(ColorDataLists._250.cyclic());
    VisualRow row0 = visualSet.add(Range.of(0, values1.length()), values1);
    visualSet.add(Range.of(0, values2.length()), values2);
    VisualRow row2 = visualSet.add(Range.of(3, 3 + values3.length()), values3);
    if (labels) {
      row0.setLabel("row 0");
      row2.setLabel("row 2");
      visualSet.setAxesLabelX("x axis");
      visualSet.setAxesLabelY("y axis");
    }
    {
      visualSet.setPlotLabel(StackedHistogram.class.getSimpleName());
      export(folder, StackedHistogram.of(visualSet));
    }
    {
      visualSet.setPlotLabel(Histogram.class.getSimpleName());
      export(folder, Histogram.of(visualSet));
    }
    {
      visualSet.setPlotLabel(Histogram.class.getSimpleName() + "Function");
      export(folder, Histogram.of(visualSet, scalar -> "[" + scalar.toString() + "]"));
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
}
