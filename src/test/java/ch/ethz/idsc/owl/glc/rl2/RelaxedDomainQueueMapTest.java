// code by jph
package ch.ethz.idsc.owl.glc.rl2;

import java.io.IOException;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.math.VectorScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class RelaxedDomainQueueMapTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    RelaxedDomainQueueMap relaxedDomainQueueMap = Serialization.copy(new RelaxedDomainQueueMap(Tensors.vector(1, 1, 1, 1)));
    GlcNode glcNode = GlcNodes.createRoot(new StateTime(Tensors.vector(11, 2, 3), RealScalar.ZERO), x -> VectorScalar.of(1, 2, 3, 5));
    relaxedDomainQueueMap.addToDomainMap(Tensors.vector(1, 2), glcNode);
  }
}
