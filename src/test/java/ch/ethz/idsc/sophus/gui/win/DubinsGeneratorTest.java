// code by jph
package ch.ethz.idsc.sophus.gui.win;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.UnitVector;
import junit.framework.TestCase;

public class DubinsGeneratorTest extends TestCase {
  public void testSimple() {
    Tensor tensor = DubinsGenerator.of(Array.zeros(3), Tensors.fromString("{{1, 0, 0}, {1, 0, .3}}"));
    assertEquals(tensor.get(1), UnitVector.of(3, 0));
    assertTrue(MatrixQ.ofSize(tensor, 3, 3));
  }

  public void testSingle() {
    Tensor init = Tensors.vector(1, 2, 3);
    Tensor tensor = DubinsGenerator.of(init, Tensors.empty());
    assertEquals(tensor, Tensors.of(init));
  }

  public void testProject() {
    Tensor tensor = Tensors.fromString("{{-1, 0, 0}, {1, 1, 1}, {-1, 2, 2}, {0, 3, 5}}");
    Tensor project = DubinsGenerator.project(tensor);
    assertTrue(MatrixQ.ofSize(project, tensor.length(), 3));
  }

  public void testFail() {
    AssertFail.of(() -> DubinsGenerator.of(Tensors.vector(1, 2, 3, 4), Tensors.fromString("{{1, 0, 0}, {1, 0, 0.3}}")));
  }
}
