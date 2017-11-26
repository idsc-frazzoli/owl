// code by bapaden and jph
package ch.ethz.idsc.owl.glc.core;

import java.util.Comparator;

import ch.ethz.idsc.tensor.Scalars;

/** compare two nodes based on {@link GlcNode#merit()} */
/* package */ enum NodeMeritComparator implements Comparator<GlcNode> {
  INSTANCE;
  // ---
  @Override
  public int compare(GlcNode o1, GlcNode o2) {
    return Scalars.compare(o1.merit(), o2.merit());
  }
}
