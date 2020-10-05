// code by gjoel, jph
package ch.ethz.idsc.tensor.fig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jfree.chart.ChartFactory;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

public class VisualSet implements Serializable {
  private static final long serialVersionUID = 4099546058796673432L;
  static {
    ChartFactory.setChartTheme(DefaultChartTheme.STANDARD);
    // BarRenderer.setDefaultBarPainter(new StandardBarPainter());
    // BarRenderer.setDefaultShadowsVisible(false);
  }
  // ---
  private final List<VisualRow> visualRows = new ArrayList<>();
  private final ColorDataIndexed colorDataIndexed;
  private String plotLabel = "";
  private String axesLabelX = "";
  private String axesLabelY = "";

  public VisualSet(ColorDataIndexed colorDataIndexed) {
    this.colorDataIndexed = Objects.requireNonNull(colorDataIndexed);
  }

  /** uses Mathematica default color scheme */
  public VisualSet() {
    this(ColorDataLists._097.cyclic());
  }

  /** @param points of the form {{x1, y1}, {x2, y2}, ..., {xn, yn}}.
   * The special case when points == {} is also allowed.
   * @return instance of the visual row, that was added to this visual set
   * @throws Exception if not all entries in points are vectors of length 2 */
  public VisualRow add(Tensor points) {
    final int index = visualRows.size();
    points.stream().forEach(row -> VectorQ.requireLength(row, 2));
    VisualRow visualRow = new VisualRow(points, index);
    visualRow.setColor(colorDataIndexed.getColor(index));
    visualRows.add(visualRow);
    return visualRow;
  }

  /** @param domain {x1, x2, ..., xn}
   * @param values {y1, y2, ..., yn}
   * @return */
  public VisualRow add(Tensor domain, Tensor values) {
    return add(Transpose.of(Tensors.of(domain, values)));
  }

  public List<VisualRow> visualRows() {
    return Collections.unmodifiableList(visualRows);
  }

  public VisualRow getVisualRow(int index) {
    return visualRows.get(index);
  }

  /** @param string to appear above plot */
  public void setPlotLabel(String string) {
    plotLabel = string;
  }

  /** @return */
  public String getPlotLabel() {
    return plotLabel;
  }

  /** @param string label of x-axis */
  public void setAxesLabelX(String string) {
    axesLabelX = string;
  }

  /** @return label of x-axis */
  public String getAxesLabelX() {
    return axesLabelX;
  }

  /** @param string label of y-axis */
  public void setAxesLabelY(String string) {
    axesLabelY = string;
  }

  /** @return label of y-axis */
  public String getAxesLabelY() {
    return axesLabelY;
  }

  public boolean hasLegend() {
    return visualRows.stream() //
        .map(VisualRow::getLabelString) //
        .anyMatch(string -> !string.isEmpty());
  }
}
