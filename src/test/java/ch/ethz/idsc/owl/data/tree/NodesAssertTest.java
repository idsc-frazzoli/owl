// code by jph
package ch.ethz.idsc.owl.data.tree;

import java.util.Collections;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class NodesAssertTest extends TestCase {
  public void testAllLeaf() {
    GlcNode glcNode = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2), RealScalar.ONE), x -> RealScalar.ZERO);
    NodesAssert.allLeaf(Collections.singletonList(glcNode));
  }
}
