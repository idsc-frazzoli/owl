// code by jph
package ch.ethz.idsc.owl.bot.esp;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.DiscreteIntegrator;
import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ class EspDemo {
  static final Tensor START = Tensors.of( //
      Tensors.vector(2, 2, 2, 0, 0), //
      Tensors.vector(2, 2, 2, 0, 0), //
      Tensors.vector(2, 2, 0, 1, 1), //
      Tensors.vector(0, 0, 1, 1, 1), //
      Tensors.vector(0, 0, 1, 1, 1), //
      Tensors.vector(2, 2) //
  ).unmodifiable();
  private final EspFrame espFrame = new EspFrame();

  public EspDemo() {
    espFrame.timerFrame.configCoordinateOffset(100, 400);
    espFrame.setVisible(500, 500);
  }

  List<StateTime> compute() {
    PlannerConstraint plannerConstraint = EmptyObstacleConstraint.INSTANCE;
    // ---
    TrajectoryPlanner trajectoryPlanner = StandardTrajectoryPlanner.create( //
        EspStateTimeRaster.INSTANCE, //
        new DiscreteIntegrator(EspModel.INSTANCE), //
        EspFlows.INSTANCE, //
        plannerConstraint, //
        EspGoalAdapter.INSTANCE);
    trajectoryPlanner.insertRoot(new StateTime(START, RealScalar.ZERO));
    while (espFrame.timerFrame.jFrame.isVisible()) {
      {
        Optional<GlcNode> optional = trajectoryPlanner.getBest();
        if (optional.isPresent()) {
          GlcNode glcNode = optional.get();
          // if (print)
          {
            System.out.println(glcNode.state());
            System.out.println("BEST IN GOAL");
            System.out.println("$=" + glcNode.costFromRoot());
          }
          return GlcNodes.getPathFromRootTo(glcNode);
        }
      }
      Optional<GlcNode> optional = trajectoryPlanner.pollNext();
      if (optional.isPresent()) {
        Collection<GlcNode> queue = trajectoryPlanner.getQueue();
        Map<Tensor, GlcNode> domainMap = trajectoryPlanner.getDomainMap();
        GlcNode nextNode = optional.get();
        espFrame._board = nextNode.state();
        trajectoryPlanner.expand(nextNode);
        System.out.println(String.format("#=%3d   q=%3d   $=%3s", domainMap.size(), queue.size(), nextNode.costFromRoot()));
      } else { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        return null;
      }
    }
    return null;
  }

  public static void main(String[] args) throws IOException {
    EspDemo espDemo = new EspDemo();
    List<StateTime> list = espDemo.compute();
    if (Objects.nonNull(list))
      Export.object(HomeDirectory.file("esp.object"), list);
  }
}
