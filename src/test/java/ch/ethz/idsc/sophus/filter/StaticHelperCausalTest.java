//code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class StaticHelperCausalTest extends TestCase {
  public void testSimple() {
    Tensor mask = Tensors.vector(.2, .4, .9, .5, .5);
    Tensor correct = Tensors.vector(0.012, 0.003, 0.01, 0.225, 0.25, 0.5);
    Chop._12.requireClose(correct, StaticHelperCausal.splits(mask));
  }
}
