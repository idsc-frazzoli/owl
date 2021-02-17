// code by gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.List;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.bot.rn.rrts.RnRrtsFlow;
import ch.ethz.idsc.owl.bot.rn.rrts.RnRrtsNodeCollection;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.se2.rrts.CarRrtsFlow;
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransitionSpace;
import ch.ethz.idsc.owl.bot.se2.rrts.DubinsTransitionSpace;
import ch.ethz.idsc.owl.bot.se2.rrts.Se2RrtsNodeCollections;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.data.tree.Expand;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparators;
import ch.ethz.idsc.sophus.math.sample.BallRandomSample;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.ConstantRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Append;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DefaultRrtsPlannerServerTest extends TestCase {
  public void testRn() {
    Tensor goal = Tensors.vector(10, 10);
    Tensor state = Tensors.vector(0, 0);
    StateTime stateTime = new StateTime(state, RealScalar.ZERO);
    Scalar radius = Vector2Norm.between(goal, state).multiply(RationalScalar.HALF).add(RealScalar.ONE);
    Tensor center = Mean.of(Tensors.of(state, goal));
    Tensor min = center.map(scalar -> scalar.subtract(radius));
    Tensor max = center.map(scalar -> scalar.add(radius));
    // ---
    RrtsPlannerServer server = new DefaultRrtsPlannerServer( //
        RnTransitionSpace.INSTANCE, //
        EmptyTransitionRegionQuery.INSTANCE, //
        RationalScalar.of(1, 10), //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        LengthCostFunction.INSTANCE) {
      @Override
      protected RrtsNodeCollection rrtsNodeCollection() {
        return new RnRrtsNodeCollection(min, max);
      }

      @Override
      protected RandomSampleInterface spaceSampler(Tensor state) {
        return BallRandomSample.of(center, radius);
      }

      @Override
      protected RandomSampleInterface goalSampler(Tensor goal) {
        return new ConstantRandomSample(goal);
      }

      @Override
      protected Tensor uBetween(StateTime orig, StateTime dest) {
        return RnRrtsFlow.uBetween(orig, dest);
      }
    };
    server.setGoal(goal);
    server.insertRoot(stateTime);
    new Expand<>(server).steps(400);
    // ---
    assertTrue(server.getTrajectory().isPresent());
    List<TrajectorySample> trajectory = server.getTrajectory().get();
    Chop._05.requireClose(goal, Lists.getLast(trajectory).stateTime().state());
  }

  public void testDubins() {
    Tensor lbounds = Tensors.vector(0, 0, -Math.PI);
    Tensor ubounds = Tensors.vector(10, 10, Math.PI);
    Tensor goal = Tensors.vector(10, 10, 0);
    Tensor state = Tensors.vector(0, 0, 0);
    StateTime stateTime = new StateTime(state, RealScalar.ZERO);
    // ---
    RrtsPlannerServer server = new DefaultRrtsPlannerServer( //
        DubinsTransitionSpace.of(RealScalar.ONE, DubinsPathComparators.LENGTH), //
        EmptyTransitionRegionQuery.INSTANCE, //
        RationalScalar.of(1, 10), //
        Se2StateSpaceModel.INSTANCE, //
        LengthCostFunction.INSTANCE) {
      @Override
      protected RrtsNodeCollection rrtsNodeCollection() {
        return new RnRrtsNodeCollection(lbounds, ubounds);
      }

      @Override
      protected RandomSampleInterface spaceSampler(Tensor state) {
        return BoxRandomSample.of(lbounds, ubounds);
      }

      @Override
      protected RandomSampleInterface goalSampler(Tensor goal) {
        return new ConstantRandomSample(goal);
      }

      @Override
      protected Tensor uBetween(StateTime orig, StateTime dest) {
        return CarRrtsFlow.uBetween(orig, dest);
      }
    };
    server.setGoal(goal);
    server.insertRoot(stateTime);
    new Expand<>(server).steps(400);
    // ---
    assertTrue(server.getTrajectory().isPresent());
    List<TrajectorySample> trajectory = server.getTrajectory().get();
    Chop._05.requireClose(goal, Lists.getLast(trajectory).stateTime().state());
  }

  public void testClothoid() {
    Tensor lbounds = Tensors.vector(0, 0);
    Tensor ubounds = Tensors.vector(10, 10);
    Tensor goal = Tensors.vector(10, 10, 0);
    Tensor state = Tensors.vector(0, 0, 0);
    StateTime stateTime = new StateTime(state, RealScalar.ZERO);
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of( //
        Append.of(lbounds, Pi.VALUE.negate()), //
        Append.of(ubounds, Pi.VALUE));
    // ---
    RrtsPlannerServer server = new DefaultRrtsPlannerServer( //
        ClothoidTransitionSpace.ANALYTIC, //
        EmptyTransitionRegionQuery.INSTANCE, //
        RationalScalar.of(1, 10), //
        Se2StateSpaceModel.INSTANCE, //
        LengthCostFunction.INSTANCE) {
      @Override
      protected RrtsNodeCollection rrtsNodeCollection() {
        return Se2RrtsNodeCollections.of(getTransitionSpace(), lbounds, ubounds);
      }

      @Override
      protected RandomSampleInterface spaceSampler(Tensor state) {
        return randomSampleInterface;
      }

      @Override
      protected RandomSampleInterface goalSampler(Tensor goal) {
        return new ConstantRandomSample(goal);
      }

      @Override
      protected Tensor uBetween(StateTime orig, StateTime dest) {
        return CarRrtsFlow.uBetween(orig, dest);
      }
    };
    server.setGoal(goal);
    server.insertRoot(stateTime);
    new Expand<>(server).steps(400);
    // ---
    assertTrue(server.getTrajectory().isPresent());
    List<TrajectorySample> trajectory = server.getTrajectory().get();
    Chop._05.requireClose(goal, Lists.getLast(trajectory).stateTime().state());
  }
}
