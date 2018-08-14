// code by ynager
package ch.ethz.idsc.owl.glc.core;

import java.util.Comparator;

import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Tensor;

/** compare two nodes based on {@link GlcNode#merit()} */
public final class CustomNodeMeritComparator implements Comparator<GlcNode> {
  private final Comparator<Tensor> comparator;

  public CustomNodeMeritComparator(Comparator<Tensor> comparator) {
    this.comparator = comparator;
  }

  @Override
  public int compare(GlcNode o1, GlcNode o2) {
    if (o1.merit() instanceof VectorScalar)
      return comparator.compare( //
          ((VectorScalar) o1.merit()).vector(), //
          ((VectorScalar) o2.merit()).vector());
    return comparator.compare(o1.merit(), o2.merit());
  }
}
