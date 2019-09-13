// code by jph
package ch.ethz.idsc.sophus.app.ext;

import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer;
import org.apache.commons.math3.random.MersenneTwister;

import ch.ethz.idsc.sophus.app.api.GokartPoseData;
import ch.ethz.idsc.sophus.app.api.GokartPoseDataV2;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.win.GaussianWindow;

/* package */ enum Se2Cmaes {
  ;
  public static void main(String[] args) {
    ConvergenceChecker<PointValuePair> convergenceChecker = new ConvergenceChecker<PointValuePair>() {
      @Override
      public boolean converged(int iteration, PointValuePair previous, PointValuePair current) {
        // System.out.println("current " + Tensors.vectorDouble(current.getPoint()));
        // System.out.println(iteration);
        return false;
      }
    };
    // ---
    GokartPoseData gokartPoseData = GokartPoseDataV2.RACING_DAY;
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(Se2Geodesic.INSTANCE, GaussianWindow.FUNCTION);
    int dims = 2;
    int width = 0;
    System.out.println("filter width = " + width);
    TensorUnaryOperator centerFilter = CenterFilter.of(tensorUnaryOperator, width);
    for (String name : gokartPoseData.list()) {
      Timing timing = Timing.started();
      Tensor poses = gokartPoseData.getPose(name, 100000);
      System.out.println("loaded: " + timing.seconds());
      Tensor pqr_t = Partition.of(poses, dims + 2, 2);
      Tensor filtr = centerFilter.apply(poses);
      Tensor xyz_t = Partition.of(filtr, dims + 2, 2);
      // List<Integer> d1 = Dimensions.of(pqr_t);
      // List<Integer> d2 = Dimensions.of(xyz_t);
      if (width == 0)
        Chop._12.requireClose( //
            xyz_t.get(Tensor.ALL, dims + 1), //
            pqr_t.get(Tensor.ALL, dims + 1));
      pqr_t.set(xyz_t.get(Tensor.ALL, dims + 1), Tensor.ALL, dims + 1);
      System.out.println("preped: " + timing.seconds());
      PredictionAccuracy predictionAccuracy = new PredictionAccuracy(pqr_t);
      System.out.println("creatd: " + timing.seconds());
      // ---
      ObjectiveFunction objectiveFunction = new ObjectiveFunction(predictionAccuracy);
      InitialGuess initialGuess = new InitialGuess(Primitives.toDoubleArray(UnitVector.of(dims, dims - 1).negate()));
      CMAESOptimizer cmaesOptimizer = //
          new CMAESOptimizer(20, -1, true, 0, 10, new MersenneTwister(), false, convergenceChecker);
      PointValuePair pointValuePair = cmaesOptimizer.optimize( //
          MaxEval.unlimited(), //
          objectiveFunction, //
          GoalType.MINIMIZE, //
          initialGuess, //
          new CMAESOptimizer.PopulationSize(20), //
          new CMAESOptimizer.Sigma(Primitives.toDoubleArray(ConstantArray.of(RealScalar.ONE, dims))), //
          new SimpleBounds( //
              Primitives.toDoubleArray(ConstantArray.of(RealScalar.of(-2), dims)), //
              Primitives.toDoubleArray(ConstantArray.of(RealScalar.of(+2), dims))));
      System.out.println("optimz: " + timing.seconds());
      Tensor vectorDouble = Tensors.vectorDouble(pointValuePair.getPoint());
      System.out.println(vectorDouble);
    }
  }
}
