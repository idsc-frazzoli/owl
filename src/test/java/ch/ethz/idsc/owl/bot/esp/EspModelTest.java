// code by jph
package ch.ethz.idsc.owl.bot.esp;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class EspModelTest extends TestCase {
  public void testSimple() {
    Tensor board = EspDemo.START;
    assertFalse(EspObstacleRegion.INSTANCE.isMember(board));
    for (Flow flow : EspControls.LIST) {
      Tensor u = flow.getU();
      Tensor tensor = EspModel.INSTANCE.f(board, u);
      int vx = tensor.Get(5, 0).number().intValue();
      int vy = tensor.Get(5, 1).number().intValue();
      assertEquals(tensor.Get(vx, vy).number().intValue(), 0);
      assertFalse(EspObstacleRegion.INSTANCE.isMember(tensor));
    }
  }

  public void testBorder() {
    Tensor board = Tensors.of( //
        Tensors.vector(2, 2, 2, 0, 0), //
        Tensors.vector(2, 2, 2, 0, 0), //
        Tensors.vector(0, 2, 2, 1, 1), //
        Tensors.vector(0, 0, 1, 1, 1), //
        Tensors.vector(0, 0, 1, 1, 1), //
        Tensors.vector(2, 0) //
    );
    assertFalse(EspObstacleRegion.INSTANCE.isMember(board));
    int collisions = 0;
    for (Flow flow : EspControls.LIST) {
      Tensor u = flow.getU();
      Tensor tensor = EspModel.INSTANCE.f(board, u);
      collisions += EspObstacleRegion.INSTANCE.isMember(tensor) ? 1 : 0;
    }
    assertEquals(collisions, 4);
  }

  public void testEdge() {
    Tensor board = Tensors.of( //
        Tensors.vector(2, 2, 2, 0, 0), //
        Tensors.vector(2, 2, 2, 0, 0), //
        Tensors.vector(2, 0, 2, 1, 1), //
        Tensors.vector(0, 0, 1, 1, 1), //
        Tensors.vector(0, 0, 1, 1, 1), //
        Tensors.vector(2, 1) //
    );
    assertFalse(EspObstacleRegion.INSTANCE.isMember(board));
    int collisions = 0;
    for (Flow flow : EspControls.LIST) {
      Tensor u = flow.getU();
      Tensor tensor = EspModel.INSTANCE.f(board, u);
      collisions += EspObstacleRegion.INSTANCE.isMember(tensor) ? 1 : 0;
    }
    assertEquals(collisions, 3);
  }
}
