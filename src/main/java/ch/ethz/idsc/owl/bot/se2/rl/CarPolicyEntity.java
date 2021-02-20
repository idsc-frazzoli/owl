// code by jph
package ch.ethz.idsc.owl.bot.se2.rl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.ani.api.PolicyEntity;
import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.subare.core.RewardInterface;
import ch.ethz.idsc.subare.core.StateActionCounter;
import ch.ethz.idsc.subare.core.StepInterface;
import ch.ethz.idsc.subare.core.adapter.StepAdapter;
import ch.ethz.idsc.subare.core.td.Sarsa;
import ch.ethz.idsc.subare.core.td.SarsaType;
import ch.ethz.idsc.subare.core.util.DefaultLearningRate;
import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import ch.ethz.idsc.subare.core.util.DiscreteStateActionCounter;
import ch.ethz.idsc.subare.core.util.LearningRate;
import ch.ethz.idsc.subare.core.util.PolicyBase;
import ch.ethz.idsc.subare.core.util.PolicyType;
import ch.ethz.idsc.subare.core.util.PolicyWrap;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.Rescale;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.qty.Degree;

public class CarPolicyEntity extends PolicyEntity implements RewardInterface {
  private static final Color[] COLORS = new Color[] { //
      new Color(255, 192, 192), //
      new Color(192, 255, 192), //
      new Color(192, 192, 255) };
  /** modified in constructor */
  private static final Tensor SHAPE = CirclePoints.of(5).multiply(RealScalar.of(0.1));
  static {
    SHAPE.set(Tensors.vector(0.2, 0), 0);
  }
  // ---
  private final Tensor start;
  private final SarsaType sarsaType;
  StateActionCounter sac = new DiscreteStateActionCounter();
  DiscreteQsa qsa;
  PolicyBase policy;
  private final LidarRaytracer LidarRaytracer;
  private LidarEmulator lidarEmulator;
  private final CarDiscreteModel carDiscreteModel;
  LearningRate learningRate = DefaultLearningRate.of(2, 0.51);
  private int collisionCount = 0;
  private final TrajectoryRegionQuery raytraceQuery;

  /** @param start
   * @param sarsaType
   * @param raytraceQuery */
  public CarPolicyEntity(Tensor start, SarsaType sarsaType, TrajectoryRegionQuery raytraceQuery) {
    this.start = start;
    this.sarsaType = sarsaType;
    CarDiscreteModel carDiscreteModel = new CarDiscreteModel(5, 2);
    qsa = DiscreteQsa.build(carDiscreteModel);
    policy = PolicyType.EGREEDY.bestEquiprobable(carDiscreteModel, qsa, sac);
    this.carDiscreteModel = carDiscreteModel;
    this.raytraceQuery = raytraceQuery;
    // ---
    LidarRaytracer = new LidarRaytracer( //
        Subdivide.of(Degree.of(+50), Degree.of(-50), carDiscreteModel.resolution - 1), //
        Subdivide.of(0.0, 5.0, 23));
    lidarEmulator = new LidarEmulator(LidarRaytracer, this::getStateTimeNow, raytraceQuery);
    reset(RealScalar.ZERO);
  }

  private void reset(Scalar now) {
    prev_state = null;
    prev_action = null;
    prev_reward = null;
    if (!episodeLog.isEmpty()) {
      // System.out.println("learn " + episodeLog.size());
      Sarsa sarsa = sarsaType.sarsa(carDiscreteModel, learningRate, qsa, sac, policy);
      int nstep = 50;
      Deque<StepInterface> deque = new LinkedList<>(episodeLog.subList(Math.max(1, episodeLog.size() - nstep), episodeLog.size()));
      while (!deque.isEmpty()) {
        sarsa.digest(deque);
        deque.poll();
      }
      policy = PolicyType.EGREEDY.bestEquiprobable(carDiscreteModel, qsa, sac);
      episodeLog.clear();
    }
    StateTime stateTime = new StateTime(start, now);
    episodeIntegrator = new SimpleEpisodeIntegrator(Se2StateSpaceModel.INSTANCE, Se2CarIntegrator.INSTANCE, stateTime);
  }

  private final List<StepInterface> episodeLog = new LinkedList<>();
  private Tensor prev_state;
  private Tensor prev_action; // {1, 0, al}
  private Scalar prev_reward;

  @Override // from AnimationInterface
  public final void integrate(Scalar now) {
    // implementation does not require that current position is perfectly located on trajectory
    StateTime stateTime = getStateTimeNow();
    Tensor pair = represent(stateTime); // may be augmented state time, and/or observation etc.
    Tensor state = pair.get(0);
    if (Objects.nonNull(prev_state) && !carDiscreteModel.isTerminal(prev_state)) {
      // <- compute reward based on prev_state,
      prev_reward = reward(prev_state, prev_action, state);
      // GlobalAssert.that(Objects.nonNull(prev_reward));
      episodeLog.add(new StepAdapter(prev_state, prev_action, prev_reward, state));
    }
    prev_state = state;
    PolicyWrap policyWrap = new PolicyWrap(policy);
    Tensor actions = carDiscreteModel.actions(state);
    prev_action = policyWrap.next(state, actions);
    prev_action.set(s -> s.multiply(pair.Get(1)), 1);
    // System.out.println(prev_action);
    episodeIntegrator.move(prev_action, now);
    if (carDiscreteModel.isTerminal(prev_state)) {
      ++collisionCount;
      reset(now);
    }
  }

  @Override
  public Tensor represent(StateTime stateTime) {
    Tensor range = LidarRaytracer.scan(stateTime, raytraceQuery);
    return ScanToState.of(range);
  }

  @Override // from RewardInterface
  public Scalar reward(Tensor state, Tensor action, Tensor next) {
    return !carDiscreteModel.isTerminal(state) && carDiscreteModel.isTerminal(next) //
        ? RealScalar.of(-1)
        : RealScalar.ZERO;
  }

  @Override
  protected Tensor shape() {
    return SHAPE;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(COLORS[sarsaType.ordinal()]);
    super.render(geometricLayer, graphics);
    {
      StateTime stateTime = getStateTimeNow();
      Point2D p = geometricLayer.toPoint2D(stateTime.state());
      int pix = (int) p.getX();
      int piy = (int) p.getY();
      {
        Tensor values = qsa.values();
        Tensor imag = Rescale.of(Partition.of(values, 8)).map(ColorDataGradients.CLASSIC);
        BufferedImage bufferedImage = ImageFormat.of(imag);
        int mag = 4;
        graphics.drawImage(bufferedImage, pix, piy, //
            bufferedImage.getWidth() * mag, //
            bufferedImage.getHeight() * mag, null);
      }
      graphics.setColor(Color.GRAY);
      graphics.drawString("  " + collisionCount, pix, piy);
    }
    {
      lidarEmulator.render(geometricLayer, graphics);
    }
  }
}
