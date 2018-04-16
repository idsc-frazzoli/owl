// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import java.util.List;

import ch.ethz.idsc.owl.bot.rn.RnNodeCollection;
import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.math.sample.CircleRandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.adapter.RrtsNodes;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsPlanner;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Norm;

class NoiseCircleHelper {
  private final StateTime tail;
  private final RrtsNode root;
  private final TransitionRegionQuery obstacleQuery;
  private final RrtsPlanner rrtsPlanner;
  private final RnTransitionSpace rnts;
  List<TrajectorySample> trajectory = null;

  NoiseCircleHelper(TransitionRegionQuery obstacleQuery, StateTime tail, Tensor goal) {
    this.tail = tail;
    Tensor orig = tail.state();
    Scalar radius = Norm._2.between(goal, orig).multiply(RealScalar.of(0.5)).add(RealScalar.ONE);
    final Tensor center = Mean.of(Tensors.of(orig, goal));
    Tensor min = center.map(s -> s.subtract(radius));
    Tensor max = center.map(s -> s.add(radius));
    rnts = new RnTransitionSpace();
    RrtsNodeCollection nc = new RnNodeCollection(min, max);
    // obstacleQuery = StaticHelper.noise1();
    this.obstacleQuery = obstacleQuery;
    // ---
    // int iters =
    Rrts rrts = new DefaultRrts(rnts, nc, obstacleQuery, LengthCostFunction.IDENTITY);
    root = rrts.insertAsNode(orig, 5).get();
    RandomSampleInterface spaceSampler = new CircleRandomSample(center, radius);
    RandomSampleInterface goalSampler = new CircleRandomSample(goal, RealScalar.of(0.5));
    rrtsPlanner = new RrtsPlanner(rrts, spaceSampler, goalSampler);
  }

  void plan(int steps) {
    Expand.steps(rrtsPlanner, steps);
    // System.out.println("found " + rrtsPlanner.getBest().isPresent());
    // System.out.println("iterations =" + iters);
    // System.out.println("rewireCount=" + rrts.rewireCount());
    RrtsNodes.costConsistency(root, rnts, LengthCostFunction.IDENTITY);
    if (rrtsPlanner.getBest().isPresent()) {
      System.out.println("Trajectory to goal region:");
      RrtsNode best = rrtsPlanner.getBest().get();
      List<RrtsNode> sequence = Nodes.listFromRoot(best);
      // magic const
      trajectory = RnFlowTrajectory.createTrajectory(rnts, sequence, tail.time(), RealScalar.of(0.1));
    }
  }

  RrtsNode getRoot() {
    return root;
  }

  TransitionRegionQuery getObstacleQuery() {
    return obstacleQuery;
  }

  RrtsPlanner getRrtsPlanner() {
    return rrtsPlanner;
  }
}
