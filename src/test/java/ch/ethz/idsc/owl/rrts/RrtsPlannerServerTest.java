// code by gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.List;

import ch.ethz.idsc.owl.bot.rn.RnRrtsNodeCollection;
import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
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
    StateTime stateTime = new StateTime(state, RationalScalar.ZERO);
    Scalar radius = Norm._2.between(goal, state).multiply(RationalScalar.HALF).add(RealScalar.ONE);
    Tensor center = Mean.of(Tensors.of(state, goal));
    Tensor min = center.map(scalar -> scalar.subtract(radius));
    Tensor max = center.map(scalar -> scalar.add(radius));
    // ---
    RrtsPlannerServer server = new RrtsPlannerServer( //
        RnTransitionSpace.INSTANCE, //
        EmptyTransitionRegionQuery.INSTANCE, //
        RationalScalar.of(1,10), //
        SingleIntegratorStateSpaceModel.INSTANCE) {
      @Override
      protected RrtsNodeCollection rrtsNodeCollection() {
        return new RnRrtsNodeCollection(min, max);
      }

      @Override
      protected RandomSampleInterface spaceSampler() {
        return SphereRandomSample.of(center, radius);
      }

      @Override
      protected RandomSampleInterface goalSampler() {
        return SphereRandomSample.of(goal, RationalScalar.ZERO);
      }
    };
    server.offer(stateTime).run(400);
    // ---
    assertTrue(server.getTrajectory().isPresent());
    List<TrajectorySample> trajectory = server.getTrajectory().get();
    Chop._01.requireClose(goal, Lists.getLast(trajectory).stateTime().state());
  }
}
