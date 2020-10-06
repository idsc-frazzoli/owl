// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.sophus.gbc.InsidePolygonCoordinate;
import ch.ethz.idsc.sophus.gbc.MetricHomogeneous;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.r2.IterativeCoordinate;
import ch.ethz.idsc.sophus.math.var.InversePowerVariogram;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum IterativeCoordinates implements LogWeighting {
  MV0(IterativeCoordinate.usingMeanValue(0)), //
  MV1(IterativeCoordinate.usingMeanValue(1)), //
  MV2(IterativeCoordinate.usingMeanValue(2)), //
  MV3(IterativeCoordinate.usingMeanValue(3)), //
  MV5(IterativeCoordinate.usingMeanValue(5)), //
  M20(MetricHomogeneous.of(InversePowerVariogram.of(2))), //
  M00(IterativeCoordinate.of(MetricHomogeneous.affine(), 0)), //
  M01(IterativeCoordinate.of(MetricHomogeneous.affine(), 1)), //
  M02(IterativeCoordinate.of(MetricHomogeneous.affine(), 2)), //
  M03(IterativeCoordinate.of(MetricHomogeneous.affine(), 3)), //
  M05(IterativeCoordinate.of(MetricHomogeneous.affine(), 5)), //
  ;

  private final TensorUnaryOperator tensorUnaryOperator;

  private IterativeCoordinates(TensorUnaryOperator tensorUnaryOperator) {
    this.tensorUnaryOperator = tensorUnaryOperator;
  }

  @Override // from LogWeighting
  public TensorUnaryOperator operator( //
      Biinvariant biinvariant, // <- ignored
      VectorLogManifold vectorLogManifold, // with 2 dimensional tangent space
      ScalarUnaryOperator variogram, // <- ignored
      Tensor sequence) {
    return WeightingOperators.wrap( //
        new InsidePolygonCoordinate(vectorLogManifold, tensorUnaryOperator), //
        sequence);
  }

  @Override // from LogWeighting
  public TensorScalarFunction function( //
      Biinvariant biinvariant, // <- ignored
      VectorLogManifold vectorLogManifold, // with 2 dimensional tangent space
      ScalarUnaryOperator variogram, // <- ignored
      Tensor sequence, Tensor values) {
    TensorUnaryOperator tensorUnaryOperator = operator(biinvariant, vectorLogManifold, variogram, sequence);
    Objects.requireNonNull(values);
    return point -> tensorUnaryOperator.apply(point).dot(values).Get();
  }

  public static List<LogWeighting> list() {
    return Arrays.asList(values());
  }
}
