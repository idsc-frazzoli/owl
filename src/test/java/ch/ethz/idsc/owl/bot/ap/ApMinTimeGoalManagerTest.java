// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.owl.math.region.LinearRegion;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class ApMinTimeGoalManagerTest extends TestCase {
  public void testSimple() {
    Scalar maxSpeed = Quantity.of(83, "m*s^-1");
    ApComboRegion apComboRegion = new ApComboRegion( //
        new LinearRegion(Quantity.of(5, "m"), Quantity.of(1, "m")), //
        new LinearRegion(Quantity.of(50, "m*s^-1"), Quantity.of(10, "m*s^-1")), //
        So2Region.periodic(RealScalar.of(0.1), RealScalar.of(0.05)));
    ApMinTimeGoalManager apMinTimeGoalManager = new ApMinTimeGoalManager(apComboRegion, maxSpeed);
    Scalar expected = Quantity.of(4, "m").divide(maxSpeed);
    assertEquals(expected, apMinTimeGoalManager.minCostToGoal(Tensors.fromString("{1000[m], 10[m], 45[m*s^-1], 0.05}")));
  }
}
