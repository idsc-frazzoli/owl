// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.Deque;

import ch.ethz.idsc.owl.data.DontModify;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

/**
 * 
 */
@DontModify
public class GeometricLayer {
  public static GeometricLayer of(Tensor model2pixel) {
    return new GeometricLayer(model2pixel, Array.zeros(3));
  }

  // ---
  private final Deque<AffineFrame2D> deque = new ArrayDeque<>();
  private final Tensor mouseSe2State;

  /** @param model2pixel matrix that becomes first element on matrix stack
   * @param mouseSe2State typically a vector of length 3 */
  public GeometricLayer(Tensor model2pixel, Tensor mouseSe2State) {
    deque.push(new AffineFrame2D(model2pixel));
    this.mouseSe2State = mouseSe2State.unmodifiable();
  }

  /** only the first 2 entries of x are taken into account
   * 
   * @param x = {px, py, ...}
   * @return */
  public Point2D toPoint2D(Tensor x) {
    return deque.peek().toPoint2D(x);
  }

  /** @param px
   * @param py
   * @return */
  public Point2D toPoint2D(double px, double py) {
    return deque.peek().toPoint2D(px, py);
  }

  /** inspired by opengl
   * 
   * @param matrix 3x3 */
  public void pushMatrix(Tensor matrix) {
    deque.push(deque.peek().dot(matrix));
  }

  /** inspired by opengl
   * 
   * @throws Exception if deque of matrices is empty */
  public void popMatrix() {
    deque.pop();
  }

  /** @return current model2pixel matrix */
  public Tensor getMatrix() {
    return deque.peek().tensor_copy();
  }

  public Shape toVector(Tensor x, Tensor dx) {
    x = x.extract(0, 2);
    dx = dx.extract(0, 2);
    return new Line2D.Double(toPoint2D(x), toPoint2D(x.add(dx)));
  }

  /** @param polygon
   * @return path that is not closed */
  public Path2D toPath2D(Tensor polygon) {
    Path2D path2d = new Path2D.Double();
    if (Tensors.nonEmpty(polygon)) {
      Point2D point2d = toPoint2D(polygon.get(0));
      path2d.moveTo(point2d.getX(), point2d.getY());
    }
    polygon.stream() //
        .skip(1) // first coordinate already used in moveTo
        .map(this::toPoint2D) //
        .forEach(point2d -> path2d.lineTo(point2d.getX(), point2d.getY()));
    return path2d;
  }

  /** @return {x, y, alpha} unmodifiable */
  public Tensor getMouseSe2State() {
    // TODO function is deprecated in the long run
    return mouseSe2State;
  }
}
