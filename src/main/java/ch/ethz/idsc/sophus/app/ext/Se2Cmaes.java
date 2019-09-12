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
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
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
    int dims = 2;
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(Se2Geodesic.INSTANCE, GaussianWindow.FUNCTION);
    TensorUnaryOperator centerFilter = CenterFilter.of(tensorUnaryOperator, 4);
    for (String name : gokartPoseData.list()) {
      Tensor seq = gokartPoseData.getPose(name, 100000);
      Tensor pqr_t = Partition.of(centerFilter.apply(seq), dims + 2, 2);
      PredictionAccuracy predictionAccuracy = new PredictionAccuracy(pqr_t);
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
      Tensor vectorDouble = Tensors.vectorDouble(pointValuePair.getPoint());
      System.out.println(vectorDouble);
    }
  }
}
