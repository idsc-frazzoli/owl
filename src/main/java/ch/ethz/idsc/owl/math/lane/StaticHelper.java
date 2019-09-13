package ch.ethz.idsc.owl.math.lane;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.sophus.math.sample.RegionRandomSample;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum StaticHelper {
  ;
  public static RegionRandomSample combine(RandomSampleInterface randomSampleInterface, Region<Tensor> region) {
    return new RegionRandomSample(randomSampleInterface, region, Extract2D.FUNCTION);
  }
}
