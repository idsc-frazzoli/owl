// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class PointsRender {
  private final Color color_fill;
  private final Color color_draw;

  public PointsRender(Color color_fill, Color color_draw) {
    this.color_fill = color_fill;
    this.color_draw = color_draw;
  }

  public RenderInterface show(TensorUnaryOperator matrixLift, Tensor shape, Tensor points) {
    return new Show(matrixLift, shape, points);
  }

  private class Show implements RenderInterface {
    private final TensorUnaryOperator matrixLift;
    private final Tensor shape;
    private final Tensor points;

    public Show(TensorUnaryOperator matrixLift, Tensor shape, Tensor points) {
      this.matrixLift = matrixLift;
      this.shape = shape;
      this.points = points;
    }

    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      for (Tensor point : points) {
        geometricLayer.pushMatrix(matrixLift.apply(point));
        Path2D path2d = geometricLayer.toPath2D(shape);
        path2d.closePath();
        graphics.setColor(color_fill);
        graphics.fill(path2d);
        graphics.setColor(color_draw);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
  }
}
