// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.adapter.AbstractMinTimeGoalManager;
import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.HeuristicConsistency;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class Tse2PlannerTest extends TestCase {
  public void testForward() {
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical( //
        Tensors.fromString("{10[m], 0[m], 2, 4[m*s^-1]}"), //
        Tensors.fromString("{1[m], 1[m], 1, 4[m*s^-1]}"));
    final Clip v_range = tse2ComboRegion.v_range();
    assertEquals(v_range.min(), Quantity.of(0, "m*s^-1"));
    assertEquals(v_range.max(), Quantity.of(8, "m*s^-1"));
    FlowsInterface flowsInterface = Tse2CarFlows.of(Quantity.of(1, "m^-1"), Tensors.fromString("{-1[m*s^-2],0[m*s^-2],1[m*s^-2]}"));
    Collection<Flow> controls = flowsInterface.getFlows(1);
    AbstractMinTimeGoalManager tse2ForwardMinTimeGoalManager = //
        new Tse2ForwardMinTimeGoalManager(tse2ComboRegion, controls);
    GoalInterface goalInterface = tse2ForwardMinTimeGoalManager.getGoalInterface();
    PlannerConstraint plannerConstraint = EmptyObstacleConstraint.INSTANCE;
    // new Tse2VelocityConstraint(v_range);
    Tensor eta = Tensors.fromString("{7[m^-1],7[m^-1],4,7[s*m^-1]}");
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        new Tse2Integrator(v_range), Scalars.fromString("1/10[s]"), 4);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, //
        stateIntegrator, //
        controls, //
        plannerConstraint, //
        goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.fromString("{0[m],0[m],0,0[m*s^-1]}"), Quantity.of(1, "s")));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    glcExpand.getExpandCount();
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    GlcNode glcNode = optional.get();
    List<TrajectorySample> trajectory = GlcTrajectories.detailedTrajectoryTo(stateIntegrator, glcNode);
    assertTrue(20 < trajectory.size());
    HeuristicConsistency.check(trajectoryPlanner);
    // TrajectoryPlannerConsistency.check(trajectoryPlanner);
  }

  public void testGeneral() {
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical( //
        Tensors.fromString("{10[m], 0[m], 2, 4[m*s^-1]}"), //
        Tensors.fromString("{1[m], 1[m], 1, 4[m*s^-1]}"));
    final Clip v_range = tse2ComboRegion.v_range();
    assertEquals(v_range.min(), Quantity.of(0, "m*s^-1"));
    assertEquals(v_range.max(), Quantity.of(8, "m*s^-1"));
    FlowsInterface flowsInterface = Tse2CarFlows.of(Quantity.of(1, "m^-1"), Tensors.fromString("{-1[m*s^-2],0[m*s^-2],1[m*s^-2]}"));
    Collection<Flow> controls = flowsInterface.getFlows(1);
    AbstractMinTimeGoalManager tse2ForwardMinTimeGoalManager = //
        new Tse2MinTimeGoalManager(tse2ComboRegion, controls, v_range.max());
    GoalInterface goalInterface = tse2ForwardMinTimeGoalManager.getGoalInterface();
    PlannerConstraint plannerConstraint = EmptyObstacleConstraint.INSTANCE;
    // new Tse2VelocityConstraint(v_range);
    Tensor eta = Tensors.fromString("{7[m^-1],7[m^-1],4,7[s*m^-1]}");
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        new Tse2Integrator(v_range), Scalars.fromString("1/10[s]"), 4);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, //
        stateIntegrator, //
        controls, //
        plannerConstraint, //
        goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.fromString("{0[m],0[m],0,0[m*s^-1]}"), Quantity.of(1, "s")));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000); // TODO YN does not find solution even with 10000
    int expandCount = glcExpand.getExpandCount();
    System.out.println(expandCount);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    System.out.println(optional.isPresent());
    HeuristicConsistency.check(trajectoryPlanner);
    // TrajectoryPlannerConsistency.check(trajectoryPlanner);
  }
}
