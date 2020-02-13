// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;
import java.util.Iterator;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.math.MidpointInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

/** {1, 2, 3} -> {1, 3/2, 5/2, 3} */
/* package */ class ControlMidpoints implements CurveSubdivision, Serializable {
  /** @param midpointInterface
   * @return */
  public static CurveSubdivision of(MidpointInterface midpointInterface) {
    return new ControlMidpoints(midpointInterface);
  }

  // ---
  private final MidpointInterface midpointInterface;

  private ControlMidpoints(MidpointInterface midpointInterface) {
    this.midpointInterface = midpointInterface;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    throw new UnsupportedOperationException();
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    if (Tensors.isEmpty(tensor))
      return Tensors.empty();
    Tensor result = Tensors.reserve(tensor.length() + 1);
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor prev = iterator.next();
    result.append(prev);
    while (iterator.hasNext())
      result.append(midpointInterface.midpoint(prev, prev = iterator.next()));
    result.append(Last.of(tensor));
    return result;
  }
}
