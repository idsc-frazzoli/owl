// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;

import ch.ethz.idsc.subare.plot.ListPlotBuilder;
import ch.ethz.idsc.subare.plot.XYDatasets;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ enum ChartDemo {
  ;
  public static void main(String[] args) throws IOException {
    Map<String, Tensor> map = new LinkedHashMap<>();
    Distribution distribution = UniformDistribution.unit();
    {
      Tensor domain = Sort.of(RandomVariate.of(distribution, 100));
      Tensor values = RandomVariate.of(distribution, 100);
      Tensor points = Transpose.of(Tensors.of(domain, values));
      map.put("uniform", points);
    }
    {
      Tensor domain = Sort.of(RandomVariate.of(distribution, 200));
      Tensor values = RandomVariate.of(NormalDistribution.standard(), 200);
      Tensor points = Transpose.of(Tensors.of(domain, values));
      map.put("normal", points);
    }
    {
      Tensor domain = Subdivide.of(0, 1.2, 200);
      Tensor values = domain.map(Series.of(Tensors.vector(.3, -.5, 0.5, 0.3)));
      Tensor points = Transpose.of(Tensors.of(domain, values));
      map.put("cubic polynomial", points);
    }
    XYDataset xyDataset = XYDatasets.create(map);
    ListPlotBuilder listPlotBuilder = new ListPlotBuilder("ListPlot Example", "axisLabelX", "axisLabelY", xyDataset);
    JFreeChart jFreeChart = listPlotBuilder.getJFreeChart();
    File file = HomeDirectory.Pictures(ChartDemo.class.getSimpleName() + ".png");
    ChartUtils.saveChartAsPNG(file, jFreeChart, 1024, 768);
    System.out.println("exported to " + file);
  }
}
