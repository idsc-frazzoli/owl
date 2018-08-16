// code by jph
package ch.ethz.idsc.owl.glc.rl;

import java.util.Collections;
import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class RLDomainQueueTest extends TestCase {
  public void testEmpty() {
    Optional<Tensor> optional = RLDomainQueue.getMinValues(Collections.emptyList(), 3);
    assertFalse(optional.isPresent());
  }
}
