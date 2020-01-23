package ch.ethz.idsc.owl.bot.kl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

enum KlotskiDemo {
  ;
  public static void main(String[] args) throws IOException {
    // initial state
    Huarong huarong = Huarong.ANDROID;
    Tensor board = huarong.getBoard();
    StateTimeRaster stateTimeRaster = StateTime::state;
    List<Flow> controls = new ArrayList<>();
    for (int index = 0; index < board.length(); ++index) {
      controls.add(StateSpaceModels.createFlow(KlotskiModel.INSTANCE, Tensors.vector(index, -1, 0)));
      controls.add(StateSpaceModels.createFlow(KlotskiModel.INSTANCE, Tensors.vector(index, +1, 0)));
      controls.add(StateSpaceModels.createFlow(KlotskiModel.INSTANCE, Tensors.vector(index, 0, -1)));
      controls.add(StateSpaceModels.createFlow(KlotskiModel.INSTANCE, Tensors.vector(index, 0, +1)));
    }
    System.out.println("controls: " + controls.size());
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(HuarongObstacleRegion.INSTANCE);
    // ---
    StandardTrajectoryPlanner standardTrajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, //
        KlotskiIntegrator.INSTANCE, //
        controls, //
        plannerConstraint, //
        HuarongGoalAdapter.INSTANCE);
    standardTrajectoryPlanner.insertRoot(new StateTime(board, RealScalar.ZERO));
    while (true) {
      {
        Optional<GlcNode> optional = standardTrajectoryPlanner.getBest();
        if (optional.isPresent()) {
          System.out.println("BEST IN GOAL");
          GlcNode glcNode = optional.get();
          System.out.println(glcNode.state());
          System.out.println("$=" + glcNode.costFromRoot());
          List<StateTime> list = GlcNodes.getPathFromRootTo(glcNode);
          Export.object(HomeDirectory.file(huarong.name() + ".object"), list);
          break;
        }
      }
      Optional<GlcNode> optional = standardTrajectoryPlanner.pollNext();
      if (optional.isPresent()) {
        Collection<GlcNode> queue = standardTrajectoryPlanner.getQueue();
        Map<Tensor, GlcNode> domainMap = standardTrajectoryPlanner.getDomainMap();
        GlcNode nextNode = optional.get();
        // System.out.println(glcNode.costFromRoot());
        standardTrajectoryPlanner.expand(nextNode);
        for (Entry<Tensor, GlcNode> entry : domainMap.entrySet()) {
          GlcNode glcNode = entry.getValue();
          if (!glcNode.state().equals(entry.getKey())) {
            System.err.println("problem");
          }
        }
        // standardTrajectoryPlanner.getBestOrElsePeek();
        System.out.println(String.format("#=%3d   q=%3d   $=%3s", domainMap.size(), queue.size(), nextNode.costFromRoot()));
        // ++expandCount;
      } else { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        break;
      }
    }
    // Expand<GlcNode> expand = new Expand<>(standardTrajectoryPlanner);
    // expand.findAny(1000);
    // Optional<GlcNode> optional = standardTrajectoryPlanner.getBestOrElsePeek();
    // if (optional.isPresent()) {
    // GlcNode glcNode = optional.get();
    // System.out.println(glcNode.state());
    // }
  }
}
