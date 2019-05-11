// code by jl
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public enum DebugUtils {
  ;
  // ---
  // function for convenience
  public static final void nodeAmountCompare(RelaxedTrajectoryPlanner rlTrajectoryPlanner) {
    if (!rlTrajectoryPlanner.getBestOrElsePeek().isPresent())
      throw new RuntimeException("Queue is emtpy");
    nodeAmountCompare( //
        Nodes.rootFrom(rlTrajectoryPlanner.getBestOrElsePeek().get()), //
        rlTrajectoryPlanner.getNodesInDomainQueueMap().size());
  }

  // ---
  private static final void nodeAmountCompare(GlcNode best, int size) {
    final GlcNode root = Nodes.rootFrom(best);
    if (size != Nodes.ofSubtree(root).size()) {
      System.out.println("****NODE CHECK****");
      System.out.println("Nodes in DomainMap: " + size);
      System.out.println("Nodes in SubTree from Node: " + Nodes.ofSubtree(best).size());
      throw new RuntimeException();
    }
    System.out.println("Nodes in DomainMap: " + size);
    System.out.println("Nodes in SubTree from Node: " + Nodes.ofSubtree(best).size());
  }

  public static final void connectivityCheck(Collection<GlcNode> treeCollection) {
    Iterator<GlcNode> iterator = treeCollection.iterator();
    while (iterator.hasNext()) {
      GlcNode node = iterator.next();
      if (!node.isRoot())
        GlobalAssert.that(node.parent().children().contains(node));
    }
  }

  public static final void noExpandedNodesInGlobalQueueCheck(Collection<GlcNode> globalQueue) {
    Iterator<GlcNode> iterator = globalQueue.iterator();
    while (iterator.hasNext()) {
      if (!iterator.next().isLeaf())
        throw new RuntimeException("Not all elements in global queue are leafs!");
    }
  }

  public static final void closeMatchesCheck(RelaxedTrajectoryPlanner rlPlanner) {
    Iterator<RelaxedDomainQueue> iterator = rlPlanner.getRelaxedDomainQueueMap().values().iterator();
    while (iterator.hasNext()) {
      RelaxedDomainQueue rlDomainQueue = iterator.next();
      System.out.println(System.getProperty("line.separator"));
      System.out.println("Number of elements in domain queue: " + rlDomainQueue.collection().size());
      rlDomainQueue.collection().stream().forEach(x -> System.out.println(x.merit()));
      Tensor bestMerit = VectorScalars.vector(rlDomainQueue.peekBest().merit());
      System.out.println("Number of elements similar to best: " + StaticHelper.numberEquals(rlDomainQueue));
      // rlDomainQueue.collection().stream().filter(a -> VectorScalars.vector(a.merit()).subtract(bestMerit).stream() //
      // .map(Scalar.class::cast).allMatch(v -> Scalars.lessThan(v.abs(), RationalScalar.of(1, 100)))).forEach(x -> System.out.println(x.merit()));
    }
  }

  /** Checks if the Cost and the Heuristic along the found trajectory are consistent
   * 
   * @param trajectoryPlanner */
  public static final void heuristicConsistencyCheck(TrajectoryPlanner trajectoryPlanner) {
    Optional<GlcNode> finalNode = trajectoryPlanner.getBest();
    if (!finalNode.isPresent()) {
      System.out.println("No Final GoalNode, therefore no ConsistencyCheck");
      return;
    }
    List<GlcNode> trajectory = Nodes.listFromRoot(finalNode.get());
    // omit last Node, since last node may lie outside of goal region, as Trajectory to it was in
    connectivityCheck(trajectory);
    for (int i = 1; i < trajectory.size() - 1; i++) {
      GlcNode current = trajectory.get(i);
      GlcNode parent = current.parent();
      if (Scalars.lessEquals(current.costFromRoot(), parent.costFromRoot())) {
        System.err.println("At time " + current.stateTime().time() + " cost from root decreased from " + //
            parent.costFromRoot() + " to " + current.costFromRoot());
        StateTimeTrajectories.print(GlcNodes.getPathFromRootTo(finalNode.get()));
        throw new RuntimeException();
      }
      if (Scalars.lessEquals(current.merit(), parent.merit())) {
        System.err.println("At time " + current.stateTime().time() + " merit decreased from  " + //
            parent.merit() + " to " + current.merit());
        StateTimeTrajectories.print(GlcNodes.getPathFromRootTo(finalNode.get()));
        throw new RuntimeException();
      }
      // monotonously increasing merit means, that delta(Cost) >= delta(CostToGo)
      // as: Cost(Goal)== Merit(Goal) >= (Cost(Node) + CostToGo(Node)) = Merit (Node)
    }
  }

  public static final void globalQueueSubsetOfQueuesInDomainMap(RelaxedTrajectoryPlanner rlTrajectoryPlanner) {
    Collection<GlcNode> globalUnexpandedNodes = rlTrajectoryPlanner.getQueue();
    Collection<GlcNode> nodesInDomainMapQueues = rlTrajectoryPlanner.getNodesInDomainQueueMap();
    if (!nodesInDomainMapQueues.containsAll(globalUnexpandedNodes)) {
      throw new RuntimeException("Some nodes in global queue are not present in queues of domain map!");
    }
    System.out.println("All nodes in global queue are contained within domain map queues.");
    System.out.println("Nodes in global queue: " + globalUnexpandedNodes.size());
    System.out.println("Nodes in queues of domain map: " + nodesInDomainMapQueues.size());
  }
}
