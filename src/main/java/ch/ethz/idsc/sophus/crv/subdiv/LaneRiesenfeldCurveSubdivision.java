// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;

import ch.ethz.idsc.sophus.math.MidpointInterface;
import ch.ethz.idsc.tensor.Integers;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

/** Reference:
 * "A theoretical development for the computer generation of piecewise polynomial surfaces"
 * by J. M. Lane and R. F. Riesenfeld; IEEE Trans. Pattern Anal. Machine Intell. 2 (1980), 35-46 */
public class LaneRiesenfeldCurveSubdivision implements CurveSubdivision, Serializable {
  /** @param midpointInterface
   * @param degree strictly positive
   * @return */
  public static CurveSubdivision of(MidpointInterface midpointInterface, int degree) {
    return new LaneRiesenfeldCurveSubdivision( //
        Objects.requireNonNull(midpointInterface), //
        Integers.requirePositive(degree));
  }

  // ---
  /** linear subdivision */
  private final BSpline1CurveSubdivision bSpline1CurveSubdivision;
  private final int degree;

  private LaneRiesenfeldCurveSubdivision(MidpointInterface midpointInterface, int degree) {
    bSpline1CurveSubdivision = new BSpline1CurveSubdivision(midpointInterface);
    this.degree = degree;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    if (length < 2)
      return tensor.copy();
    Tensor value = bSpline1CurveSubdivision.cyclic(tensor);
    for (int count = 2; count <= degree; ++count) {
      if (Tensors.isEmpty(value)) // TODO JPH test coverage
        return value;
      boolean odd = count % 2 == 1;
      Tensor queue = Tensors.reserve(value.length());
      if (odd) {
        Tensor p = Last.of(value);
        for (int index = 0; index < value.length(); ++index) {
          Tensor q = value.get(index);
          queue.append(bSpline1CurveSubdivision.midpoint(p, q));
          p = q;
        }
      } else {
        Tensor p = value.get(0);
        for (int index = 1; index <= value.length(); ++index) {
          Tensor q = value.get(index % value.length());
          queue.append(bSpline1CurveSubdivision.midpoint(p, q));
          p = q;
        }
      }
      tensor = value;
      value = queue;
    }
    return value;
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    if (length < 2)
      return tensor.copy();
    Tensor value = bSpline1CurveSubdivision.string(tensor);
    for (int count = 2; count <= degree; ++count) {
      if (Tensors.isEmpty(value)) // TODO JPH test coverage
        return value;
      boolean odd = count % 2 == 1;
      Tensor queue = Tensors.reserve(value.length() + 1);
      if (odd)
        queue.append(tensor.get(0));
      Iterator<Tensor> iterator = value.iterator();
      Tensor p = iterator.next();
      while (iterator.hasNext())
        queue.append(bSpline1CurveSubdivision.midpoint(p, p = iterator.next()));
      if (odd)
        queue.append(Last.of(tensor));
      tensor = value;
      value = queue;
    }
    return value;
  }
}
