// code by jph
package ch.ethz.idsc.sophus.app.curve;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ enum StaticHelper {
  ;
  static final RenderInterface GRID_RENDER = new GridRender(Subdivide.of(0, 10, 10), Subdivide.of(0, 10, 10));
}
