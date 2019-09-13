// code by gjoel
package ch.ethz.idsc.sophus.math.sample;

import java.util.Random;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

// TODO JPH these are two classes implemented in 1 class: implements region with member region! do not implement region
public class RegionRandomSample implements Region<Tensor>, RandomSampleInterface {
  private static final int MAX_ITERATIONS = 100;
  // ---
  private final RandomSampleInterface randomSampleInterface;
  private final Region<Tensor> region;
  private final TensorUnaryOperator tensorUnaryOperator;

  public RegionRandomSample( //
      RandomSampleInterface randomSampleInterface, //
      Region<Tensor> region, //
      TensorUnaryOperator tensorUnaryOperator) {
    this.randomSampleInterface = randomSampleInterface;
    this.region = region;
    this.tensorUnaryOperator = tensorUnaryOperator;
  }

  @Override // from Region<Tensor>
  public boolean isMember(Tensor element) {
    return region.isMember(tensorUnaryOperator.apply(element));
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    for (int count = 0; count < MAX_ITERATIONS; ++count) {
      Tensor sample = randomSampleInterface.randomSample(random);
      if (isMember(sample))
        return sample;
    }
    throw new RuntimeException("unable to generate sample within region");
  }

  // function is used for drawing
  public Region<Tensor> region() {
    return region;
  }
}
