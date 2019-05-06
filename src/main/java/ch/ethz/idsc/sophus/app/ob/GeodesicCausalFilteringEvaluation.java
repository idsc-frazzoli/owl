// code by ob
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.sophus.filter.GeodesicFIR2Filter;
import ch.ethz.idsc.sophus.filter.GeodesicIIR2Filter;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.LieGroupGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;

public class GeodesicCausalFilteringEvaluation {
  public static GeodesicCausalFilteringEvaluation se2(Tensor measurements, Tensor reference) {
    return new GeodesicCausalFilteringEvaluation(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, measurements, reference);
  }

  private final LieDifferences lieDifferences;
  private final GeodesicInterface geodesicInterface;
  /** raw data */
  private final Tensor measurements;
  /** filtered data which we use as 'truth' */
  public Tensor reference;
  public Tensor _control;

  GeodesicCausalFilteringEvaluation(LieGroup lieGroup, LieExponential lieExponential, Tensor measurements, Tensor reference) {
    this.lieDifferences = new LieDifferences(lieGroup, lieExponential);
    this.geodesicInterface = new LieGroupGeodesic(lieGroup::element, lieExponential);
    this.measurements = measurements;
    this.reference = reference;
  }

  /** @param alpha filter parameter
   * @param boolean: true -> IIR, false -> FIR
   * @return filtered signal when using given alpha */
  public Tensor filteredSignal(Scalar alpha, Boolean IIR) {
    Tensor refined = IIR //
        ? Tensor.of(measurements.stream().map(new GeodesicIIR2Filter(geodesicInterface, alpha)))
        : Tensor.of(measurements.stream().map(new GeodesicFIR2Filter(geodesicInterface, alpha)));
    return refined;
  }

  /** @param: alpha: filter parameter
   * @param: separated: should xy errors be sepearted from heading errors?
   * @param: finitenes: true: IIR, false: FIR
   * @return cumulated 0-order-errors between reference and filtered signal */
  public Scalar evaluate0Error(Scalar alpha, Boolean separated, Boolean IIR) {
    Tensor errors = Tensors.empty();
    Tensor filtering = filteredSignal(alpha, IIR);
    for (int i = 0; i < measurements.length(); ++i) {
      Tensor difference = lieDifferences.pair(reference.get(i), filtering.get(i));
      if (separated == true) {
        Scalar xyDiff = Norm._2.ofVector(difference.extract(0, 2));
        Scalar aDiff = Norm._2.ofVector(difference.extract(2, 3));
        errors.append(Tensors.of(xyDiff, aDiff));
      } else {
        Scalar xyaDiff = Norm._2.ofVector(lieDifferences.pair(reference.get(i), filtering.get(i)));
        errors.append(xyaDiff);
      }
    }
    return Total.of(errors).Get();
  }

  /** @param alpha: filter parameter
   * @param separated: should xy errors be sepearted from heading errors?
   * @param finitenes: true: IIR, false: FIR
   * @return cumulated 1-order-errors between reference and filtered signal */
  public Tensor evaluate1Error(Scalar alpha, Boolean separated, Boolean IIR) {
    Tensor errors = Tensors.empty();
    Tensor filtering = filteredSignal(alpha, IIR);
    for (int i = 1; i < measurements.length(); ++i) {
      Tensor derivRefDiff = lieDifferences.pair(reference.get(i - 1), reference.get(i));
      Tensor derivFiltDiff = lieDifferences.pair(filtering.get(i - 1), filtering.get(i));
      if (separated == true) {
        Scalar xyDerivDiff = Norm._2.between(derivRefDiff.extract(0, 2), derivFiltDiff.extract(0, 2));
        Scalar aDerivDiff = Norm._2.between(derivRefDiff.extract(2, 3), derivFiltDiff.extract(2, 3));
        errors.append(Tensors.of(xyDerivDiff, aDerivDiff));
      } else {
        Scalar xyaDerivDiff = Norm._2.between(derivRefDiff.extract(0, 2), derivFiltDiff.extract(0, 2));
        errors.append(Tensors.of(xyaDerivDiff));
      }
    }
    return Total.of(errors);
  }
}
