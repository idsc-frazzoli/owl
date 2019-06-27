// code by gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.List;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransitionSpace;
import ch.ethz.idsc.owl.bot.se2.rrts.DubinsTransitionSpace;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.sample.BoxRandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.math.sample.SphereRandomSample;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RrtsPlannerServerTest extends TestCase {
  public void testRn() throws Exception {
    Tensor goal = Tensors.vector(10, 10);
    Tensor state = Tensors.vector(0, 0);
    StateTime stateTime = new StateTime(state, RealScalar.ZERO);
    Scalar radius = Norm._2.between(goal, state).multiply(RationalScalar.HALF).add(RealScalar.ONE);
    Tensor center = Mean.of(Tensors.of(state, goal));
    Tensor min = center.map(scalar -> scalar.subtract(radius));
    Tensor max = center.map(scalar -> scalar.add(radius));
    // ---
    RrtsPlannerServer server = new RrtsPlannerServer( //
        RnTransitionSpace.INSTANCE, //
        EmptyTransitionRegionQuery.INSTANCE, //
        RationalScalar.of(1, 10), //
        SingleIntegratorStateSpaceModel.INSTANCE) {
      @Override
      protected RrtsNodeCollection rrtsNodeCollection() {
        return RrtsNodeCollections.rn(min, max);
      }

      @Override
      protected RandomSampleInterface spaceSampler(Tensor state) {
        return SphereRandomSample.of(center, radius);
      }

      @Override
      protected RandomSampleInterface goalSampler(Tensor goal) {
        return SphereRandomSample.of(goal, RealScalar.ZERO);
      }
    };
    server.setGoal(goal);
    server.offer(stateTime).run(400);
    // ---
    assertTrue(server.getTrajectory().isPresent());
    List<TrajectorySample> trajectory = server.getTrajectory().get();
    Chop._01.requireClose(goal, Lists.getLast(trajectory).stateTime().state());
  }

  public void testDubins() throws Exception {
    Tensor lbounds = Tensors.vector(0, 0, 0);
    Tensor ubounds = Tensors.vector(10, 10, 2 * Math.PI);
    Tensor goal = Tensors.vector(10, 10, 0);
    Tensor state = Tensors.vector(0, 0, 0);
    StateTime stateTime = new StateTime(state, RealScalar.ZERO);
    // ---
    RrtsPlannerServer server = new RrtsPlannerServer( //
        DubinsTransitionSpace.withRadius(RealScalar.ONE), //
        EmptyTransitionRegionQuery.INSTANCE, //
        RationalScalar.of(1, 10), //
        Se2StateSpaceModel.INSTANCE) {
      @Override
      protected RrtsNodeCollection rrtsNodeCollection() {
        return RrtsNodeCollections.euclidean(lbounds, ubounds);
      }

      @Override
      protected RandomSampleInterface spaceSampler(Tensor state) {
        return BoxRandomSample.of(lbounds, ubounds);
      }

      @Override
      protected RandomSampleInterface goalSampler(Tensor goal) {
        return SphereRandomSample.of(goal, RealScalar.ZERO);
      }
    };
    server.setGoal(goal);
    server.offer(stateTime).run(400);
    // ---
    assertTrue(server.getTrajectory().isPresent());
    List<TrajectorySample> trajectory = server.getTrajectory().get();
    Chop._01.requireClose(goal, Lists.getLast(trajectory).stateTime().state());
  }

  public void testClothoid() throws Exception {
    Tensor lbounds = Tensors.vector(0, 0, 0);
    Tensor ubounds = Tensors.vector(10, 10, 2 * Math.PI);
    Tensor goal = Tensors.vector(10, 10, 0);
    Tensor state = Tensors.vector(0, 0, 0);
    StateTime stateTime = new StateTime(state, RealScalar.ZERO);
    // ---
    RrtsPlannerServer server = new RrtsPlannerServer( //
        ClothoidTransitionSpace.INSTANCE, //
        EmptyTransitionRegionQuery.INSTANCE, //
        RationalScalar.of(1, 10), //
        Se2StateSpaceModel.INSTANCE) {
      @Override
      protected RrtsNodeCollection rrtsNodeCollection() {
        return RrtsNodeCollections.clothoid(lbounds, ubounds);
      }

      @Override
      protected RandomSampleInterface spaceSampler(Tensor state) {
        return BoxRandomSample.of(lbounds, ubounds);
      }

      @Override
      protected RandomSampleInterface goalSampler(Tensor goal) {
        return SphereRandomSample.of(goal, RealScalar.ZERO);
      }
    };
    server.setGoal(goal);
    server.offer(stateTime).run(400);
    // ---
    assertTrue(server.getTrajectory().isPresent());
    List<TrajectorySample> trajectory = server.getTrajectory().get();
    Chop._01.requireClose(goal, Lists.getLast(trajectory).stateTime().state());
  }
}
