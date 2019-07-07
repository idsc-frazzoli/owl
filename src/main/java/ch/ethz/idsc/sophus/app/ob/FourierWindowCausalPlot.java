// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.sophus.app.api.GokartPoseDataV1;
import ch.ethz.idsc.sophus.app.api.LieGroupCausalFilters;
import ch.ethz.idsc.sophus.flt.WindowSideExtrapolation;
import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanFIRnFilter;
import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanIIRnFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.flt.ga.GeodesicFIRnFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicIIRnFilter;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2.Se2Differences;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualRow;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.mat.SpectrogramArray;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class FourierWindowCausalPlot {
  private static final Scalar WINDOW_DURATION = Quantity.of(1, "s");
  private static final Scalar SAMPLING_FREQUENCY = Quantity.of(20, "s^-1");
  private static final TensorUnaryOperator SPECTROGRAM_ARRAY = SpectrogramArray.of(WINDOW_DURATION, SAMPLING_FREQUENCY, 1);

  private static void plot(Tensor data, int radius, String signal, Scalar alpha) throws IOException {
    Tensor yData = Tensors.empty();
    for (Tensor meanData : data)
      yData.append(TransferFunctionResponse.MAGNITUDE.apply(meanData));
    // ---
    Tensor xAxis = Tensors.empty();
    for (int index = -yData.get(0).length() / 2; index < yData.get(0).length() / 2; ++index) {
      xAxis.append(RationalScalar.of(index, yData.get(0).length()).multiply(SAMPLING_FREQUENCY));
    }
    VisualSet visualSet = new VisualSet();
    // visualSet.setPlotLabel("Causal IIR Filters: length = "+radius+" Magnitude Response - $" + signal + "$");
    // visualSet.setPlotLabel("Causal IIR Filters: length = "+radius+" Phase Response - $" + signal + "$");
    visualSet.setPlotLabel("Causal FIR Filters: length = " + radius + "  Magnitude Response - $" + signal + "$");
    // visualSet.setPlotLabel("Causal FIR Filters: length = "+radius+" Phase Response - $" + signal + "$");
    visualSet.setAxesLabelX("Frequency $[Hz]$");
    visualSet.setAxesLabelY("Phase $H(\\Omega)$");
    // visualSet.setAxesLabelY("Magnitude $|H(\\Omega)|$");
    int index = 0;
    for (Tensor yAxis : yData) {
      VisualRow visualRow = visualSet.add( //
          xAxis, //
          Tensor.of(yAxis.append(yAxis).flatten(1)).extract(xAxis.length() / 2, xAxis.length() * 3 / 2));
      visualRow.setLabel(LieGroupCausalFilters.values()[index].toString());
      ++index;
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    jFreeChart.setBackgroundPaint(Color.WHITE);
    // Exportable as SVG?
    String fileName = "Causal_Magnitude" + radius + "_" + signal + ".png";
    // String fileName = "Causal_Phase_" + radius + "_" + signal + ".png";
    File file = HomeDirectory.Pictures(fileName);
    // impove DPI?
    ChartUtils.saveChartAsPNG(file, jFreeChart, 1024, 768);
  }

  private static void process(List<String> listData, ScalarUnaryOperator smoothingKernel, int radius, int limit, Scalar alpha) throws IOException {
    Se2BiinvariantMean se2BiinvariantMean = Se2BiinvariantMean.FILTER;
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    TensorUnaryOperator geodesicExtrapolation = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel);
    // ---
    Tensor smoothedX = Tensors.empty();
    Tensor smoothedY = Tensors.empty();
    Tensor smoothedA = Tensors.empty();
    Iterator<String> iterator = listData.iterator();
    for (int index = 0; index < limit; ++index) {
      Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + iterator.next() + ".csv").stream().map(row -> row.extract(1, 4)));
      Tensor tempX = Tensors.empty();
      Tensor tempY = Tensors.empty();
      Tensor tempA = Tensors.empty();
      for (LieGroupCausalFilters lgcf : LieGroupCausalFilters.values()) {
        Tensor smoothd = Tensors.empty();
        switch (lgcf) {
        case GEODESIC_FIR:
          smoothd = GeodesicFIRnFilter.of(geodesicExtrapolation, geodesicInterface, radius, alpha).apply(control);
          break;
        case GEODESIC_IIR:
          smoothd = GeodesicIIRnFilter.of(geodesicExtrapolation, geodesicInterface, radius, alpha).apply(control);
          break;
        case BIINVARIANT_MEAN_FIR:
          smoothd = BiinvariantMeanFIRnFilter.of(se2BiinvariantMean, WindowSideExtrapolation.of(smoothingKernel), Se2Geodesic.INSTANCE, radius, alpha)
              .apply(control);
          break;
        case BIINVARIANT_MEAN_IIR:
          smoothd = BiinvariantMeanIIRnFilter.of(se2BiinvariantMean, WindowSideExtrapolation.of(smoothingKernel), Se2Geodesic.INSTANCE, radius, alpha)
              .apply(control);
          break;
        }
        Tensor rawVec = Se2Differences.INSTANCE.apply(control);
        Tensor smdVec = Se2Differences.INSTANCE.apply(smoothd);
        tempX.append(FilterResponse.of(smdVec.get(Tensor.ALL, 0), rawVec.get(Tensor.ALL, 0), SPECTROGRAM_ARRAY));
        tempY.append(FilterResponse.of(smdVec.get(Tensor.ALL, 1), rawVec.get(Tensor.ALL, 1), SPECTROGRAM_ARRAY));
        tempA.append(FilterResponse.of(smdVec.get(Tensor.ALL, 2), rawVec.get(Tensor.ALL, 2), SPECTROGRAM_ARRAY));
      }
      smoothedX.append(tempX);
      smoothedY.append(tempY);
      smoothedA.append(tempA);
    }
    plot(Mean.of(smoothedX), radius, "x", alpha);
    plot(Mean.of(smoothedY), radius, "y", alpha);
    plot(Mean.of(smoothedA), radius, "a", alpha);
  }

  public static void main(String[] args) throws IOException {
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    List<String> listData = GokartPoseDataV1.INSTANCE.list();
    int radius = 7;
    int limit = 5;
    Scalar alpha = RealScalar.of(0.8);
    process(listData, smoothingKernel, radius, limit, alpha);
  }
}