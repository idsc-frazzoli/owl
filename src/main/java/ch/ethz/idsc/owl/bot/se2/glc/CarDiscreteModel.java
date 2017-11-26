package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.CarFlows;
import ch.ethz.idsc.owl.bot.se2.CarForwardFlows;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.subare.core.DiscreteModel;
import ch.ethz.idsc.subare.core.TerminalInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.lie.Permutations;
import ch.ethz.idsc.tensor.sca.Rationalize;

public class CarDiscreteModel implements DiscreteModel, TerminalInterface {
  public static final Tensor COLLISION = Tensors.vector(0).unmodifiable();
  // ---
  private final Tensor states;
  private final Tensor actions;
  public final int resolution;

  public CarDiscreteModel(int resolution) {
    states = Permutations.of(Range.of(0, resolution)).append(COLLISION).unmodifiable();
    CarFlows carFlows = new CarForwardFlows(RealScalar.of(1), RealScalar.of(2));
    Collection<Flow> collection = carFlows.getFlows(6);
    actions = Tensor.of(collection.stream() //
        .map(Flow::getU) //
        .map(u -> Rationalize.of(u, 100)) //
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
}
