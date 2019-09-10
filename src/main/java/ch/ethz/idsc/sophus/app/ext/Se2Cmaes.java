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
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Primitives;

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
    int m = 4;
    for (String name : gokartPoseData.list()) {
      Tensor pqr_t = Partition.of(gokartPoseData.getPose(name, 10 * 500), m + 1);
      PredictionAccuracy predictionAccuracy = new PredictionAccuracy(pqr_t);
      // ---
      ObjectiveFunction objectiveFunction = new ObjectiveFunction(predictionAccuracy);
      InitialGuess initialGuess = new InitialGuess(Primitives.toDoubleArray(UnitVector.of(m - 1, m - 2).negate()));
      CMAESOptimizer cmaesOptimizer = //
          new CMAESOptimizer(20, -1, true, 0, 10, new MersenneTwister(), false, convergenceChecker);
      PointValuePair pointValuePair = cmaesOptimizer.optimize( //
          MaxEval.unlimited(), //
          objectiveFunction, //
          GoalType.MINIMIZE, //
          initialGuess, //
          new CMAESOptimizer.PopulationSize(20), //
          new CMAESOptimizer.Sigma(Primitives.toDoubleArray(ConstantArray.of(RealScalar.ONE, m - 1))), //
          new SimpleBounds( //
              Primitives.toDoubleArray(ConstantArray.of(RealScalar.of(-2), m - 1)), //
              Primitives.toDoubleArray(ConstantArray.of(RealScalar.of(+2), m - 1))));
      Tensor vectorDouble = Tensors.vectorDouble(pointValuePair.getPoint());
      System.out.println(vectorDouble);
    }
  }
}
