// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualRow;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RealScalar;
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
  // signal cases: 0:x , 1:y, 2;heading
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
    Tensor yAxisAsymm = Tensor.of(yData.stream().map(x -> Abs.FUNCTION.apply((Scalar) x)));
    Tensor yAxis = Tensor.of(yAxisAsymm.append(yAxisAsymm).flatten(1)).extract(yData.length() / 2, yData.length() * 3 / 2);
    Tensor xAxis = Tensors.empty();
    for (int index = -yData.length() / 2; index < yData.length() / 2; ++index)
      xAxis.append(RealScalar.of((double) index * SAMPLING_FREQUENCY / yData.length()));
    // ---
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Frequence response");
    visualSet.setAxesLabelX("Frequency [Hz]");
    visualSet.setAxesLabelY("Magniture");
    {
      VisualRow visualRow = visualSet.add(xAxis, yAxis);
      visualRow.setLabel("Filter Gain for x-component");
    }
    // TODO OB: Does logarithmic plot exist?
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    File file = HomeDirectory.Pictures("test.png");
    ChartUtils.saveChartAsPNG(file, jFreeChart, 1024, 768);
  }

  private static void process(List<String> list, TensorUnaryOperator tensorUnaryOperator, int signal) throws IOException {
    Tensor results = Tensors.empty();
    Iterator<String> iterator = list.iterator();
    int limit = 15;
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
    FourierWindowPlot.process(list, tensorUnaryOperator, 2);
  }
}