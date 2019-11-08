// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.mat.Eigensystem;

public class CovarianceRender implements RenderInterface {
  private static final Tensor CIRCLE_POINTS = CirclePoints.of(43);
  private final Eigensystem eigensystem;

  public CovarianceRender(Tensor p) {
    eigensystem = Eigensystem.ofSymmetric(p);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setStroke(new BasicStroke(1.5f));
    Tensor se2 = Tensor.of(eigensystem.vectors().stream().map(row -> row.copy().append(RealScalar.ZERO)));
    se2.append(UnitVector.of(3, 2));
    geometricLayer.pushMatrix(se2);
    Tensor ellipse = Tensor.of(CIRCLE_POINTS.stream().map(row -> eigensystem.values().pmul(row)));
    Path2D path2d = geometricLayer.toPath2D(ellipse, true);
    graphics.draw(path2d);
    geometricLayer.popMatrix();
    graphics.setStroke(new BasicStroke());
  }
}
