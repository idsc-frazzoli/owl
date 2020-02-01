// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.owl.bot.se2.glc.Se2CarFlows;
import ch.ethz.idsc.owl.bot.se2.twd.TwdDuckieFlows;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
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
    Collection<Tensor> controls = carFlows.getFlows(6);
    Scalar maxSpeed = Se2Controls.maxSpeed(controls);
    assertTrue(Chop._13.close(maxSpeed, RealScalar.ONE));
    Scalar maxTurn = Se2Controls.maxTurning(controls);
    assertTrue(Chop._13.close(maxTurn, RealScalar.of(45 * Math.PI / 180)));
    assertTrue(Chop._13.close(maxTurn, Degree.of(45)));
  }

  public void testMaxRate() {
    List<Tensor> list = new ArrayList<>();
    for (Tensor angle : Subdivide.of(RealScalar.of(-.1), RealScalar.of(0.3), 5))
      list.add(Se2CarFlows.singleton(RealScalar.of(2), angle.Get()));
    Scalar maxR = Se2Controls.maxTurning(list);
    assertEquals(maxR, RealScalar.of(0.6));
  }

  public void testMaxRate2() {
    List<Tensor> list = new ArrayList<>();
    for (Tensor angle : Subdivide.of(RealScalar.of(-.3), RealScalar.of(0.1), 5))
      list.add(Se2CarFlows.singleton(RealScalar.of(2), angle.Get()));
    Scalar maxR = Se2Controls.maxTurning(list);
    assertEquals(maxR, RealScalar.of(0.6));
  }

  public void testUnits() {
    final Scalar ms = Quantity.of(2, "m*s^-1");
    final Scalar mr = Scalars.fromString("3[m^-1]");
    Tensor flow = Se2CarFlows.singleton(ms, mr);
    assertEquals(QuantityUnit.of(flow.Get(2)), Unit.of("s^-1"));
    Collection<Tensor> controls = Collections.singleton(flow);
    Scalar maxSpeed = Se2Controls.maxSpeed(controls);
    assertEquals(maxSpeed, ms.abs());
    assertEquals(QuantityUnit.of(maxSpeed), Unit.of("m*s^-1"));
    Scalar maxTurning = Se2Controls.maxTurning(controls);
    assertEquals(QuantityUnit.of(maxTurning), Unit.of("s^-1"));
    assertEquals(maxTurning, Quantity.of(6, "s^-1"));
  }

  public void testUnitsNonSI() {
    final Scalar ms = Quantity.of(2, "m*s^-1");
    final Scalar mr = Scalars.fromString("3[rad*m^-1]");
    Tensor flow = Se2CarFlows.singleton(ms, mr);
    assertEquals(QuantityUnit.of(flow.Get(2)), Unit.of("rad*s^-1"));
    Collection<Tensor> controls = Collections.singleton(flow);
    Scalar maxSpeed = Se2Controls.maxSpeed(controls);
    assertEquals(maxSpeed, ms.abs());
    assertEquals(QuantityUnit.of(maxSpeed), Unit.of("m*s^-1"));
    Scalar maxTurning = Se2Controls.maxTurning(controls);
    assertEquals(QuantityUnit.of(maxTurning), Unit.of("rad*s^-1"));
    assertEquals(maxTurning, Quantity.of(6, "rad*s^-1"));
  }

  public void testMaxSpeed() {
    TwdDuckieFlows twdConfig = new TwdDuckieFlows(RealScalar.of(3), RealScalar.of(0.567));
    Collection<Tensor> controls = twdConfig.getFlows(8);
    Scalar maxSpeed = Se2Controls.maxSpeed(controls);
    assertEquals(maxSpeed, RealScalar.of(3));
  }

  public void testUnit() {
    Scalar ms = Quantity.of(3, "m*s^-1");
    Scalar sa = Quantity.of(0.567, "m*rad^-1");
    TwdDuckieFlows twdConfig = new TwdDuckieFlows(ms, sa);
    Collection<Tensor> controls = twdConfig.getFlows(8);
    Scalar maxSpeed = Se2Controls.maxSpeed(controls);
    assertEquals(maxSpeed, ms);
    assertEquals(QuantityUnit.of(maxSpeed), Unit.of("m*s^-1"));
    Scalar maxTurng = Se2Controls.maxTurning(controls);
    assertEquals(QuantityUnit.of(maxTurng), Unit.of("rad*s^-1"));
    assertEquals(maxTurng, ms.divide(sa));
  }
}
