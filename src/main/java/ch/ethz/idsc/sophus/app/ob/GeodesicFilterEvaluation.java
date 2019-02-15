// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.filter.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.filter.GeodesicIIRnFilter;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.WindowSideSampler;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;

public class GeodesicFilterEvaluation {
  public static final File ROOT = new File("C:/Users/Oliver/Desktop/MA/owl_export");
  // ---
  private final LieDifferences lieDifferences;

  GeodesicFilterEvaluation(LieGroup lieGroup, LieExponential lieExponential) {
    this.lieDifferences = new LieDifferences(lieGroup, lieExponential);
  }

  public Tensor evaluate0ErrorSeperated(Tensor causal, Tensor center) {
    Tensor errors = Tensors.empty();
    for (int i = 0; i < causal.length(); ++i) {
      Tensor difference = lieDifferences.pair(causal.get(i), center.get(i));
      Scalar scalar1 = Norm._2.ofVector(difference.extract(0, 2));
      Scalar scalar2 = Norm._2.ofVector(difference.extract(2, 3));
      errors.append(Tensors.of(scalar1, scalar2));
    }
    return Total.of(errors);
  }

  public Tensor evaluate1ErrorSeperated(Tensor causal, Tensor center) {
    Tensor errors = Tensors.empty();
    for (int i = 1; i < causal.length(); ++i) {
      Tensor pair1 = lieDifferences.pair(causal.get(i - 1), causal.get(i));
      Tensor pair2 = lieDifferences.pair(center.get(i - 1), center.get(i));
      Scalar scalar1 = Norm._2.between(pair1.extract(0, 2), pair2.extract(0, 2));
      Scalar scalar2 = Norm._2.between(pair1.extract(2, 3), pair2.extract(2, 3));
      errors.append(Tensors.of(scalar1, scalar2));
    }
    return Total.of(errors);
  }
//  TODO OB: adapt for new filter structure
//  public Tensor processErrors(Tensor control, int width) {
//    TableBuilder tableBuilder = new TableBuilder();
//    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
//    TensorUnaryOperator CenterFilter = GeodesicCenter.of(Se2Geodesic.INSTANCE, smoothingKernel);
//    Tensor refinedCenter = GeodesicCenterFilter.of(CenterFilter, 6).apply(control);
//    Tensor alpharange = Subdivide.of(0.1, 1, 40);
//    WindowSideSampler windowSideSampler = new WindowSideSampler(smoothingKernel);
//    for (int index = 0; index < alpharange.length(); index++) {
//      Tensor refinedCausal = Tensors.empty();
//      Tensor mask = windowSideSampler.apply(width).extract(0, width + 1);
//      mask.append(alpharange.get(index));
//      TensorUnaryOperator causalFilter = new GeodesicIIRnFilter(Se2Geodesic.INSTANCE, mask);
//      refinedCausal = Tensor.of(control.stream().map(causalFilter));
//      Tensor row = Tensors.of(alpharange.Get(index), evaluate0ErrorSeperated(refinedCausal, refinedCenter), //
//          evaluate1ErrorSeperated(refinedCausal, refinedCenter));
//      tableBuilder.appendRow(row);
//    }
//    Tensor log = tableBuilder.toTable();
//    return log;
//  }

  public Tensor minimizer(Tensor control) {
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
    Scalar err_x = RealScalar.of(10000);
    Scalar err_a = RealScalar.of(10000);
    Scalar err_xdot = RealScalar.of(10000);
    Scalar err_adot = RealScalar.of(10000);
    // starting values of kernels
    SmoothingKernel smoothingKernel_x = SmoothingKernel.DIRICHLET;
    SmoothingKernel smoothingKernel_a = SmoothingKernel.DIRICHLET;
    SmoothingKernel smoothingKernel_xdot = SmoothingKernel.DIRICHLET;
    SmoothingKernel smoothingKernel_adot = SmoothingKernel.DIRICHLET;
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    // define our 'truth' signal
    TensorUnaryOperator centerFilter = GeodesicCenter.of(Se2Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN);
    Tensor refinedCenter = GeodesicCenterFilter.of(centerFilter, 6).apply(control);
    Tensor alpharange = Subdivide.of(0, 1, 50);
    // Iterate over Kernels, then windowsize and then alphas
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      System.out.println(smoothingKernel.toString());
      TensorUnaryOperator causalFilter = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel);
      // Arbitrary upper limit of windowsize
      for (int windowSize = 1; windowSize < 20; windowSize++) {
        for (int index = 0; index < alpharange.length(); index++) {
          Tensor refinedCausal = Tensors.empty();
          refinedCausal = GeodesicIIRnFilter.of(causalFilter, geodesicInterface, windowSize, alpharange.Get(index)).apply(control);
          Tensor error1 = evaluate0ErrorSeperated(refinedCausal, refinedCenter);
          Tensor error2 = evaluate1ErrorSeperated(refinedCausal, refinedCenter);
          // xy-error update
          if (Scalars.lessEquals(error1.Get(0), err_x)) {
            alpha_x = alpharange.Get(index);
            win_x = windowSize;
            err_x = error1.Get(0);
            smoothingKernel_x = smoothingKernel;
            System.out.println("xy: Windowsize: " + windowSize + "   alpha: " + alpharange.Get(index) + "   Error: " + err_x);
          }
          // oerientation-error update
          if (Scalars.lessEquals(error1.Get(1), err_a)) {
            alpha_a = alpharange.Get(index);
            win_a = windowSize;
            err_a = error1.Get(1);
            smoothingKernel_a = smoothingKernel;
            System.out.println("a:  Windowsize: " + windowSize + "   alpha: " + alpharange.Get(index) + "   Error: " + err_a);
          }
          // xy velocity error update;
          if (Scalars.lessEquals(error2.Get(0), err_xdot)) {
            alpha_xdot = alpharange.Get(index);
            win_xdot = windowSize;
            err_xdot = error2.Get(1);
            smoothingKernel_xdot = smoothingKernel;
            System.out.println("xdot: Windowsize: " + windowSize + "   alpha: " + alpharange.Get(index) + "   Error: " + err_xdot);
          }
          // orientation velocity update
          if (Scalars.lessEquals(error2.Get(1), err_adot)) {
            alpha_adot = alpharange.Get(index);
            win_adot = windowSize;
            err_adot = error2.Get(1);
            smoothingKernel_adot = smoothingKernel;
            System.out.println("adot: Windowsize: " + windowSize + "   alpha: " + alpharange.Get(index) + "   Error: " + err_adot);
          }
        }
      }
    }
    control.length();
    Tensor minimizingAlphas = Tensors.of(alpha_x, alpha_a, alpha_xdot, alpha_adot);
    Tensor minimizingWindows = Tensors.vector(win_x, win_a, win_xdot, win_adot);
    Tensor minimizingErrors = Tensors.of(err_x, err_a, err_xdot, err_adot);
    System.out.println("xy: " + smoothingKernel_x);
    System.out.println("a: " + smoothingKernel_a);
    System.out.println("xydot: " + smoothingKernel_xdot);
    System.out.println("adot: " + smoothingKernel_adot);
    return Tensors.of(minimizingAlphas, minimizingWindows, minimizingErrors);
  }

  public static void main(String[] args) throws IOException {
    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + "0w/20180702T133612_2" + ".csv").stream() //
        // .limit(300) //
        .map(row -> row.extract(1, 4)));
    // ---
    GeodesicFilterEvaluation geodesicEvaluation = new GeodesicFilterEvaluation(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
    Tensor result = geodesicEvaluation.minimizer(control);
    System.out.println(Pretty.of(result));
  }
}
