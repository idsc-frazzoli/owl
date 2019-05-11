// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Collections;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class DebugUtilsTest extends TestCase {
  public void testAllLeaf() {
    GlcNode glcNode = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2), RealScalar.ONE), x -> RealScalar.ZERO);
    DebugUtils.assertAllLeaf(Collections.singletonList(glcNode));
  }
}
