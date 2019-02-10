// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualRow;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ enum ChartDemo {
  ;
  public static void main(String[] args) throws IOException {
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("PlotLabel");
    visualSet.setAxesLabelX("AxesLabelX");
    visualSet.setAxesLabelY("AxesLabelY");
    Distribution distribution = UniformDistribution.unit();
    {
      Tensor domain = Sort.of(RandomVariate.of(distribution, 100));
      Tensor values = RandomVariate.of(distribution, 100);
      VisualRow visualRow = visualSet.add(domain, values);
      visualRow.setLabel("uniform");
    }
    {
      Tensor domain = Sort.of(RandomVariate.of(distribution, 200));
      Tensor values = RandomVariate.of(NormalDistribution.standard(), 200);
      VisualRow visualRow = visualSet.add(domain, values);
      visualRow.setLabel("normal");
    }
    {
      Tensor domain = Subdivide.of(0, 1.2, 200);
      Tensor values = domain.map(Series.of(Tensors.vector(.3, -.5, 0.5, 0.3)));
      VisualRow visualRow = visualSet.add(domain, values);
      visualRow.setLabel("cubic polynomial");
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    File file = HomeDirectory.Pictures(ChartDemo.class.getSimpleName() + ".png");
    ChartUtils.saveChartAsPNG(file, jFreeChart, 1024, 768);
  }
}
