// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.util.Objects;

import ch.ethz.idsc.tensor.DeterminateScalarQ;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Mod;

/* package */ class GridLines implements TensorScalarFunction {
  private static final Mod MOD = Mod.function(1.0);
  private static final Clip CLIP = Clips.positive(0.25);
  // ---
  private final TensorUnaryOperator tensorUnaryOperator;

  public GridLines(TensorUnaryOperator tensorUnaryOperator) {
    this.tensorUnaryOperator = Objects.requireNonNull(tensorUnaryOperator);
  }

  @Override
  public Scalar apply(Tensor point) {
    for (Tensor _scalar : tensorUnaryOperator.apply(point)) {
      Scalar scalar = (Scalar) _scalar;
      if (DeterminateScalarQ.of(scalar)) {
        if (CLIP.isInside(MOD.apply(scalar)))
          return RealScalar.ZERO;
      } else
        return DoubleScalar.INDETERMINATE;
    }
    return RealScalar.ONE;
  }
}