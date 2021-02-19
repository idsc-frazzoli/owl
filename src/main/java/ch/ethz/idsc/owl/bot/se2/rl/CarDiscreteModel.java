// code by jph
package ch.ethz.idsc.owl.bot.se2.rl;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.glc.Se2CarFlows;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.subare.core.DiscreteModel;
import ch.ethz.idsc.subare.core.TerminalInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.ext.Integers;
import ch.ethz.idsc.tensor.lie.Permutations;
import ch.ethz.idsc.tensor.num.Rationalize;
import ch.ethz.idsc.tensor.sca.Factorial;

/* package */ class CarDiscreteModel implements DiscreteModel, TerminalInterface {
  // TODO should depend on time!
  private static final Scalar DISCOUNT_FACTOR = RealScalar.of(0.98);
  // ---
  private final Tensor states;
  private final Tensor actions;
  public final int resolution;

  /** Hint: the model will have (resolution!)/2 + 1 states
   * 
   * @param scans */
  public CarDiscreteModel(int scans, int flows) {
    Integers.requirePositive(scans);
    states = Tensors.reserve(Scalars.intValueExact(Factorial.of(scans).multiply(RationalScalar.HALF)) + 1);
    for (Tensor perm : Permutations.of(Range.of(0, scans))) { // for instance perm = {2, 0, 1, 4, 3}
      Tensor pair = ScanToState.of(perm);
      if (pair.Get(1).equals(RealScalar.ONE))
        states.append(pair.get(0));
    }
    states.append(ScanToState.COLLISION);
    FlowsInterface flowsInterface = Se2CarFlows.forward(RealScalar.of(1), RealScalar.of(3));
    Collection<Tensor> collection = flowsInterface.getFlows(flows);
    ScalarUnaryOperator suo = Rationalize.withDenominatorLessEquals(100);
    actions = Tensor.of(collection.stream() //
        .map(u -> u.map(suo)) //
    ).unmodifiable();
    System.out.println(actions);
    this.resolution = scans;
  }

  @Override // from DiscreteModel
  public Tensor states() {
    return states;
  }

  @Override // from DiscreteModel
  public Tensor actions(Tensor state) {
    return isTerminal(state) //
        ? Array.zeros(1, 3)
        : actions;
  }

  @Override // from DiscreteModel
  public Scalar gamma() {
    return DISCOUNT_FACTOR;
  }

  @Override // from TerminalInterface
  public boolean isTerminal(Tensor state) {
    return state.equals(ScanToState.COLLISION);
  }
}
