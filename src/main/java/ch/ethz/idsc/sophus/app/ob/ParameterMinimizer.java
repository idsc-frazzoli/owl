// code by ob
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.sophus.filter.CenterFilter;
import ch.ethz.idsc.sophus.filter.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.ga.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.filter.ga.GeodesicIIRnFilter;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class ParameterMinimizer {
  private static final GeodesicErrorEvaluation GEODESIC_ERROR_EVALUATION = //
      new GeodesicErrorEvaluation(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);

  // ---
  public Tensor minimizer(String data) {
    Tensor control = control(data);
    // starting values of alpha
    Scalar alpha_x = RealScalar.of(-1);
    Scalar alpha_a = RealScalar.of(-1);
    Scalar alpha_xdot = RealScalar.of(-1);
    Scalar alpha_adot = RealScalar.of(-1);
    // starting values of windowsizes
    int win_x = -1;
    int win_a = -1;
    int win_xdot = -1;
    int win_adot = -1;
    // starting values of errors
    Scalar err_x = RealScalar.of(10_000);
    Scalar err_a = RealScalar.of(10_000);
    Scalar err_xdot = RealScalar.of(10_000);
    Scalar err_adot = RealScalar.of(10_000);
    // starting values of kernels
    SmoothingKernel smoothingKernel_x = SmoothingKernel.DIRICHLET;
    SmoothingKernel smoothingKernel_a = SmoothingKernel.DIRICHLET;
    SmoothingKernel smoothingKernel_xdot = SmoothingKernel.DIRICHLET;
    SmoothingKernel smoothingKernel_adot = SmoothingKernel.DIRICHLET;
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    // define our 'truth' signal
    TensorUnaryOperator centerFilter = GeodesicCenter.of(Se2Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN);
    Tensor refinedCenter = CenterFilter.of(centerFilter, 6).apply(control);
    Tensor alpharange = Subdivide.of(0.1, 1, 50);
    // Iterate over Kernels, then windowsize and then alphas
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      // SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
      System.out.println(smoothingKernel.toString());
      TensorUnaryOperator causalFilter = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel);
      // Arbitrary upper limit of windowsize
      for (int windowSize = 1; windowSize < 25; windowSize++) {
        for (int index = 0; index < alpharange.length(); index++) {
          Tensor refinedCausal = Tensors.empty();
          refinedCausal = GeodesicIIRnFilter.of(causalFilter, geodesicInterface, windowSize, alpharange.Get(index)).apply(control);
          Tensor error0 = GEODESIC_ERROR_EVALUATION.evaluate0ErrorSeperated(refinedCausal, refinedCenter);
          Tensor error1 = GEODESIC_ERROR_EVALUATION.evaluate1ErrorSeperated(refinedCausal, refinedCenter);
          // xy-error update
          if (Scalars.lessEquals(error0.Get(0), err_x)) {
            alpha_x = alpharange.Get(index);
            win_x = windowSize;
            err_x = error0.Get(0);
            smoothingKernel_x = smoothingKernel;
            System.out.println("xy: Windowsize: " + windowSize + "   alpha: " + alpharange.Get(index) + "   Error: " + err_x);
          }
          // oerientation-error update
          if (Scalars.lessEquals(error0.Get(1), err_a)) {
            alpha_a = alpharange.Get(index);
            win_a = windowSize;
            err_a = error0.Get(1);
            smoothingKernel_a = smoothingKernel;
            System.out.println("a:  Windowsize: " + windowSize + "   alpha: " + alpharange.Get(index) + "   Error: " + err_a);
          }
          // xy velocity error update;
          if (Scalars.lessEquals(error1.Get(0), err_xdot)) {
            alpha_xdot = alpharange.Get(index);
            win_xdot = windowSize;
            err_xdot = error1.Get(1);
            smoothingKernel_xdot = smoothingKernel;
            System.out.println("xdot: Windowsize: " + windowSize + "   alpha: " + alpharange.Get(index) + "   Error: " + err_xdot);
          }
          // orientation velocity update
          if (Scalars.lessEquals(error1.Get(1), err_adot)) {
            alpha_adot = alpharange.Get(index);
            win_adot = windowSize;
            err_adot = error1.Get(1);
            smoothingKernel_adot = smoothingKernel;
            System.out.println("adot: Windowsize: " + windowSize + "   alpha: " + alpharange.Get(index) + "   Error: " + err_adot);
          }
        }
      }
    }
    Tensor minimizingAlphas = Tensors.of(alpha_x, alpha_a, alpha_xdot, alpha_adot);
    Tensor minimizingWindows = Tensors.vector(win_x, win_a, win_xdot, win_adot);
    Tensor minimizingKernels = Tensors.vector(smoothingKernel_x.ordinal(), smoothingKernel_a.ordinal(), smoothingKernel_xdot.ordinal(),
        smoothingKernel_adot.ordinal());
    Tensor minimizingErrors = Tensors.of(err_x, err_a, err_xdot, err_adot);
    return Tensors.of(minimizingAlphas, minimizingWindows, minimizingKernels, minimizingErrors);
  }

  private static Tensor control(String data) {
    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + data + ".csv").stream() //
        .map(row -> row.extract(1, 4)));
    return control;
  }
}
