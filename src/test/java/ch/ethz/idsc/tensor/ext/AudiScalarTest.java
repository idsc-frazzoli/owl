// code by jph
package ch.ethz.idsc.tensor.ext;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.sca.Power;
import junit.framework.TestCase;

public class AudiScalarTest extends TestCase {
  public void testMultiply() {
    Scalar s1 = AudiScalar.of(Tensors.vector(4, 1, 2));
    Scalar s2 = AudiScalar.of(Tensors.vector(2, 3, -1));
    Scalar scalar = s1.multiply(s2);
    AudiScalar audiScalar = (AudiScalar) scalar;
    assertEquals(audiScalar.vector(), Tensors.vector(8, 14, 6));
  }

  public void testReciprocal() {
    Scalar s1 = AudiScalar.of(Tensors.vector(4, 1, 2));
    Scalar reciprocal = s1.reciprocal();
    assertEquals(((AudiScalar) reciprocal).vector(), Tensors.fromString("{1/4, -1/16, -3/32}"));
    Scalar neutral = s1.multiply(reciprocal);
    assertEquals(((AudiScalar) neutral).vector(), UnitVector.of(3, 0));
  }

  public void testPower() {
    Scalar s1 = AudiScalar.of(Tensors.vector(4, 1, 2, -3));
    Scalar scalar = Power.of(s1, 5);
    AudiScalar audiScalar = (AudiScalar) scalar;
    assertEquals(audiScalar.vector(), Tensors.vector(1024, 1280, 3840, 4800));
  }

  public void testScalar() {
    Scalar s1 = AudiScalar.of(RealScalar.of(3), 4);
    AudiScalar audiScalar = (AudiScalar) s1;
    assertEquals(audiScalar.vector(), Tensors.vector(3, 1, 0, 0));
  }

  public void testNegate() {
    Scalar s1 = AudiScalar.of(Tensors.vector(4, 1, 2, -3));
    Scalar s2 = RealScalar.of(3);
    AudiScalar audiScalar = (AudiScalar) s2.multiply(s1);
    assertEquals(audiScalar.vector(), Tensors.vector(12, 3, 6, -9));
  }

  public void testScalarFail() {
    try {
      AudiScalar.of(RealScalar.of(2));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testMatrixFail() {
    try {
      AudiScalar.of(HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
