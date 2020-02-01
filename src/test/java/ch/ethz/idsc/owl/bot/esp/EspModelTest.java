// code by jph
package ch.ethz.idsc.owl.bot.esp;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class EspModelTest extends TestCase {
  public void testSimple() {
    Tensor board = EspDemo.START;
    for (Tensor flow : EspFlows.INSTANCE.flows(new StateTime(board, RealScalar.ZERO))) {
      Tensor u = flow;
      Tensor tensor = EspModel.INSTANCE.f(board, u);
      int vx = tensor.Get(5, 0).number().intValue();
      int vy = tensor.Get(5, 1).number().intValue();
      assertEquals(tensor.Get(vx, vy).number().intValue(), 0);
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
    int count = 0;
    for (Tensor flow : EspFlows.INSTANCE.flows(new StateTime(board, RealScalar.ZERO))) {
      Tensor u = flow;
      EspModel.INSTANCE.f(board, u);
      ++count;
    }
    assertEquals(count, 4);
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
    int count = 0;
    for (Tensor flow : EspFlows.INSTANCE.flows(new StateTime(board, RealScalar.ZERO))) {
      Tensor u = flow;
      EspModel.INSTANCE.f(board, u);
      ++count;
    }
    assertEquals(count, 5);
  }
}
