// code by jl
package ch.ethz.idsc.owl.glc.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.adapter.GlcNodes;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.tensor.Scalars;

public enum DebugUtils {
  ;
  // ---
  // function for convenience
  public static final void nodeAmountCompare(TrajectoryPlanner trajectoryPlanner) {
    nodeAmountCompare( //
        Nodes.rootFrom(trajectoryPlanner.getBestOrElsePeek().get()), //
        trajectoryPlanner.domainMap().size());
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
  }

  public static final void connectivityCheck(Collection<GlcNode> treeCollection) {
    Iterator<GlcNode> iterator = treeCollection.iterator();
    while (iterator.hasNext()) {
      GlcNode node = iterator.next();
      if (!node.isRoot())
        GlobalAssert.that(node.parent().children().contains(node));
    }
    if (treeCollection instanceof List<?>) {
      GlobalAssert.that(((List<GlcNode>) treeCollection).get(0).isRoot());
      for (int i = 1; i < treeCollection.size(); i++) {
        GlcNode node = ((List<GlcNode>) treeCollection).get(i);
        GlcNode previous = ((List<GlcNode>) treeCollection).get(i - 1);
        GlobalAssert.that(node.parent() == previous);
      }
    }
  }

  /** Checks if the Cost and the Heuristic along the found trajectory are consistent
   * 
   * @param trajectoryPlanner */
  public static final void heuristicConsistencyCheck(TrajectoryPlanner trajectoryPlanner) {
    Optional<GlcNode> finalNode = trajectoryPlanner.getFinalGoalNode();
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
}
