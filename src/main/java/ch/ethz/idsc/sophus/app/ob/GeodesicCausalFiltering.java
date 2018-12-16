// code by ob
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.filter.GeodesicIIR2Filter;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.LieGroupGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RealScalar;
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

public class GeodesicCausalFiltering {
  public static GeodesicCausalFiltering se2(Tensor measurements, Tensor reference, int order) {
    return new GeodesicCausalFiltering(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, measurements, reference, order);
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

  GeodesicCausalFiltering(LieGroup lieGroup, LieExponential lieExponential, Tensor measurements, Tensor reference, int order) {
    this.lieDifferences = new LieDifferences(lieGroup, lieExponential);
    this.geodesicInterface = new LieGroupGeodesic(lieGroup::element, lieExponential);
    this.measurements = measurements;
    this.reference = reference;
  }

  /** @param alpha filter parameter
   * @return filtered signal when using given alpha */
  public Tensor filteredSignal(Scalar alpha) {
    return Tensor.of(measurements.stream() //
        .map(new GeodesicIIR2Filter(geodesicInterface, alpha)));
  }

  /** filter Lie Group elements and perform check
   * 
   * @param alpha
   * @return */
  public Scalar evaluate0Error(Scalar alpha) {
    Tensor errors = Tensors.empty();
    Tensor filteredSignal = filteredSignal(alpha);
    for (int i = 0; i < measurements.length(); ++i) {
      Tensor result = filteredSignal.get(i);
      Scalar scalar = Norm._2.ofVector(lieDifferences.pair(reference.get(i), result));
      errors.append(scalar);
    }
    return Total.of(errors).Get();
  }
  
  public Tensor evaluate0ErrorSeperated(Scalar alpha) {
    Tensor errors1 = Tensors.empty();
    Tensor errors2 = Tensors.empty();
    Tensor filteredSignal = filteredSignal(alpha);
    for (int i = 0; i < measurements.length(); ++i) {
      Tensor result = filteredSignal.get(i);
      Tensor difference = lieDifferences.pair(reference.get(i), result);
      Scalar scalar1 = Norm._2.ofVector(difference.extract(0, 2));
      Scalar scalar2 = Norm._2.ofVector(difference.extract(2, 3));
      errors1.append(scalar1);
      errors2.append(scalar2);
    }
    return Tensors.of(Total.of(errors1), Total.of(errors2));
  }

  public Scalar evaluate1Error(Scalar alpha) {
    Tensor errors = Tensors.of(RealScalar.ZERO);
    Tensor filteredSignal = filteredSignal(alpha);
    Tensor result_prev = filteredSignal.get(0);
    Tensor ref_prev = reference.get(0);
    for (int i = 2; i < measurements.length(); ++i) {
      Tensor pair1 = lieDifferences.pair(ref_prev, reference.get(i));
      Tensor pair2 = lieDifferences.pair(result_prev, filteredSignal.get(i));
      result_prev = filteredSignal.get(i);
      ref_prev = reference.get(i);
      Scalar scalar = Norm._2.between(pair1, pair2);
      errors.append(scalar);
    }
    return Total.of(errors).Get();
  }
  
  public Tensor evaluate1ErrorSeperated(Scalar alpha) {
    Tensor errors1 = Tensors.of(RealScalar.ZERO);
    Tensor errors2 = Tensors.of(RealScalar.ZERO);
    Tensor filteredSignal = filteredSignal(alpha);
    Tensor result_prev = filteredSignal.get(0);
    Tensor ref_prev = reference.get(0);
    for (int i = 2; i < measurements.length(); ++i) {
      Tensor pair1 = lieDifferences.pair(ref_prev, reference.get(i));
      Tensor pair2 = lieDifferences.pair(result_prev, filteredSignal.get(i));
      result_prev = filteredSignal.get(i);
      ref_prev = reference.get(i);
      Scalar scalar1 = Norm._2.between(pair1.extract(0, 2), pair2.extract(0, 2));
      Scalar scalar2 = Norm._2.between(pair1.extract(2, 3), pair2.extract(2, 3));   
      errors1.append(scalar1);
      errors2.append(scalar2);
    }
    return Tensors.of(Total.of(errors1), Total.of(errors2));
  }
  
//  Nur zum Testen von neuen methoden
//  public static void main(String[] args) {
//    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + //
//        "0w/20180702T133612_1" + ".csv").stream().map(row -> row.extract(1, 4)));
//    TensorUnaryOperator geodesicCenterFilter = //
//        GeodesicCenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN), 7);
//    System.out.println(7);
//    GeodesicCausalFiltering geodesicCausal1Filtering = GeodesicCausalFiltering.se2(control, geodesicCenterFilter.apply(control), 0);
//    Tensor alpharange = Subdivide.of(0.1, 1, 12);
//    for (int j = 0; j < alpharange.length(); ++j) {
//      Scalar alpha = alpharange.Get(j);
//      System.out.println(geodesicCausal1Filtering.evaluate1ErrorSeperated(alpha));
//    }
//  }
  
}
