// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatios;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.AbsSquared;
import ch.ethz.idsc.tensor.sca.Chop;

public enum ClothoidContinuityCostFunction implements TransitionCostFunction {
  INSTANCE;
  // ---
  private final static int ITERATIONS = 4;

  @Override // from TransitionCostFunction
  public Scalar cost(RrtsNode rrtsNode, Transition transition) {
    ClothoidTransition clothoidTransition = (ClothoidTransition) transition;
    if (rrtsNode.isRoot())
      return rrtsNode.costFromRoot().zero();
    // TODO remove check once tested sufficiently
    Chop._12.requireClose(rrtsNode.state(), clothoidTransition.start());
    Scalar tail = ClothoidTerminalRatios.tail( //
        rrtsNode.parent().state(), //
        rrtsNode.state(), ITERATIONS);
    Scalar head = ClothoidTerminalRatios.head( //
        clothoidTransition.start(), // should coincide with rrtsNode.state()
        clothoidTransition.end(), ITERATIONS);
    // return Abs.FUNCTION.apply(head.subtract(tail));
    return AbsSquared.FUNCTION.apply(head.subtract(tail));
  }

  @Override // from TransitionCostFunction
  public int influence() {
    return 1;
  }
}
