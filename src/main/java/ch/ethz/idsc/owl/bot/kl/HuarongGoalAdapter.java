// code by jph
package ch.ethz.idsc.owl.bot.kl;

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
import ch.ethz.idsc.tensor.red.Norm;

/* package */ enum HuarongGoalAdapter implements GoalInterface {
  INSTANCE;
  // ---
  @Override
  public Scalar minCostToGoal(Tensor x) {
    return Norm._1.between(x.get(0).extract(1, 3), Tensors.vector(4, 2));
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
  public boolean isMember(StateTime x) {
    return HuarongGoalRegion.INSTANCE.isMember(x.state());
  }
}
