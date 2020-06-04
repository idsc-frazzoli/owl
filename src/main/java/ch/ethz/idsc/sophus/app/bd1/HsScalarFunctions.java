// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.gbc.AbsoluteCoordinate;
import ch.ethz.idsc.sophus.gbc.BarycentricCoordinate;
import ch.ethz.idsc.sophus.gbc.Relative2Coordinate;
import ch.ethz.idsc.sophus.gbc.Relative1Coordinate;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.itp.CrossAveraging;
import ch.ethz.idsc.sophus.krg.InversePowerVariogram;
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
  ABS_KR() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      return kriging(PseudoDistances.ABSOLUTE.create(flattenLogManifold, variogram), //
          RealScalar.of(0), flattenLogManifold, sequence, values);
    }
  }, //
  REL_KR() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      return kriging(PseudoDistances.RELATIVE.create(flattenLogManifold, variogram), //
          RealScalar.of(0), flattenLogManifold, sequence, values);
    }
  }, //
  ABS_SI() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      WeightingInterface weightingInterface = ShepardWeighting.absolute(flattenLogManifold, InversePowerVariogram.of(2));
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          point -> weightingInterface.weights(sequence, point), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> tensorUnaryOperator.apply(point).Get();
    }
  }, //
  REL_SI() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      WeightingInterface weightingInterface = ShepardWeighting.relative(flattenLogManifold, InversePowerVariogram.of(2));
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          point -> weightingInterface.weights(sequence, point), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> tensorUnaryOperator.apply(point).Get();
    }
  }, //
  ABS_ID() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      BarycentricCoordinate projectedCoordinate = AbsoluteCoordinate.of(flattenLogManifold, variogram);
      return point -> projectedCoordinate.weights(sequence, point).Get(0);
    }
  }, //
  REL_A() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      TensorUnaryOperator tuo = CrossAveraging.of( //
          LogWeightings.BI_STANDARD.from(flattenLogManifold, InversePowerVariogram.of(0), sequence), RnBiinvariantMean.INSTANCE, values);
      return t -> (Scalar) tuo.apply(t);
    }
  }, //
  REL_1() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      BarycentricCoordinate projectedCoordinate = Relative1Coordinate.of(flattenLogManifold, variogram);
      return point -> projectedCoordinate.weights(sequence, point).Get(0);
    }
  }, //
  REL_2() {
    @Override
    public TensorScalarFunction build(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      Relative2Coordinate grCoordinate = new Relative2Coordinate(flattenLogManifold, variogram, sequence);
      return point -> grCoordinate.apply(point).Get(0);
    }
  }, //
  ;

  public static final HsScalarFunctions[] GBCS = { ABS_ID, REL_A, REL_1, REL_2 };

  private static TensorScalarFunction kriging( //
      WeightingInterface weightingInterface, Scalar cvar, //
      VectorLogManifold flattenLogManifold, Tensor sequence, Tensor values) {
    Tensor covariance = DiagonalMatrix.with(ConstantArray.of(cvar, sequence.length()));
    Kriging kriging = Kriging.regression(weightingInterface, sequence, values, covariance);
    return point -> (Scalar) kriging.estimate(point);
  }
}
