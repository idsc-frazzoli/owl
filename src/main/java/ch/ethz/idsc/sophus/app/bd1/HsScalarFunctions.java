// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.gbc.AbsoluteCoordinate;
import ch.ethz.idsc.sophus.gbc.GrCoordinate;
import ch.ethz.idsc.sophus.gbc.ProjectedCoordinate;
import ch.ethz.idsc.sophus.gbc.RelativeCoordinate;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.itp.CrossAveraging;
import ch.ethz.idsc.sophus.krg.Kriging;
import ch.ethz.idsc.sophus.krg.PseudoDistances;
import ch.ethz.idsc.sophus.krg.ShepardWeighting;
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

/** TODO need to document */
/* package */ enum HsScalarFunctions implements HsScalarFunction {
  KR_ABSOLUTE() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      return kriging(PseudoDistances.ABSOLUTE.create(flattenLogManifold, variogram), //
          RealScalar.of(0), flattenLogManifold, sequence, values);
    }
  }, //
  KR_RELATIVE() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      return kriging(PseudoDistances.RELATIVE.create(flattenLogManifold, variogram), //
          RealScalar.of(0), flattenLogManifold, sequence, values);
    }
  }, //
  SI_ABSOLUTE() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          ShepardWeighting.absolute(flattenLogManifold, 2), sequence, RnBiinvariantMean.INSTANCE, values);
      return point -> tensorUnaryOperator.apply(point).Get();
    }
  }, //
  SI_RELATIVE() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          ShepardWeighting.relative(flattenLogManifold, 2), sequence, RnBiinvariantMean.INSTANCE, values);
      return point -> tensorUnaryOperator.apply(point).Get();
    }
  }, //
  ID_ABSOLUTE() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      ProjectedCoordinate projectedCoordinate = AbsoluteCoordinate.nugenx(flattenLogManifold, variogram);
      return point -> projectedCoordinate.weights(sequence, point).Get(0);
      // return logWeighting(LogWeightings.ID_LINEAR, flattenLogManifold, sequence, values);
    }
  }, //
  // LW_ID_SMOOTH() {
  // @Override
  // public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
  // return logWeighting(LogWeightings.ID_SMOOTH, flattenLogManifold, sequence, values);
  // }
  // }, //
  BI_RELATIVE() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      ProjectedCoordinate projectedCoordinate = RelativeCoordinate.nugenx(flattenLogManifold, variogram);
      return point -> projectedCoordinate.weights(sequence, point).Get(0);
      // return logWeighting(LogWeightings.BI_LINEAR, flattenLogManifold, sequence, values);
    }
  }, //
  // LW_BI_SMOOTH() {
  // @Override
  // public TensorScalarFunction build(FlattenLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
  // return logWeighting(LogWeightings.BI_SMOOTH, flattenLogManifold, sequence, values);
  // }
  // }, //
  LW_BI_AFFINE() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      return logWeighting(LogWeightings.BI_AFFINE.from(flattenLogManifold), sequence, values);
    }
  }, //
  GR_BI_SMOOTH() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      GrCoordinate grCoordinate = new GrCoordinate(flattenLogManifold, variogram, sequence);
      return point -> grCoordinate.apply(point).Get(0);
    }
  }, //
  ;

  private static TensorScalarFunction kriging( //
      WeightingInterface weightingInterface, Scalar cvar, //
      VectorLogManifold flattenLogManifold, Tensor sequence, Tensor values) {
    Tensor covariance = DiagonalMatrix.with(ConstantArray.of(cvar, sequence.length()));
    Kriging kriging = Kriging.regression(weightingInterface, sequence, values, covariance);
    return point -> (Scalar) kriging.estimate(point);
  }

  private static TensorScalarFunction logWeighting( //
      WeightingInterface weightingInterface, Tensor sequence, Tensor values) {
    TensorUnaryOperator tuo = CrossAveraging.of( //
        weightingInterface, sequence, RnBiinvariantMean.INSTANCE, values);
    return t -> (Scalar) tuo.apply(t);
  }
}
