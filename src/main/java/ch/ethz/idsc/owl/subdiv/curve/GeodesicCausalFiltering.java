// code by ob
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.owl.math.SmoothingKernel;
import ch.ethz.idsc.owl.math.group.LieDifferences;
import ch.ethz.idsc.owl.math.group.LieExponential;
import ch.ethz.idsc.owl.math.group.LieGroup;
import ch.ethz.idsc.owl.math.group.LieGroupGeodesic;
import ch.ethz.idsc.owl.math.group.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.group.Se2Geodesic;
import ch.ethz.idsc.owl.math.group.Se2Group;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Round;

class GeodesicCausalFiltering {
  public static GeodesicCausalFiltering se2(Tensor measurements) {
    return new GeodesicCausalFiltering(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, measurements);
  }

  private final LieDifferences LIE_DIFFERENCES;
  // = //
  //
  private final GeodesicInterface GEODESIC_INTERFACE;
  // = //
  // new LieGroupGeodesic(Se2Group.INSTANCE::element, Se2CoveringExponential.INSTANCE);
  // ---
  /** raw data */
  private final Tensor measurements;
  /** filtered data which we use as 'truth' */
  private final Tensor reference;
  /** window size of Filter */
  private int width = 5;

  GeodesicCausalFiltering(LieGroup lieGroup, LieExponential lieExponential, Tensor measurements) {
    this.LIE_DIFFERENCES = new LieDifferences(lieGroup, lieExponential);
    this.GEODESIC_INTERFACE = new LieGroupGeodesic(lieGroup::element, lieExponential);
    this.measurements = measurements;
    TensorUnaryOperator geodesicCenterFilter = //
        GeodesicCenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN), width);
    reference = geodesicCenterFilter.apply(measurements);
  }

  public Tensor filteredSignal(Scalar alpha) {
    return Tensor.of(measurements.stream() //
        .map(new GeodesicCausal1Filter(GEODESIC_INTERFACE, alpha)));
  }

  public Scalar evaluate0Error(Scalar alpha) {
    Tensor errors = Tensors.empty();
    GeodesicCausal1Filter geodesicCausal1Filter = //
        new GeodesicCausal1Filter(GEODESIC_INTERFACE, alpha);
    for (int i = 0; i < measurements.length(); ++i) {
      Tensor result = geodesicCausal1Filter.apply(measurements.get(i));
      errors.append(Norm._2.ofVector(LIE_DIFFERENCES.pair(reference.get(i), result)));
    }
    return Total.of(errors).Get();
  }

  
  public Scalar evaluate1Error(Scalar alpha) {
    Tensor errors = Tensors.of(RealScalar.of(0));
    GeodesicCausal1Filter geodesicCausal1Filter = //
        new GeodesicCausal1Filter(GEODESIC_INTERFACE, alpha);
    for (int i = 1; i < measurements.length(); ++i) {      
      Tensor result = geodesicCausal1Filter.apply(measurements.get(i));
      Tensor result_prev = geodesicCausal1Filter.apply(measurements.get(i-1));
      
      errors.append((Norm._2.of((LIE_DIFFERENCES.pair(reference.get(i-1), reference.get(i))).subtract(LIE_DIFFERENCES.pair(result_prev, result)))));
          
    }
    return Total.of(errors).Get();
  }
  
  public static void main(String[] args) {
    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + "0w/20180702T133612_1" + ".csv").stream().map(row -> row.extract(1, 4)));
    GeodesicCausalFiltering geodesicCausalFiltering = GeodesicCausalFiltering.se2(control);

    Tensor alpharange = Subdivide.of(0.1, 1, 12);
    Tensor log = Tensors.empty();
    for (int j = 0; j < alpharange.length(); ++j) {
      Scalar alpha = alpharange.Get(j);
      log.append(Tensors.of(alpha, geodesicCausalFiltering.evaluate1Error(alpha)));

    }
    System.out.println(Pretty.of(log.map(Round._4)));
  }
}
