// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.sophus.gbc.HsCoordinates;
import ch.ethz.idsc.sophus.gbc.InsidePolygonCoordinate;
import ch.ethz.idsc.sophus.gbc.MetricCoordinate;
import ch.ethz.idsc.sophus.gbc.TargetCoordinate;
import ch.ethz.idsc.sophus.gbc.ZeroCoordinate;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.IterativeCoordinate;
import ch.ethz.idsc.sophus.lie.r2.ThreePointCoordinate;
import ch.ethz.idsc.sophus.math.var.InversePowerVariogram;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum PolygonCoordinates implements LogWeighting {
  MEAN_VALUE(ThreePointCoordinate.of(Barycenter.MEAN_VALUE)), //
  WACHSPRESS(ThreePointCoordinate.of(Barycenter.WACHSPRESS)), //
  DISCRETE_HARMONIC(ThreePointCoordinate.of(Barycenter.DISCRETE_HARMONIC)), //
  MV1(IterativeCoordinate.usingMeanValue(1)), //
  MV2(IterativeCoordinate.usingMeanValue(2)), //
  MV3(IterativeCoordinate.usingMeanValue(3)), //
  MV5(IterativeCoordinate.usingMeanValue(5)), //
  M20(MetricCoordinate.of(InversePowerVariogram.of(2))), //
  M00(IterativeCoordinate.of(MetricCoordinate.affine(), 0)), //
  M01(IterativeCoordinate.of(MetricCoordinate.affine(), 1)), //
  M02(IterativeCoordinate.of(MetricCoordinate.affine(), 2)), //
  M03(IterativeCoordinate.of(MetricCoordinate.affine(), 3)), //
  M05(IterativeCoordinate.of(MetricCoordinate.affine(), 5)), //
  TA0(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 0)), //
  TA1(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 1)), //
  TA2(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 2)), //
  TA3(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 3)), //
  TA5(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 5)), //
  ;

  private final ZeroCoordinate tensorUnaryOperator;

  private PolygonCoordinates(ZeroCoordinate tensorUnaryOperator) {
    this.tensorUnaryOperator = tensorUnaryOperator;
  }

  @Override // from LogWeighting
  public TensorUnaryOperator operator( //
      Biinvariant biinvariant, // <- ignored
      VectorLogManifold vectorLogManifold, // with 2 dimensional tangent space
      ScalarUnaryOperator variogram, // <- ignored
      Tensor sequence) {
    return WeightingOperators.wrap( //
        HsCoordinates.wrap(vectorLogManifold, InsidePolygonCoordinate.of(tensorUnaryOperator)), //
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
