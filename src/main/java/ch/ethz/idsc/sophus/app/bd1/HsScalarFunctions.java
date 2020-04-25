// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.itp.CrossAveraging;
import ch.ethz.idsc.sophus.krg.Kriging;
import ch.ethz.idsc.sophus.krg.PseudoDistances;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ enum HsScalarFunctions implements HsScalarFunction {
  KR_ABSOLUTE() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      return kriging(PseudoDistances.ABSOLUTE.of(flattenLogManifold, variogram), //
          RealScalar.of(0), flattenLogManifold, sequence, values);
    }
  }, //
  KR_RELATIVE() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      return kriging(PseudoDistances.RELATIVE.of(flattenLogManifold, variogram), //
          RealScalar.of(0), flattenLogManifold, sequence, values);
    }
  }, //
  LW_ID_LINEAR() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      return logWeighting(LogWeightings.ID_LINEAR, flattenLogManifold, sequence, values);
    }
  }, //
  LW_ID_SMOOTH() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      return logWeighting(LogWeightings.ID_SMOOTH, flattenLogManifold, sequence, values);
    }
  }, //
  LW_BI_LINEAR() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      return logWeighting(LogWeightings.BI_LINEAR, flattenLogManifold, sequence, values);
    }
  }, //
  LW_BI_SMOOTH() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      return logWeighting(LogWeightings.BI_SMOOTH, flattenLogManifold, sequence, values);
    }
  }, //
  LW_BI_AFFINE() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      return logWeighting(LogWeightings.BI_AFFINE, flattenLogManifold, sequence, values);
    }
  }, //
  ;

  private static TensorScalarFunction kriging( //
      WeightingInterface weightingInterface, Scalar cvar, //
      FlattenLogManifold flattenLogManifold, Tensor sequence, Tensor values) {
    Tensor covariance = DiagonalMatrix.with(ConstantArray.of(cvar, sequence.length()));
    Kriging kriging = Kriging.regression(weightingInterface, sequence, values, covariance);
    return point -> (Scalar) kriging.estimate(point);
  }

  private static TensorScalarFunction logWeighting( //
      LogWeighting logWeighting, //
      FlattenLogManifold flattenLogManifold, Tensor sequence, Tensor values) {
    TensorUnaryOperator tuo = CrossAveraging.of( //
        logWeighting.from(flattenLogManifold), sequence, RnBiinvariantMean.INSTANCE, values);
    return t -> (Scalar) tuo.apply(t);
  }
}
