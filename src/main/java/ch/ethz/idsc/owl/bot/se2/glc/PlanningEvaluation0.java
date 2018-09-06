// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.r2.ImageEdges;
import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.tse2.Tse2CarFlows;
import ch.ethz.idsc.owl.bot.tse2.Tse2ComboRegion;
import ch.ethz.idsc.owl.bot.tse2.Tse2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.tse2.Tse2VelocityConstraint;
import ch.ethz.idsc.owl.bot.tse2.Tse2Wrap;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.data.Stopwatch;
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
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.gui.region.EllipseRegionRender;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
import ch.ethz.idsc.owl.gui.ren.TreeRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.mapping.ShadowMapDirected;
import ch.ethz.idsc.owl.mapping.ShadowMapSpherical;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.sca.Sqrt;

public class PlanningEvaluation0 extends Se2Demo {
  // Entity Stuff
  static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(10).reciprocal(), RealScalar.of(10)).unmodifiable();
  public static final Scalar MAX_SPEED = RealScalar.of(8); //
  static final Scalar MAX_TURNING_PLAN = Degree.of(30); // 45
  static final FlowsInterface CARFLOWS = Tse2CarFlows.of(MAX_TURNING_PLAN, Tensors.vector(-3, 0, 3));
  static final int FLOWRES = 9;
  private static final Scalar SQRT2 = Sqrt.of(RealScalar.of(2));
  static final Tensor SHAPE = Tensors.matrixDouble( //
      new double[][] { //
          { .2, +.07 }, //
          { .25, +.0 }, //
          { .2, -.07 }, //
          { -.1, -.07 }, //
          { -.1, +.07 } //
      }).unmodifiable();
  final StateTime initial = new StateTime(Tensors.vector(12, 3.5, 1.571, 6), RealScalar.ZERO);
  // v_init = 4 ok for illegal
  // private Tensor goal = Tensors.vector(22, 33.5, 0, MAX_SPEED.number()); // around curve
  private Tensor goal = Tensors.vector(12, 30, 1.571, MAX_SPEED.number());  // only straigh
  private Tensor goalRadius;
  //
  private static final float PED_VELOCITY = 2.0f;
  private static final float CAR_VELOCITY = 4;
  private static final float PED_RADIUS = 0.3f;
  private static final float MAX_A = 6.0f; // [m/s²]
  private static final float REACTION_TIME = 0.2f;
  private static final Tensor RANGE = Tensors.vector(30.5, 43.1);
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-180), Degree.of(180), 72), Subdivide.of(0, 30, 90));
  //
  static final int MAX_STEPS = 10000;
  public static final FixedStateIntegrator FIXEDSTATEINTEGRATOR = // node interval == 2/5
      FixedStateIntegrator.create(RungeKutta4Integrator.INSTANCE, RationalScalar.of(1, 7), 4);
  protected final Collection<Flow> controls;
  public final Collection<CostFunction> extraCosts = new LinkedList<>();

  public PlanningEvaluation0() {
    final Scalar goalRadius_xy = SQRT2.divide(PARTITIONSCALE.Get(0));
    final Scalar goalRadius_theta = SQRT2.divide(RealScalar.of(20)); //SQRT2.divide(PARTITIONSCALE.Get(2));
    final Scalar goalRadius_v = RealScalar.of(10); // SQRT2.divide(PARTITIONSCALE.Get(3));
    this.goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta, goalRadius_v);
    this.controls = CARFLOWS.getFlows(FLOWRES);
  }

  @Override
  void configure(OwlyAnimationFrame owlyAnimationFrame) {
    // ---
    Tensor image = ResourceData.of("/simulation/s3/render.png");
    BufferedImage bufferedImage = ImageFormat.of(image);
    //
    ImageRender imgRender = ImageRender.of(bufferedImage, RANGE);
    owlyAnimationFrame.addBackground(imgRender);
    //
    // IMAGE REGIONS
    Tensor imagePedLegal = ResourceData.of("/simulation/s3/ped_obs_legal.png");
    Tensor imagePedIllegal = ResourceData.of("/simulation/s3/ped_obs_illegal.png");
    Tensor imageCar = ResourceData.of("/simulation/s3/car_obs_1.png");
    imageCar = ImageEdges.extrusion(imageCar, 10); // == 0.73 * 7.5 == 5.475
    Tensor imageLid = ResourceData.of("/simulation/s2/ped_obs_illegal.png");
    ImageRegion irPedLegal = new ImageRegion(imagePedLegal, RANGE, false);
    ImageRegion irPedIllegal = new ImageRegion(imagePedIllegal, RANGE, false);
    ImageRegion irCar = new ImageRegion(imageCar, RANGE, false);
    ImageRegion irLid = new ImageRegion(imageLid, RANGE, false);
    //
    // Setup constraints
    List<PlannerConstraint> constraints = new ArrayList<>();
    constraints.add(RegionConstraints.timeInvariant(irCar));
    constraints.add(new Tse2VelocityConstraint(RealScalar.ZERO, MAX_SPEED));
    PlannerConstraint plannerConstraints = MultiConstraintAdapter.of(constraints);
    //
    //
    // LIDAR EMULATOR
    TrajectoryRegionQuery lidarRay = SimpleTrajectoryRegionQuery.timeInvariant(irLid);
    LidarEmulator lidarEmulator = new LidarEmulator( //
        LIDAR_RAYTRACER, () -> new StateTime(Tensors.vector(0, 0, 0), RealScalar.ZERO), lidarRay);
    owlyAnimationFrame.addBackground(lidarEmulator);
    // SHADOW REGIONS
    ShadowMapSpherical smPedLegal = //
        new ShadowMapSpherical(lidarEmulator, irPedLegal, PED_VELOCITY, PED_RADIUS);
    ShadowMapSpherical smPedIllegal = //
        new ShadowMapSpherical(lidarEmulator, irPedIllegal, PED_VELOCITY, PED_RADIUS);
    ShadowMapDirected smCar = //
        new ShadowMapDirected(lidarEmulator, irCar, "/simulation/s3/car_lanes.png", CAR_VELOCITY);
    //
    // SHADOW REGION CONSTRAINTS
    PlannerConstraint pedLegalConst = new SimpleShadowConstraintCV(smPedLegal, MAX_A, REACTION_TIME, true);
    PlannerConstraint pedIllegalConst = new SimpleShadowConstraintCV(smPedIllegal, MAX_A, REACTION_TIME, true);
    PlannerConstraint carConst = new SimpleShadowConstraintCV(smCar, MAX_A, REACTION_TIME, true);
    //constraints.add(pedLegalConst);
    constraints.add(pedIllegalConst);
    //constraints.add(carConst);
    //
    // SETUP PLANNER
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical(goal, goalRadius);
    // RENDERING
    Tse2MinTimeGoalManager tse2MinTimeGoalManager = new Tse2MinTimeGoalManager(tse2ComboRegion, controls, MAX_SPEED);
    GoalInterface goalInterface = MultiCostGoalAdapter.of(tse2MinTimeGoalManager.getGoalInterface(), extraCosts);
    TrajectoryPlanner tp = new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXEDSTATEINTEGRATOR, controls, plannerConstraints, goalInterface);
    // SETUP CALLBACKS
    List<GlcPlannerCallback> callbacks = new ArrayList<>();
    //
    Thread mpw = new Thread(new Runnable() {
      @Override // from Runnable
      public void run() {
        Stopwatch stopwatch = Stopwatch.started();
        tp.insertRoot(initial);
        GlcExpand glcExpand = new GlcExpand(tp);
        glcExpand.findAny(MAX_STEPS);
        stopwatch.stop();
        System.out.println("Planning time: " + stopwatch.display_seconds());
        for (GlcPlannerCallback glcPlannerCallback : callbacks)
          glcPlannerCallback.expandResult(Collections.emptyList(), tp);
        //
        Optional<GlcNode> optional = tp.getBest();
        if (optional.isPresent()) {
          System.out.println("Cost to Goal: " + optional.get().costFromRoot());
          List<TrajectorySample> traj = //
              GlcTrajectories.detailedTrajectoryTo(tp.getStateIntegrator(), optional.get());
          owlyAnimationFrame.addBackground(new TreeRender(tp.getDomainMap().values()));
          TrajectoryRender trajectoryRender = new TrajectoryRender();
          trajectoryRender.trajectory(traj);
          trajectoryRender.setColor(Color.GREEN);
          owlyAnimationFrame.addBackground(trajectoryRender);
          // owlyAnimationFrame.addBackground(new QueueRender(tp.getQueue()));
        } else {
          System.out.println("no traj found");
        }
      }
    });
    mpw.start();
    // RENDERING
    owlyAnimationFrame.addBackground(EllipseRegionRender.of(new SphericalRegion(goal, goalRadius.Get(0))));
  }

  protected StateTimeRaster stateTimeRaster() {
    return new EtaRaster(PARTITIONSCALE, StateTimeTensorFunction.state(Tse2Wrap.INSTANCE::represent));
  }

  public static void main(String[] args) {
    new PlanningEvaluation0().start().jFrame.setVisible(true);
  }
}
