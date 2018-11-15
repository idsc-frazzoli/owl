// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.r2.ImageGradientInterpolation;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class DeltaControlsTest extends TestCase {
  public void testFlows() {
    ImageGradientInterpolation imageGradientInterpolation = ImageGradientInterpolation.linear( //
        ResourceData.of("/io/delta_uxy.png"), Tensors.vector(10, 10), RealScalar.of(.1));
    Scalar maxNormGradient = imageGradientInterpolation.maxNormGradient();
    assertTrue(Sign.isPositive(maxNormGradient));
    Scalar amp = RealScalar.of(2);
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation);
    Collection<Flow> controls = new DeltaFlows(stateSpaceModel, amp).getFlows(20);
    Scalar max = DeltaControls.maxSpeed(controls);
    assertTrue(Chop._12.close(max, amp));
  }
}
