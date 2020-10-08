// code by jph
package ch.ethz.idsc.tensor.fig;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ enum ListPlotDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor values1 = RandomVariate.of(UniformDistribution.unit(), 5);
    Tensor values2 = RandomVariate.of(UniformDistribution.unit(), 15);
    Tensor values3 = RandomVariate.of(UniformDistribution.unit(), 10);
    VisualSet visualSet = new VisualSet();
    Tensor domain1 = RandomVariate.of(UniformDistribution.unit(), values1.length());
    VisualRow visualRow1 = visualSet.add(domain1, values1);
    visualRow1.setLabel("first");
    Tensor domain2 = RandomVariate.of(UniformDistribution.unit(), values2.length());
    visualSet.add(domain2, values2);
    Tensor domain3 = RandomVariate.of(UniformDistribution.unit(), values3.length());
    visualSet.add(domain3, values3);
    Tensor domain4 = Tensors.vector(1, 3, 2, 5, 4).multiply(RealScalar.of(0.2));
    visualSet.add(domain4, domain4);
    /* amodeus specific */
    // ChartFactory.setChartTheme(ChartTheme.STANDARD);
    {
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      File file = HomeDirectory.Pictures(ListPlot.class.getSimpleName() + ".png");
      ChartUtils.saveChartAsPNG(file, jFreeChart, 500, 300);
    }
  }
}
