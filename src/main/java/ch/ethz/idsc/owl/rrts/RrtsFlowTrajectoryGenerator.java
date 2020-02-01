// code by jph, gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.math.IntegerLog2;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.adapter.DirectedTransition;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.math.Distances;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class RrtsFlowTrajectoryGenerator {
  private final StateSpaceModel stateSpaceModel;
  private final BiFunction<StateTime, StateTime, Tensor> uBetween;
  private CurveSubdivision curveSubdivision = null;
  private TensorMetric tensorMetric = null;

  public RrtsFlowTrajectoryGenerator( //
      StateSpaceModel stateSpaceModel, //
      BiFunction<StateTime, StateTime, Tensor> uBetween) {
    this.stateSpaceModel = stateSpaceModel;
    this.uBetween = uBetween;
  }

  /** @param curveSubdivision interpolation scheme
   * @param tensorMetric distance metric between samples */
  public void addPostProcessing(CurveSubdivision curveSubdivision, TensorMetric tensorMetric) {
    this.curveSubdivision = Objects.requireNonNull(curveSubdivision);
    this.tensorMetric = Objects.requireNonNull(tensorMetric);
  }

  /** @param transitionSpace
   * @param sequence of control point nodes
   * @param dt minimal time resolution
   * @return trajectory */
  public List<TrajectorySample> createTrajectory( //
      TransitionSpace transitionSpace, List<RrtsNode> sequence, Scalar t0, final Scalar dt) {
    if (Objects.isNull(curveSubdivision))
      return standardTrajectory(transitionSpace, sequence, t0, dt);
    return postProcessedTrajectory(transitionSpace, sequence, t0, dt);
  }

  private List<TrajectorySample> standardTrajectory( //
      TransitionSpace transitionSpace, List<RrtsNode> sequence, Scalar t0, final Scalar dt) {
    List<TrajectorySample> trajectory = new LinkedList<>();
    RrtsNode prev = sequence.get(0);
    trajectory.add(TrajectorySample.head(new StateTime(prev.state(), t0)));
    for (RrtsNode node : sequence.subList(1, sequence.size())) {
      Transition transition = transitionSpace.connect(prev.state(), node.state());
      TransitionWrap transitionWrap = transition.wrapped(dt);
      Tensor samples = transitionWrap.samples();
      Tensor spacing = transitionWrap.spacing();
      Scalar ti = t0;
      for (int i = 0; i < samples.length(); i++) {
        ti = ti.add(spacing.Get(i));
        StateTime stateTime = new StateTime(samples.get(i), ti);
        StateTime orig = Lists.getLast(trajectory).stateTime();
        // TODO GJOEL this boolean expression appears twice => extract to function
        Tensor u = (transition instanceof DirectedTransition && !((DirectedTransition) transition).isForward) //
            ? uBetween.apply(stateTime, orig) //
            : uBetween.apply(orig, stateTime);
        trajectory.add(new TrajectorySample(stateTime, u));
      }
      prev = node;
      t0 = t0.add(transition.length());
    }
    return trajectory;
  }

  private List<TrajectorySample> postProcessedTrajectory( //
      TransitionSpace transitionSpace, List<RrtsNode> sequence, Scalar t0, final Scalar dt) {
    List<TrajectorySample> trajectory = new LinkedList<>();
    Iterator<RrtsNode> iterator = sequence.iterator();
    RrtsNode prev = iterator.next();
    trajectory.add(TrajectorySample.head(new StateTime(prev.state(), t0)));
    boolean prevDirection = true;
    List<RrtsNode> segment = new ArrayList<>();
    while (iterator.hasNext()) {
      RrtsNode node = iterator.next();
      Transition transition = transitionSpace.connect(prev.state(), node.state());
      boolean direction = (!(transition instanceof DirectedTransition)) || ((DirectedTransition) transition).isForward;
      if (direction != prevDirection) {
        flush(transitionSpace, trajectory, segment, prevDirection, dt);
        prevDirection = direction;
        segment = new ArrayList<>();
      }
      if (segment.isEmpty())
        segment.add(prev);
      segment.add(node);
      prev = node;
    }
    return flush(transitionSpace, trajectory, segment, prevDirection, dt);
  }

  private List<TrajectorySample> flush(TransitionSpace transitionSpace, List<TrajectorySample> trajectory, List<RrtsNode> segment, boolean direction,
      Scalar dt) {
    if (!segment.isEmpty()) {
      Tensor points = Tensor.of(segment.stream().map(RrtsNode::state));
      Scalar maxLength = //
          segment.subList(1, segment.size()).stream().map(node -> transitionSpace.connect(node.parent().state(), node.state()).length()).reduce(Max::of).get();
      Scalar t0 = Lists.getLast(trajectory).stateTime().time();
      int depth = IntegerLog2.ceiling(Ceiling.of(maxLength.divide(Sign.requirePositive(dt))).number().intValue());
      if (!direction)
        points = Reverse.of(points);
      Tensor samples = Nest.of(curveSubdivision::string, points, depth);
      Tensor spacing = Distances.of(tensorMetric, samples);
      if (!direction) {
        samples = Reverse.of(samples);
        spacing = Reverse.of(spacing);
      }
      Scalar ti = t0;
      for (int i = 1; i < samples.length(); i++) {
        ti = ti.add(spacing.Get(i - 1));
        StateTime stateTime = new StateTime(samples.get(i), ti);
        StateTime orig = Lists.getLast(trajectory).stateTime();
        Tensor u = direction //
            ? uBetween.apply(orig, stateTime) //
            : uBetween.apply(stateTime, orig);
        trajectory.add(new TrajectorySample(stateTime, u));
      }
    }
    return trajectory;
  }
}
