// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ArrayQ;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class GokartPoseDataTest extends TestCase {
  public void testSimple() {
    List<String> list = GokartPoseDataV2.INSTANCE.list();
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
