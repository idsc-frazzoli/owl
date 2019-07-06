// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GokartPoseData;
import ch.ethz.idsc.sophus.app.api.GokartPoseDataV2;
import ch.ethz.idsc.sophus.app.api.LieGroupFilters;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanCenter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenterMidSeeded;
import ch.ethz.idsc.sophus.lie.se2.Se2Differences;
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
import ch.ethz.idsc.tensor.mat.Fourier;
import ch.ethz.idsc.tensor.mat.SpectrogramArray;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Arg;
import ch.ethz.idsc.tensor.sca.Log;

/* package */ class FourierWindowPlot {
  private static final Scalar WINDOW_DURATION = Quantity.of(1, "s");
  private static final Scalar SAMPLING_FREQUENCY = Quantity.of(50, "s^-1");
  private static final TensorUnaryOperator SPECTROGRAM_ARRAY = SpectrogramArray.of(WINDOW_DURATION, SAMPLING_FREQUENCY, 1);
  private static final GokartPoseData GOKART_POSE_DATA = GokartPoseDataV2.INSTANCE;

  private static void phasePlot(Tensor data, int radius, String signal, SmoothingKernel smoothingKernel) throws IOException {
    Tensor yData = Tensors.empty();
    for (Tensor meanData : data)
      yData.append(TransferFunctionResponse.FREQUENCY.apply(meanData));
    // ---
    Tensor xAxis = Tensors.empty();
    for (int index = -yData.get(0).length() / 2; index < yData.get(0).length() / 2; ++index) {
      xAxis.append(RationalScalar.of(index, yData.get(0).length()).multiply(SAMPLING_FREQUENCY));
    }
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Lie Group Filters: radius = " + radius + "  Phase Response - $" + signal + "$");
    visualSet.setAxesLabelX("Frequency $[Hz]$");
    visualSet.setAxesLabelY("Magnitude $|H(\\Omega)|$");
    int index = 0;
    for (Tensor yAxis : yData) {
      VisualRow visualRow = visualSet.add( //
          xAxis, //
          Tensor.of(yAxis.append(yAxis).flatten(1)).extract(xAxis.length() / 2, xAxis.length() * 3 / 2));
      visualRow.setLabel(LieGroupFilters.values()[index].toString());
      ++index;
    }
    VisualRow visualRow = visualSet.add(//
        xAxis, //
        Arg.of(Tensor.of(linearResponse(smoothingKernel, radius).append(linearResponse(smoothingKernel, radius)).flatten(1)).extract(xAxis.length() / 2,
            xAxis.length() * 3 / 2)));
    visualRow.setLabel("Linear Filter Response");
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    SVGGraphics2D svg = new SVGGraphics2D(600, 400);
    Rectangle rectangle = new Rectangle(0, 0, 600, 400);
    jFreeChart.draw(svg, rectangle);
    String fileNameSVG = "PhaseResponse(" + radius + ")" + signal + ".svg";
    File fileSVG = HomeDirectory.Pictures(fileNameSVG);
    SVGUtils.writeToSVG(fileSVG, svg.getSVGElement());
  }

  private static void magniutdePlot(Tensor data, int radius, String signal, SmoothingKernel smoothingKernel) throws IOException {
    Tensor yData = Tensors.empty();
    for (Tensor meanData : data)
      yData.append(TransferFunctionResponse.MAGNITUDE.apply(meanData));
    // ---
    Tensor xAxis = Tensors.empty();
    for (int index = -yData.get(0).length() / 2; index < yData.get(0).length() / 2; ++index) {
      xAxis.append(RationalScalar.of(index, yData.get(0).length()).multiply(SAMPLING_FREQUENCY));
    }
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Lie Group Filters: radius = " + radius + "  Magnitude Response - $" + signal + "$");
    visualSet.setAxesLabelX("Frequency $[Hz]$");
    visualSet.setAxesLabelY("Magnitude $|H(\\Omega)|$");
    int index = 0;
    for (Tensor yAxis : yData) {
      VisualRow visualRow = visualSet.add( //
          xAxis, //
          decibelConversion(Tensor.of(yAxis.append(yAxis).flatten(1)).extract(xAxis.length() / 2, xAxis.length() * 3 / 2)));
      visualRow.setLabel(LieGroupFilters.values()[index].toString());
      ++index;
    }
    Tensor reference = decibelConversion(Tensor.of(Abs.of(linearResponse(smoothingKernel, radius).append(linearResponse(smoothingKernel, radius))).flatten(1))
        .extract(xAxis.length() / 2, xAxis.length() * 3 / 2));
    VisualRow visualRow = visualSet.add(//
        xAxis, //
        reference);
    visualRow.setLabel("Linear Filter Response");
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    SVGGraphics2D svg = new SVGGraphics2D(600, 400);
    Rectangle rectangle = new Rectangle(0, 0, 600, 400);
    jFreeChart.draw(svg, rectangle);
    String fileNameSVG = "MagnitudeResponse(" + radius + ")" + signal + ".svg";
    File fileSVG = HomeDirectory.Pictures(fileNameSVG);
    SVGUtils.writeToSVG(fileSVG, svg.getSVGElement());
  }

  private static Tensor decibelConversion(Tensor magnitude) {
    return Tensor.of(magnitude.stream().map(h -> Log.base(10).apply((Scalar) h))).multiply(RealScalar.of(20));
  }

  private static Tensor linearResponse(SmoothingKernel smoothingKernel, int radius) {
    Tensor ref = Tensors.empty();
    for (int j = 0; j < 2 * radius + 1; ++j) {
      ref.append(RealScalar.of(j - radius).divide(RealScalar.of(2 * radius + 1)));
    }
    ref = Tensor.of(ref.stream().map(x -> SmoothingKernel.GAUSSIAN.apply((Scalar) x)));
    for (int index = 0; index < 15; ++index)
      ref.append(RealScalar.ZERO);
    ref = ref.divide(Total.ofVector(ref)).multiply(RealScalar.of(Math.sqrt(ref.length())));
    return Fourier.of(ref);
  }

  private static void process(List<String> listData, Map<LieGroupFilters, TensorUnaryOperator> map, int radius, int limit, SmoothingKernel smoothingKernel)
      throws IOException {
    Tensor smoothedX = Tensors.empty();
    Tensor smoothedY = Tensors.empty();
    Tensor smoothedA = Tensors.empty();
    Iterator<String> iterator = listData.iterator();
    for (int index = 0; index < limit; ++index) {
      Tensor control = GOKART_POSE_DATA.getPose(iterator.next(), 1000);
      Tensor tempX = Tensors.empty();
      Tensor tempY = Tensors.empty();
      Tensor tempA = Tensors.empty();
      for (TensorUnaryOperator tensorUnaryOperator : map.values()) {
        TensorUnaryOperator unaryOperator = CenterFilter.of(tensorUnaryOperator, radius);
        Tensor smoothd = unaryOperator.apply(control);
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
    magniutdePlot(Mean.of(smoothedX), radius, "x", smoothingKernel);
    magniutdePlot(Mean.of(smoothedY), radius, "y", smoothingKernel);
    magniutdePlot(Mean.of(smoothedA), radius, "a", smoothingKernel);
    phasePlot(Mean.of(smoothedX), radius, "x", smoothingKernel);
    phasePlot(Mean.of(smoothedY), radius, "y", smoothingKernel);
    phasePlot(Mean.of(smoothedA), radius, "a", smoothingKernel);
  }

  public static void main(String[] args) throws IOException {
    GeodesicDisplay geodesicDisplay = Se2GeodesicDisplay.INSTANCE;
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    Map<LieGroupFilters, TensorUnaryOperator> map = new EnumMap<>(LieGroupFilters.class);
    map.put(LieGroupFilters.GEODESIC, GeodesicCenter.of(geodesicDisplay.geodesicInterface(), smoothingKernel));
    map.put(LieGroupFilters.GEODESIC_MID, GeodesicCenterMidSeeded.of(geodesicDisplay.geodesicInterface(), smoothingKernel));
    map.put(LieGroupFilters.BIINVARIANT_MEAN, BiinvariantMeanCenter.of(geodesicDisplay.biinvariantMean(), smoothingKernel));
    List<String> listData = GOKART_POSE_DATA.list();
    int limit = 10;
    int rad = 24;
    process(listData, map, rad, limit, smoothingKernel);
  }
}