// code by ynager
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Comparator;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.tensor.Tensor;

/** compare two nodes based on {@link GlcNode#merit()}
 * the merit is required to be of type {@link VectorScalar} */
public class CustomNodeMeritComparator implements Comparator<GlcNode> {
  private final Comparator<Tensor> comparator;

  public CustomNodeMeritComparator(Comparator<Tensor> comparator) {
    this.comparator = comparator;
  }

  @Override
  public int compare(GlcNode glcNode1, GlcNode glcNode2) {
    return comparator.compare( //
        VectorScalars.vector(glcNode1.merit()), //
        VectorScalars.vector(glcNode2.merit()));
  }
}
