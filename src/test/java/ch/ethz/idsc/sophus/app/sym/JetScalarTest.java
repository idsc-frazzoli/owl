// code by jph
package ch.ethz.idsc.sophus.app.sym;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Sin;
import ch.ethz.idsc.tensor.sca.Sinh;
import ch.ethz.idsc.tensor.sca.Sqrt;
import junit.framework.TestCase;

public class JetScalarTest extends TestCase {
  public void testMultiply() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2));
    Scalar s2 = JetScalar.of(Tensors.vector(2, 3, -1));
    Scalar scalar = s1.multiply(s2);
    JetScalar audiScalar = (JetScalar) scalar;
    assertEquals(audiScalar.vector(), Tensors.vector(8, 14, 6));
  }

  public void testReciprocal() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2));
    Scalar reciprocal = s1.reciprocal();
    assertEquals(((JetScalar) reciprocal).vector(), Tensors.fromString("{1/4, -1/16, -3/32}"));
    Scalar neutral = s1.multiply(reciprocal);
    assertEquals(((JetScalar) neutral).vector(), UnitVector.of(3, 0));
  }

  public void testPower() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2, -3));
    Scalar scalar = Power.of(s1, 5);
    JetScalar audiScalar = (JetScalar) scalar;
    assertEquals(audiScalar.vector(), Tensors.vector(1024, 1280, 3840, 4800));
  }

  public void testScalar() {
    Scalar s1 = JetScalar.of(RealScalar.of(3), 4);
    JetScalar audiScalar = (JetScalar) s1;
    assertEquals(audiScalar.vector(), Tensors.vector(3, 1, 0, 0));
  }

  public void testNegate() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2, -3));
    Scalar s2 = RealScalar.of(3);
    JetScalar audiScalar = (JetScalar) s2.multiply(s1);
    assertEquals(audiScalar.vector(), Tensors.vector(12, 3, 6, -9));
  }

  public void testSqrt() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 1, -3));
    JetScalar scalar = (JetScalar) Sqrt.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), Tensors.vector(2, 0.5, 0.125, -0.84375));
  }

  public void testExp() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Exp.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), //
        Tensors.vector(54.598150033144236, 109.19630006628847, 218.39260013257694, 272.9907501657212));
  }

  public void testLog() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Log.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), Tensors.vector(1.3862943611198906, 0.5, -0.25, -0.5));
  }

  public void testSin() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Sin.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), //
        Tensors.vector(-0.7568024953079282, -1.3072872417272239, 3.027209981231713, 7.190079829499732));
  }

  public void testSinh() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Sinh.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), //
        Tensors.vector(27.28991719712775, 54.61646567203297, 109.159668788511, 136.54116418008243));
  }

  public void testScalarFail() {
    try {
      JetScalar.of(RealScalar.of(2));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testMatrixFail() {
    try {
      JetScalar.of(HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
