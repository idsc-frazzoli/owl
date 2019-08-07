// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.rl2.RelaxedGlcExpand;
import ch.ethz.idsc.owl.glc.rl2.RelaxedTrajectoryPlanner;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Se2RelaxedCornerCuttingDemoTest extends TestCase {
  public void testSimple() {
    StateTime stateTime = new StateTime(Tensors.vector(1.7, 2.2, 0), RealScalar.ZERO);
    Tensor slacks = Tensors.vector(2, 0);
    CarRelaxedEntity carRelaxedEntity = CarRelaxedEntity.createDefault(stateTime, slacks);
    // ---
    R2ImageRegionWrap r2ImageRegionWrap = Se2RelaxedCornerCuttingDemo.createResLo();
    carRelaxedEntity.setAdditionalCostFunction(r2ImageRegionWrap.costFunction());
    Region<Tensor> region = r2ImageRegionWrap.imageRegion();
    PlannerConstraint plannerConstraint = Se2CarDemo.createConstraint(region);
    Tensor goal = Tensors.vector(4.183, 5.017, 1.571);
    RelaxedTrajectoryPlanner relaxedTrajectoryPlanner = //
        carRelaxedEntity.createTreePlanner(plannerConstraint, goal);
    relaxedTrajectoryPlanner.insertRoot(stateTime);
    RelaxedGlcExpand relaxedGlcExpand = new RelaxedGlcExpand(relaxedTrajectoryPlanner);
    relaxedGlcExpand.findAny(1000);
    int count = relaxedGlcExpand.getExpandCount();
    assertTrue(count < 800);
    relaxedGlcExpand.untilOptimal(300);
  }
}
