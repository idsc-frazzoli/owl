// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Collections;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class StateTimeTrajectoriesTest extends TestCase {
  public void testSimple() {
    GlcNode glcNode = GlcNodes.createRoot( //
        new StateTime(Tensors.vector(1, 2), RealScalar.of(10)), //
        x -> RealScalar.ZERO);
    StateTime last = new StateTime(Tensors.vector(1, 2), RealScalar.of(15));
    Scalar dt = StateTimeTrajectories.timeIncrement(glcNode, Collections.singletonList(last));
    assertEquals(dt, RealScalar.of(5));
  }
}
