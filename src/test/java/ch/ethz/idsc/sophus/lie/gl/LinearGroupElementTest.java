// code by jph
package ch.ethz.idsc.sophus.lie.gl;

import java.io.IOException;

import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.sophus.lie.se2.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class LinearGroupElementTest extends TestCase {
  public void testSimple() {
    int n = 5;
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    LinearGroupElement linearGroupElement = LinearGroupElement.of(matrix);
    Tensor result = linearGroupElement.inverse().combine(matrix);
    assertTrue(Chop._10.close(result, IdentityMatrix.of(n)));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    Tensor tensor = DiagonalMatrix.of(1, 2, 3);
    LinearGroupElement linearGroupElement = LinearGroupElement.of(tensor);
    LinearGroupElement copy = Serialization.copy(linearGroupElement);
    Tensor result = copy.inverse().combine(tensor);
    assertEquals(result, IdentityMatrix.of(3));
  }

  public void testLinearGroupSe2() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 10; ++count) {
      Tensor g = RandomVariate.of(distribution, 3);
      Tensor uvw = RandomVariate.of(distribution, 3);
      Tensor adjoint = new Se2GroupElement(g).adjoint(uvw);
      LinearGroupElement linearGroupElement = LinearGroupElement.of(Se2Utils.toSE2Matrix(g));
      Tensor X = Tensors.matrix(new Scalar[][] { //
          { RealScalar.ZERO, uvw.Get(2).negate(), uvw.Get(0) }, //
          { uvw.Get(2), RealScalar.ZERO, uvw.Get(1) }, //
          { RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO } });
      Tensor tensor = linearGroupElement.adjoint(X);
      Tensor xya = Tensors.of(tensor.Get(0, 2), tensor.Get(1, 2), tensor.Get(1, 0));
      Chop._12.requireClose(adjoint, xya);
    }
  }

  public void testAdjoint() {
    Tensor xya = Tensors.vector(1, 2, 3);
    Se2GroupElement se2GroupElement = new Se2GroupElement(xya);
    Tensor matrix = Se2Utils.toSE2Matrix(xya);
    Tensor adjointGl = LinearGroupElement.of(matrix).adjoint(Tensors.fromString("{{0, 1, 0}, {-1, 0, 0}, {0, 0, 0}}")).map(Chop._10);
    Tensor adjointSe = se2GroupElement.adjoint(Tensors.vector(0, 0, -1));
    Chop._12.requireClose(adjointGl.get(0, 2), adjointSe.get(0));
    Chop._12.requireClose(adjointGl.get(1, 2), adjointSe.get(1));
    Chop._12.requireClose(adjointGl.get(1, 0), adjointSe.get(2));
    // System.out.println(adjointGl);
    // System.out.println(adjointSe);
  }

  public void testAdjointFail() {
    LinearGroupElement linearGroupElement = LinearGroupElement.of(IdentityMatrix.of(5));
    try {
      linearGroupElement.adjoint(Tensors.vector(1, 2, 3, 4, 5));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNonSquareFail() {
    try {
      LinearGroupElement.of(HilbertMatrix.of(2, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testCombineNonSquareFail() {
    LinearGroupElement linearGroupElement = LinearGroupElement.of(DiagonalMatrix.of(1, 2));
    try {
      linearGroupElement.combine(HilbertMatrix.of(2, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      linearGroupElement.combine(Tensors.vector(1, 2));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNonInvertibleFail() {
    try {
      LinearGroupElement.of(DiagonalMatrix.of(1, 0, 2));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
