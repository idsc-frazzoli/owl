// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.util.ArrayDeque;
import java.util.Deque;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class GeometricLayerTest extends TestCase {
  public void testSimple() {
    Deque<Integer> ad = new ArrayDeque<>();
    ad.push(2);
    ad.push(4);
    ad.push(9);
    assertEquals((int) ad.peek(), 9);
    ad.pop();
    assertEquals((int) ad.peek(), 4);
    ad.pop();
    assertEquals((int) ad.peek(), 2);
    ad.pop();
    assertEquals(ad.peek(), null);
  }

  public void testConstruction() {
    Tensor model2pixel = Tensors.fromString("{{1, 2, 3}, {2, -1, 7}, {0, 0, 1}}");
    Tensor mouseSe2State = Tensors.vector(9, 7, 2);
    GeometricLayer geometricLayer = new GeometricLayer(model2pixel, mouseSe2State);
    geometricLayer.toPoint2D(Tensors.vector(1, 2));
    assertEquals(geometricLayer.getMatrix(), model2pixel);
    geometricLayer.pushMatrix(IdentityMatrix.of(3));
    assertEquals(geometricLayer.getMatrix(), model2pixel);
    geometricLayer.popMatrix();
    geometricLayer.popMatrix();
    assertEquals(mouseSe2State, geometricLayer.getMouseSe2State());
    try {
      geometricLayer.popMatrix();
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testVector() {
    Tensor model2pixel = Tensors.fromString("{{1, 2, 3}, {2, -1, 7}, {0, 0, 1}}");
    Tensor mouseSe2State = Tensors.vector(9, 7, 2);
    GeometricLayer geometricLayer = new GeometricLayer(model2pixel, mouseSe2State);
    Tensor vector = Tensors.vector(9, 20, 1);
    Tensor v1 = geometricLayer.toVector(vector);
    Tensor v2 = geometricLayer.toVector(9, 20);
    Tensor expected = model2pixel.dot(vector).extract(0, 2);
    assertEquals(expected, v1);
    assertEquals(expected, v2);
  }

  public void testFail() {
    GeometricLayer geometricLayer = new GeometricLayer(IdentityMatrix.of(3), Array.zeros(3));
    geometricLayer.popMatrix();
    try {
      geometricLayer.popMatrix();
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
