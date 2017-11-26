// code by jph and jl
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Map;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

class DomainRender implements RenderInterface {
  private static final Color INTERIOR = new Color(192, 192, 192, 64);
  private static final Color BOUNDARY = Color.WHITE;
  public static final TensorUnaryOperator EXTRACT2 = tensor -> tensor.extract(0, 2);
  // ---
  private final Map<Tensor, GlcNode> map;
  private final Tensor eta_invert;
  private final Tensor ratios;

  DomainRender(Map<Tensor, GlcNode> map, Tensor eta) {
    this.map = map;
    eta_invert = eta.extract(0, 2).map(Scalar::reciprocal);
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
    map.keySet().stream().map(EXTRACT2).distinct().forEach(key -> {
      Tensor x = key.pmul(eta_invert);
      Path2D path2d = geometricLayer.toPath2D(Tensor.of(ratios.stream().map(x::add)));
      graphics.setColor(INTERIOR);
      graphics.fill(path2d);
      // ---
      graphics.setColor(BOUNDARY);
      path2d.closePath();
      graphics.draw(path2d);
    });
  }
}
