// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.sophus.gbc.AffineCoordinate;
import ch.ethz.idsc.sophus.gbc.Genesis;
import ch.ethz.idsc.sophus.gbc.HsCoordinates;
import ch.ethz.idsc.sophus.gbc.MetricCoordinate;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.r2.ExponentialCoordinate;
import ch.ethz.idsc.sophus.lie.r2.InsideConvexHullCoordinate;
import ch.ethz.idsc.sophus.math.var.InversePowerVariogram;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

public enum ExponentialCoordinates implements LogWeighting {
  METRIC_00(ExponentialCoordinate.of(MetricCoordinate.of(InversePowerVariogram.of(2)), 0)), //
  METRIC_01(ExponentialCoordinate.of(MetricCoordinate.of(InversePowerVariogram.of(2)), 1)), //
  METRIC_02(ExponentialCoordinate.of(MetricCoordinate.of(InversePowerVariogram.of(2)), 2)), //
  METRIC_03(ExponentialCoordinate.of(MetricCoordinate.of(InversePowerVariogram.of(2)), 3)), //
  METRIC_04(ExponentialCoordinate.of(MetricCoordinate.of(InversePowerVariogram.of(2)), 4)), //
  METRIC_05(ExponentialCoordinate.of(MetricCoordinate.of(InversePowerVariogram.of(2)), 5)), //
  METRIC_08(ExponentialCoordinate.of(MetricCoordinate.of(InversePowerVariogram.of(2)), 8)), //
  AFFINE_00(ExponentialCoordinate.of(AffineCoordinate.INSTANCE, 0)), //
  AFFINE_01(ExponentialCoordinate.of(AffineCoordinate.INSTANCE, 1)), //
  AFFINE_02(ExponentialCoordinate.of(AffineCoordinate.INSTANCE, 2)), //
  AFFINE_03(ExponentialCoordinate.of(AffineCoordinate.INSTANCE, 3)), //
  AFFINE_05(ExponentialCoordinate.of(AffineCoordinate.INSTANCE, 5)), //
  AFFINE_10(ExponentialCoordinate.of(AffineCoordinate.INSTANCE, 10)), //
  AFFINE_20(ExponentialCoordinate.of(AffineCoordinate.INSTANCE, 20)), //
  AFFINE_30(ExponentialCoordinate.of(AffineCoordinate.INSTANCE, 30)), //
  AFFINE_50(ExponentialCoordinate.of(AffineCoordinate.INSTANCE, 50)), //
  ;

  private final Genesis genesis;

  private ExponentialCoordinates(Genesis genesis) {
    this.genesis = genesis;
  }

  @Override // from LogWeighting
  public TensorUnaryOperator operator( //
      Biinvariant biinvariant, // <- ignored
      VectorLogManifold vectorLogManifold, // with 2 dimensional tangent space
      ScalarUnaryOperator variogram, // <- ignored
      Tensor sequence) {
    return WeightingOperators.wrap( //
        HsCoordinates.wrap(vectorLogManifold, InsideConvexHullCoordinate.of(genesis)), //
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

  public Genesis genesis() {
    return genesis;
  }
}
