// code by jph 
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Map;

import ch.ethz.idsc.owl.glc.rl2.RelaxedPriorityQueue;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

public class DomainQueueMapRender implements RenderInterface {
  private static final ColorDataIndexed INTERIOR = ColorDataLists._097.cyclic().deriveWithAlpha(128);
  private static final Color BOUNDARY = Color.WHITE;
  // ---
  private final Map<Tensor, RelaxedPriorityQueue> map;
  private final Tensor eta_invert;
  private final Tensor ratios;

  public DomainQueueMapRender(Map<Tensor, RelaxedPriorityQueue> map, Tensor eta) {
    this.map = map;
    eta_invert = Extract2D.FUNCTION.apply(eta).map(Scalar::reciprocal);
    int lo = 0;
    int hi = 1;
    ratios = Tensors.of( //
        eta_invert.pmul(Tensors.vector(lo, lo)), //
        eta_invert.pmul(Tensors.vector(hi, lo)), //
        eta_invert.pmul(Tensors.vector(hi, hi)), //
        eta_invert.pmul(Tensors.vector(lo, hi)));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    map.keySet().stream().map(Extract2D.FUNCTION).distinct().forEach(key -> {
      Tensor x = key.pmul(eta_invert);
      Path2D path2d = geometricLayer.toPath2D(Tensor.of(ratios.stream().map(x::add)));
      path2d.closePath();
      int size = map.get(key).collection().size() - 1;
      graphics.setColor(INTERIOR.getColor(size));
      graphics.fill(path2d);
      graphics.setColor(BOUNDARY);
      graphics.draw(path2d);
    });
  }
}
