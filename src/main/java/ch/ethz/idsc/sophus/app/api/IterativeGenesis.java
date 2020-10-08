// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.stream.Stream;

import ch.ethz.idsc.sophus.gbc.Genesis;
import ch.ethz.idsc.sophus.gbc.MetricCoordinate;
import ch.ethz.idsc.sophus.hs.HsDesign;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.IterativeCoordinateLevel;
import ch.ethz.idsc.sophus.lie.r2.ThreePointCoordinate;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;

public enum IterativeGenesis {
  MEAN_VALUE(ThreePointCoordinate.of(Barycenter.MEAN_VALUE)), //
  INVERSE_DISTANCE(MetricCoordinate.affine()), //
  // INVERSE_LEVERAGE(TargetCoordinate.of(InversePowerVariogram.of(2))), //
  ;

  private final Genesis genesis;

  private IterativeGenesis(Genesis genesis) {
    this.genesis = genesis;
  }

  public TensorScalarFunction with(int max) {
    return IterativeCoordinateLevel.of(genesis, Chop._08, max);
  }

  public static TensorUnaryOperator counts(VectorLogManifold vectorLogManifold, Tensor sequence, int max) {
    HsDesign hsDesign = new HsDesign(vectorLogManifold);
    TensorScalarFunction[] array = Stream.of(values()).map(ig -> ig.with(max)).toArray(TensorScalarFunction[]::new);
    return point -> {
      Tensor matrix = hsDesign.matrix(sequence, point);
      return Tensor.of(Stream.of(array).map(ig -> ig.apply(matrix)));
    };
  }
}
