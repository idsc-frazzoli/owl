// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.gbc.AbsoluteCoordinate;
import ch.ethz.idsc.sophus.gbc.BarycentricCoordinate;
import ch.ethz.idsc.sophus.gbc.Relative1Coordinate;
import ch.ethz.idsc.sophus.gbc.Relative2Coordinate;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.itp.CrossAveraging;
import ch.ethz.idsc.sophus.krg.InversePowerVariogram;
import ch.ethz.idsc.sophus.krg.Kriging;
import ch.ethz.idsc.sophus.krg.PseudoDistances;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
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
    public TensorScalarFunction build(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      return kriging(PseudoDistances.ABSOLUTE.create(vectorLogManifold, variogram, sequence), //
          RealScalar.of(0), vectorLogManifold, sequence, values);
    }
  }, //
  REL_KR() {
    @Override
    public TensorScalarFunction build(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      return kriging(PseudoDistances.RELATIVE1.create(vectorLogManifold, variogram, sequence), //
          RealScalar.of(0), vectorLogManifold, sequence, values);
    }
  }, //
  ABS_SI() {
    @Override
    public TensorScalarFunction build(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      TensorUnaryOperator create = //
          PseudoDistances.ABSOLUTE.affine(vectorLogManifold, variogram, sequence);
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          create, RnBiinvariantMean.INSTANCE, values);
      return point -> tensorUnaryOperator.apply(point).Get();
    }
  }, //
  REL1_SI() {
    @Override
    public TensorScalarFunction build(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          PseudoDistances.RELATIVE1.affine(vectorLogManifold, variogram, sequence), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> tensorUnaryOperator.apply(point).Get();
    }
  }, //
  REL2_SI() {
    @Override
    public TensorScalarFunction build(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          PseudoDistances.RELATIVE2.affine(vectorLogManifold, variogram, sequence), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> tensorUnaryOperator.apply(point).Get();
    }
  }, //
  ABS_ID() {
    @Override
    public TensorScalarFunction build(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      BarycentricCoordinate barycentricCoordinate = AbsoluteCoordinate.of(vectorLogManifold, variogram);
      return point -> barycentricCoordinate.weights(sequence, point).Get(0);
    }
  }, //
  REL_A() {
    @Override
    public TensorScalarFunction build(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      TensorUnaryOperator tuo = CrossAveraging.of( //
          LogWeightings.BI_STANDARD.from(vectorLogManifold, InversePowerVariogram.of(0), sequence), RnBiinvariantMean.INSTANCE, values);
      return t -> (Scalar) tuo.apply(t);
    }
  }, //
  REL_S() {
    @Override
    public TensorScalarFunction build(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      BarycentricCoordinate barycentricCoordinate = Relative1Coordinate.of(vectorLogManifold, variogram);
      return point -> barycentricCoordinate.weights(sequence, point).Get(0);
    }
  }, //
  REL_G() {
    @Override
    public TensorScalarFunction build(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      Relative2Coordinate grCoordinate = new Relative2Coordinate(vectorLogManifold, variogram, sequence);
      return point -> grCoordinate.apply(point).Get(0);
    }
  }, //
  ;

  public static final HsScalarFunctions[] GBCS = { ABS_ID, REL_A, REL_S, REL_G };

  private static TensorScalarFunction kriging( //
      TensorUnaryOperator weightingInterface, Scalar cvar, //
      VectorLogManifold vectorLogManifold, Tensor sequence, Tensor values) {
    Tensor covariance = DiagonalMatrix.with(ConstantArray.of(cvar, sequence.length()));
    Kriging kriging = Kriging.regression(weightingInterface, sequence, values, covariance);
    return point -> (Scalar) kriging.estimate(point);
  }
}
