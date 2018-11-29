// code by ob
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.SmoothingKernel;
import ch.ethz.idsc.owl.math.group.LieDifferences;
import ch.ethz.idsc.owl.math.group.LieGroupGeodesic;
import ch.ethz.idsc.owl.math.group.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.group.Se2CoveringGroup;
import ch.ethz.idsc.owl.math.group.Se2Geodesic;
import ch.ethz.idsc.owl.math.group.Se2Group;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

public class GeodesicCausalFiltering {
  private Tensor control = Tensors.of(Array.zeros(3)); // Raw data
  private Tensor reference = Tensors.of(Array.zeros(3)); // Filtered data which we use as 'truth'
  private Tensor causal; // Applied geodesic prediction
  private Tensor update; // Updated tensor
  private Tensor delta;
  private Scalar alpha = RealScalar.of(0);
  private Scalar error = RealScalar.of(0);
  private Tensor errors = Tensors.of(Array.zeros(1));
  private int width = 5;

  GeodesicCausalFiltering() {
    // 1. load data, zur Zeit manuell
    control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + "0w/20180702T133612_1" + ".csv").stream().limit(270).map(row -> row.extract(1, 4)));
    // 2. apply filter(width, function) to get 'true'/refined signal
    TensorUnaryOperator geodesicCenterFilter = GeodesicCenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN), width);
    reference = geodesicCenterFilter.apply(control);
    // 3. create causal prediction from the control data using geodesic prediction
    // 4. update with weight lambda. lambda €(0,1)
    // 5. create lie differences between update and truth
    LieGroupGeodesic lieGroupGeodesic = new LieGroupGeodesic(Se2CoveringGroup.INSTANCE::element, Se2CoveringExponential.INSTANCE);
    LieDifferences lieDifferences = new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
    // Resolution of finding minimizing alpha
    int resolution = 10;
    for (int j = 0; j < resolution; j++) {
      // Reset calculated vectors
      error = RealScalar.of(0);
      update = Tensors.of(Array.zeros(3));
      delta = Tensors.of(Array.zeros(3));
      causal = Tensors.of(Array.zeros(3));
      // Vary the kalman-analoguous alpha
      alpha = RealScalar.of(j).divide(RealScalar.of(resolution));
      for (int i = 0; i < control.length() - 1; i++) {
        // Calculate geodesic prediction
        causal.append(Se2Geodesic.INSTANCE.split(control.get(i), control.get(i + 1), RealScalar.of(2)));
        // create measurement update between measurement and prediction
        update.append(lieGroupGeodesic.split(causal.get(i), control.get(i), alpha));
        delta.append(lieDifferences.apply(Tensors.of(update.get(i), reference.get(i))));
        // Use 2-norm to add up liedifferences between our reference and our updated signal
        error = error.add(Norm._2.of((delta.get(i))));
      }
      // array of all errors, however strangely for various window-widths always alpha = 1 yields minimum error... => maybe sth wrong with indices of prediction
      errors.append(error);
    }
  }
  // 6. loop over many window sizes & plot
  // 7. opt. GUI

  public static void main(String[] args) {
    GeodesicCausalFiltering GeodesicCausalFiltering = new GeodesicCausalFiltering();
    System.out.println(GeodesicCausalFiltering.errors);
  }
}
