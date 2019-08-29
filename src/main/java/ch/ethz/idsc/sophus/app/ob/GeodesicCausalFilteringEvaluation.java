// code by ob
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.sophus.flt.ga.GeodesicFIR2;
import ch.ethz.idsc.sophus.flt.ga.GeodesicIIR2;
import ch.ethz.idsc.sophus.lie.LieDifferences;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupGeodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;

/* package */ class GeodesicCausalFilteringEvaluation {
  private final LieDifferences lieDifferences;
  private final GeodesicInterface geodesicInterface;
  /** raw data */
  private final Tensor measurements;
  /** filtered data which we use as 'truth' */
  private Tensor reference;

  public GeodesicCausalFilteringEvaluation( //
      LieGroup lieGroup, LieExponential lieExponential, Tensor measurements, Tensor reference) {
    this.lieDifferences = new LieDifferences(lieGroup, lieExponential);
    this.geodesicInterface = new LieGroupGeodesic(lieGroup::element, lieExponential);
    this.measurements = measurements;
    this.reference = reference;
  }

  /** @param alpha filter parameter
   * @param boolean: true -> IIR, false -> FIR
   * @return filtered signal when using given alpha */
  public Tensor filteredSignal(Scalar alpha, boolean IIR) {
    Tensor refined = IIR //
        ? Tensor.of(measurements.stream().map(new GeodesicIIR2(geodesicInterface, alpha)))
        : Tensor.of(measurements.stream().map(GeodesicFIR2.of(geodesicInterface, alpha)));
    return refined;
  }

  /** @param: alpha: filter parameter
   * @param: separated: should xy errors be sepearted from heading errors?
   * @param: finitenes: true: IIR, false: FIR
   * @return cumulated 0-order-errors between reference and filtered signal */
  public Scalar evaluate0Error(Scalar alpha, boolean separated, boolean IIR) {
    Tensor errors = Tensors.empty();
    Tensor filtering = filteredSignal(alpha, IIR);
    for (int i = 0; i < measurements.length(); ++i) {
      Tensor difference = lieDifferences.pair(reference.get(i), filtering.get(i));
      if (separated == true) {
        // TODO SE2 specific
        Scalar xyDiff = Norm._2.ofVector(difference.extract(0, 2));
        Scalar aDiff = Norm._2.ofVector(difference.extract(2, 3));
        errors.append(Tensors.of(xyDiff, aDiff));
      } else {
        Scalar xyaDiff = Norm._2.ofVector(difference);
        errors.append(xyaDiff);
      }
    }
    return (Scalar) Total.of(errors);
  }

  /** @param alpha: filter parameter
   * @param separated: should xy errors be sepearted from heading errors?
   * @param finitenes: true: IIR, false: FIR
   * @return cumulated 1-order-errors between reference and filtered signal */
  public Tensor evaluate1Error(Scalar alpha, boolean separated, boolean IIR) {
    Tensor errors = Tensors.empty();
    Tensor filtering = filteredSignal(alpha, IIR);
    for (int i = 1; i < measurements.length(); ++i) {
      Tensor derivRefDiff = lieDifferences.pair(reference.get(i - 1), reference.get(i));
      Tensor derivFiltDiff = lieDifferences.pair(filtering.get(i - 1), filtering.get(i));
      if (separated == true) {
        // TODO SE2 specific
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
