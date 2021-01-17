// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import ch.ethz.idsc.sophus.app.io.GokartPoseData;
import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanCenter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenterMidSeeded;
import ch.ethz.idsc.sophus.gds.GeodesicDisplay;
import ch.ethz.idsc.sophus.gds.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.lie.se2.Se2Differences;
import ch.ethz.idsc.sophus.math.Decibel;
import ch.ethz.idsc.sophus.opt.GeodesicFilters;
import ch.ethz.idsc.sophus.opt.SmoothingKernel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.fft.Fourier;
import ch.ethz.idsc.tensor.fft.SpectrogramArray;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualRow;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Arg;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum FourierWindowPlot {
  ;
  private static void phasePlot(Tensor data, int radius, String signal, SmoothingKernel smoothingKernel) throws IOException {
    Tensor xAxis = Tensors.empty();
    for (int index = -data.get(0).length() / 2; index < data.get(0).length() / 2; ++index) {
      // System.out.println((Scalar) GokartPoseDataV2.INSTANCE.getSampleRate().multiply(Quantity.of(1, "s")));
      xAxis.append(RationalScalar.of(index, data.get(0).length()).multiply(GokartPoseDataV2.INSTANCE.getSampleRate()));
    }
    Tensor yData = Tensors.empty();
    for (Tensor meanData : data)
      yData.append(FrequencyResponse.PHASE.apply(meanData));
    // ---
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Lie Group Filters: radius = " + radius + "  Phase Response - $" + signal + "$");
    visualSet.setAxesLabelX("Frequency $[Hz]$");
    visualSet.setAxesLabelY("Magnitude $|H(\\Omega)|$");
    int index = 0;
    for (Tensor yAxis : yData) {
      VisualRow visualRow = visualSet.add( //
          xAxis, //
          Join.of(yAxis, yAxis).extract(xAxis.length() / 2, xAxis.length() * 3 / 2));
      visualRow.setLabel(GeodesicFilters.values()[index].toString());
      ++index;
    }
    VisualRow visualRow = visualSet.add(//
        xAxis, //
        Arg.of(Join.of(linearResponse(smoothingKernel, radius), linearResponse(smoothingKernel, radius)).extract(xAxis.length() / 2, xAxis.length() * 3 / 2)));
    visualRow.setLabel("Linear Filter Response");
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    SVGGraphics2D svg = new SVGGraphics2D(600, 400);
    Rectangle rectangle = new Rectangle(0, 0, 600, 400);
    jFreeChart.draw(svg, rectangle);
    String fileNameSVG = "PhaseResponse(" + radius + ")" + smoothingKernel.toString() + " " + signal + ".svg";
    File fileSVG = HomeDirectory.Pictures(fileNameSVG);
    SVGUtils.writeToSVG(fileSVG, svg.getSVGElement());
  }

  private static void magniutdePlot(Tensor data, int radius, String signal, SmoothingKernel smoothingKernel) throws IOException {
    Tensor yData = Tensors.empty();
    for (Tensor meanData : data) {
      yData.append(FrequencyResponse.MAGNITUDE.apply(meanData));
    }
    // ---
    Tensor xAxis = Tensors.empty();
    for (int index = -data.get(0).length() / 2; index < data.get(0).length() / 2; ++index) {
      xAxis.append(RationalScalar.of(index, data.get(0).length()).multiply(GokartPoseDataV2.INSTANCE.getSampleRate().multiply(Quantity.of(1, "s"))));
    }
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Lie Group Filters: radius = " + radius + "  Magnitude Response - $" + signal + "$");
    visualSet.setAxesLabelX("Frequency $[Hz]$");
    visualSet.setAxesLabelY("Magnitude [dB]");
    int index = 0;
    Tensor factor = Tensors.empty();
    for (int j = 0; j < xAxis.length(); j++) {
      if (xAxis.Get(j).equals(RealScalar.ZERO))
        factor.append(RealScalar.ONE);
      else
        factor.append(RealScalar.ONE.divide(Pi.TWO.multiply(Abs.of(xAxis.Get(j)))));
    }
    for (Tensor yAxis : yData) {
      Tensor temp = Join.of(yAxis, yAxis).extract(xAxis.length() / 2, xAxis.length() * 3 / 2).pmul(factor);
      VisualRow visualRow = visualSet.add( //
          xAxis, //
          Decibel.of(temp));
      visualRow.setLabel(GeodesicFilters.values()[index].toString());
      ++index;
    }
    Tensor reference = Decibel.of(Abs.of(Join.of( //
        linearResponse(smoothingKernel, radius), //
        linearResponse(smoothingKernel, radius)) // not efficient
        .extract(xAxis.length() / 2, xAxis.length() * 3 / 2)));
    VisualRow visualRow = visualSet.add(//
        xAxis, //
        reference);
    visualRow.setLabel("Linear Filter Response");
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    SVGGraphics2D svg = new SVGGraphics2D(600, 400);
    Rectangle rectangle = new Rectangle(0, 0, 600, 400);
    jFreeChart.draw(svg, rectangle);
    String fileNameSVG = "MagnitudeResponse(" + radius + ")" + smoothingKernel.toString() + " " + signal + ".svg";
    File fileSVG = HomeDirectory.Pictures(fileNameSVG);
    SVGUtils.writeToSVG(fileSVG, svg.getSVGElement());
  }

  private static Tensor linearResponse(SmoothingKernel smoothingKernel, int radius) {
    Tensor ref = Tensors.empty();
    for (int j = 0; j < 2 * radius + 1; ++j) {
      ref.append(RealScalar.of(j - radius).divide(RealScalar.of(2 * radius + 1)));
    }
    ref = Tensor.of(ref.stream().map(Scalar.class::cast).map(smoothingKernel));
    Scalar powerOf2Length = Power.of(2, Ceiling.of(Log.base(2).apply(RealScalar.of(ref.length()))));
    ref = PadRight.zeros(powerOf2Length.number().intValue()).apply(ref);
    ref = ref.divide(Total.ofVector(ref)).multiply(RealScalar.of(Math.sqrt(ref.length())));
    return Fourier.of(ref);
  }

  private static void process( //
      GokartPoseData gokartPoseData, Map<GeodesicFilters, TensorUnaryOperator> map, //
      int radius, int limit, SmoothingKernel smoothingKernel) throws IOException {
    int windowLength = Scalars.intValueExact(Round.FUNCTION.apply(Quantity.of(1, "s").multiply(gokartPoseData.getSampleRate())));
    int offset = Scalars.intValueExact(Round.FUNCTION.apply(RationalScalar.of(windowLength, 3)));
    TensorUnaryOperator spectrogramArray = SpectrogramArray.of(windowLength, offset);
    Tensor smoothedX = Tensors.empty();
    Tensor smoothedY = Tensors.empty();
    Tensor smoothedA = Tensors.empty();
    Iterator<String> iterator = gokartPoseData.list().iterator();
    for (int index = 0; index < limit; ++index) {
      Tensor control = gokartPoseData.getPose(iterator.next(), 10000);
      Tensor tempX = Tensors.empty();
      Tensor tempY = Tensors.empty();
      Tensor tempA = Tensors.empty();
      for (TensorUnaryOperator tensorUnaryOperator : map.values()) {
        TensorUnaryOperator unaryOperator = CenterFilter.of(tensorUnaryOperator, radius);
        Tensor smoothd = unaryOperator.apply(control);
        Tensor rawVec = Se2Differences.INSTANCE.apply(control);
        Tensor smdVec = Se2Differences.INSTANCE.apply(smoothd);
        tempX.append(FilterResponse.of(smdVec.get(Tensor.ALL, 0), rawVec.get(Tensor.ALL, 0), spectrogramArray));
        tempY.append(FilterResponse.of(smdVec.get(Tensor.ALL, 1), rawVec.get(Tensor.ALL, 1), spectrogramArray));
        tempA.append(FilterResponse.of(smdVec.get(Tensor.ALL, 2), rawVec.get(Tensor.ALL, 2), spectrogramArray));
      }
      smoothedX.append(tempX);
      smoothedY.append(tempY);
      smoothedA.append(tempA);
    }
    magniutdePlot(Mean.of(smoothedX), radius, "_x", smoothingKernel);
    magniutdePlot(Mean.of(smoothedY), radius, "_y", smoothingKernel);
    magniutdePlot(Mean.of(smoothedA), radius, "_a", smoothingKernel);
    phasePlot(Mean.of(smoothedX), radius, "_x", smoothingKernel);
    phasePlot(Mean.of(smoothedY), radius, "_y", smoothingKernel);
    phasePlot(Mean.of(smoothedA), radius, "_a", smoothingKernel);
  }

  public static void main(String[] args) throws IOException {
    GeodesicDisplay geodesicDisplay = Se2GeodesicDisplay.INSTANCE;
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    Map<GeodesicFilters, TensorUnaryOperator> map = new EnumMap<>(GeodesicFilters.class);
    map.put(GeodesicFilters.GEODESIC, GeodesicCenter.of(geodesicDisplay.geodesicInterface(), smoothingKernel));
    map.put(GeodesicFilters.GEODESIC_MID, GeodesicCenterMidSeeded.of(geodesicDisplay.geodesicInterface(), smoothingKernel));
    map.put(GeodesicFilters.BIINVARIANT_MEAN, BiinvariantMeanCenter.of(geodesicDisplay.biinvariantMean(), smoothingKernel));
    int limit = 5;
    int rad = 24;
    process(GokartPoseDataV2.INSTANCE, map, rad, limit, smoothingKernel);
  }
}