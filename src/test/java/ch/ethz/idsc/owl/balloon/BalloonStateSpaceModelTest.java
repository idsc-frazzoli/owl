package ch.ethz.idsc.owl.balloon;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class BalloonStateSpaceModelTest extends TestCase {
  Tensor x = Tensors.fromString("{2[m],2[m*s^-1],4[K]}");
  Tensor u = Tensors.fromString("{3[K*s^-1]}");

  public void testValidity() {
    Tensor expected = Tensors.fromString("{2[m*s^-1],2[m*s^-2],-1[K*s^-1]}");
    assertEquals(expected, BalloonStateSpaceModel.INSTANCE.f(x, u));
  }
}
