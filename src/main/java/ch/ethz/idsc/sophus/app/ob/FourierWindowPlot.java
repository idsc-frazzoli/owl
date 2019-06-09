// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.filter.BiinvariantMeanCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterMidSeeded;
import ch.ethz.idsc.sophus.filter.LieGroupFilters;
import ch.ethz.idsc.sophus.filter.TangentSpaceCenter;
import ch.ethz.idsc.sophus.group.Se2Differences;
import ch.ethz.idsc.sophus.math.FilterResponse;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.SpectrogramArray;
import ch.ethz.idsc.sophus.math.TransferFunctionResponse;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualRow;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;

/* package */ class FourierWindowPlot {
  private static final Scalar WINDOW_DURATION = Quantity.of(1, "s");
  private static final Scalar SAMPLING_FREQUENCY = Quantity.of(20, "s^-1");
  private static final TensorUnaryOperator SPECTROGRAM_ARRAY = SpectrogramArray.of(WINDOW_DURATION, SAMPLING_FREQUENCY, 1);

  // TODO OB: make logPlot (standard)
  private static void plot(Tensor data) throws IOException {
    Tensor yData = Tensors.empty();
    for (Tensor meanData : data)
      yData.append(TransferFunctionResponse.MAGNITUDE.apply(meanData));
    // ---
    Tensor xAxis = Tensors.empty();
    // FIXME OB shouldn't the freq. labels also depend on WINDOW_DURATION?
    for (int index = -yData.get(0).length() / 2; index < yData.get(0).length() / 2; ++index)
      xAxis.append(RationalScalar.of(index, yData.get(0).length()).multiply(SAMPLING_FREQUENCY));
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Filter Gain");
    visualSet.setAxesLabelX("Frequency [Hz]");
    visualSet.setAxesLabelY("Magnitude");
    int index = 0;
    for (Tensor yAxis : yData) {
      VisualRow visualRow = visualSet.add( //
          xAxis, //
          Tensor.of(yAxis.append(yAxis).flatten(1)).extract(xAxis.length() / 2, xAxis.length() * 3 / 2));
      visualRow.setLabel(LieGroupFilters.values()[index].toString());
      ++index;
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    jFreeChart.setBackgroundPaint(Color.WHITE);
    // Exportable as SVG?
    File file = HomeDirectory.Pictures("FilterGain.png");
    // impove DPI?
    ChartUtils.saveChartAsPNG(file, jFreeChart, 1024, 768);
  }

  private static void process(List<String> listData, Map<LieGroupFilters, TensorUnaryOperator> map, int radius) throws IOException {
    Tensor smoothed = Tensors.empty();
    Iterator<String> iterator = listData.iterator();
    int limit = 2;
    for (int index = 0; index < limit; ++index) {
      Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + iterator.next() + ".csv").stream().map(row -> row.extract(1, 4)));
      Tensor temp = Tensors.empty();
      for (TensorUnaryOperator tensorUnaryOperator : map.values()) {
        TensorUnaryOperator unaryOperator = GeodesicCenterFilter.of(tensorUnaryOperator, radius);
        Tensor smoothd = unaryOperator.apply(control);
        Tensor rawVec = Se2Differences.INSTANCE.apply(control);
        Tensor smdVec = Se2Differences.INSTANCE.apply(smoothd);
        // signal cases: 0:x , 1:y, 2;heading
        int signal = 2;
        temp.append(FilterResponse.of(smdVec.get(Tensor.ALL, signal), rawVec.get(Tensor.ALL, signal), SPECTROGRAM_ARRAY));
      }
      smoothed.append(temp);
    }
    plot(Mean.of(smoothed));
  }

  public static void main(String[] args) throws IOException {
    GeodesicDisplay geodesicDisplay = Se2GeodesicDisplay.INSTANCE;
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    Map<LieGroupFilters, TensorUnaryOperator> map = new EnumMap<>(LieGroupFilters.class);
    map.put(LieGroupFilters.GEODESIC, GeodesicCenter.of(geodesicDisplay.geodesicInterface(), smoothingKernel));
    map.put(LieGroupFilters.GEODESIC_MID, GeodesicCenterMidSeeded.of(geodesicDisplay.geodesicInterface(), smoothingKernel));
    map.put(LieGroupFilters.TANGENT_SPACE, TangentSpaceCenter.of(geodesicDisplay.lieGroup(), geodesicDisplay.lieExponential(), smoothingKernel));
    map.put(LieGroupFilters.BIINVARIANT_MEAN, BiinvariantMeanCenter.of(geodesicDisplay.biinvariantMean(), smoothingKernel));
    List<String> listData = ResourceData.lines("/dubilab/app/pose/index.vector");
    int radius = 5;
    process(listData, map, radius);
  }
}