// code by jph
package ch.ethz.idsc.owl.bot.se2.tse2;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.tse2.Tse2CarFlows;
import ch.ethz.idsc.owl.bot.tse2.Tse2Controls;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Tse2CarFlowsTest extends TestCase {
  public void testSimple() {
    FlowsInterface flowsInterface = Tse2CarFlows.of(RealScalar.of(3), Tensors.vector(-2, 0, 1));
    Collection<Flow> flows = flowsInterface.getFlows(10);
    // TODO YN are these values intended, if so update comments in Tse2Controls
    assertEquals(Tse2Controls.maxAcc(flows), RealScalar.of(1));
    assertEquals(Tse2Controls.minAcc(flows), RealScalar.of(-2));
    assertEquals(Tse2Controls.maxTurning(flows), RealScalar.of(3));
  }
}
