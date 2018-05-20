// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.bot.util.Lexicographic;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.subare.core.DiscreteModel;
import ch.ethz.idsc.subare.core.TerminalInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Ordering;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.lie.Permutations;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Rationalize;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class CarDiscreteModel implements DiscreteModel, TerminalInterface {
  public static final Tensor COLLISION = Tensors.vector(0).unmodifiable();
  // ---
  private final Tensor states;
  private final Tensor actions;
  public final int resolution;

  public CarDiscreteModel(int resolution) {
    states = Tensors.empty();
    for (Tensor perm : Permutations.of(Range.of(0, resolution))) {
      Tensor pair = represent(perm);
      if (pair.Get(1).equals(RealScalar.ONE)) {
        states.append(pair.get(0));
      }
    }
    states.append(COLLISION);
    FlowsInterface carFlows = CarFlows.forward(RealScalar.of(1), RealScalar.of(2));
    Collection<Flow> collection = carFlows.getFlows(6);
    ScalarUnaryOperator suo = Rationalize.withDenominatorLessEquals(100);
    actions = Tensor.of(collection.stream() //
        .map(Flow::getU) //
        .map(u -> u.map(suo)) //
    ).unmodifiable();
    this.resolution = resolution;
  }

  @Override // from DiscreteModel
  public Tensor states() {
    return states;
  }

  @Override // from DiscreteModel
  public Tensor actions(Tensor state) {
    return isTerminal(state) ? Array.zeros(1, 3) : actions;
  }

  @Override // from DiscreteModel
  public Scalar gamma() {
    return RealScalar.of(.98);
  }

  @Override // from TerminalInterface
  public boolean isTerminal(Tensor state) {
    return state.equals(COLLISION);
  }

  public static Tensor represent(Tensor range) {
    if (Chop.NONE.allZero(range))
      return Tensors.of(CarDiscreteModel.COLLISION, RealScalar.ONE);
    Tensor tensor = Tensors.vectorInt(Ordering.DECREASING.of(range));
    Tensor revrse = Reverse.of(tensor);
    int cmp = Lexicographic.COMPARATOR.compare(tensor, revrse);
    if (1 == cmp)
      return Tensors.of(revrse, RealScalar.ONE.negate());
    return Tensors.of(tensor, RealScalar.ONE);
  }
}
