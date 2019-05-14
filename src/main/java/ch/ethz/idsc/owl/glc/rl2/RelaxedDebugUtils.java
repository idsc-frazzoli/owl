// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Collection;
import java.util.Iterator;

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.adapter.DebugUtils;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.tensor.Tensor;

public enum RelaxedDebugUtils {
  ;
  /** Throws an exception if the number of nodes in the trajectory planner is not the same as the number of nodes in the domain queues of the domain map.
   * 
   * @param rlTrajectoryPlanner */
  public static void nodeAmountCompare(RelaxedTrajectoryPlanner rlTrajectoryPlanner) {
    if (!rlTrajectoryPlanner.getBestOrElsePeek().isPresent())
      throw new RuntimeException("Queue is emtpy");
    DebugUtils.nodeAmountCompare( //
        Nodes.rootFrom(rlTrajectoryPlanner.getBestOrElsePeek().get()), //
        rlTrajectoryPlanner.getNodesInDomainQueueMap().size());
  }

  /** Checks how many elements within one domain queue are similiar to numerically similar (merit of nodes) to each other.
   * 
   * @param rlPlanner */
  public static void closeMatchesCheck(RelaxedTrajectoryPlanner rlPlanner) {
    Iterator<RelaxedPriorityQueue> iterator = rlPlanner.getRelaxedDomainQueueMap().values().iterator();
    while (iterator.hasNext()) {
      RelaxedPriorityQueue rlDomainQueue = iterator.next();
      System.out.println(System.getProperty("line.separator"));
      System.out.println("Number of elements in domain queue: " + rlDomainQueue.collection().size());
      rlDomainQueue.collection().stream().forEach(x -> System.out.println(x.merit()));
      Tensor bestMerit = VectorScalars.vector(rlDomainQueue.peekBest().merit());
      System.out.println("Number of elements similar to best: " + StaticHelper.numberEquals(rlDomainQueue));
      // rlDomainQueue.collection().stream().filter(a -> VectorScalars.vector(a.merit()).subtract(bestMerit).stream() //
      // .map(Scalar.class::cast).allMatch(v -> Scalars.lessThan(v.abs(), RationalScalar.of(1, 100)))).forEach(x -> System.out.println(x.merit()));
    }
  }

  /** Checks if all nodes of the global queue are contained in any queue of the domain map.
   * 
   * @param rlTrajectoryPlanner */
  public static void globalQueueSubsetOfQueuesInDomainMap(RelaxedTrajectoryPlanner rlTrajectoryPlanner) {
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
