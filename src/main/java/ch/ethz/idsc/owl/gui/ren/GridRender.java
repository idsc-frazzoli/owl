// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.N;

/** rendering of grid with lines parallel/orthogonal to the x and y axes
 * 
 * Example:
 * <pre>
 * new GridRender(Subdivide.of(0, 50, 5));
 * </pre> */
public class GridRender implements RenderInterface, Serializable {
  private static final Color COLOR_DEFAULT = new Color(224, 224, 224, 128);
  // ---
  private final Tensor x_grid;
  private final Tensor y_grid;
  private final Scalar x_lo;
  private final Scalar x_hi;
  private final Scalar y_lo;
  private final Scalar y_hi;
  private final Color color;

  /** the input coordinates divide the axes where the grid is drawn
   * 
   * @param x vector of coordinates along the x axis
   * @param y vector of coordinates along the y axis
   * @param color of grid lines */
  public GridRender(Tensor x, Tensor y, Color color) {
    this.x_grid = N.DOUBLE.of(x);
    x_lo = (Scalar) x_grid.stream().reduce(Min::of).get();
    x_hi = (Scalar) x_grid.stream().reduce(Max::of).get();
    this.y_grid = N.DOUBLE.of(y);
    y_lo = (Scalar) y_grid.stream().reduce(Min::of).get();
    y_hi = (Scalar) y_grid.stream().reduce(Max::of).get();
    this.color = Objects.requireNonNull(color);
  }

  /** the input coordinates divide the axes where the grid is drawn
   * 
   * @param x vector of coordinates along the x axis
   * @param y vector of coordinates along the y axis */
  public GridRender(Tensor x, Tensor y) {
    this(x, y, COLOR_DEFAULT);
  }

  /** @param vector of coordinates along the x axis and y axis
   * @param color of grid lines */
  public GridRender(Tensor vector, Color color) {
    this(vector, vector, color);
  }

  /** @param vector of coordinates along the x axis and y axis */
  public GridRender(Tensor vector) {
    this(vector, vector);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(color);
    for (Tensor x : x_grid)
      graphics.draw(new Line2D.Double( //
          geometricLayer.toPoint2D(Tensors.of(x, y_lo)), //
          geometricLayer.toPoint2D(Tensors.of(x, y_hi))));
    for (Tensor y : y_grid)
      graphics.draw(new Line2D.Double( //
          geometricLayer.toPoint2D(Tensors.of(x_lo, y)), //
          geometricLayer.toPoint2D(Tensors.of(x_hi, y))));
  }
}
