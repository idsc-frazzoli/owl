// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparator;
import ch.ethz.idsc.tensor.RealScalar;

enum Se2TransitionNdType {
  CLOTHOID(ClothoidTransitionSpace.INSTANCE), //
  DUBINS(DubinsTransitionSpace.of(RealScalar.of(0.4), DubinsPathComparator.LENGTH)), //
  R2(RnTransitionSpace.INSTANCE), //
  ;
  final TransitionSpace transitionSpace;

  private Se2TransitionNdType(TransitionSpace transitionSpace) {
    this.transitionSpace = transitionSpace;
  }
}