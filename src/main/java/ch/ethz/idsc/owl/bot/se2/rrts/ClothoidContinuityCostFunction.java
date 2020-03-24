// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.AbsSquared;

/** abs squared of difference in curvature at common node
 * 
 * if the coordinate unit is [m], then the cost has unit [m^-2] */
public enum ClothoidContinuityCostFunction implements TransitionCostFunction {
  INSTANCE;

  @Override // from TransitionCostFunction
  public Scalar cost(RrtsNode rrtsNode, Transition transition) {
    if (rrtsNode.isRoot())
      // TODO GJOEL why not just return "rrtsNode.costFromRoot()"?
      return rrtsNode.costFromRoot().zero();
    return transitionCost(rrtsNode.parent().state(), rrtsNode.state(), transition.end());
  }

  static Scalar transitionCost(Tensor p, Tensor q, Tensor r) {
    return AbsSquared.between( //
        new Clothoid(p, q).new Curvature().tail(), //
        new Clothoid(q, r).new Curvature().head());
  }

  @Override // from TransitionCostFunction
  public int influence() {
    return 1;
  }
}
