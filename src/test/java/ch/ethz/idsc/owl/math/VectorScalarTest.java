// code by ynager
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.MachineNumberQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class VectorScalarTest extends TestCase {
  public void testOne() {
    Scalar a = VectorScalar.of(1, -1, 2);
    assertEquals(a.abs(), VectorScalar.of(Tensors.vector(1, 1, 2)));
    assertEquals(a.add(VectorScalar.of(Tensors.vector(0, 1, 0))), VectorScalar.of(Tensors.vector(1, 0, 2)));
    assertEquals(a.divide(RealScalar.of(2)), VectorScalar.of(Tensors.vector(0.5, -0.5, 1)));
    assertEquals(a.zero(), VectorScalar.of(Tensors.vector(0, 0, 0)));
    assertEquals(((VectorScalar) a).vector().length(), 3);
    // ---
    a = VectorScalar.of(Tensors.vector(0.00001, 0.00005, 0));
    assertEquals(((VectorScalar) a).chop(Chop._04), VectorScalar.of(Tensors.vector(0, 0, 0)));
    // ---
    a = VectorScalar.of(Tensors.of(RealScalar.ONE, DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(MachineNumberQ.of(a));
    assertFalse(ExactScalarQ.of(a));
    a = VectorScalar.of(Tensors.of(RealScalar.ONE, DoubleScalar.ONE));
    assertTrue(ExactScalarQ.of(a));
    // ---
    Scalar v1 = VectorScalar.of(Tensors.vector(1, 6, 1));
    Scalar v2 = VectorScalar.of(Tensors.vector(1, 5, 10));
    assertEquals(Scalars.compare(v1, v2), Integer.compare(1, 0));
  }

  public void testString() {
    Scalar a = VectorScalar.of(Tensors.vector(1, -1, 2));
    assertEquals(a.toString(), "[1, -1, 2]");
  }

  public void testFail() {
    try {
      VectorScalar.of(Tensors.empty()).number();
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    try {
      VectorScalar.of(Tensors.empty().add(RealScalar.ONE));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailNested() {
    Scalar a = VectorScalar.of(Tensors.vector(1, -1, 2));
    try {
      VectorScalar.of(Tensors.of(RealScalar.ONE, a));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailScalar() {
    try {
      VectorScalar.of(RealScalar.ONE);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
