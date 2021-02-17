// code by jph
package ch.ethz.idsc.tensor.demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.opt.hun.BipartiteMatching;

/* package */ class BipartiteMatchingDemo extends ControlPointsDemo {
  private static final Tensor CIRCLE = CirclePoints.of(5).multiply(RealScalar.of(3));

  public BipartiteMatchingDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    // ---
    setControlPointsSe2(Tensors.fromString("{{1, 0, 0}, {0, 1, 0}, {1, 1, 0}}"));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    if (0 < control.length()) {
      new PathRender(Color.GRAY).setCurve(CIRCLE, true).render(geometricLayer, graphics);
      Tensor matrix = Tensors.matrix((i, j) -> //
      Vector2Norm.between(control.get(i), CIRCLE.get(j)), control.length(), CIRCLE.length());
      BipartiteMatching bipartiteMatching = BipartiteMatching.of(matrix);
      int[] matching = bipartiteMatching.matching();
      graphics.setColor(Color.RED);
      for (int index = 0; index < matching.length; ++index)
        if (matching[index] != BipartiteMatching.UNASSIGNED) {
          Path2D path2d = geometricLayer.toPath2D(Tensors.of(control.get(index), CIRCLE.get(matching[index])));
          graphics.draw(path2d);
        }
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new BipartiteMatchingDemo().setVisible(1000, 600);
  }
}
