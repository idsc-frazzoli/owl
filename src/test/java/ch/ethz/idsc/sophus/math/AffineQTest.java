// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class AffineQTest extends TestCase {
  public void testRequire() {
    AffineQ.require(Tensors.vector(0.5, 0.5));
    AffineQ.require(Tensors.vector(0.25, 0.25, 0.25, 0.25));
    AffineQ.require(Tensors.vector(1, 0));
    AffineQ.require(Tensors.vector(-0.5, 1.5));
    AffineQ.require(Tensors.vector(-0.25, 0.75, 0.25, 0.25));
    AffineQ.require(Tensors.vector(1, -2, 2));
  }

  public void testRequirePositive() {
    AffineQ.requirePositiveOrZero(Tensors.vector(0.5, 0.5));
    AffineQ.requirePositiveOrZero(Tensors.vector(0.25, 0.25, 0.25, 0.25));
    AffineQ.requirePositiveOrZero(Tensors.vector(1, 0));
  }

  public void testFail() {
    try {
      AffineQ.requirePositiveOrZero(Tensors.vector(2, -1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFail2() {
    try {
      AffineQ.requirePositiveOrZero(Tensors.vector(1, 1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailScalar() {
    try {
      AffineQ.require(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      AffineQ.requirePositiveOrZero(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailMatrix() {
    try {
      AffineQ.require(HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      AffineQ.requirePositiveOrZero(HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
