// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.r2.ImageGradientInterpolation;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class DeltaControlsTest extends TestCase {
  public void testFlows() {
    ImageGradientInterpolation imageGradientInterpolation = ImageGradientInterpolation.linear( //
        ResourceData.of("/io/delta_uxy.png"), Tensors.vector(10, 10), RealScalar.of(0.1));
    Scalar maxNormGradient = imageGradientInterpolation.maxNormGradient();
    assertTrue(Sign.isPositive(maxNormGradient));
    Scalar amp = RealScalar.of(2);
    new DeltaStateSpaceModel(imageGradientInterpolation);
    Collection<Tensor> controls = new DeltaFlows(amp).getFlows(20);
    Scalar max = DeltaControls.maxSpeed(controls);
    Chop._12.requireClose(max, amp);
  }
}
