// code by jph
package ch.ethz.idsc.owl.bot.se2.rl;

import ch.ethz.idsc.owl.math.order.VectorLexicographic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Ordering;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.sca.Chop;

/* package */ enum ScanToState {
  ;
  // TODO possibly switch to empty {} instead of {0}
  public static final Tensor COLLISION = Tensors.vector(0).unmodifiable();

  /** @param range
   * @return {ordering, parity +1 or -1} */
  public static Tensor of(Tensor range) {
    if (Chop.NONE.allZero(range))
      return Tensors.of(COLLISION, RealScalar.ONE);
    Tensor tensor = Tensors.vectorInt(Ordering.DECREASING.of(range));
    Tensor revrse = Reverse.of(tensor);
    return VectorLexicographic.COMPARATOR.compare(tensor, revrse) <= 0 //
        ? Tensors.of(tensor, RealScalar.ONE)
        : Tensors.of(revrse, RealScalar.ONE.negate());
  }
}
