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
import ch.ethz.idsc.sophus.app.api.LieGroupFilters;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.filter.CenterFilter;
import ch.ethz.idsc.sophus.filter.bm.BiinvariantMeanCenter;
import ch.ethz.idsc.sophus.filter.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.ga.GeodesicCenterMidSeeded;
import ch.ethz.idsc.sophus.filter.ts.TangentSpaceCenter;
import ch.ethz.idsc.sophus.lie.se2.Se2Differences;
import ch.ethz.idsc.sophus.math.FilterResponse;
import ch.ethz.idsc.sophus.math.TransferFunctionResponse;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualRow;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.mat.SpectrogramArray;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;

/* package */ class FourierWindowPlot {
  private static final Scalar WINDOW_DURATION = Quantity.of(1, "s");
  private static final Scalar SAMPLING_FREQUENCY = Quantity.of(20, "s^-1");
  private static final TensorUnaryOperator SPECTROGRAM_ARRAY = SpectrogramArray.of(WINDOW_DURATION, SAMPLING_FREQUENCY, 1);

  // TODO OB: make logPlot (standard)
  private static void plot(Tensor data, int radius, String signal) throws IOException {
    Tensor yData = Tensors.empty();
    for (Tensor meanData : data)
      yData.append(TransferFunctionResponse.FREQUENCY.apply(meanData));
    // ---
    Tensor xAxis = Tensors.empty();
    for (int index = -yData.get(0).length() / 2; index < yData.get(0).length() / 2; ++index) {
      xAxis.append(RationalScalar.of(index, yData.get(0).length()).multiply(SAMPLING_FREQUENCY));
    }
    VisualSet visualSet = new VisualSet();
    // visualSet.setPlotLabel("Lie Group Filters: radius = "+radius+" Magnitude Response - $" + signal + "$");
    visualSet.setPlotLabel("Lie Group Filters: radius = " + radius + "  Phase Response - $" + signal + "$");
    visualSet.setAxesLabelX("Frequency $[Hz]$");
    visualSet.setAxesLabelY("Phase $H(\\Omega)$");
    // visualSet.setAxesLabelY("Magnitude $|H(\\Omega)|$");
    int index = 0;
    for (Tensor yAxis : yData) {
      VisualRow visualRow = visualSet.add( //
          xAxis, //
          Tensor.of(yAxis.append(yAxis).flatten(1)).extract(xAxis.length() / 2, xAxis.length() * 3 / 2));
      visualRow.setLabel(LieGroupFilters.values()[index].toString());
      ++index;
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    SVGGraphics2D svg = new SVGGraphics2D(600, 400);
    Rectangle rectangle = new Rectangle(0, 0, 600, 400);
    jFreeChart.draw(svg, rectangle);
    // String fileNameSVG = "Geodesic Center Filter("+radius+") Gain " + signal + ".svg";
    String fileNameSVG = "Geodesic Center Filter(" + radius + ") Phase " + signal + ".svg";
    File fileSVG = HomeDirectory.Pictures(fileNameSVG);
    SVGUtils.writeToSVG(fileSVG, svg.getSVGElement());
  }

  private static void process(List<String> listData, Map<LieGroupFilters, TensorUnaryOperator> map, int radius, int limit) throws IOException {
    Tensor smoothedX = Tensors.empty();
    Tensor smoothedY = Tensors.empty();
    Tensor smoothedA = Tensors.empty();
    Iterator<String> iterator = listData.iterator();
    for (int index = 0; index < limit; ++index) {
      Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + iterator.next() + ".csv").stream().map(row -> row.extract(1, 4)));
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
    plot(Mean.of(smoothedX), radius, "x");
    plot(Mean.of(smoothedY), radius, "y");
    plot(Mean.of(smoothedA), radius, "a");
  }

  public static void main(String[] args) throws IOException {
    GeodesicDisplay geodesicDisplay = Se2GeodesicDisplay.INSTANCE;
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    // smoothing
    Map<LieGroupFilters, TensorUnaryOperator> map = new EnumMap<>(LieGroupFilters.class);
    map.put(LieGroupFilters.GEODESIC, GeodesicCenter.of(geodesicDisplay.geodesicInterface(), smoothingKernel));
    map.put(LieGroupFilters.GEODESIC_MID, GeodesicCenterMidSeeded.of(geodesicDisplay.geodesicInterface(), smoothingKernel));
    map.put(LieGroupFilters.TANGENT_SPACE, TangentSpaceCenter.of(geodesicDisplay.lieGroup(), geodesicDisplay.lieExponential(), smoothingKernel));
    map.put(LieGroupFilters.BIINVARIANT_MEAN, BiinvariantMeanCenter.of(geodesicDisplay.biinvariantMean(), smoothingKernel));
    // causal filters
    // Map<LieGroupCausalFilters, TensorUnaryOperator> map = new EnumMap<>(LieGroupCausalFilters.class);
    // map.put(LieGroupCausalFilters.GEODESIC_FIR, GeodesicFIRnNEW.of(geodesicDisplay, smoothingKernel, radius, alpha()));
    // map.put(LieGroupCausalFilters.GEODESIC_IIR, GeodesicIIRnNEW.of(geodesicDisplay, smoothingKernel, radius, alpha()));
    // map.put(LieGroupCausalFilters.TANGENT_SPACE_FIR, TangentSpaceFIRnNEW.of(geodesicDisplay, smoothingKernel, radius, alpha()));
    // map.put(LieGroupCausalFilters.TANGENT_SPACE_IIR, TangentSpaceIIRnNEW.of(geodesicDisplay, smoothingKernel, radius, alpha()));
    // map.put(LieGroupCausalFilters.BIINVARIANT_MEAN_FIR, BiinvariantMeanFIRnNEW.of(geodesicDisplay, smoothingKernel, radius, alpha()));
    // map.put(LieGroupCausalFilters.BIINVARIANT_MEAN_IIR, BiinvariantMeanIIRnNEW.of(geodesicDisplay, smoothingKernel, radius, alpha()));
    // signal cases: 0:x , 1:y, 2;heading
    List<String> listData = GokartPoseData.INSTANCE.list();
    int limit = 10;
    for (int rad = 0; rad < 14; rad++) {
      System.out.println(rad);
      process(listData, map, rad, limit);
    }
  }
}