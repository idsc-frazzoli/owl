// code by jph
package ch.ethz.idsc.owl.rrts.core;

import java.io.Serializable;

public interface TransitionRegionQuery extends Serializable {
  /** @param transition
   * @return true, if the transition does not intersect this region */
  boolean isDisjoint(Transition transition);
}
