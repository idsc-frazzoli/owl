// code by ob, jph
package ch.ethz.idsc.sophus.app.ob;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import org.jfree.chart.JFreeChart;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import ch.ethz.idsc.sophus.app.api.GokartPoseData;
import ch.ethz.idsc.sophus.app.api.GokartPoseDataV1;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.lie.se2.Se2Differences;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualRow;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.SpectrogramArray;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class FourierWindowKernelPlot {
  private static final Scalar WINDOW_DURATION = Quantity.of(1, "s");
  // ---
  private final GokartPoseData gokartPoseData;
  private final int radius;
  private final TensorUnaryOperator spectrogramArray;

  public FourierWindowKernelPlot(GokartPoseData gokartPoseData, int radius) {
    this.gokartPoseData = gokartPoseData;
    this.radius = radius;
    // TODO JPH TENSOR V075
    spectrogramArray = SpectrogramArray.of(WINDOW_DURATION, gokartPoseData.getSampleRate(), 1);
  }

  private void process() throws IOException {
    Tensor smoothedX = Tensors.empty();
    Tensor smoothedY = Tensors.empty();
    Tensor smoothedA = Tensors.empty();
    for (String name : gokartPoseData.list()) {
      Tensor control = gokartPoseData.getPose(name, Integer.MAX_VALUE);
      Tensor tempX = Tensors.empty();
      Tensor tempY = Tensors.empty();
      Tensor tempA = Tensors.empty();
      for (ScalarUnaryOperator smoothingKernel : SmoothingKernel.values()) {
        TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(Se2Geodesic.INSTANCE, smoothingKernel);
        TensorUnaryOperator centerFilter = CenterFilter.of(tensorUnaryOperator, radius);
        Tensor smoothd = centerFilter.apply(control);
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
      yData.append(FrequencyResponse.PHASE.apply(meanData));
    // ---
    Tensor xAxis = Tensors.empty();
    for (int index = -yData.get(0).length() / 2; index < yData.get(0).length() / 2; ++index) {
      xAxis.append(RationalScalar.of(index, yData.get(0).length()).multiply(gokartPoseData.getSampleRate()));
    }
    VisualSet visualSet = new VisualSet();
    // visualSet.setPlotLabel("Geodesic Center Filter("+radius+") Magnitude Response $" + signal + "$");
    visualSet.setPlotLabel("Geodesic Center Filter(" + radius + ") Phase Response $" + signal + "$");
    visualSet.setAxesLabelX("Frequency $[Hz]$");
    visualSet.setAxesLabelY("Phase $H(\\Omega)$");
    // visualSet.setAxesLabelY("Magnitude $|H(\\Omega)|$");
    int index = 0;
    for (Tensor yAxis : yData) {
      VisualRow visualRow = visualSet.add( //
          xAxis, //
          Tensor.of(yAxis.append(yAxis).flatten(1)).extract(xAxis.length() / 2, xAxis.length() * 3 / 2));
      visualRow.setLabel(SmoothingKernel.values()[index].toString());
      ++index;
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    SVGGraphics2D svg = new SVGGraphics2D(600, 400);
    Rectangle rectangle = new Rectangle(0, 0, 600, 400);
    jFreeChart.draw(svg, rectangle);
    // String fileNameSVG = "Geodesic Center Filter("+radius+") Magnitude" + signal + ".svg";
    String fileNameSVG = "Geodesic Center Filter(" + radius + ") Phase " + signal + ".svg";
    File fileSVG = HomeDirectory.Pictures(fileNameSVG);
    SVGUtils.writeToSVG(fileSVG, svg.getSVGElement());
  }

  public static void main(String[] args) throws IOException {
    for (int radius = 2; radius < 10; radius++) {
      FourierWindowKernelPlot fourierWindowKernelPlot = //
          new FourierWindowKernelPlot(GokartPoseDataV1.INSTANCE, radius);
      fourierWindowKernelPlot.process();
      System.out.println(radius);
    }
  }
}