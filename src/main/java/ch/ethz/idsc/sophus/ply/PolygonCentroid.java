// code by jph
package ch.ethz.idsc.sophus.ply;

import ch.ethz.idsc.sophus.math.Det2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.red.Total;

public enum PolygonCentroid {
  ;
  /** @param polygon
   * @return */
  public static Tensor of(Tensor polygon) {
    Tensor prev = Last.of(polygon);
    Tensor contrib = Tensors.empty();
    for (int index = 0; index < polygon.length(); ++index) {
      Tensor next = polygon.get(index);
      contrib.append(prev.add(next).multiply(Det2D.of(prev, next)));
      prev = next;
    }
    return Total.of(contrib).divide(PolygonArea.FUNCTION.apply(polygon).multiply(RealScalar.of(6)));
  }
}
