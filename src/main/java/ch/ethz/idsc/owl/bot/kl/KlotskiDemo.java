// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
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
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

/* package */ class KlotskiDemo {
  public static final File FOLDER_SOLUTIONS = HomeDirectory.Documents("klotski");
  private final KlotskiProblem klotskiProblem;
  private final KlotskiFrame klotskiFrame;

  public KlotskiDemo(KlotskiProblem klotskiProblem) {
    this.klotskiProblem = klotskiProblem;
    klotskiFrame = new KlotskiFrame(klotskiProblem);
    klotskiFrame.timerFrame.configCoordinateOffset(100, 500);
    klotskiFrame.setVisible(700, 700);
  }

  KlotskiSolution compute() {
    PlannerConstraint plannerConstraint = //
        RegionConstraints.timeInvariant(KlotskiObstacleRegion.fromSize(klotskiProblem.size()));
    // ---
    TableBuilder tableBuilder = new TableBuilder();
    CTrajectoryPlanner standardTrajectoryPlanner = StandardTrajectoryPlanner.create( //
        klotskiProblem.stateTimeRaster(), //
        new DiscreteIntegrator(KlotskiModel.INSTANCE), //
        new KlotskiFlows(klotskiProblem), //
        plannerConstraint, //
        new KlotskiGoalAdapter(klotskiProblem.getGoal()));
    standardTrajectoryPlanner.insertRoot(new StateTime(klotskiProblem.startState(), RealScalar.ZERO));
    int expandCount = 0;
    while (true) {
      {
        Optional<GlcNode> optional = standardTrajectoryPlanner.getBest();
        if (optional.isPresent()) {
          GlcNode glcNode = optional.get();
          System.out.println("BEST IN GOAL $=" + glcNode.costFromRoot());
          return new KlotskiSolution( //
              klotskiProblem, //
              GlcNodes.getPathFromRootTo(glcNode), //
              tableBuilder.getTable());
        }
      }
      Optional<GlcNode> optional = standardTrajectoryPlanner.pollNext();
      if (optional.isPresent()) {
        Collection<GlcNode> queue = standardTrajectoryPlanner.getQueue();
        Map<Tensor, GlcNode> domainMap = standardTrajectoryPlanner.getDomainMap();
        GlcNode nextNode = optional.get();
        {
          klotskiFrame._board = nextNode.state();
          tableBuilder.appendRow(Tensors.vector(expandCount, domainMap.size(), queue.size(), nextNode.costFromRoot().number().intValue()));
        }
        // System.out.println(String.format("#=%5d q=%3d $=%3s", domainMap.size(), queue.size(), nextNode.costFromRoot()));
        standardTrajectoryPlanner.expand(nextNode);
        ++expandCount;
      } else { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        return null;
      }
    }
  }

  public void close() {
    klotskiFrame.timerFrame.close();
  }

  public static File solutionFile(KlotskiProblem klotskiProblem) {
    FOLDER_SOLUTIONS.mkdir();
    return new File(FOLDER_SOLUTIONS, klotskiProblem.name() + ".object");
  }

  public static void main(String[] args) throws IOException {
    KlotskiProblem klotskiProblem = Huarong.RED_DONKEY.create();
    KlotskiDemo klotskiDemo = new KlotskiDemo(klotskiProblem);
    KlotskiSolution klotskiSolution = klotskiDemo.compute();
    Export.object(solutionFile(klotskiProblem), klotskiSolution);
    klotskiDemo.close();
    KlotskiPlot.export(klotskiSolution);
  }
}
