// code by gjoel, jph
package ch.ethz.idsc.sophus.util.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;

public class VisualRow implements Serializable {
  private static final Stroke STROKE_DEFAULT = new BasicStroke(1f);
  // ---
  private final Tensor points;
  private final ComparableLabel comparableLabel;
  private Color color = Color.BLUE;
  /** not serializable */
  private transient Stroke stroke;

  /** Mathematica::ListPlot[points]
   * 
   * @param points of the form {{x1, y1}, {x2, y2}, ..., {xn, yn}}.
   * The special case when points == {} is also allowed. */
  VisualRow(Tensor points, int index) {
    ScalarQ.thenThrow(points);
    this.points = points;
    this.comparableLabel = new ComparableLabel(index);
  }

  /** @return points of the form {{x1, y1}, {x2, y2}, ..., {xn, yn}} */
  public Tensor points() {
    return points.unmodifiable();
  }

  public void setColor(Color color) {
    this.color = Objects.requireNonNull(color);
  }

  public Color getColor() {
    return color;
  }

  public void setStroke(Stroke stroke) {
    this.stroke = Objects.requireNonNull(stroke);
  }

  public Stroke getStroke() {
    return Objects.isNull(stroke) //
        ? STROKE_DEFAULT
        : stroke;
  }

  public void setLabel(String string) {
    comparableLabel.setString(string);
  }

  public String getLabelString() {
    return getLabel().toString();
  }

  /* package */ ComparableLabel getLabel() {
    return comparableLabel;
  }
}
