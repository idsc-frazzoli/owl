// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.gui.GeometricLayer;
import ch.ethz.idsc.owl.gui.ani.PolicyEntity;
import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.sim.LidarEmulator;
import ch.ethz.idsc.subare.core.LearningRate;
import ch.ethz.idsc.subare.core.Policy;
import ch.ethz.idsc.subare.core.RewardInterface;
import ch.ethz.idsc.subare.core.StepInterface;
import ch.ethz.idsc.subare.core.adapter.StepAdapter;
import ch.ethz.idsc.subare.core.td.OriginalSarsa;
import ch.ethz.idsc.subare.core.td.Sarsa;
import ch.ethz.idsc.subare.core.td.SarsaType;
import ch.ethz.idsc.subare.core.util.DefaultLearningRate;
import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import ch.ethz.idsc.subare.core.util.EGreedyPolicy;
import ch.ethz.idsc.subare.core.util.PolicyWrap;
import ch.ethz.idsc.subare.util.GlobalAssert;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Ordering;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.sca.Chop;

public class CarPolicyEntity extends PolicyEntity implements RewardInterface {
  private final Tensor SHAPE = CirclePoints.of(5).multiply(RealScalar.of(.1));
  // ---
  private final Tensor start;
  DiscreteQsa qsa;
  Policy policy;
  private LidarEmulator lidarEmulator;
  private final CarDiscreteModel carDiscreteModel;
  LearningRate learningRate = DefaultLearningRate.of(2, 0.51);

  /** @param start
   * @param raytraceQuery */
  public CarPolicyEntity(Tensor start, TrajectoryRegionQuery raytraceQuery) {
    this.start = start;
    CarDiscreteModel carDiscreteModel = new CarDiscreteModel(5);
    qsa = DiscreteQsa.build(carDiscreteModel);
    policy = EGreedyPolicy.bestEquiprobable(carDiscreteModel, qsa, RealScalar.of(0.2));
    this.carDiscreteModel = carDiscreteModel;
    // ---
    lidarEmulator = new LidarEmulator( //
        Subdivide.of(Degree.of(+90), Degree.of(-90), carDiscreteModel.resolution - 1), //
        this::getStateTimeNow, raytraceQuery);
    SHAPE.set(Tensors.vector(.2, 0), 0);
    reset(RealScalar.ZERO);
  }

  private void reset(Scalar now) {
    prev_state = null;
    prev_action = null;
    prev_reward = null;
    if (!episodeLog.isEmpty()) {
      // System.out.println("learn " + episodeLog.size());
      SarsaType.qlearning.supply(carDiscreteModel, qsa, learningRate);
      Sarsa sarsa = new OriginalSarsa(carDiscreteModel, qsa, learningRate);
      int nstep = 50;
      Deque<StepInterface> deque = new LinkedList<>(episodeLog.subList(Math.max(1, episodeLog.size() - nstep), episodeLog.size()));
      while (!deque.isEmpty()) {
        sarsa.digest(deque);
        deque.poll();
      }
      policy = EGreedyPolicy.bestEquiprobable(carDiscreteModel, qsa, RealScalar.of(0.1));
      episodeLog.clear();
    }
    StateTime stateTime = new StateTime(start, now);
    episodeIntegrator = new SimpleEpisodeIntegrator(Se2StateSpaceModel.INSTANCE, Se2CarIntegrator.INSTANCE, stateTime);
  }

  private final List<StepInterface> episodeLog = new LinkedList<>();
  private Tensor prev_state;
  private Tensor prev_action;
  private Scalar prev_reward;

  @Override // from AnimationInterface
  public final void integrate(Scalar now) {
    // implementation does not require that current position is perfectly located on trajectory
    StateTime stateTime = getStateTimeNow();
    Tensor state = represent(stateTime); // may be augmented state time, and/or observation etc.
    if (Objects.nonNull(prev_state) && !carDiscreteModel.isTerminal(prev_state)) {
      // <- compute reward based on prev_state,
      prev_reward = reward(prev_state, prev_action, state);
      GlobalAssert.that(Objects.nonNull(prev_reward));
      episodeLog.add(new StepAdapter(prev_state, prev_action, prev_reward, state));
    }
    prev_state = state;
    PolicyWrap policyWrap = new PolicyWrap(policy);
    Tensor actions = carDiscreteModel.actions(state);
    prev_action = policyWrap.next(state, actions);
    episodeIntegrator.move(prev_action, now);
    if (carDiscreteModel.isTerminal(prev_state)) {
      reset(now);
    }
  }

  @Override
  public Tensor represent(StateTime stateTime) {
    Tensor range = lidarEmulator.detectRange(stateTime);
    if (Chop.NONE.allZero(range))
      return CarDiscreteModel.COLLISION;
    return Tensors.vectorInt(Ordering.DECREASING.of(range));
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
    super.render(geometricLayer, graphics);
    {
      StateTime stateTime = getStateTimeNow();
      Point2D p = geometricLayer.toPoint2D(stateTime.state());
      graphics.drawString(":-)", (int) p.getX(), (int) p.getY());
    }
    {
      lidarEmulator.render(geometricLayer, graphics);
    }
  }
}
