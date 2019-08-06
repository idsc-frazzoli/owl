// code by gjoel
package ch.ethz.idsc.owl.math.lane;

import java.util.Arrays;
import java.util.Random;

import ch.ethz.idsc.sophus.math.sample.BiasedSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.EmpiricalDistribution;
import ch.ethz.idsc.tensor.pdf.InverseCDF;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.N;

public class BiasedLaneSample extends LaneRandomSample implements BiasedSample {
  public static RandomSampleInterface along(LaneInterface laneInterface) {
    return new BiasedLaneSample(laneInterface);
  }

  // ---
  private int[] weights;
  private int index;

  private BiasedLaneSample(LaneInterface lane) {
    super(lane);
    resetAll();
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    Distribution distribution = EmpiricalDistribution.fromUnscaledPDF(Tensor.of(Arrays.stream(weights).mapToObj(RealScalar::of)));
    Scalar p = RandomVariate.of(UniformDistribution.unit(), random);
    index = ((InverseCDF) distribution).quantile(p).number().intValue();
    return around(index).randomSample(random);
  }

  @Override // from BiasedSample
  public void encourage() {
    weights[index] = Math.max(1, weights[index] + 1);
  }

  @Override // from BiasedSample
  public void discourage() {
    weights[index] = Math.max(1, weights[index] - 1);
  }

  @Override // from BiasedSample
  public void resetCurrent() {
    weights[index] = 1;
  }

  @Override // from BiasedSample
  public void resetAll() {
    weights = laneInterface.midLane().stream().mapToInt(t -> 1).toArray();
  }

  /* package for testing */ Tensor weights() {
    Tensor weights_ = Tensor.of(Arrays.stream(weights).mapToObj(RealScalar::of));
    return weights_.divide(Norm._1.ofVector(weights_)).map(N.DOUBLE);
  }
}
