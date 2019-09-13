// code by jph
package ch.ethz.idsc.owl.math.lane;

import java.util.Random;

import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.sophus.math.sample.RegionRandomSample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Clips;

public class ConeRandomSample extends RegionRandomSample {
  /** TODO GJOEL document
   * 
   * @param point
   * @param rotDist
   * @param mu_r
   * @param semi
   * @return */
  public static RegionRandomSample of(Tensor point, Distribution rotDist, Scalar mu_r, Scalar semi) {
    Distribution distribution = UniformDistribution.of(Clips.absolute(semi));
    Se2GroupElement se2GroupElement = new Se2GroupElement(point);
    RandomSampleInterface randomSampleInterface = new RandomSampleInterface() {
      @Override
      public Tensor randomSample(Random random) {
        Scalar r = RandomVariate.of(NormalDistribution.standard(), random).abs().multiply(mu_r);
        Scalar a1 = RandomVariate.of(distribution, random);
        Tensor xy = AngleVector.of(a1).multiply(r);
        Scalar a2 = RandomVariate.of(rotDist, random);
        return se2GroupElement.combine(xy.append(a2));
      }
    };
    return new ConeRandomSample(randomSampleInterface, new ConeRegion(point, semi));
  }

  // ---
  private ConeRandomSample(RandomSampleInterface randomSampleInterface, Region<Tensor> region) {
    super(randomSampleInterface, region, Extract2D.FUNCTION);
  }
}
