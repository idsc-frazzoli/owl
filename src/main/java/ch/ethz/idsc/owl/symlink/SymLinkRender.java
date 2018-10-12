// code by jph
package ch.ethz.idsc.owl.symlink;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.sca.Round;

public class SymLinkRender implements RenderInterface {
  private static final Tensor CIRCLE_END = CirclePoints.of(31).multiply(RealScalar.of(.066));
  private static final Tensor CIRCLE_MID = CirclePoints.of(31).multiply(RealScalar.of(.033));
  // ---
  private final SymLink symLink;

  public SymLinkRender(SymLink symLink) {
    this.symLink = symLink;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor here = symLink.getPosition();
    if (symLink instanceof SymNode) {
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(here));
      Path2D path2d = geometricLayer.toPath2D(CIRCLE_END);
      path2d.closePath();
      graphics.setColor(Color.BLACK);
      graphics.setStroke(new BasicStroke(1f));
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    } else {
      {
        geometricLayer.pushMatrix(Se2Utils.toSE2Translation(here));
        Path2D path2d = geometricLayer.toPath2D(CIRCLE_MID);
        path2d.closePath();
        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(1f));
        graphics.fill(path2d);
        geometricLayer.popMatrix();
      }
      {
        new SymLinkRender(symLink.lP).render(geometricLayer, graphics);
        Tensor there = symLink.lP.getPosition();
        Path2D path2d = geometricLayer.toPath2D(Tensors.of(here, there));
        graphics.setStroke(new BasicStroke(1.5f));
        graphics.setColor(Color.BLACK);
        graphics.draw(path2d);
      }
      {
        new SymLinkRender(symLink.lQ).render(geometricLayer, graphics);
        Tensor there = symLink.lQ.getPosition();
        Path2D path2d = geometricLayer.toPath2D(Tensors.of(here, there));
        graphics.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
        graphics.setColor(Color.BLACK);
        graphics.draw(path2d);
      }
      {
        Point2D point2d = geometricLayer.toPoint2D(here);
        String string = nice(symLink.lambda);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(string);
        int rgb = 192 + 32;
        graphics.setColor(new Color(rgb, rgb, rgb, 192));
        int pix = (int) point2d.getX() - stringWidth / 2;
        int piy = (int) point2d.getY() - 10;
        graphics.fillRect(pix, piy - 18, stringWidth, 22);
        graphics.setColor(Color.BLACK);
        graphics.drawString(string, pix, piy);
      }
    }
  }

  public static String nice(Scalar scalar) {
    Scalar simplified = ExactScalarQ.of(scalar) //
        ? scalar
        : Round._4.apply(scalar);
    return simplified.toString();
  }
}
