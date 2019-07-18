// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jfree.chart.JFreeChart;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import ch.ethz.idsc.sophus.app.api.GokartPoseData;
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
import ch.ethz.idsc.sophus.math.Decibel;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualRow;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.SpectrogramArray;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class FourierWindowCausalPlot {
  private final GokartPoseData gokartPoseData;
  private final ScalarUnaryOperator smoothingKernel;
  private final int radius;
  private final Scalar alpha;

  public FourierWindowCausalPlot(GokartPoseData gokartPoseData, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    this.gokartPoseData = gokartPoseData;
    this.smoothingKernel = smoothingKernel;
    this.radius = radius;
    this.alpha = alpha;
  }

  private void process(int limit) throws IOException {
    Se2BiinvariantMean se2BiinvariantMean = Se2BiinvariantMean.FILTER;
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    TensorUnaryOperator geodesicExtrapolation = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel);
    // ---
    // TODO JPH TENSOR 075
    int windowLength = Scalars.intValueExact(Round.FUNCTION.apply(Quantity.of(1, "s").multiply(gokartPoseData.getSampleRate())));
    int offset = Scalars.intValueExact(Round.FUNCTION.apply(RationalScalar.of(windowLength, 3)));
    TensorUnaryOperator spectrogramArray = SpectrogramArray.of(windowLength, offset);
    // ---
    Tensor smoothedX = Tensors.empty();
    Tensor smoothedY = Tensors.empty();
    Tensor smoothedA = Tensors.empty();
    Iterator<String> iterator = gokartPoseData.list().iterator();
    for (int index = 0; index < limit; ++index) {
      Tensor control = gokartPoseData.getPose(iterator.next(), Integer.MAX_VALUE);
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
        tempX.append(FilterResponse.of(smdVec.get(Tensor.ALL, 0), rawVec.get(Tensor.ALL, 0), spectrogramArray));
        tempY.append(FilterResponse.of(smdVec.get(Tensor.ALL, 1), rawVec.get(Tensor.ALL, 1), spectrogramArray));
        tempA.append(FilterResponse.of(smdVec.get(Tensor.ALL, 2), rawVec.get(Tensor.ALL, 2), spectrogramArray));
      }
      smoothedX.append(tempX);
      smoothedY.append(tempY);
      smoothedA.append(tempA);
    }
    plot(Mean.of(smoothedX), "x");
    plot(Mean.of(smoothedY), "y");
    plot(Mean.of(smoothedA), "a");
  }

  private void plot(Tensor data, String signal) throws IOException {
    Tensor yData = Tensors.empty();
    for (Tensor meanData : data)
      yData.append(FrequencyResponse.MAGNITUDE.apply(meanData));
    Tensor xAxis = Tensors.empty();
    for (int index = -data.get(0).length() / 2; index < data.get(0).length() / 2; ++index)
      xAxis.append(RationalScalar.of(index, data.get(0).length()).multiply(gokartPoseData.getSampleRate().multiply(Quantity.of(1, "s"))));
    // ---
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel(
        "Lie Group Filters: radius = " + radius + "  Magnitude Response - $" + smoothingKernel.toString() + "- alpha: " + alpha + " - " + signal + "$");
    visualSet.setAxesLabelX("Frequency $[Hz]$");
    visualSet.setAxesLabelY("Magnitude [dB]");
    // ---
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
      visualRow.setLabel(LieGroupCausalFilters.values()[index].toString());
      ++index;
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    SVGGraphics2D svg = new SVGGraphics2D(600, 400);
    Rectangle rectangle = new Rectangle(0, 0, 600, 400);
    jFreeChart.draw(svg, rectangle);
    String fileNameSVG = "MagnitudeResponse(" + radius + ")" + smoothingKernel.toString() + " " + signal + ".svg";
    File fileSVG = HomeDirectory.Pictures(fileNameSVG);
    SVGUtils.writeToSVG(fileSVG, svg.getSVGElement());
  }

  public static void main(String[] args) throws IOException {
    FourierWindowCausalPlot fourierWindowCausalPlot = //
        new FourierWindowCausalPlot(GokartPoseDataV1.INSTANCE, SmoothingKernel.GAUSSIAN, 7, RealScalar.of(0.8));
    int limit = 1;
    fourierWindowCausalPlot.process(limit);
  }
}