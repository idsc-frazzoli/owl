// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.flt.ga.GeodesicIIRnFilter;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualRow;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class GeodesicCurveEvaluation {
  public static final GeodesicErrorEvaluation GEODESIC_ERROR_EVALUATION = //
      new GeodesicErrorEvaluation(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
  // ---
  private Tensor minimizingAlphas;
  private Tensor minimizingWindowSize;
  private Tensor minimizingKernels;
  private String signalname;
  private GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;

  GeodesicCurveEvaluation(Tensor minimizer, String signalname) {
    this.minimizingAlphas = minimizer.get(0);
    this.minimizingWindowSize = minimizer.get(1);
    this.minimizingKernels = minimizer.get(2);
    this.signalname = signalname;
  }

  public void plotter(Tensor xaxis, Tensor error, String parameter) throws IOException {
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel(parameter + " for " + signalname);
    Tensor domain = xaxis;
    Tensor values = error.divide(length());
    // X-Axis labels
    if (parameter.contains("alpha"))
      visualSet.setAxesLabelX("alpha");
    else
      visualSet.setAxesLabelX("window size");
    // Y-Axis label
    String yaxis = "Error per Measurement: [";
    // Seperate plots in position and orientation
    if (parameter.contains("x"))
      yaxis = yaxis.concat("meter");
    else
      yaxis = yaxis.concat("radiants");
    // seperate plots in 0-order error and 1-order error
    if (parameter.contains("dot")) {
      yaxis = yaxis.concat("/second");
      values.divide(time());
    }
    yaxis = yaxis.concat("]");
    visualSet.setAxesLabelY(yaxis);
    {
      VisualRow visualRow = visualSet.add(domain, values);
      visualRow.setLabel(parameter);
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    File file = HomeDirectory.Pictures(GeodesicFilterEvaluation.class.getSimpleName() + "_" + signalname.replace('/', '_') + "_" + parameter + ".png");
    ChartUtils.saveChartAsPNG(file, jFreeChart, 1024, 768);
  }

  private Tensor control() {
    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + signalname + ".csv").stream() //
        .map(row -> row.extract(1, 4)));
    return control;
  }

  private Scalar time() {
    Tensor time = Tensor.of(ResourceData.of("/dubilab/app/pose/" + signalname + ".csv").stream() //
        .map(row -> row.extract(0, 1)));
    Scalar duration = time.Get(time.length() - 1, 0).subtract(time.Get(0, 0));
    return duration;
  }

  private Scalar length() {
    Scalar length = RealScalar.of(Tensor.of(ResourceData.of("/dubilab/app/pose/" + signalname + ".csv").stream() //
        .map(row -> row.extract(0, 1))).length());
    return length;
  }

  public void windowSizeCurves() throws IOException {
    TensorUnaryOperator centerFilter = GeodesicCenter.of(geodesicInterface, SmoothingKernel.GAUSSIAN);
    Tensor refinedCenter = CenterFilter.of(centerFilter, 6).apply(control());
    // optimal windowsize for each parameter
    Scalar alpha_x = minimizingAlphas.Get(0);
    Scalar alpha_a = minimizingAlphas.Get(1);
    Scalar alpha_xdot = minimizingAlphas.Get(2);
    Scalar alpha_adot = minimizingAlphas.Get(3);
    // optimal Kernels for each parameter
    SmoothingKernel smoothingKernel_x = SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(0))];
    SmoothingKernel smoothingKernel_a = SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(1))];
    SmoothingKernel smoothingKernel_xdot = SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(2))];
    SmoothingKernel smoothingKernel_adot = SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(3))];
    // this will be our y-axis
    Tensor result_x = Tensors.empty();
    Tensor result_a = Tensors.empty();
    Tensor result_xdot = Tensors.empty();
    Tensor result_adot = Tensors.empty();
    // this will be our x-axis
    // Tensor windowRange = Subdivide.of(1, 10, 9);
    // Lower for TESTING reasons
    Tensor windowRange = Subdivide.of(1, 25, 24);
    // for (int windowSize = 1; windowSize < windowRange.length(); windowSize++) {
    for (int windowSize = 1; windowSize <= windowRange.length(); windowSize++) {
      // pose
      TensorUnaryOperator causalFilter_x = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel_x);
      Tensor refinedCausal_x = GeodesicIIRnFilter.of(causalFilter_x, geodesicInterface, windowSize, alpha_x).apply(control());
      Tensor error_x = GEODESIC_ERROR_EVALUATION.evaluate0ErrorSeperated(refinedCausal_x, refinedCenter).Get(0);
      result_x.append(error_x);
      // orientation
      TensorUnaryOperator causalFilter_a = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel_a);
      Tensor refinedCausal_a = GeodesicIIRnFilter.of(causalFilter_a, geodesicInterface, windowSize, alpha_a).apply(control());
      Tensor error_a = GEODESIC_ERROR_EVALUATION.evaluate0ErrorSeperated(refinedCausal_a, refinedCenter).Get(1);
      result_a.append(error_a);
      // pose velocity
      TensorUnaryOperator causalFilter_xdot = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel_xdot);
      Tensor refinedCausal_xdot = GeodesicIIRnFilter.of(causalFilter_xdot, geodesicInterface, windowSize, alpha_xdot).apply(control());
      Tensor error_xdot = GEODESIC_ERROR_EVALUATION.evaluate1ErrorSeperated(refinedCausal_xdot, refinedCenter).Get(0);
      result_xdot.append(error_xdot);
      // orientation velocity
      TensorUnaryOperator causalFilter_adot = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel_adot);
      Tensor refinedCausal_adot = GeodesicIIRnFilter.of(causalFilter_adot, geodesicInterface, windowSize, alpha_adot).apply(control());
      Tensor error_adot = GEODESIC_ERROR_EVALUATION.evaluate1ErrorSeperated(refinedCausal_adot, refinedCenter).Get(1);
      result_adot.append(error_adot);
    }
    plotter(windowRange, result_x, "windowSizeCurves_x");
    plotter(windowRange, result_a, "windowSizeCurves_a");
    plotter(windowRange, result_xdot, "windowSizeCurves_xdot");
    plotter(windowRange, result_adot, "windowSizeCurves_adot");
  }

  public void alphaCurves() throws IOException {
    TensorUnaryOperator centerFilter = GeodesicCenter.of(geodesicInterface, SmoothingKernel.GAUSSIAN);
    Tensor refinedCenter = CenterFilter.of(centerFilter, 6).apply(control());
    // optimal windowsize for each parameter
    int win_x = Scalars.intValueExact(minimizingWindowSize.Get(0));
    int win_a = Scalars.intValueExact(minimizingWindowSize.Get(1));
    int win_xdot = Scalars.intValueExact(minimizingWindowSize.Get(2));
    int win_adot = Scalars.intValueExact(minimizingWindowSize.Get(3));
    // optimal Kernels for each parameter
    SmoothingKernel smoothingKernel_x = SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(0))];
    SmoothingKernel smoothingKernel_a = SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(1))];
    SmoothingKernel smoothingKernel_xdot = SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(2))];
    SmoothingKernel smoothingKernel_adot = SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(3))];
    // this will be our y-axis
    Tensor result_x = Tensors.empty();
    Tensor result_a = Tensors.empty();
    Tensor result_xdot = Tensors.empty();
    Tensor result_adot = Tensors.empty();
    // This will be our x-axis
    // Tensor alpharange = Subdivide.of(0.1, 1, 50);
    Tensor alpharange = Subdivide.of(0.1, 1, 50);
    for (int index = 0; index < alpharange.length(); index++) {
      // pose
      TensorUnaryOperator causalFilter_x = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel_x);
      Tensor refinedCausal_x = GeodesicIIRnFilter.of(causalFilter_x, geodesicInterface, win_x, alpharange.Get(index)).apply(control());
      Tensor error_x = GEODESIC_ERROR_EVALUATION.evaluate0ErrorSeperated(refinedCausal_x, refinedCenter).Get(0);
      result_x.append(error_x);
      // orientation
      TensorUnaryOperator causalFilter_a = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel_a);
      Tensor refinedCausal_a = GeodesicIIRnFilter.of(causalFilter_a, geodesicInterface, win_a, alpharange.Get(index)).apply(control());
      Tensor error_a = GEODESIC_ERROR_EVALUATION.evaluate0ErrorSeperated(refinedCausal_a, refinedCenter).Get(1);
      result_a.append(error_a);
      // pose velocity
      TensorUnaryOperator causalFilter_xdot = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel_xdot);
      Tensor refinedCausal_xdot = GeodesicIIRnFilter.of(causalFilter_xdot, geodesicInterface, win_xdot, alpharange.Get(index)).apply(control());
      Tensor error_xdot = GEODESIC_ERROR_EVALUATION.evaluate1ErrorSeperated(refinedCausal_xdot, refinedCenter).Get(0);
      result_xdot.append(error_xdot);
      // orientation velocity
      TensorUnaryOperator causalFilter_adot = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel_adot);
      Tensor refinedCausal_adot = GeodesicIIRnFilter.of(causalFilter_adot, geodesicInterface, win_adot, alpharange.Get(index)).apply(control());
      Tensor error_adot = GEODESIC_ERROR_EVALUATION.evaluate1ErrorSeperated(refinedCausal_adot, refinedCenter).Get(1);
      result_adot.append(error_adot);
    }
    plotter(alpharange, result_x, "alphaCurves_x");
    plotter(alpharange, result_a, "alphaCurves_a");
    plotter(alpharange, result_xdot, "alphaCurves_xdot");
    plotter(alpharange, result_adot, "alphaCurves_adot");
  }
}
