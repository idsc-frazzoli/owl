// code by ob
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.SmoothingKernel;
import ch.ethz.idsc.owl.math.group.LieDifferences;
import ch.ethz.idsc.owl.math.group.LieGroupGeodesic;
import ch.ethz.idsc.owl.math.group.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.group.Se2Geodesic;
import ch.ethz.idsc.owl.math.group.Se2Group;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Round;

class GeodesicCausalFiltering {
  private static final LieGroupGeodesic LIE_GROUP_GEODESIC = //
      new LieGroupGeodesic(Se2Group.INSTANCE::element, Se2CoveringExponential.INSTANCE);
  private static final LieDifferences LIE_DIFFERENCES = //
      new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
  private static final Scalar TWO = RealScalar.of(2);
  // ---
  /** raw data */
  private final Tensor measurements;
  /** filtered data which we use as 'truth' */
  private final Tensor reference;
  /** applied geodesic prediction */
  private Tensor predictions; //
  // private
  private int width = 5;

  GeodesicCausalFiltering(Tensor measurements) {
    // 1. load data, zur Zeit manuell
    this.measurements = measurements;
    // 2. apply filter(width, function) to get 'true'/refined signal
    {
      TensorUnaryOperator geodesicCenterFilter = //
          GeodesicCenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN), width);
      reference = geodesicCenterFilter.apply(measurements);
    }
    // 3. create causal prediction from the control data using geodesic prediction
    // 4. update with weight lambda. lambda €(0,1)
    // 5. create lie differences between update and truth
    // resolution of finding minimizing alpha
    // int resolution = 10;
    Tensor alpharange = Subdivide.of(0.1, 1, 16);
    for (int j = 0; j < alpharange.length(); ++j) {
      // Reset calculated vectors
      predictions = measurements.extract(0, 2); // first 2 estimations equals to first 2 measurements
      // Vary the kalman-analoguous alpha
      Scalar alpha = alpharange.Get(j);
      for (int i = 2; i < measurements.length(); ++i) {
        // calculate geodesic prediction
        Tensor extrapolation = LIE_GROUP_GEODESIC.split(predictions.get(i - 2), predictions.get(i - 1), TWO);
        Tensor prediction = LIE_GROUP_GEODESIC.split(extrapolation, measurements.get(i), alpha);
        predictions.append(prediction);
        // create measurement update between measurement and prediction
        // delta.append(LIE_DIFFERENCES.apply(Tensors.of(prediction.get(i), reference.get(i))));
        // Use 2-norm to add up liedifferences between our reference and our updated signal
        // error = error.add(Norm._2.of((delta.get(i))));
      }
      // Use 2-norm to add up liedifferences between our reference and our updated signal
      Tensor errors = Tensors.empty();
      for (int i = 0; i < measurements.length(); ++i) {
        Tensor difference = LIE_DIFFERENCES.pair(reference.get(i), predictions.get(i));
        // difference is of the form {dx,dy,da}
        Scalar error = Norm._2.ofVector(difference);
        errors.append(error);
      }
      // array of all errors, however strangely for various window-widths always alpha = 1 yields minimum error... => maybe sth wrong with indices of prediction
      System.out.println(Tensors.of(alpha, Total.of(errors)).map(Round._5));
    }
  }
  // 6. loop over many window sizes & plot
  // 7. opt. GUI

  public static void main(String[] args) {
    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + "0w/20180702T133612_1" + ".csv").stream().map(row -> row.extract(1, 4)));
    GeodesicCausalFiltering GeodesicCausalFiltering = new GeodesicCausalFiltering(control);
    // System.out.println(GeodesicCausalFiltering.errors);
  }
}
