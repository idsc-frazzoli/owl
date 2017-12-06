// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.Deque;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;

/**  */
public class GeometricLayer {
  private final Deque<AffineFrame2D> deque = new ArrayDeque<>();
  private final Tensor mouseSe2State;

  /** @param model2pixel matrix that becomes first element on matrix stack
   * @param mouseSe2State */
  public GeometricLayer(Tensor model2pixel, Tensor mouseSe2State) {
    deque.push(new AffineFrame2D(model2pixel));
    this.mouseSe2State = mouseSe2State;
    GlobalAssert.that(VectorQ.ofLength(mouseSe2State, 3));
  }

  /** only the first 2 entries of x are taken into account
   * 
   * @param x = {px, py, ...}
   * @return */
  public Point2D toPoint2D(Tensor x) {
    return deque.peek().toPoint2D(x);
  }

  /** inspired by opengl
   * 
   * @param matrix 3x3 */
  public void pushMatrix(Tensor matrix) {
    deque.push(deque.peek().dot(matrix));
  }

  /** inspired by opengl */
  public void popMatrix() {
    deque.pop();
  }

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
    if (!Tensors.isEmpty(polygon)) {
      Point2D point2d = toPoint2D(polygon.get(0));
      path2d.moveTo(point2d.getX(), point2d.getY());
    }
    polygon.stream() //
        .skip(1) // first coordinate already used in moveTo
        .map(this::toPoint2D) //
        .forEach(point2d -> path2d.lineTo(point2d.getX(), point2d.getY()));
    return path2d;
  }

  /** @return {x, y, alpha} */
  public Tensor getMouseSe2State() {
    return mouseSe2State;
  }

  /** @return affine matrix that combines mouse location and mouse wheel rotation */
  public Tensor getMouseSe2Matrix() {
    return Se2Utils.toSE2Matrix(getMouseSe2State());
  }
}
