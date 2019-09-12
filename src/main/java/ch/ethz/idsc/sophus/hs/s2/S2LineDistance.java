// code by jph
package ch.ethz.idsc.sophus.hs.s2;

import java.util.Optional;

import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.VectorAngle;

/* package */ class S2LineDistance implements TensorNorm {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);
  // ---
  private final Tensor cross;

  public S2LineDistance(Tensor p, Tensor q) {
    cross = NORMALIZE.apply(Cross.of(p, q));
  }

  @Override
  public Scalar norm(Tensor tensor) {
    Optional<Scalar> optional = VectorAngle.of(cross, tensor);
    return Pi.HALF.subtract(optional.get()).abs();
  }
}