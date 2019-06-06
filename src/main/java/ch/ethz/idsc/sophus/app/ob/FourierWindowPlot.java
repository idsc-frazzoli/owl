// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.filter.BiinvariantMeanCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterMidSeeded;
import ch.ethz.idsc.sophus.filter.GeodesicCenterTangentSpace;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualRow;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Abs;

/* package */ class FourierWindowPlot {
  private static final int WINDOW_DURATION = 2;
  private static final int SAMPLING_FREQUENCY = 20;

  // TODO OB/JH, ist das so loesbar oder eher unschoen?
  enum Filter {
    GEODESIC_CENTER, GEODESIC_CENTER_MIDSEEDED, TANGENT_SPACE_CENTER, BIINVARIANT_CENTER;
  }

  // TODO OB: make logPlot (standard)
  private static void plot(Tensor data) throws IOException {
    Tensor yData = Tensors.empty();
    for (Tensor meanData : data) {
      Tensor temp = Tensor.of(meanData.stream().map(x -> Abs.FUNCTION.apply((Scalar) x)));
      yData.append(temp);
    }
    // ---
    Tensor xAxis = Tensors.empty();
    for (int index = -yData.get(0).length() / 2; index < yData.get(0).length() / 2; ++index)
      xAxis.append(RealScalar.of((double) index * SAMPLING_FREQUENCY / yData.get(0).length()));
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Filter Gain");
    visualSet.setAxesLabelX("Frequency [Hz]");
    visualSet.setAxesLabelY("Magnitude");
    int index = 0;
    for (Tensor yAxis : yData) {
      {
        VisualRow visualRow = visualSet.add(//
            xAxis, //
            Tensor.of(yAxis.append(yAxis).flatten(1)).extract(xAxis.length() / 2, xAxis.length() * 3 / 2));
        visualRow.setLabel(Filter.values()[index].toString());
        index++;
      }
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    // Exportable as SVG?
    File file = HomeDirectory.Pictures("FilterGain.png");
    // impove DPI?
    ChartUtils.saveChartAsPNG(file, jFreeChart, 1024, 768);
  }

  private static void process(List<String> listData, List<TensorUnaryOperator> listOperator, int radius, int signal) throws IOException {
    Tensor smoothed = Tensors.empty();
    Iterator<String> iterator = listData.iterator();
    int limit = 2;
    for (int index = 0; index < limit; ++index) {
      Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + iterator.next() + ".csv").stream().map(row -> row.extract(1, 4)));
      Tensor temp = Tensors.empty();
      for (TensorUnaryOperator tuo : listOperator)
        temp.append(SpectrogramAnaglogous.transferFunction(control, radius, tuo, signal, WINDOW_DURATION, SAMPLING_FREQUENCY));
      smoothed.append(temp);
    }
    plot(Mean.of(smoothed));
  }

  public static void main(String[] args) throws IOException {
    GeodesicDisplay geodesicDisplay = Se2GeodesicDisplay.INSTANCE;
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    List<TensorUnaryOperator> listOperator = new ArrayList<TensorUnaryOperator>();
    listOperator.add(GeodesicCenter.of(geodesicDisplay.geodesicInterface(), smoothingKernel));
    listOperator.add(GeodesicCenterMidSeeded.of(geodesicDisplay.geodesicInterface(), smoothingKernel));
    listOperator.add(GeodesicCenterTangentSpace.of(geodesicDisplay.lieGroup(), geodesicDisplay.lieExponential(), smoothingKernel));
    listOperator.add(BiinvariantMeanCenter.of(geodesicDisplay.biinvariantMeanInterface(), smoothingKernel));
    List<String> listData = ResourceData.lines("/dubilab/app/pose/index.vector");
    int radius = 7;
    // signal cases: 0:x , 1:y, 2;heading
    FourierWindowPlot.process(listData, listOperator, radius, 2);
  }
}