// code by jph
package ch.ethz.idsc.owl.rrts.core;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransitionSpace;
import ch.ethz.idsc.owl.bot.se2.rrts.DubinsTransitionSpace;
import ch.ethz.idsc.tensor.Tensor;

/** TransitionSpace is a factory for {@link Transition}s
 * 
 * An instance of TransitionSpace is immutable.
 * 
 * Examples:
 * @see RnTransitionSpace
 * @see DubinsTransitionSpace
 * @see ClothoidTransitionSpace */
@FunctionalInterface
public interface TransitionSpace {
  /** @param start state
   * @param end state
   * @return transition that represents the (unique) connection between the start and end state */
  Transition connect(Tensor start, Tensor end);
}
