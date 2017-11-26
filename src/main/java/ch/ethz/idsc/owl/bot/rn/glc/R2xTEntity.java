// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.Collection;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** 
 * 
 */
/* package */ class R2xTEntity extends R2Entity {
  private final Scalar delay;

  public R2xTEntity(Tensor state, Scalar delay) {
    super(state);
    represent_entity = StateTime::joined;
    this.delay = delay;
  }

  // TODO not sure what is a good approach here:
  private static final Tensor WEIGHT = Tensors.vector(1.0, 1.0, 0.2);

  @Override
  protected Scalar distance(Tensor x, Tensor y) {
    Tensor d = x.subtract(y);
    return d.pmul(WEIGHT).dot(d).Get();
  }

  @Override
  public Scalar delayHint() {
    return delay;
  }

  @Override
  public TrajectoryPlanner createTrajectoryPlanner(TrajectoryRegionQuery obstacleQuery, Tensor goal) {
    TrajectoryPlanner trajectoryPlanner = super.createTrajectoryPlanner(obstacleQuery, goal);
    trajectoryPlanner.represent = StateTime::joined;
    return trajectoryPlanner;
  }

  @Override
  protected Tensor eta() {
    return Tensors.vector(8, 8, 4);
  }

  @Override
  Collection<Flow> createControls() {
    /** 36 corresponds to 10[Degree] resolution */
    Collection<Flow> collection = super.createControls();
    collection.add(r2Config.stayPut()); // <- does not go well with min-dist cost function
    return collection;
  }
}
