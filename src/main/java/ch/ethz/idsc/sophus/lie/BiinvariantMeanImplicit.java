// code by ob
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Chop;

/** Calculates the Biinvariant mean of a sequence in any Lie group by iterating over the implicit
 * formulation of the barycentric equation up until a proximity condition is fulfilled */
public class BiinvariantMeanImplicit {
  private final LieExponential lieExponential;
  private LieGroup lieGroup;;

  BiinvariantMeanImplicit(GeodesicDisplay geodesicDisplay) {
    this.lieExponential = geodesicDisplay.lieExponential();
    this.lieGroup = geodesicDisplay.lieGroup();
  }

  private Tensor estimationUpdate(Tensor sequence, Tensor weights, Tensor estimate) {
    LieGroupElement m = lieGroup.element(estimate);
    return m.combine(lieExponential.exp(weights.dot(Tensor.of(sequence.stream().map(p -> lieExponential.log(m.inverse().combine(p)))))));
  }

  public Tensor apply(Tensor sequence, Tensor weights) {
    Tensor estimate = sequence.get(0);
    Tensor estimateOld = estimate;
    while (true) {
      estimate = estimationUpdate(sequence, weights, estimateOld);
      if (Chop._14.close(lieExponential.log(lieGroup.element(estimate).inverse().combine(estimateOld)), Array.zeros(estimate.length())))
        return estimate;
      estimateOld = estimate;
    }
  }
}
