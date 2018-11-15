// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.r2.ImageGradientInterpolation;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.HeuristicQ;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DeltaMinTimeGoalManagerTest extends TestCase {
  public void testConstructors() {
    ImageGradientInterpolation imageGradientInterpolation = ImageGradientInterpolation.linear( //
        ResourceData.of("/io/delta_uxy.png"), Tensors.vector(10, 10), RealScalar.of(.1));
    Scalar maxNormGradient = imageGradientInterpolation.maxNormGradient();
    assertTrue(Scalars.lessThan(RealScalar.ZERO, maxNormGradient));
    Scalar amp = RealScalar.of(2);
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation);
    Collection<Flow> controls = new DeltaFlows(stateSpaceModel, amp).getFlows(20);
    assertTrue(Chop._10.close(DeltaControls.maxSpeed(controls), amp));
    Scalar maxMove = DeltaControls.maxSpeed(controls).add(imageGradientInterpolation.maxNormGradient());
    assertTrue(Chop._10.close(maxMove, stateSpaceModel.getLipschitz().add(amp)));
    RegionWithDistance<Tensor> regionWithDistance = //
        new SphericalRegion(Tensors.vector(1, 1), RealScalar.ONE);
    GoalInterface dmtgm = new DeltaMinTimeGoalManager(regionWithDistance, maxMove);
    assertTrue(HeuristicQ.of(dmtgm));
  }
}
