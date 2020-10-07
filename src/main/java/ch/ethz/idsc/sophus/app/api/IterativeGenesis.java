// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.stream.Stream;

import ch.ethz.idsc.sophus.gbc.Genesis;
import ch.ethz.idsc.sophus.gbc.MetricCoordinate;
import ch.ethz.idsc.sophus.gbc.TargetCoordinate;
import ch.ethz.idsc.sophus.hs.HsDesign;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.IterativeCoordinateLevel;
import ch.ethz.idsc.sophus.lie.r2.ThreePointCoordinate;
import ch.ethz.idsc.sophus.math.var.InversePowerVariogram;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;

public enum IterativeGenesis {
  MEAN_VALUE(ThreePointCoordinate.of(Barycenter.MEAN_VALUE)), //
  INVERSE_DISTANCE(MetricCoordinate.affine()), //
  TARGET(TargetCoordinate.of(InversePowerVariogram.of(2))), //
  ;

  private final TensorScalarFunction tsf;

  private IterativeGenesis(Genesis genesis) {
    tsf = IterativeCoordinateLevel.of(genesis, Tolerance.CHOP, 32);
  }

  public static Tensor counts(VectorLogManifold vectorLogManifold, Tensor point, Tensor sequence) {
    Tensor matrix = new HsDesign(vectorLogManifold).matrix(sequence, point);
    return Tensor.of(Stream.of(values()).map(ig -> ig.tsf.apply(matrix)));
  }
}
