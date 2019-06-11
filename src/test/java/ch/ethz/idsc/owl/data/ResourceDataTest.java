// code by jph
package ch.ethz.idsc.owl.data;

import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.sophus.app.data.GokartPoseData;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ArrayQ;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class ResourceDataTest extends TestCase {
  public void testSimple() {
    List<String> list = GokartPoseData.INSTANCE.list();
    assertTrue(50 < list.size());
  }

  public void testResourceTensor() {
    Tensor tensor = ResourceData.of("/colorscheme/aurora.csv"); // resource in tensor
    Objects.requireNonNull(tensor);
    assertTrue(ArrayQ.of(tensor));
  }

  public void testResourceOwl() {
    Tensor tensor = ResourceData.of("/io/delta_free.png"); // resource in owl
    Objects.requireNonNull(tensor);
    assertTrue(ArrayQ.of(tensor));
  }
}
