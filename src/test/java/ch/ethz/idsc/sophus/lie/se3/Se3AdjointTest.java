// code by jph
package ch.ethz.idsc.sophus.lie.se3;

import java.util.Arrays;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se3AdjointTest extends TestCase {
  public void testForwardInverse() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Tensor g = Se3Utils.toMatrix4x4(Rodrigues.exp(RandomVariate.of(distribution, 3)), RandomVariate.of(distribution, 3));
      TensorUnaryOperator se3Adjoint = Se3Adjoint.forward(g);
      Tensor u_w = RandomVariate.of(distribution, 2, 3);
      Tensor out = se3Adjoint.apply(u_w);
      assertEquals(Dimensions.of(out), Arrays.asList(2, 3));
      TensorUnaryOperator se3Inverse = Se3Adjoint.inverse(g);
      Tensor apply = se3Inverse.apply(out);
      Chop._11.requireClose(u_w, apply);
    }
  }

  public void testForwardInsteadInverse() {
    TensorUnaryOperator se3Adjoint = new Se3Adjoint(IdentityMatrix.of(3), Tensors.vector(0, 1, 0)); // "left rear wheel"
    Tensor tensor = se3Adjoint.apply(Tensors.of(Tensors.vector(1, 0, 1), Tensors.vector(1, 0, 1))); // more forward and turn left
    assertEquals(tensor, Tensors.fromString("{{2, 0, 0}, {1, 0, 1}}")); // only rotation
    ExactTensorQ.require(tensor);
  }

  public void testRotationSideLeft() {
    TensorUnaryOperator se3Adjoint = new Se3Adjoint(IdentityMatrix.of(3), Tensors.vector(0, 1, 0)); // "left rear wheel"
    Tensor tensor = se3Adjoint.apply(Tensors.of(Tensors.vector(1, 0, 0), Tensors.vector(0, 0, -1))); // more forward and turn right
    assertEquals(tensor, Tensors.fromString("{{0, 0, 0}, {0, 0, -1}}")); // only rotation
    ExactTensorQ.require(tensor);
  }

  public void testFail() {
    try {
      Se3Adjoint.forward(Tensors.vector(1, 2, 3, 4));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      Se3Adjoint.forward(HilbertMatrix.of(4, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
