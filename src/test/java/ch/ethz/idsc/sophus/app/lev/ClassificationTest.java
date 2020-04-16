// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ClassificationTest extends TestCase {
  public void testSimple() {
    LabelInterface classification = Classification.accMax(Tensors.vector(3, 3, 2, 4));
    Tensor weights = Tensors.vector(0.7, 0.2, 0.3, 0.8);
    assertEquals(classification.label(weights), 3);
  }

  public void testArgMax() {
    LabelInterface classification = Classification.argMax(Tensors.vector(3, 3, 2, 4));
    Tensor weights = Tensors.vector(0.7, 0.2, 0.3, 0.8);
    assertEquals(classification.label(weights), 4);
  }

  public void testSimple2() {
    LabelInterface argMaxClassification = Classification.accMax(Tensors.vector(3, 2, 4));
    int argMax = argMaxClassification.label(Tensors.vector(0.2, 0.3, 0.8));
    assertEquals(argMax, 4);
  }
}
