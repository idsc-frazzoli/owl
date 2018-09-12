// code by jph
package ch.ethz.idsc.owl.data.tree;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.HeuristicFunction;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class NodesTest extends TestCase {
  public void testGetParent() {
    HeuristicFunction hf = new HeuristicFunction() {
      @Override
      public Scalar minCostToGoal(Tensor x) {
        return RealScalar.ZERO;
      }
    };
    GlcNode node1 = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2, 3), RealScalar.ZERO), hf);
    GlcNode node2 = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2, 3), RealScalar.ZERO), hf);
    node1.insertEdgeTo(node2);
    assertEquals(Nodes.getParent(node2, 0), node2);
    assertEquals(Nodes.getParent(node2, 1), node1);
    assertEquals(Nodes.getParent(node2, 2), node1);
  }

  public void testFail() {
    try {
      Nodes.rootFrom(null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    try {
      Nodes.listFromRoot(null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    try {
      Nodes.listToRoot(null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
