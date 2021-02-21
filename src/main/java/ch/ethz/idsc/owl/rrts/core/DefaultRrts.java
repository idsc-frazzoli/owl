// code by jph
package ch.ethz.idsc.owl.rrts.core;

import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** class implements the capability to build an rrts tree.
 * 
 * "Sampling-based algorithms for optimal motion planning"
 * by Sertac Karaman and Emilio Frazzoli
 * 
 * <p>the class does not require the concept of a sampler, or goal region.
 * @see DefaultRrtsPlanner */
public class DefaultRrts implements Rrts {
  private final TransitionSpace transitionSpace;
  private final RrtsNodeCollection nodeCollection;
  private final TransitionRegionQuery obstacleQuery;
  private final TransitionCostFunction transitionCostFunction;
  private int rewireCount = 0;

  public DefaultRrts( //
      TransitionSpace transitionSpace, //
      RrtsNodeCollection rrtsNodeCollection, //
      TransitionRegionQuery obstacleQuery, //
      TransitionCostFunction transitionCostFunction) {
    this.transitionSpace = transitionSpace;
    this.nodeCollection = rrtsNodeCollection;
    this.obstacleQuery = obstacleQuery;
    this.transitionCostFunction = transitionCostFunction;
  }

  @Override // from Rrts
  public Optional<RrtsNode> insertAsNode(Tensor state, int k_nearest) {
    return insertAsNode(state, k_nearest, false);
  }

  /* package */ Optional<RrtsNode> insertAsNode(Tensor state, int k_nearest, boolean ignoreCheck) {
    // the collision check available to class works on transitions, but not on states
    // that means no sanity collision check on state is carried out inside function insertAsNode
    int size = nodeCollection.size();
    if (size == 0) {
      RrtsNode rrtsNode = RrtsNode.createRoot(state, RealScalar.ZERO); // TODO JPH/GJOEL units?
      nodeCollection.insert(rrtsNode);
      return Optional.of(rrtsNode);
    }
    if (ignoreCheck || isInsertPlausible(state)) { // TODO GJOEL/JPH is this needed?
      k_nearest = Math.min(Math.max(1, k_nearest), size);
      Optional<RrtsNode> optional = connectAlongMinimumCost(state, k_nearest);
      if (optional.isPresent()) {
        RrtsNode rrtsNode = optional.get();
        rewireAround(rrtsNode, k_nearest); // first: rewire
        nodeCollection.insert(rrtsNode); // second: insert to collection
        return Optional.of(rrtsNode);
      }
      System.err.println("Unable to connect " + state);
    }
    return Optional.empty();
  }

  // TODO GJOEL/JPH probably remove
  private boolean isInsertPlausible(Tensor state) {
    RrtsNode nearest = nodeCollection.nearTo(state, 1).iterator().next();
    return isCollisionFree(transitionSpace.connect(nearest.state(), state));
  }

  private Optional<RrtsNode> connectAlongMinimumCost(Tensor state, int k_nearest) {
    /* RrtsNode parent = null;
     * Scalar costFromRoot = null;
     * for (RrtsNode node : nodeCollection.nearTo(state, k_nearest)) {
     * Transition transition = transitionSpace.connect(node.state(), state);
     * Scalar cost = transitionCostFunction.cost(transition);
     * Scalar compare = node.costFromRoot().add(cost);
     * if (Objects.isNull(costFromRoot) || Scalars.lessThan(compare, costFromRoot))
     * if (isCollisionFree(transition)) {
     * parent = node;
     * costFromRoot = compare;
     * }
     * }
     * if (Objects.nonNull(parent))
     * return Optional.of(parent.connectTo(state, costFromRoot)); */
    final NavigableMap<Scalar, RrtsNode> updates = new TreeMap<>(Scalars::compare);
    nodeCollection.nearFrom(state, k_nearest).stream()
        // .parallel()
        .forEach(node -> {
          Transition transition = transitionSpace.connect(node.state(), state);
          Scalar cost = transitionCostFunction.cost(node, transition);
          Scalar compare = node.costFromRoot().add(cost);
          synchronized (updates) {
            if (updates.isEmpty() || Scalars.lessThan(compare, updates.firstKey()))
              if (isCollisionFree(transition))
                updates.put(compare, node);
          }
        });
    if (!updates.isEmpty())
      return Optional.of(updates.firstEntry().getValue().connectTo(state, updates.firstKey()));
    return Optional.empty();
  }

  @Override // from Rrts
  public final void rewireAround(RrtsNode parent, int k_nearest) {
    for (RrtsNode child : nodeCollection.nearFrom(parent.state(), k_nearest)) {
      Transition transition = transitionSpace.connect(parent.state(), child.state());
      Scalar costFromParent = transitionCostFunction.cost(parent, transition);
      if (Scalars.lessThan(parent.costFromRoot().add(costFromParent), child.costFromRoot()) && // reduce costs
          isCollisionFree(transition)) {
        parent.rewireTo(child, costFromParent); // , transitionCostFunction.influence());
        ++rewireCount;
      }
    }
  }

  @Override // from Rrts
  public int rewireCount() {
    return rewireCount;
  }

  // private helper function
  private boolean isCollisionFree(Transition transition) {
    return obstacleQuery.isDisjoint(transition);
  }

  /* package */ TransitionRegionQuery getObstacleQuery() {
    return obstacleQuery;
  }
}
