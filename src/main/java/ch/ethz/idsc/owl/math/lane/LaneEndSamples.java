// code by gjoel
package ch.ethz.idsc.owl.math.lane;

import java.util.Random;

import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.sophus.math.sample.RegionRandomSample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Clips;

public enum LaneEndSamples {
  ;
  /** TODO GJOEL document
   * 
   * @param laneInterface
   * @param rotDist
   * @param mu_r
   * @param semi
   * @return */
  public static RegionRandomSample cone(LaneInterface laneInterface, Distribution rotDist, Scalar mu_r, Scalar semi) {
    Distribution distribution = UniformDistribution.of(Clips.absolute(semi));
    Tensor point = Last.of(laneInterface.midLane());
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
    return StaticHelper.combine(randomSampleInterface, new ConeRegion(point, semi));
  }

  public static RegionRandomSample endSample(LaneInterface laneInterface, Distribution rotDist) {
    return StaticHelper.combine( //
        LaneRandomSample.of(laneInterface, rotDist).around(laneInterface.midLane().length() - 1), //
        new SphericalRegion(Extract2D.FUNCTION.apply(Last.of(laneInterface.midLane())), Last.of(laneInterface.margins())));
  }
}
