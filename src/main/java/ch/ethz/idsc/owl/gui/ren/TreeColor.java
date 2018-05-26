// code by jph
package ch.ethz.idsc.owl.gui.ren;

import ch.ethz.idsc.owl.gui.ColorLookup;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;

/** helper class to adapt the node and edge colors of a tree
 * to the dimension of the state space */
enum TreeColor {
  LO(128, 76), //
  HI(51, 19), //
  ;
  // ---
  public static TreeColor ofDimensions(int dims) {
    return dims <= 2 ? LO : HI;
  }

  // ---
  public final ColorDataIndexed nodeColor;
  public final ColorDataIndexed edgeColor;

  private TreeColor(int node_alpha, int edge_alpha) {
    nodeColor = ColorLookup.hsluv_lightness(0.50).deriveWithAlpha(node_alpha);
    edgeColor = ColorLookup.hsluv_lightness(0.65).deriveWithAlpha(edge_alpha);
  }
}
