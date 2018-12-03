// code by ob
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.owl.math.SmoothingKernel;
import ch.ethz.idsc.owl.math.group.LieDifferences;
import ch.ethz.idsc.owl.math.group.LieGroupGeodesic;
import ch.ethz.idsc.owl.math.group.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.group.Se2Geodesic;
import ch.ethz.idsc.owl.math.group.Se2Group;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Round;

class GeodesicCausalFiltering {
  private static final LieDifferences LIE_DIFFERENCES = //
      new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
  // ---
  /** raw data */
  private final Tensor measurements;
  /** filtered data which we use as 'truth' */
  private final Tensor reference;
  /** applied geodesic prediction */
  private Tensor update; //
  /** window size of Filter */
  private int width = 5;
  /** log of alphas and corresponding errors */
  public Tensor log = Tensors.empty();

  GeodesicCausalFiltering(Tensor measurements) {
    this.measurements = measurements;
    GeodesicInterface geodesicInterface = //
        new LieGroupGeodesic(Se2Group.INSTANCE::element, Se2CoveringExponential.INSTANCE);
    {
      TensorUnaryOperator geodesicCenterFilter = //
          GeodesicCenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN), width);
      reference = geodesicCenterFilter.apply(measurements);
    }
    Tensor alpharange = Subdivide.of(0.1, 1, 12);
    for (int j = 0; j < alpharange.length(); ++j) {
      update = measurements.extract(0, 2);
      Scalar alpha = alpharange.Get(j);
      Tensor errors = Tensors.empty();
      GeodesicCausal1Filter geodesicCausal1Filter = //
          new GeodesicCausal1Filter(geodesicInterface, alpha, measurements.get(0), measurements.get(1));
      for (int i = 2; i < measurements.length(); ++i) {
        update.append(geodesicCausal1Filter.apply(measurements.get(i)));
        errors.append(Norm._2.ofVector(LIE_DIFFERENCES.pair(reference.get(i), update.get(i))));
      }
      log.append(Tensors.of(alpha, Total.of(errors)).map(Round._5));
    }
  }

  public static void main(String[] args) {
    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + "0w/20180702T133612_1" + ".csv").stream().map(row -> row.extract(1, 4)));
    GeodesicCausalFiltering GeodesicCausalFiltering = new GeodesicCausalFiltering(control);
    System.out.println(Pretty.of(GeodesicCausalFiltering.log));
  }
}
