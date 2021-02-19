// code by jph
package ch.ethz.idsc.sophus.ply;

import java.util.Iterator;

import ch.ethz.idsc.sophus.math.d2.Det2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Total;

public enum PolygonCentroid {
  ;
  private static final Scalar _6 = RealScalar.of(6);

  /** @param polygon
   * @return
   * @throws Exception if polygon is empty */
  public static Tensor of(Tensor polygon) {
    if (polygon.length() == 1)
      return VectorQ.requireLength(polygon.get(0), 2);
    if (polygon.length() == 2)
      return VectorQ.requireLength(Mean.of(polygon), 2);
    Tensor prev = Last.of(polygon);
    Tensor contrib = Tensors.empty();
    Iterator<Tensor> iterator = polygon.iterator();
    while (iterator.hasNext()) {
      Tensor next = iterator.next();
      contrib.append(prev.add(next).multiply(Det2D.of(prev, next)));
      prev = next;
    }
    return Total.of(contrib).divide(PolygonArea.of(polygon)).divide(_6);
  }
}
