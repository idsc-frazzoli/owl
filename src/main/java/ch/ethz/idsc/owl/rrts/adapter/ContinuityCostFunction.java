// code by gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.util.Objects;

import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransition;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatios;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Abs;

public enum ContinuityCostFunction implements TransitionCostFunction {
  INSTANCE;
  private final static int ITERATIONS = 5;

  @Override
  public Scalar cost(RrtsNode rrtsNode, Transition transition) {
    if (!(transition instanceof ClothoidTransition))
      throw new IllegalArgumentException(transition.getClass().toString());
    if (Objects.isNull(rrtsNode.parent()))
      return RealScalar.ZERO;
    Scalar in = ClothoidTerminalRatios.head(rrtsNode.parent().state(), rrtsNode.state(), ITERATIONS);
    Scalar out = ClothoidTerminalRatios.tail(transition.start(), transition.end(), ITERATIONS);
    return Abs.of(out.subtract(in));
  }

  @Override
  public int influence() {
    return 1;
  }
}
