// code by jph
package ch.ethz.idsc.sophus.lie.so3;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.mat.Orthogonalize;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class So3ExponentialFailTest extends TestCase {
  public void testOrthPassFormatFailEye() {
    Scalar one = RealScalar.ONE;
    Tensor eyestr = Tensors.matrix((i, j) -> i.equals(j) ? one : one.zero(), 3, 4);
    try {
      So3Exponential.logMatrix(eyestr);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testOrthPassFormatFail2() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 3, 5);
    Tensor orthog = Orthogonalize.of(matrix);
    assertTrue(OrthogonalMatrixQ.of(orthog));
    try {
      So3Exponential.logMatrix(orthog);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFail() {
    try {
      So3Exponential.INSTANCE.exp(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      So3Exponential.INSTANCE.exp(Tensors.vector(0, 0));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      So3Exponential.INSTANCE.exp(Tensors.vector(0, 0, 0, 0));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testLogTrash() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 3, 3);
    try {
      So3Exponential.INSTANCE.log(matrix);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testLogFail() {
    try {
      So3Exponential.logMatrix(Array.zeros(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      So3Exponential.logMatrix(Array.zeros(3, 4));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      So3Exponential.logMatrix(Array.zeros(3, 3, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
