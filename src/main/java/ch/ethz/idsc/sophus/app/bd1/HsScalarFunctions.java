// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.itp.CrossAveraging;
import ch.ethz.idsc.sophus.krg.Kriging;
import ch.ethz.idsc.sophus.krg.Krigings;
import ch.ethz.idsc.sophus.krg.PowerVariogram;
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

/* package */ enum HsScalarFunctions implements HsScalarFunction {
  KR_AB_10_0() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, Tensor sequence, Tensor values) {
      return kriging(PseudoDistances.ABSOLUTE.of(flattenLogManifold, PowerVariogram.of(RealScalar.ONE, RealScalar.of(1.0))), //
          RealScalar.of(0), flattenLogManifold, sequence, values);
    }
  }, //
  KR_AB_15_0() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, Tensor sequence, Tensor values) {
      return kriging(PseudoDistances.ABSOLUTE.of(flattenLogManifold, PowerVariogram.of(RealScalar.ONE, RealScalar.of(1.5))), //
          RealScalar.of(0), flattenLogManifold, sequence, values);
    }
  }, //
  KR_RL_10_0() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, Tensor sequence, Tensor values) {
      return kriging(PseudoDistances.RELATIVE.of(flattenLogManifold, PowerVariogram.of(RealScalar.ONE, RealScalar.of(1.0))), //
          RealScalar.of(0), flattenLogManifold, sequence, values);
    }
  }, //
  KR_RL_15_0() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, Tensor sequence, Tensor values) {
      return kriging(PseudoDistances.RELATIVE.of(flattenLogManifold, PowerVariogram.of(RealScalar.ONE, RealScalar.of(1.5))), //
          RealScalar.of(0), flattenLogManifold, sequence, values);
    }
  }, //
  LW_ID_LINEAR() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, Tensor sequence, Tensor values) {
      return logWeighting(LogWeightings.ID_LINEAR, flattenLogManifold, sequence, values);
    }
  }, //
  LW_ID_SMOOTH() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, Tensor sequence, Tensor values) {
      return logWeighting(LogWeightings.ID_SMOOTH, flattenLogManifold, sequence, values);
    }
  }, //
  LW_BI_LINEAR() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, Tensor sequence, Tensor values) {
      return logWeighting(LogWeightings.BI_LINEAR, flattenLogManifold, sequence, values);
    }
  }, //
  LW_BI_SMOOTH() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, Tensor sequence, Tensor values) {
      return logWeighting(LogWeightings.BI_SMOOTH, flattenLogManifold, sequence, values);
    }
  }, //
  LW_BI_AFFINE() {
    @Override
    public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, Tensor sequence, Tensor values) {
      return logWeighting(LogWeightings.BI_AFFINE, flattenLogManifold, sequence, values);
    }
  }, //
  ;

  private static TensorScalarFunction kriging( //
      WeightingInterface pseudoDistances, Scalar cvar, //
      FlattenLogManifold flattenLogManifold, Tensor sequence, Tensor values) {
    // ScalarUnaryOperator variogram = PowerVariogram.of(RealScalar.ONE, beta);
    Tensor covariance = DiagonalMatrix.with(ConstantArray.of(cvar, sequence.length()));
    Kriging kriging = Krigings.regression(pseudoDistances, sequence, values, covariance);
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
