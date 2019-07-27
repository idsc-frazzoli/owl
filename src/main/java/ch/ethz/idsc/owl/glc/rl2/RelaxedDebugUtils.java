// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.data.tree.NodesAssert;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.UserName;

public enum RelaxedDebugUtils {
  ;
  private static final boolean PRINT = !(UserName.is("travis") || UserName.is("datahaki"));

  public static List<GlcNode> allNodes(RelaxedTrajectoryPlanner relaxedTrajectoryPlanner) {
    return allNodes(relaxedTrajectoryPlanner.getRelaxedDomainQueueMap().getMap().values());
  }

  /** @param collection of RelaxedPriorityQueue's
   * @return collection of all GlcNodes managed by the given collection of RelaxedPriorityQueue's */
  private static List<GlcNode> allNodes(Collection<RelaxedPriorityQueue> collection) {
    return collection.stream() //
        .map(RelaxedPriorityQueue::collection) //
        .flatMap(Collection::stream) //
        .collect(Collectors.toList());
  }

  /** Throws an exception if the number of nodes in the trajectory planner is not
   * the same as the number of nodes in the domain queues of the domain map.
   * 
   * @param relaxedTrajectoryPlanner */
  public static void nodeAmountCompare(RelaxedTrajectoryPlanner relaxedTrajectoryPlanner) {
    if (!relaxedTrajectoryPlanner.getBestOrElsePeek().isPresent())
      throw new RuntimeException("Queue is emtpy");
    NodesAssert.check( //
        Nodes.rootFrom(relaxedTrajectoryPlanner.getBestOrElsePeek().get()), //
        allNodes(relaxedTrajectoryPlanner).size());
  }

  /** Checks how many elements within one domain queue are similar to numerically
   * similar (merit of nodes) to each other.
   * 
   * @param relaxedTrajectoryPlanner */
  public static void closeMatchesCheck(RelaxedTrajectoryPlanner relaxedTrajectoryPlanner) {
    for (RelaxedPriorityQueue relaxedPriorityQueue : relaxedTrajectoryPlanner.getRelaxedDomainQueueMap().getMap().values()) {
      if (PRINT) {
        System.out.println(System.getProperty("line.separator"));
        System.out.println("Number of elements in domain queue: " + relaxedPriorityQueue.collection().size());
        relaxedPriorityQueue.collection().stream().forEach(glcNode -> System.out.println("merit=" + glcNode.merit() + "\n" + "state=" + glcNode.state()));
      }
      Tensor bestMerit = VectorScalars.vector(relaxedPriorityQueue.peekBest().merit());
      if (PRINT)
        System.out.println("Number of elements similar to best: " + StaticHelper.numberEquals(relaxedPriorityQueue));
      relaxedPriorityQueue.collection().stream().filter(a -> VectorScalars.vector(a.merit()).subtract(bestMerit).stream() //
          .map(Scalar.class::cast).allMatch(v -> Scalars.lessThan(v.abs(), RationalScalar.of(1, 100)))).forEach(x -> System.out.println(x.merit()));
    }
  }

  /** Checks if all nodes of the global queue are contained in any queue of the domain map.
   * 
   * @param relaxedTrajectoryPlanner */
  public static void globalQueueSubsetOfQueuesInDomainMap(RelaxedTrajectoryPlanner relaxedTrajectoryPlanner) {
    Collection<GlcNode> globalUnexpandedNodes = relaxedTrajectoryPlanner.getQueue();
    Collection<GlcNode> nodesInDomainMapQueues = allNodes(relaxedTrajectoryPlanner);
    if (!nodesInDomainMapQueues.containsAll(globalUnexpandedNodes))
      throw new RuntimeException("Some nodes in global queue are not present in queues of domain map!");
    if (PRINT) {
      System.out.println("All nodes in global queue are contained within domain map queues.");
      System.out.println("Nodes in global queue: " + globalUnexpandedNodes.size());
      System.out.println("Nodes in queues of domain map: " + nodesInDomainMapQueues.size());
    }
  }
}
