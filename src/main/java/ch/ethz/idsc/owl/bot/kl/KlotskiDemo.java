// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.DiscreteIntegrator;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.CTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ class KlotskiDemo {
  private final KlotskiProblem klotskiProblem;
  private final KlotskiFrame klotskiFrame;

  public KlotskiDemo(KlotskiProblem klotskiProblem) {
    this.klotskiProblem = klotskiProblem;
    klotskiFrame = new KlotskiFrame(klotskiProblem);
    klotskiFrame.timerFrame.configCoordinateOffset(100, 500);
    klotskiFrame.setVisible(700, 700);
  }

  List<StateTime> compute() {
    // List<Tensor> controls = KlotskiControls.of(klotskiProblem.getStones());
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(KlotskiObstacleRegion.fromSize(klotskiProblem.size()));
    // ---
    CTrajectoryPlanner standardTrajectoryPlanner = StandardTrajectoryPlanner.create( //
        KlotskiStateTimeRaster.INSTANCE, //
        new DiscreteIntegrator(KlotskiModel.INSTANCE), //
        new KlotskiFlows(klotskiProblem), //
        plannerConstraint, //
        new KlotskiGoalAdapter(klotskiProblem.getGoal()));
    standardTrajectoryPlanner.insertRoot(new StateTime(klotskiProblem.getState(), RealScalar.ZERO));
    while (true) {
      {
        Optional<GlcNode> optional = standardTrajectoryPlanner.getBest();
        if (optional.isPresent()) {
          GlcNode glcNode = optional.get();
          {
            System.out.println(glcNode.state());
            System.out.println("BEST IN GOAL");
            System.out.println("$=" + glcNode.costFromRoot());
          }
          return GlcNodes.getPathFromRootTo(glcNode);
        }
      }
      Optional<GlcNode> optional = standardTrajectoryPlanner.pollNext();
      if (optional.isPresent()) {
        Collection<GlcNode> queue = standardTrajectoryPlanner.getQueue();
        Map<Tensor, GlcNode> domainMap = standardTrajectoryPlanner.getDomainMap();
        GlcNode nextNode = optional.get();
        {
          klotskiFrame._board = nextNode.state();
        }
        standardTrajectoryPlanner.expand(nextNode);
        // if (print)
        System.out.println(String.format("#=%3d   q=%3d   $=%3s", domainMap.size(), queue.size(), nextNode.costFromRoot()));
      } else { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        return null;
      }
    }
  }

  public void close() {
    klotskiFrame.timerFrame.close();
  }

  public static void main(String[] args) throws IOException {
    KlotskiProblem klotskiProblem = Huarong.AMBUSH.create();
    // Pennant.PUZZLE.create();
    KlotskiDemo klotskiDemo = new KlotskiDemo(klotskiProblem);
    List<StateTime> list = klotskiDemo.compute();
    Export.object(HomeDirectory.file(klotskiProblem.name() + ".object"), list);
    klotskiDemo.close();
    KlotskiPlot.export(klotskiProblem, list);
  }
}
