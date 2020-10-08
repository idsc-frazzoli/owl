// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.File;

import ch.ethz.idsc.sophus.hs.HsDifferences;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;

/* package */ class GeodesicErrorEvaluation {
  public static final File ROOT = HomeDirectory.Desktop("MA/owl_export");
  // ---
  private final HsDifferences lieDifferences;

  public GeodesicErrorEvaluation(HsExponential hsExponential) {
    this.lieDifferences = new HsDifferences(hsExponential);
  }

  public Tensor evaluate0ErrorSeperated(Tensor causal, Tensor center) {
    Tensor errors = Tensors.empty();
    for (int i = 0; i < causal.length(); ++i) {
      Tensor difference = lieDifferences.pair(causal.get(i), center.get(i));
      Scalar scalar1 = Norm._2.ofVector(difference.extract(0, 2));
      Scalar scalar2 = Norm._2.ofVector(difference.extract(2, 3));
      errors.append(Tensors.of(scalar1, scalar2));
    }
    return Total.of(errors);
  }

  public Tensor evaluate1ErrorSeperated(Tensor causal, Tensor center) {
    Tensor errors = Tensors.empty();
    for (int i = 1; i < causal.length(); ++i) {
      Tensor pair1 = lieDifferences.pair(causal.get(i - 1), causal.get(i));
      Tensor pair2 = lieDifferences.pair(center.get(i - 1), center.get(i));
      Scalar scalar1 = Norm._2.between(pair1.extract(0, 2), pair2.extract(0, 2));
      Scalar scalar2 = Norm._2.between(pair1.extract(2, 3), pair2.extract(2, 3));
      errors.append(Tensors.of(scalar1, scalar2));
    }
    return Total.of(errors);
  }
}