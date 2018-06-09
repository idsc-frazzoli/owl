// code by ynager
package ch.ethz.idsc.owl.math;

import java.io.IOException;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.MachineNumberQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
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

  public void testMultiply() {
    Scalar a = VectorScalar.of(1, 2, 3);
    Scalar b = VectorScalar.of(0, 3, 6);
    try {
      a.multiply(b);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    Scalar c = Quantity.of(2, "Apples");
    Scalar d = a.multiply(c);
    assertEquals(d.toString(), "[2[Apples], 4[Apples], 6[Apples]]");
    // ---
    // the next expression gives [2, 4, 6][Apples]
    // which is not desired by cannot be prevented easily
    c.multiply(a);
  }

  public void testCommute() {
    Scalar a = VectorScalar.of(1, 2, 3);
    assertFalse(a.equals(null));
    Scalar b = RealScalar.of(4);
    assertEquals(a.multiply(b), b.multiply(a));
    assertFalse(a.equals(b));
    assertFalse(b.equals(a));
  }

  public void testString() {
    Scalar a = VectorScalar.of(Tensors.vector(1, -1, 2));
    assertEquals(a.toString(), "[1, -1, 2]");
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    Scalar a = VectorScalar.of(1, 2, 3);
    Scalar b = Serialization.copy(a);
    assertEquals(a, b);
    assertEquals(b, a);
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
