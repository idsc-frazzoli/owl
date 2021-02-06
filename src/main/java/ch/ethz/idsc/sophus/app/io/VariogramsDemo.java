// code by jph
package ch.ethz.idsc.sophus.app.io;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.sophus.math.var.Variograms;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualRow;
import ch.ethz.idsc.tensor.fig.VisualSet;

public enum VariogramsDemo {
  ;
  public static void main(String[] args) throws IOException {
    File folder = HomeDirectory.Pictures("Variograms");
    folder.mkdir();
    Tensor domain = Subdivide.of(0.0, 2.0, 30);
    Scalar[] params = { RealScalar.ZERO, RealScalar.of(0.1), RationalScalar.HALF, RealScalar.ONE, RealScalar.TWO };
    for (Variograms variograms : Variograms.values()) {
      VisualSet visualSet = new VisualSet();
      visualSet.setPlotLabel(variograms.toString());
      for (Scalar param : params)
        try {
          Tensor values = domain.map(variograms.of(param));
          VisualRow visualRow = visualSet.add(domain, values);
          visualRow.setLabel("" + param);
        } catch (Exception exception) {
          System.out.println(variograms);
        }
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      jFreeChart.setBackgroundPaint(Color.WHITE);
      File file = new File(folder, variograms + ".png");
      ChartUtils.saveChartAsPNG(file, jFreeChart, 500, 300);
    }
  }
}
