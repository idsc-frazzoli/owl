// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2AdjointTest extends TestCase {
  public void testRotationFixpointSide() {
    Se2Adjoint se2Adjoint = new Se2Adjoint(Tensors.vector(0, 1, 0));
    Tensor tensor = se2Adjoint.apply(Tensors.vector(1, 0, -1));
    Chop._13.requireClose(tensor, UnitVector.of(3, 2).negate()); // only rotation
  }

  public void testRotationId() {
    Se2Adjoint se2Adjoint = new Se2Adjoint(Tensors.vector(0, 0, 2));
    Tensor tensor = se2Adjoint.apply(Tensors.vector(0, 0, 1));
    Chop._13.requireClose(tensor, UnitVector.of(3, 2)); // same rotation
  }

  public void testRotationTranslation() {
    Se2Adjoint se2Adjoint = new Se2Adjoint(Tensors.vector(1, 0, Math.PI / 2));
    Tensor tensor = se2Adjoint.apply(Tensors.vector(0, 0, 1));
    Chop._13.requireClose(tensor, Tensors.vector(0, -1, 1));
  }

  public void testTranslate() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Se2Adjoint se2Adjoint = new Se2Adjoint(RandomVariate.of(distribution, 2).append(RealScalar.ZERO));
      Tensor uvw = RandomVariate.of(distribution, 2).append(RealScalar.ZERO);
      Tensor tensor = se2Adjoint.apply(uvw);
      Chop._13.requireClose(tensor, uvw); // only translation
    }
  }

  public void testComparison() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Tensor xya = RandomVariate.of(distribution, 3);
      Se2Adjoint se2Adjoint = new Se2Adjoint(xya);
      Se2AdjointComp se2AdjointComp = new Se2AdjointComp(xya);
      Tensor uvw = RandomVariate.of(distribution, 3);
      Chop._13.requireClose(se2Adjoint.apply(uvw), se2AdjointComp.apply(uvw)); // only translation
    }
  }

  public void testFail() {
    try {
      new Se2Adjoint(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      new Se2Adjoint(HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
