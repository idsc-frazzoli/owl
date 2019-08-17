// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ClothoidContinuityCostFunctionTest extends TestCase {
  public void testSimple() {
    RrtsNode root = RrtsNode.createRoot(Tensors.vector(1, 2, 3), RealScalar.ONE);
    Tensor connect = Tensors.vector(10, 3, 2);
    RrtsNode next = root.connectTo(connect, RealScalar.of(2));
    Transition transition = ClothoidTransitionSpace.INSTANCE.connect(connect, Tensors.vector(20, 5, 4));
    Scalar scalar = ClothoidContinuityCostFunction.INSTANCE.cost(next, transition);
    // System.out.println(scalar);
    Chop._12.requireClose(scalar, RealScalar.of(0.8454084844431387));
  }

  public void testStraight() {
    RrtsNode root = RrtsNode.createRoot(Tensors.vector(1, 2, 0), RealScalar.ONE);
    Tensor connect = Tensors.vector(10, 2, 0);
    RrtsNode next = root.connectTo(connect, RealScalar.of(2));
    Transition transition = ClothoidTransitionSpace.INSTANCE.connect(connect, Tensors.vector(20, 2, 0));
    Scalar scalar = ClothoidContinuityCostFunction.INSTANCE.cost(next, transition);
    Chop._12.requireClose(scalar, RealScalar.of(0));
  }
}
