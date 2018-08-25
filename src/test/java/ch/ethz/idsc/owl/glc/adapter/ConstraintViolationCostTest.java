// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Arrays;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class ConstraintViolationCostTest extends TestCase {
  public void testSimple() {
    Tensor polygon = Tensors.matrixInt(new int[][] { { 1, 0 }, { 4, 0 }, { 4, 3 } });
    PolygonRegion polygonRegion = new PolygonRegion(polygon);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction costFunction = ConstraintViolationCost.of(plannerConstraint, Quantity.of(10, "CHF"));
    assertTrue(polygonRegion.isMember(Tensors.vector(3, 1)));
    boolean isSatisfied = //
        plannerConstraint.isSatisfied(null, Arrays.asList(new StateTime(Tensors.vector(3, 1), RealScalar.ZERO)), null);
    assertFalse(isSatisfied);
    assertEquals( //
        costFunction.costIncrement(null, Arrays.asList(new StateTime(Tensors.vector(3, 1), RealScalar.ZERO)), null), //
        Quantity.of(10, "CHF"));
    assertEquals(costFunction.costIncrement( //
        null, Arrays.asList(new StateTime(Tensors.vector(1, 1), RealScalar.ZERO)), null), //
        Quantity.of(0, "CHF"));
    assertEquals(costFunction.minCostToGoal(null), Quantity.of(0.0, "CHF"));
  }
}
