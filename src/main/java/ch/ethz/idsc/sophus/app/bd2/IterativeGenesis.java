// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.util.stream.Stream;

import ch.ethz.idsc.sophus.gbc.AffineCoordinate;
import ch.ethz.idsc.sophus.hs.HsDesign;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.math.Genesis;
import ch.ethz.idsc.sophus.ply.d2.Barycenter;
import ch.ethz.idsc.sophus.ply.d2.IterativeCoordinateLevel;
import ch.ethz.idsc.sophus.ply.d2.ThreePointWeighting;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;

/* package */ enum IterativeGenesis {
  MEAN_VALUE(ThreePointWeighting.of(Barycenter.MEAN_VALUE)), //
  INVERSE_DISTANCE(AffineCoordinate.INSTANCE), //
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
