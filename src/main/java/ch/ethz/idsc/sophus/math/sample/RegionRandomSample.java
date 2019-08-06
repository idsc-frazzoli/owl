// code by gjoel
package ch.ethz.idsc.sophus.math.sample;

import java.util.Random;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class RegionRandomSample implements Region<Tensor>, RandomSampleInterface {
  private static final int MAX_TRIES = 100;

  public static RegionRandomSample combine(RandomSampleInterface randomSampleInterface, Region<Tensor> region) {
    return combine(randomSampleInterface, region, Extract2D.FUNCTION);
  }

  public static RegionRandomSample combine(RandomSampleInterface randomSampleInterface, Region<Tensor> region, TensorUnaryOperator conversion) {
    return new RegionRandomSample(randomSampleInterface, region, conversion);
  }

  // ---
  private final RandomSampleInterface randomSampleInterface;
  private final Region<Tensor> region;
  private final TensorUnaryOperator conversion;

  private RegionRandomSample(RandomSampleInterface randomSampleInterface, Region<Tensor> region, TensorUnaryOperator conversion) {
    this.randomSampleInterface = randomSampleInterface;
    this.region = region;
    this.conversion = conversion;
  }

  @Override // from Region<Tensor>
  public boolean isMember(Tensor element) {
    return region.isMember(conversion.apply(element));
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    for (int count = 0; count < MAX_TRIES; count++) {
      Tensor sample = randomSampleInterface.randomSample(random);
      if (isMember(sample))
        return sample;
    }
    throw new RuntimeException("unable to generate sample within region");
  }

  public Region<Tensor> region() {
    return region;
  }
}
