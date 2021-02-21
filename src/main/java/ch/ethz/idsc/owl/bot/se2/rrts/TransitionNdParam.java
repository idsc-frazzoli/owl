// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ref.FieldIntegerQ;

public class TransitionNdParam {
  private static final Tensor LBOUNDS = Tensors.vector(-5, -5).unmodifiable();
  private static final Tensor UBOUNDS = Tensors.vector(+5, +5).unmodifiable();

  @FieldIntegerQ
  public Scalar points = RealScalar.of(100);
  @FieldIntegerQ
  public Scalar connect = RealScalar.of(3);
  
  /** @param not used */
  TransitionNdContainer config() {
    return new TransitionNdContainer( //
        LBOUNDS, UBOUNDS, //
        Scalars.intValueExact(points), //
        Scalars.intValueExact(connect));
  }

}
