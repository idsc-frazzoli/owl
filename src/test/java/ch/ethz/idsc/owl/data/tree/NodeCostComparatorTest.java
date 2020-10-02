// code by jph
package ch.ethz.idsc.owl.data.tree;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class NodeCostComparatorTest extends TestCase {
  public void testSimple() {
    GlcNode root = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2), RealScalar.ZERO), x -> RealScalar.ZERO);
    assertEquals(NodeCostComparator.INSTANCE.compare(root, root), 0);
  }

  public void testFail() {
    AssertFail.of(() -> 
      NodeCostComparator.INSTANCE.compare(null, null));
  }
}
