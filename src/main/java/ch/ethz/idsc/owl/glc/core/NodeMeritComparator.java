// code by bapaden and jph
package ch.ethz.idsc.owl.glc.core;

import java.util.Comparator;

import ch.ethz.idsc.tensor.Scalars;

/** compare two nodes based on {@link GlcNode#merit()} */
/* package */ enum NodeMeritComparator implements Comparator<GlcNode> {
  INSTANCE;
  // ---
  @Override // from Comparator
  public int compare(GlcNode glcNode1, GlcNode glcNode2) {
    return Scalars.compare(glcNode1.merit(), glcNode2.merit());
  }
}
