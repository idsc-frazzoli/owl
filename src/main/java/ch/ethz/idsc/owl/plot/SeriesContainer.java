// code by jph
package ch.ethz.idsc.owl.plot;

import java.awt.Color;

import ch.ethz.idsc.tensor.Tensor;

public class SeriesContainer {
  private final Tensor points;
  public Color color;
  private boolean joined;

  public SeriesContainer(Tensor points, Color color) {
    this.points = points;
    this.color = color;
  }

  public Tensor points() {
    return points;
  }

  public void setJoined(boolean joined) {
    this.joined = joined;
  }

  public boolean isJoined() {
    return joined;
  }
}
