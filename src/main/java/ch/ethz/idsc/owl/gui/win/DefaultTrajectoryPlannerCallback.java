// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.adapter.GlcNodes;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.AnimationInterface;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.gui.ani.RrtsPlannerCallback;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsPlanner;

@Deprecated
public class DefaultTrajectoryPlannerCallback implements //
    GlcPlannerCallback, RrtsPlannerCallback {
  public AnimationInterface controllable;

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    // etaRender.setEta(trajectoryPlanner.getEta());
    Optional<GlcNode> optional = GlcNodes.getFinalGoalNode(trajectoryPlanner);
    // test without heuristic
    if (optional.isPresent()) {
      List<TrajectorySample> trajectory = new ArrayList<>();
      if (controllable instanceof TrajectoryEntity) {
        TrajectoryEntity abstractEntity = (TrajectoryEntity) controllable;
        List<TrajectorySample> tail = //
            GlcTrajectories.detailedTrajectoryTo(trajectoryPlanner.getStateIntegrator(), optional.get());
        // Optional<GlcNode> temp = trajectoryPlanner.getBestOrElsePeek();
        // List<StateTime> tempList = GlcNodes.getPathFromRootTo(temp.get());
        // System.out.println("Root is: " + tempList.get(0).toInfoString());
        // System.out.println("TAIL: <<<<<<<");
        // Trajectories.print(tail);
        trajectory = Trajectories.glue(head, tail);
        abstractEntity.setTrajectory(trajectory);
      } else {
        System.err.println("controllable not set");
      }
      // trajectoryRender.setTrajectory(trajectory);
    } else {
      System.err.println("NO TRAJECTORY BETWEEN ROOT TO GOAL");
    }
    // goalRender.fromStateTimeCollector(trajectoryPlanner.getGoalInterface());
    // treeRender.setCollection(new ArrayList<>(trajectoryPlanner.getDomainMap().values()));
    // no repaint
  }

  @Override
  public void expandResult(List<TrajectorySample> head, RrtsPlanner rrtsPlanner, List<TrajectorySample> tail) {
    List<TrajectorySample> trajectory = new ArrayList<>();
    if (controllable instanceof TrajectoryEntity) {
      TrajectoryEntity abstractEntity = (TrajectoryEntity) controllable;
      trajectory = Trajectories.glue(head, tail);
      abstractEntity.setTrajectory(trajectory);
    }
    // trajectoryRender.setTrajectory(trajectory);
    if (rrtsPlanner.getBest().isPresent()) {
      RrtsNode root = Nodes.rootFrom(rrtsPlanner.getBest().get());
      Collection<RrtsNode> collection = Nodes.ofSubtree(root);
      // treeRender.setCollection(collection);
    }
  }
}
