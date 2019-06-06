// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Abs;

/* package */ class FourierWindowPlot {
  private static final int WINDOW_DURATION = 2;
  private static final int SAMPLING_FREQUENCY = 20;

  // TODO OB: replace with faster division
  private static Tensor elementwiseDivision(Tensor nominator, Tensor denominator) {
    Tensor result = Tensors.empty();
    for (int index = 0; index < nominator.length(); ++index) {
      Tensor temp = Tensors.empty();
      for (int j = 0; j < nominator.get(0).length(); ++j) {
        temp.append(nominator.get(index).get(j).divide(denominator.get(index).Get(j)));
      }
      result.append(temp);
    }
    return Mean.of(result);
  }

  // Make more generic for any type of filter
  private static Tensor transferFunction(Tensor control, TensorUnaryOperator tensorUnaryOperator, int signal) {
    Tensor refined = GeodesicCenterFilter.of(tensorUnaryOperator, 8).apply(control);
    // ---
    Tensor diffRefined = Tensor.of(Differences.of(refined).stream().map(xya -> xya.Get(signal)));
    Tensor diffRaw = Tensor.of(Differences.of(control).stream().map(xya -> xya.Get(signal)));
    // ---
    FourierWindow fw = new FourierWindow(WINDOW_DURATION, SAMPLING_FREQUENCY);
    Tensor specRefined = fw.apply(diffRefined);
    Tensor specRaw = fw.apply(diffRaw);
    // ---
    Tensor quotient = elementwiseDivision(specRefined, specRaw);
    return quotient;
  }

  private static void plot(Tensor yData) throws IOException {
    Tensor yDataAbs = Tensor.of(yData.stream().map(x -> Abs.FUNCTION.apply((Scalar) x)));
    // ---
    XYSeries series = new XYSeries("Series");
    for (int index = -yData.length() / 2; index < yData.length() / 2; ++index) {
      series.add((double) index * SAMPLING_FREQUENCY / yData.length(), yDataAbs.Get(Math.abs(index)).number().doubleValue());
    }
    LogarithmicAxis yAxis = new LogarithmicAxis("Magnitude");
    yAxis.setLog10TickLabelsFlag(true);
    NumberAxis xAxis = new NumberAxis("Frequency [Hz]");
    XYPlot plot = new XYPlot(new XYSeriesCollection(series), xAxis, yAxis, new XYLineAndShapeRenderer(true, false));
    JFreeChart jFreeChart = new JFreeChart("Filter Gain", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
    JFrame frame = new JFrame("LogAxis Test");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setContentPane(new ChartPanel(jFreeChart));
    frame.pack();
    frame.setVisible(true);
    File file = HomeDirectory.Pictures("test.png");
    ChartUtils.saveChartAsPNG(file, jFreeChart, 1024, 768);
  }

  private static void process(List<String> list, TensorUnaryOperator tensorUnaryOperator, int signal) throws IOException {
    Tensor results = Tensors.empty();
    Iterator<String> iterator = list.iterator();
    int limit = 5;
    for (int index = 0; index < limit; ++index) {
      Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + //
          iterator.next() + ".csv").stream().map(row -> row.extract(1, 4)));
      index++;
      results.append(transferFunction(control, tensorUnaryOperator, signal));
    }
    plot(Mean.of(results));
  }

  public static void main(String[] args) throws IOException {
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(Se2Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN);
    FourierWindowPlot fwp = new FourierWindowPlot();
    List<String> list = ResourceData.lines("/dubilab/app/pose/index.vector");
    // signal cases: 0:x , 1:y, 2;heading
    FourierWindowPlot.process(list, tensorUnaryOperator, 0);
    FourierWindowPlot.process(list, tensorUnaryOperator, 1);
    FourierWindowPlot.process(list, tensorUnaryOperator, 2);
  }
}