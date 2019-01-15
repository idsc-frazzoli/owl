// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.owl.bot.se2.glc.CarHelper;
import ch.ethz.idsc.owl.bot.se2.glc.Se2CarFlows;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2ControlsTest extends TestCase {
  public void testSimple() {
    FlowsInterface carFlows = Se2CarFlows.standard(RealScalar.ONE, Degree.of(45));
    Collection<Flow> controls = carFlows.getFlows(6);
    Scalar maxSpeed = Se2Controls.maxSpeed(controls);
    assertTrue(Chop._13.close(maxSpeed, RealScalar.ONE));
    Scalar maxTurn = Se2Controls.maxTurning(controls);
    assertTrue(Chop._13.close(maxTurn, RealScalar.of(45 * Math.PI / 180)));
    assertTrue(Chop._13.close(maxTurn, Degree.of(45)));
  }

  public void testMaxRate() {
    List<Flow> list = new ArrayList<>();
    for (Tensor angle : Subdivide.of(RealScalar.of(-.1), RealScalar.of(0.3), 5))
      list.add(CarHelper.singleton(RealScalar.of(2), angle));
    Scalar maxR = Se2Controls.maxTurning(list);
    assertEquals(maxR, RealScalar.of(0.6));
  }

  public void testMaxRate2() {
    List<Flow> list = new ArrayList<>();
    for (Tensor angle : Subdivide.of(RealScalar.of(-.3), RealScalar.of(0.1), 5))
      list.add(CarHelper.singleton(RealScalar.of(2), angle));
    Scalar maxR = Se2Controls.maxTurning(list);
    assertEquals(maxR, RealScalar.of(0.6));
  }

  public void testUnits() {
    final Scalar ms = Quantity.of(2, "m*s^-1");
    final Scalar mr = Scalars.fromString("3[rad*m^-1]");
    Flow flow = CarHelper.singleton(ms, mr);
    assertEquals(QuantityUnit.of(flow.getU().Get(2)), Unit.of("rad*s^-1"));
    Collection<Flow> controls = Collections.singleton(flow);
    Scalar maxSpeed = Se2Controls.maxSpeed(controls);
    assertEquals(maxSpeed, ms.abs());
    assertEquals(QuantityUnit.of(maxSpeed), Unit.of("m*s^-1"));
    Scalar maxTurning = Se2Controls.maxTurning(controls);
    assertEquals(QuantityUnit.of(maxTurning), Unit.of("rad*s^-1"));
    assertEquals(maxTurning, Quantity.of(6, "rad*s^-1"));
  }
}
