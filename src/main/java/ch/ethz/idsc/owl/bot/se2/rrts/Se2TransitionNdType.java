// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparator;
import ch.ethz.idsc.tensor.RealScalar;

/* package */ enum Se2TransitionNdType {
  /** clothoid curves */
  CLOTHOID(ClothoidTransitionSpace.INSTANCE), //
  /** dubins paths */
  DUBINS(DubinsTransitionSpace.of(RealScalar.of(0.4), DubinsPathComparator.LENGTH)), //
  /** straight lines in R^2 that results from ignoring heading */
  R2(RnTransitionSpace.INSTANCE);

  private final TransitionSpace transitionSpace;

  private Se2TransitionNdType(TransitionSpace transitionSpace) {
    this.transitionSpace = transitionSpace;
  }

  TransitionSpace transitionSpace() {
    return transitionSpace;
  }

  static Se2TransitionNdType fromString(String string) {
    switch (string) {
    case "Cl":
      return CLOTHOID;
    case "SE2":
      return DUBINS;
    case "R2":
      return R2;
    }
    throw new IllegalArgumentException();
  }
}