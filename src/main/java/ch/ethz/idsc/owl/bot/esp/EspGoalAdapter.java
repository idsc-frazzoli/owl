// code by jph
package ch.ethz.idsc.owl.bot.esp;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class EspGoalAdapter implements GoalInterface {
  static final Tensor GOAL = Tensors.of( //
      Tensors.vector(1, 1, 1, 0, 0), //
      Tensors.vector(1, 1, 1, 0, 0), //
      Tensors.vector(1, 1, 0, 2, 2), //
      Tensors.vector(0, 0, 2, 2, 2), //
      Tensors.vector(0, 0, 2, 2, 2), //
      Tensors.vector(2, 2) //
  ).unmodifiable();

  public static GoalInterface standard() {
    return new EspGoalAdapter(GOAL);
  }

  private final Tensor goal;

  private EspGoalAdapter(Tensor goal) {
    this.goal = goal;
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    // if (x instanceof Scalar)
    // return DoubleScalar.POSITIVE_INFINITY;
    // Scalar p0 = Norm._1.between(x.get(0).extract(0, 3), Tensors.vector(1, 1, 1));
    // Scalar p1 = Norm._1.between(x.get(1).extract(0, 3), Tensors.vector(1, 1, 1));
    // Scalar p2 = Norm._1.between(x.get(2).extract(0, 2), Tensors.vector(1, 1));
    // return p0.add(p1).add(p2);
    return RealScalar.ZERO;
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return RealScalar.ONE;
  }

  @Override
  public Optional<StateTime> firstMember(List<StateTime> trajectory) {
    return trajectory.stream().filter(this::isMember).findFirst();
  }

  @Override
  public boolean isMember(StateTime element) {
    return goal.equals(element.state());
  }
}
