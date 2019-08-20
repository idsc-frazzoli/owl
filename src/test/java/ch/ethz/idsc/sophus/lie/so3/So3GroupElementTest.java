// code by jph
package ch.ethz.idsc.sophus.lie.so3;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class So3GroupElementTest extends TestCase {
  public void testBlub() {
    Tensor orth = So3Exponential.INSTANCE.exp(Tensors.vector(-.2, .3, .1));
    Tensor matr = So3Exponential.INSTANCE.exp(Tensors.vector(+.1, .2, .3));
    So3GroupElement.of(orth).combine(matr);
    try {
      So3GroupElement.of(orth).combine(matr.add(matr));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testSimple() {
    So3GroupElement so3GroupElement = So3GroupElement.of(IdentityMatrix.of(3));
    so3GroupElement.inverse();
    try {
      so3GroupElement.combine(HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFail() {
    try {
      So3GroupElement.of(HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testSizeFail() {
    try {
      So3GroupElement.of(IdentityMatrix.of(4));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
