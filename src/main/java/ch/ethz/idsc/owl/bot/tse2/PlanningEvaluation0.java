// code by ynager
package ch.ethz.idsc.owl.bot.tse2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.se2.glc.Se2Demo;
import ch.ethz.idsc.owl.bot.se2.glc.SimpleShadowConstraintCV;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.bot.util.StreetScenario;
import ch.ethz.idsc.owl.bot.util.StreetScenarioData;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.MultiConstraintAdapter;
import ch.ethz.idsc.owl.glc.adapter.MultiCostGoalAdapter;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.GlcTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardGlcTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.SphericalRegionRender;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
import ch.ethz.idsc.owl.gui.ren.TreeRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.mapping.ShadowEvaluator;
import ch.ethz.idsc.owl.mapping.ShadowMapDirected;
import ch.ethz.idsc.owl.mapping.ShadowMapSpherical;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sqrt;

public class PlanningEvaluation0 extends Se2Demo {
  // Entity Stuff
  static final int ID = 30;
  static final StreetScenarioData STREET_SCENARIO_DATA = StreetScenario.S3.load();
  //
  static final boolean SR_PED_LEGAL = true;
  static final boolean SR_PED_ILLEGAL = false;
  static final boolean SR_CAR = false;
  static final boolean EVAL_PED_LEGAL = false;
  static final boolean EVAL_PED_ILLEGAL = false;
  static final boolean EVAL_CAR = false;
  //
  static final Scalar MAX_SPEED = RealScalar.of(8); // 8
  static final Scalar MAX_TURNING_PLAN = Degree.of(7); // 12
  static final FlowsInterface TSE2_CARFLOWS = Tse2CarFlows.of(MAX_TURNING_PLAN, Tensors.vector(-2, 0, 2));
  static final int FLOWRES = 7;
  static final float CAR_RAD = 1.0f; // [m]
  static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal(), RealScalar.of(10)).unmodifiable();
  static final StateTime INITIAL = new StateTime(Tensors.vector(12, 3.0, 1.571, 8.0), RealScalar.ZERO); // normal (s3, s4)
  // static final StateTime INITIAL = new StateTime(Tensors.vector(10.5, 3.0, 1.571, 8), RealScalar.ZERO); // left (s3, s4)
  // static final StateTime INITIAL = new StateTime(Tensors.vector(20.5, 1.5, 1.571, 7), RealScalar.ZERO); // normal (s6)
  // static final StateTime INITIAL = new StateTime(Tensors.vector(20.0, 1.5, 1.571, 7), RealScalar.ZERO); // left (s6)
  // static final StateTime INITIAL = new StateTime(Tensors.vector(21.5, 20.0, 1.571, 7), RealScalar.ZERO); // up (s6)
  // static final StateTime INITIAL = new StateTime(Tensors.vector(19.0, 6.0, Math.PI / 2.0f, 8), RealScalar.ZERO); // normal (s7)
  // static final StateTime INITIAL = new StateTime(Tensors.vector(21.8, 1.5, Math.PI / 2.0f, 8), RealScalar.ZERO); // normal (s8)
  static final Tensor GOAL = Tensors.vector(12, 31, 1.571, MAX_SPEED.divide(RealScalar.of(2)).number()); // only straight (s3, s4)
  // static final Tensor GOAL = Tensors.vector(27, 33.5, 0, MAX_SPEED.divide(RealScalar.of(2)).number()); // around curve (s3, s4)
  // static final Tensor GOAL = Tensors.vector(20.0, 34, 1.3f*Math.PI / 2.0f, MAX_SPEED.divide(RealScalar.of(2)).number()); // hc (s6)
  // static final Tensor GOAL = Tensors.vector(21.5, 32, Math.PI / 2.0f, MAX_SPEED.divide(RealScalar.of(2)).number()); // normal (s6)
  // static final Tensor GOAL = Tensors.vector(19.0, 42, Math.PI / 2.0f, MAX_SPEED.divide(RealScalar.of(2)).number()); // normal (s7)
  // static final Tensor GOAL = Tensors.vector(21.8, 22.0, Math.PI / 2.0f, MAX_SPEED.divide(RealScalar.of(2)).number()); // normal (s8)
  final Tensor goalRadius;
  //
  static final float PED_VELOCITY = 1.6f;
  static final float CAR_VELOCITY = 11.0f;
  static final float PED_RADIUS = 0.3f;
  static final float MAX_A = 5.0f; // [m/s²]
  static final float REACTION_TIME = 0.3f;
  static final Tensor RANGE = Tensors.vector(30.5, 43.1);
  static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-180), Degree.of(180), 72), Subdivide.of(0, 40, 120));
  //
  static final int MAX_STEPS = 10_000;
  /** node interval == 2/5 */
  static final FixedStateIntegrator FIXEDSTATEINTEGRATOR = FixedStateIntegrator.create( //
      new Tse2Integrator(Clips.positive(MAX_SPEED)), RationalScalar.of(1, 10), 3);
  final Collection<Flow> controls;
  final Collection<CostFunction> extraCosts = new LinkedList<>();

  public PlanningEvaluation0() {
    final Scalar goalRadius_xy = DoubleScalar.of(1.3); // Sqrt.of(RealScalar.of(2)).divide(PARTITIONSCALE.Get(0));
    final Scalar goalRadius_theta = Sqrt.of(RealScalar.of(2)).divide(RealScalar.of(20)); // SQRT2.divide(PARTITIONSCALE.Get(2));
    final Scalar goalRadius_v = MAX_SPEED.divide(RealScalar.of(2)); // SQRT2.divide(PARTITIONSCALE.Get(3));
    this.goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta, goalRadius_v);
    this.controls = TSE2_CARFLOWS.getFlows(FLOWRES);
  }

  @Override
  protected void configure(OwlyAnimationFrame owlyAnimationFrame) {
    ImageRender imageRender = ImageRender.of(STREET_SCENARIO_DATA.render, RANGE);
    owlyAnimationFrame.addBackground(imageRender);
    //
    // IMAGE REGIONS
    Tensor imageCar = STREET_SCENARIO_DATA.imageCar_extrude(10);
    ImageRegion irPedLegal = new ImageRegion(STREET_SCENARIO_DATA.imagePedLegal, RANGE, false);
    ImageRegion irPedIllegal = new ImageRegion(STREET_SCENARIO_DATA.imagePedIllegal, RANGE, false);
    ImageRegion irCar = new ImageRegion(imageCar, RANGE, false);
    ImageRegion irLid = new ImageRegion(STREET_SCENARIO_DATA.imageLid, RANGE, false);
    //
    // SETUP CONSTRAINTS
    List<PlannerConstraint> constraints = new ArrayList<>();
    constraints.add(RegionConstraints.timeInvariant(irCar));
    PlannerConstraint plannerConstraints = MultiConstraintAdapter.of(constraints);
    //
    // LIDAR EMULATOR
    TrajectoryRegionQuery lidarRay = SimpleTrajectoryRegionQuery.timeInvariant(irLid);
    LidarEmulator lidarEmulator = new LidarEmulator( //
        LIDAR_RAYTRACER, () -> new StateTime(Tensors.vector(0, 0, 0), RealScalar.ZERO), lidarRay);
    // SHADOW REGIONS
    ShadowMapSpherical smPedLegal = //
        new ShadowMapSpherical(lidarEmulator, irPedLegal, PED_VELOCITY, PED_RADIUS);
    ShadowMapSpherical smPedIllegal = //
        new ShadowMapSpherical(lidarEmulator, irPedIllegal, PED_VELOCITY, PED_RADIUS);
    ShadowMapDirected smCar = new ShadowMapDirected( //
        lidarEmulator, irCar, STREET_SCENARIO_DATA.imageLanesString, CAR_VELOCITY);
    //
    // SHADOW REGION CONSTRAINTS
    if (SR_PED_LEGAL) {
      PlannerConstraint pedLegalConst = new SimpleShadowConstraintCV(smPedLegal, irCar, CAR_RAD, MAX_A, REACTION_TIME, true);
      constraints.add(pedLegalConst);
    }
    if (SR_PED_ILLEGAL) {
      PlannerConstraint pedIllegalConst = new SimpleShadowConstraintCV(smPedIllegal, irCar, CAR_RAD, MAX_A, REACTION_TIME, true);
      constraints.add(pedIllegalConst);
    }
    if (SR_CAR) {
      PlannerConstraint carConst = new SimpleShadowConstraintCV(smCar, irCar, CAR_RAD, MAX_A, REACTION_TIME, true);
      constraints.add(carConst);
    }
    //
    // SETUP PLANNER
    extraCosts.add(Tse2LateralAcceleration.INSTANCE);
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical(GOAL, goalRadius);
    // Tse2MinTimeGoalManager tse2MinTimeGoalManager = new Tse2MinTimeGoalManager(tse2ComboRegion, controls, MAX_SPEED);
    Tse2ForwardMinTimeGoalManager tse2MinTimeGoalManager = new Tse2ForwardMinTimeGoalManager(tse2ComboRegion, controls);
    GoalInterface goalInterface = MultiCostGoalAdapter.of(tse2MinTimeGoalManager.getGoalInterface(), extraCosts);
    owlyAnimationFrame.addBackground(new SphericalRegionRender(new SphericalRegion(GOAL, goalRadius.Get(0))));
    GlcTrajectoryPlanner tp = new StandardGlcTrajectoryPlanner( //
        stateTimeRaster(), FIXEDSTATEINTEGRATOR, controls, plannerConstraints, goalInterface);
    // SETUP CALLBACKS
    List<GlcPlannerCallback> callbacks = new ArrayList<>();
    //
    // EVALUATOR
    if (EVAL_PED_LEGAL) {
      ShadowEvaluator evaluator = new ShadowEvaluator(smPedLegal, RealScalar.of(MAX_A), RealScalar.of(CAR_RAD), "legal" + String.valueOf(ID));
      callbacks.add(evaluator.sectorTimeToReact);
    }
    if (EVAL_PED_ILLEGAL) {
      ShadowEvaluator evaluator = new ShadowEvaluator(smPedIllegal, RealScalar.of(MAX_A), RealScalar.of(CAR_RAD), "illegal" + String.valueOf(ID));
      callbacks.add(evaluator.sectorTimeToReact);
    }
    if (EVAL_CAR) {
      ShadowEvaluator evaluator = new ShadowEvaluator(smCar, RealScalar.of(MAX_A), RealScalar.of(CAR_RAD), "car" + String.valueOf(ID));
      callbacks.add(evaluator.sectorTimeToReact);
    }
    //
    // MOTION PLAN WORKER
    Thread mpw = new Thread(new Runnable() {
      @Override // from Runnable
      public void run() {
        Timing timing = Timing.started();
        tp.insertRoot(INITIAL);
        GlcExpand glcExpand = new GlcExpand(tp);
        glcExpand.findAny(MAX_STEPS);
        timing.stop();
        System.out.println("Planning time: " + timing.seconds());
        //
        Optional<GlcNode> optional = tp.getBest();
        if (optional.isPresent()) {
          System.out.println("Cost to Goal: " + optional.get().costFromRoot());
          List<TrajectorySample> traj = //
              GlcTrajectories.detailedTrajectoryTo(tp.getStateIntegrator(), optional.get());
          owlyAnimationFrame.addBackground(new TreeRender().setCollection(tp.getDomainMap().values()));
          TrajectoryRender trajectoryRender = new TrajectoryRender();
          trajectoryRender.trajectory(traj);
          trajectoryRender.setColor(Color.GREEN);
          owlyAnimationFrame.addBackground(trajectoryRender);
          traj.stream().forEach(a -> System.out.println(a.stateTime().state().get(3)));
        } else {
          System.out.println("no traj found");
        }
        for (GlcPlannerCallback glcPlannerCallback : callbacks)
          glcPlannerCallback.expandResult(Collections.emptyList(), tp);
      }
    });
    System.out.println("Planning...");
    mpw.start();
  }

  protected StateTimeRaster stateTimeRaster() {
    return new EtaRaster(PARTITIONSCALE, StateTimeTensorFunction.state(Tse2Wrap.INSTANCE::represent));
  }

  public static void main(String[] args) {
    new PlanningEvaluation0().start().jFrame.setVisible(true);
  }
}
