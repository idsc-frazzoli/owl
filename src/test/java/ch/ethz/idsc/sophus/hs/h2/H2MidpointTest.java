// code by jph
package ch.ethz.idsc.sophus.hs.h2;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.math.sample.BallRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.SphereFit;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class H2MidpointTest extends TestCase {
  public void testSymmetric() {
    Random random = new Random();
    RandomSampleInterface randomSampleInterface = BallRandomSample.of(Array.zeros(2), RealScalar.ONE);
    for (int count = 0; count < 10; ++count) {
      Tensor a = randomSampleInterface.randomSample(random);
      Tensor midpoint = H2Midpoint.INSTANCE.midpoint(a, a.negate());
      Chop._12.requireClose(midpoint, Array.zeros(2));
    }
  }

  public void testLine() {
    Tensor midpoint = H2Midpoint.INSTANCE.midpoint(Tensors.vector(0.5, 0.5), Tensors.vector(0.25, 0.25));
    Chop._12.requireClose(midpoint.Get(0), midpoint.Get(1));
  }

  public void testCircle() throws IOException, ClassNotFoundException {
    CurveSubdivision curveSubdivision = //
        Serialization.copy(LaneRiesenfeldCurveSubdivision.of(H2Midpoint.INSTANCE, 1));
    Tensor tensor = Nest.of(curveSubdivision::string, Tensors.fromString("{{0.5, 0}, {0.0, 0.5}}"), 6);
    Optional<SphereFit> optional = SphereFit.of(tensor);
    SphereFit sphereFit = optional.get();
    Chop._12.requireClose(sphereFit.center(), Tensors.vector(1.25, 1.25));
    Chop._12.requireClose(sphereFit.radius(), RealScalar.of(1.457737973711335));
    Tensor residual = Tensor.of(tensor.stream() //
        .map(sphereFit.center()::subtract) //
        .map(Norm._2::ofVector) //
        .map(sphereFit.radius()::subtract));
    assertTrue(Chop._12.allZero(residual));
  }
}
