// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Clip;

/** TODO i don't understand what's the binary relation of: "Interval order"
 * https://en.wikipedia.org/wiki/Interval_order */
public enum ClipPartialComparator {
  ;
  /** reflexive
   * antisymmetric */
  public static final BinaryRelation<Clip> BINARY_RELATION = //
      (x, y) -> Scalars.lessEquals(x.min(), y.min()) && Scalars.lessEquals(x.max(), y.max());
  public static final PartialComparator<Clip> INSTANCE = PartialOrder.comparator(BINARY_RELATION);
}
