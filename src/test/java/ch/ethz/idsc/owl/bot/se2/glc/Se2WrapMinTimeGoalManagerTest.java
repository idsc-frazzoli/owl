// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Arrays;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.Se2CoveringMetric;
import ch.ethz.idsc.owl.bot.se2.Se2CoveringWrap;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Se2WrapMinTimeGoalManagerTest extends TestCase {
  public void testSimple() {
    CoordinateWrap coordinateWrap = Se2CoveringWrap.INSTANCE;
    // Tensor eta = Tensors.vector(3, 3, 50 / Math.PI);
    // StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
    // Se2CarIntegrator.INSTANCE, RationalScalar.of(1, 6), 5);
    FlowsInterface carFlows = Se2CarFlows.forward(RealScalar.ONE, Degree.of(45));
    Collection<Flow> controls = carFlows.getFlows(6);
    Tensor GOAL = Tensors.vector(-.5, 0, 0);
    Se2WrapMinTimeGoalManager manager = new Se2WrapMinTimeGoalManager(Se2CoveringMetric.INSTANCE, GOAL, RealScalar.of(0.25), controls);
    //
    GlcNode glcNode = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2, 0), Quantity.of(3, "s")), manager);
    System.out.println(glcNode.merit());
    Scalar scalar = manager.costIncrement( //
        glcNode, //
        Arrays.asList(new StateTime(Tensors.vector(1, 2, 3), Quantity.of(10, "s"))), //
        null);
    assertEquals(scalar, Quantity.of(7, "s"));
    assertTrue(ExactScalarQ.of(scalar));
  }
}
