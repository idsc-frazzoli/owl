// code by jph
package ch.ethz.idsc.owl.math;

import java.util.Objects;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ArrayQ;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class DegreeTest extends TestCase {
  public void testSimple() {
    assertEquals(Degree.of(360), DoubleScalar.of(Math.PI * 2));
  }

  public void testReciprocal() {
    Scalar rad = RealScalar.of(0.2617993877991494);
    Scalar r = Degree.of(15).reciprocal();
    assertEquals(r.multiply(rad), RealScalar.ONE);
  }

  public void testReciprocal10() {
    Scalar rad = RealScalar.of(0.17453292519943295);
    Scalar r = Degree.of(10);
    r = r.reciprocal();
    assertEquals(r.multiply(rad), RealScalar.ONE);
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
