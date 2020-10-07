// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.sophus.gbc.Genesis;
import ch.ethz.idsc.sophus.gbc.HsCoordinates;
import ch.ethz.idsc.sophus.gbc.InsidePolygonCoordinate;
import ch.ethz.idsc.sophus.gbc.MetricCoordinate;
import ch.ethz.idsc.sophus.gbc.TargetCoordinate;
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
  ITERATIVE_MEAN_VALUE_1(IterativeCoordinate.meanValue(1)), //
  ITERATIVE_MEAN_VALUE_2(IterativeCoordinate.meanValue(2)), //
  ITERATIVE_MEAN_VALUE_3(IterativeCoordinate.meanValue(3)), //
  ITERATIVE_MEAN_VALUE_5(IterativeCoordinate.meanValue(5)), //
  WACHSPRESS(ThreePointCoordinate.of(Barycenter.WACHSPRESS)), //
  DISCRETE_HARMONIC(ThreePointCoordinate.of(Barycenter.DISCRETE_HARMONIC)), //
  INVERSE_DISTANCE(MetricCoordinate.of(InversePowerVariogram.of(2))), //
  ITERATIVE_INVERSE_DISTANCE_0(IterativeCoordinate.of(MetricCoordinate.affine(), 0)), //
  ITERATIVE_INVERSE_DISTANCE_1(IterativeCoordinate.of(MetricCoordinate.affine(), 1)), //
  ITERATIVE_INVERSE_DISTANCE_2(IterativeCoordinate.of(MetricCoordinate.affine(), 2)), //
  ITERATIVE_INVERSE_DISTANCE_3(IterativeCoordinate.of(MetricCoordinate.affine(), 3)), //
  ITERATIVE_INVERSE_DISTANCE_5(IterativeCoordinate.of(MetricCoordinate.affine(), 5)), //
  TARGET(TargetCoordinate.of(InversePowerVariogram.of(2))), //
  ITERATIVE_TARGET_0(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 0)), //
  ITERATIVE_TARGET_1(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 1)), //
  ITERATIVE_TARGET_2(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 2)), //
  ITERATIVE_TARGET_3(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 3)), //
  ITERATIVE_TARGET_5(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 5)), //
  ;

  public static final Genesis[] GENESIS = { //
      ThreePointCoordinate.of(Barycenter.MEAN_VALUE), //
      MetricCoordinate.affine(), //
      TargetCoordinate.of(InversePowerVariogram.of(2)), //
  };
  private final Genesis genesis;

  private PolygonCoordinates(Genesis genesis) {
    this.genesis = genesis;
  }

  @Override // from LogWeighting
  public TensorUnaryOperator operator( //
      Biinvariant biinvariant, // <- ignored
      VectorLogManifold vectorLogManifold, // with 2 dimensional tangent space
      ScalarUnaryOperator variogram, // <- ignored
      Tensor sequence) {
    return WeightingOperators.wrap( //
        HsCoordinates.wrap(vectorLogManifold, InsidePolygonCoordinate.of(genesis)), //
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
