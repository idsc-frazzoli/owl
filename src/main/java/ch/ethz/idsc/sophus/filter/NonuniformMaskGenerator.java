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
import ch.ethz.idsc.tensor.io.ResourceData;

public class NonuniformMaskGenerator {
  /** @param time stamp of control sequence
   * @return affine combination used to generate mask
   * @throws Exception if mask is not a vector or empty */
  // TODO OB: hand over length argument to create boundedlinkedlist dynamically
  // TODO OB: hand over interval argument dynamically
  // TODO OB: hand over argument to choose SmoothingKernel dynamically
  private static BoundedLinkedList<StateTime> boundedLinkedList = new BoundedLinkedList<>(10);
  private static Scalar interval = RealScalar.of(0.5);

  // maps gaussian window to last 10 samples
  public static Tensor fixedLength(StateTime statetime) {
    boundedLinkedList.add(statetime);
    if (boundedLinkedList.size() == 1) {
      return RealScalar.ONE;
    }
    Tensor weight = Tensors.empty();
    Scalar delta = boundedLinkedList.getFirst().time().subtract(boundedLinkedList.getLast().time());
    for (int index = 0; index < boundedLinkedList.size(); index++) {
      Scalar conversion = boundedLinkedList.get(index).time().subtract(boundedLinkedList.getFirst().time()).divide(delta.add(delta))
          .subtract(RationalScalar.HALF);
      weight.append(SmoothingKernel.GAUSSIAN.apply(conversion));
    }
    return weight;
  }

  // Maps t(n) - I to x = -0.5;
  public static Tensor fixedIntervalVariant1(StateTime statetime) {
    boundedLinkedList.add(statetime);
    while (true) {
      if (Scalars.lessEquals(boundedLinkedList.getFirst().time(), boundedLinkedList.getLast().time().subtract(interval))) {
        boundedLinkedList.remove();
      } else {
        break;
      }
    }
    if (boundedLinkedList.size() == 1) {
      return Tensors.of(RealScalar.ONE);
    }
    Tensor weight = Tensors.empty();
    Scalar delta = interval;
    for (int index = 0; index < boundedLinkedList.size(); index++) {
      Scalar conversion = boundedLinkedList.get(index).time().subtract(interval).divide(delta.add(delta)).subtract(RationalScalar.HALF);
      weight.append(SmoothingKernel.GAUSSIAN.apply(conversion));
    }
    return weight;
  }

  // Maps t(n)-t(0) with t(0) smallest element within interval to x = .5
  public static Tensor fixedIntervalVariant2(StateTime statetime) {
    boundedLinkedList.add(statetime);
    while (true) {
      if (Scalars.lessEquals(boundedLinkedList.getFirst().time(), boundedLinkedList.getLast().time().subtract(interval))) {
        boundedLinkedList.remove();
      } else {
        break;
      }
    }
    if (boundedLinkedList.size() == 1) {
      return Tensors.of(RealScalar.ONE);
    }
    Tensor weight = Tensors.empty();
    Scalar delta = boundedLinkedList.getLast().time().subtract(boundedLinkedList.getFirst().time());
    for (int index = 0; index < boundedLinkedList.size(); index++) {
      Scalar conversion = boundedLinkedList.get(index).time().subtract(interval).divide(delta.add(delta)).subtract(RationalScalar.HALF);
      weight.append(SmoothingKernel.GAUSSIAN.apply(conversion));
    }
    return weight;
  }

  public static void main(String[] args) {
    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + //
        "0w/20180702T133612_1" + ".csv").stream().map(row -> row.extract(0, 4)));
    StateTime statetime = new StateTime(control.get(0).extract(1, 4), control.get(0).Get(0));
  }
}
