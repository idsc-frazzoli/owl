// code by jph
package ch.ethz.idsc.owl.data.nd;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class EuclideanNdCenterTest extends TestCase {
  public void testSerializable() throws Exception {
    NdCenterInterface ndCenterInterface = EuclideanNdCenter.of(Tensors.vector(1, 2, 3));
    Serialization.copy(ndCenterInterface);
  }
}
