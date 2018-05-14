// code by jph
package ch.ethz.idsc.owl.math;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ArrayQ;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class DegreeTest extends TestCase {
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
