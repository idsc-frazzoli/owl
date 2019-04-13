// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class NonuniformMaskGenerator {
  /** @param time stamp of control sequence
   * @return affine combination used to generate mask
   * @throws Exception if mask is not a vector or empty */
  // TODO OB TESTS!
  public Tensor fixedLength(BoundedLinkedList<StateTime> boundedLinkedList, Scalar length) {
    Tensor weight = Tensors.empty();
    Scalar delta = boundedLinkedList.getFirst().time().subtract(boundedLinkedList.getLast().time());
    for (int index = 0; index < boundedLinkedList.size(); ++index) {
      Scalar conversion = boundedLinkedList.get(index).time().subtract(boundedLinkedList.getFirst().time()).divide(delta.add(delta))
          .subtract(RationalScalar.HALF);
      weight.append(SmoothingKernel.GAUSSIAN.apply(conversion));
    }
    return weight;
  }

  // Maps t(n) - I to x = -0.5;
  public Tensor fixedIntervalVariant1(BoundedLinkedList<StateTime> boundedLinkedList, Scalar interval) {
    while (Scalars.lessEquals(boundedLinkedList.getFirst().time(), boundedLinkedList.getLast().time().subtract(interval)))
      boundedLinkedList.remove();
    if (boundedLinkedList.size() == 1)
      return Tensors.of(RealScalar.ONE);
    Tensor weight = Tensors.empty();
    Scalar delta = interval;
    for (int index = 0; index < boundedLinkedList.size(); ++index) {
      Scalar conversion = boundedLinkedList.get(index).time().subtract(interval).divide(delta.add(delta)).subtract(RationalScalar.HALF);
      weight.append(SmoothingKernel.GAUSSIAN.apply(conversion));
    }
    return weight;
  }

  // Maps t(n)-t(0) with t(0) smallest element within interval to x = .5
  public Tensor fixedIntervalVariant2(BoundedLinkedList<StateTime> boundedLinkedList, Scalar interval) {
    while (Scalars.lessEquals(boundedLinkedList.getFirst().time(), boundedLinkedList.getLast().time().subtract(interval)))
      boundedLinkedList.remove();
    if (boundedLinkedList.size() == 1)
      return Tensors.of(RealScalar.ONE);
    Tensor weight = Tensors.empty();
    Scalar delta = boundedLinkedList.getLast().time().subtract(boundedLinkedList.getFirst().time());
    for (int index = 0; index < boundedLinkedList.size(); ++index) {
      Scalar conversion = boundedLinkedList.get(index).time().subtract(interval).divide(delta.add(delta)).subtract(RationalScalar.HALF);
      weight.append(SmoothingKernel.GAUSSIAN.apply(conversion));
    }
    return weight;
  }
}