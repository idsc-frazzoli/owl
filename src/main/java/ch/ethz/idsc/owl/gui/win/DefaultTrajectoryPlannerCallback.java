// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.gui.ani.AnimationInterface;
import ch.ethz.idsc.owl.gui.ani.RrtsPlannerCallback;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsPlanner;

@Deprecated
class DefaultTrajectoryPlannerCallback implements RrtsPlannerCallback {
  public AnimationInterface controllable;

  @Override
  public void expandResult(List<TrajectorySample> head, RrtsPlanner rrtsPlanner, List<TrajectorySample> tail) {
    List<TrajectorySample> trajectory = new ArrayList<>();
    if (controllable instanceof TrajectoryEntity) {
      TrajectoryEntity abstractEntity = (TrajectoryEntity) controllable;
      trajectory = Trajectories.glue(head, tail);
      abstractEntity.trajectory(trajectory);
    }
    // trajectoryRender.setTrajectory(trajectory);
    if (rrtsPlanner.getBest().isPresent()) {
      RrtsNode root = Nodes.rootFrom(rrtsPlanner.getBest().get());
      // Collection<RrtsNode> collection =
      Nodes.ofSubtree(root);
      // treeRender.setCollection(collection);
    }
  }
}
