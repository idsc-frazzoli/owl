// code by ob
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.filter.GeodesicFIR2Filter;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.LieGroupGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.math.CenterWindowSampler;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;

public class GeodesicCausalFilteringFIR {
  public static GeodesicCausalFilteringFIR se2(Tensor measurements, Tensor reference, int order) {
    return new GeodesicCausalFilteringFIR(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, measurements, reference, order);
  }

  private final LieDifferences lieDifferences;
  // = //
  //
  private final GeodesicInterface geodesicInterface;
  // = //
  // new LieGroupGeodesic(Se2Group.INSTANCE::element, Se2CoveringExponential.INSTANCE);
  // ---
  /** raw data */
  private final Tensor measurements;
  /** filtered data which we use as 'truth' */
  public Tensor reference;

  GeodesicCausalFilteringFIR(LieGroup lieGroup, LieExponential lieExponential, Tensor measurements, Tensor reference, int order) {
    this.lieDifferences = new LieDifferences(lieGroup, lieExponential);
    this.geodesicInterface = new LieGroupGeodesic(lieGroup::element, lieExponential);
    this.measurements = measurements;
    this.reference = reference;
  }

  /** @param alpha filter parameter
   * @return filtered signal when using given alpha */
  public Tensor filteredSignal(Scalar alpha) {
    return Tensor.of(measurements.stream() //
        .map(new GeodesicFIR2Filter(geodesicInterface, alpha)));
    // return Tensor.of(measurements.stream() //
    // .map(new GeodesicFIR3Filter(geodesicInterface, alpha)));
  }

  /** filter Lie Group elements and perform check
   * 
   * @param alpha
   * @return */
  public Scalar evaluate0Error(Scalar alpha) {
    Tensor errors = Tensors.empty();
    Tensor filtering = filteredSignal(alpha);
    for (int i = 0; i < measurements.length(); ++i) {
      Scalar scalar = Norm._2.ofVector(lieDifferences.pair(reference.get(i), filtering.get(i)));
      errors.append(scalar);
    }
    return Total.of(errors).Get();
  }

  public Tensor evaluate0ErrorSeperated(Scalar alpha) {
    Tensor errors = Tensors.empty();
    Tensor filtering = filteredSignal(alpha);
    for (int i = 0; i < measurements.length(); ++i) {
      Tensor difference = lieDifferences.pair(reference.get(i), filtering.get(i));
      Scalar scalar1 = Norm._2.ofVector(difference.extract(0, 2));
      Scalar scalar2 = Norm._2.ofVector(difference.extract(2, 3));
      errors.append(Tensors.of(scalar1, scalar2));
    }
    return Total.of(errors);
  }

  public Scalar evaluate1Error(Scalar alpha) {
    Tensor errors = Tensors.empty();
    Tensor filtering = filteredSignal(alpha);
    for (int i = 1; i < measurements.length(); ++i) {
      Tensor pair1 = lieDifferences.pair(reference.get(i - 1), reference.get(i));
      Tensor pair2 = lieDifferences.pair(filtering.get(i - 1), filtering.get(i));
      Scalar scalar = Norm._2.between(pair1, pair2);
      errors.append(scalar);
    }
    return Total.of(errors).Get();
  }

  public Tensor evaluate1ErrorSeperated(Scalar alpha) {
    Tensor errors = Tensors.empty();
    Tensor filtering = filteredSignal(alpha);
    for (int i = 1; i < measurements.length(); ++i) {
      Tensor pair1 = lieDifferences.pair(reference.get(i - 1), reference.get(i));
      Tensor pair2 = lieDifferences.pair(filtering.get(i - 1), filtering.get(i));
      Scalar scalar1 = Norm._2.between(pair1.extract(0, 2), pair2.extract(0, 2));
      Scalar scalar2 = Norm._2.between(pair1.extract(2, 3), pair2.extract(2, 3));
      errors.append(Tensors.of(scalar1, scalar2));
    }
    return Total.of(errors);
  }

  // Nur zum Testen von neuen methoden
  public static void main(String[] args) {
    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + //
        "0w/20180702T133612_1" + ".csv").stream().map(row -> row.extract(1, 4)));
    CenterWindowSampler centerWindowSampler = new CenterWindowSampler(SmoothingKernel.GAUSSIAN);
    for (int i = 1; i < 2; i++) {
      TensorUnaryOperator geodesicCenterFilter = //
          GeodesicCenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, centerWindowSampler), i);
      System.out.println(i);
      GeodesicCausalFilteringFIR geodesicCausal1Filtering = GeodesicCausalFilteringFIR.se2(control, geodesicCenterFilter.apply(control), 0);
      Tensor alpharange = Subdivide.of(0.1, 1, 12);
      for (int j = 0; j < alpharange.length(); ++j) {
        Scalar alpha = alpharange.Get(j);
        System.out.println(geodesicCausal1Filtering.evaluate1ErrorSeperated(alpha));
      }
    }
  }
}
