// code by ynager
package ch.ethz.idsc.owl.glc.core;

import java.util.Comparator;

import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.tensor.Tensor;

/** compare two nodes based on {@link GlcNode#merit()} */
// TODO JPH check if design is plausible
public final class CustomNodeMeritComparator implements Comparator<GlcNode> {
  private final Comparator<Tensor> comparator;

  public CustomNodeMeritComparator(Comparator<Tensor> comparator) {
    this.comparator = comparator;
  }

  @Override
  public int compare(GlcNode o1, GlcNode o2) {
    if (o1.merit() instanceof VectorScalar)
      return comparator.compare( //
          VectorScalars.vector(o1.merit()), //
          VectorScalars.vector(o2.merit()));
    return comparator.compare(o1.merit(), o2.merit());
  }
}
