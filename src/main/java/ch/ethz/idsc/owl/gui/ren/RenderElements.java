// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.core.CTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.math.state.StateTimeCollector;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;

public enum RenderElements {
  ;
  public static Collection<RenderInterface> create(TrajectoryPlanner trajectoryPlanner) {
    List<RenderInterface> list = new LinkedList<>();
    list.add(AxesRender.INSTANCE);
    // ---
    if (trajectoryPlanner instanceof CTrajectoryPlanner) {
      CTrajectoryPlanner cTrajectoryPlanner = (CTrajectoryPlanner) trajectoryPlanner;
      StateTimeRaster stateTimeRaster = cTrajectoryPlanner.getStateTimeRaster();
      if (stateTimeRaster instanceof EtaRaster) {
        EtaRaster etaRaster = (EtaRaster) stateTimeRaster;
        list.add(new EtaRender(etaRaster.eta()));
        list.add(new DomainRender(trajectoryPlanner.getDomainMap(), etaRaster.eta()));
      }
    }
    list.add(new QueueRender(trajectoryPlanner.getQueue()));
    list.add(new TreeRender().setCollection(trajectoryPlanner.getDomainMap().values()));
    // {
    // TrajectoryRegionQuery trq = trajectoryPlanner.getHeuristicFunction();
    // if (trq instanceof StateTimeCollector)
    // list.add(new GoalRender(((StateTimeCollector) trq).getMembers()));
    // if (trq instanceof StateTimeCollector)
    // list.add(new GoalRender(((StateTimeCollector) trq).getMembers()));
    // }
    {
      Optional<GlcNode> goalNode = trajectoryPlanner.getBest();
      if (goalNode.isPresent()) {
        List<TrajectorySample> trajectory = GlcTrajectories.detailedTrajectoryTo( //
            trajectoryPlanner.getStateIntegrator(), goalNode.get());
        TrajectoryRender trajectoryRender = new TrajectoryRender();
        trajectoryRender.trajectory(trajectory);
        list.add(trajectoryRender);
      }
    }
    list.add(new HudRender(trajectoryPlanner));
    return list;
  }

  public static RenderInterface create(StateTimeRaster stateTimeRaster) {
    if (stateTimeRaster instanceof EtaRaster) {
      EtaRaster etaRaster = (EtaRaster) stateTimeRaster;
      return new EtaRender(etaRaster.eta());
    }
    return null;
  }

  // public static RenderInterface create(PlannerConstraint plannerConstraint) {
  // if (plannerConstraint instanceof TrajectoryObstacleConstraint) {
  // TrajectoryRegionQuery trajectoryRegionQuery = //
  // ((TrajectoryObstacleConstraint) plannerConstraint).getTrajectoryRegionQuery();
  // if (trajectoryRegionQuery instanceof StateTimeCollector)
  // return new ObstacleRender(((StateTimeCollector) trajectoryRegionQuery).getMembers());
  // }
  // return null;
  // }
  public static Collection<RenderInterface> create( //
      Collection<? extends StateCostNode> collection, TransitionRegionQuery transitionRegionQuery) {
    List<RenderInterface> list = new LinkedList<>();
    list.add(AxesRender.INSTANCE);
    if (transitionRegionQuery instanceof StateTimeCollector)
      list.add(new ObstacleRender(((StateTimeCollector) transitionRegionQuery).getMembers()));
    list.add(new TreeRender().setCollection(collection));
    return list;
  }
}
