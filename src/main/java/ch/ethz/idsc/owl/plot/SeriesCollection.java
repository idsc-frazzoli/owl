// code by jph
package ch.ethz.idsc.owl.plot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.red.Entrywise;

public class SeriesCollection implements Iterable<SeriesContainer> {
  private String title = "untitled";
  private final List<SeriesContainer> list = new ArrayList<>();

  public SeriesContainer add(Tensor xData, Tensor yData) {
    return add(Transpose.of(Tensors.of(xData, yData)));
  }

  public SeriesContainer add(Tensor points) {
    Color color = ColorLists._97.get(list.size());
    SeriesContainer seriesContainer = new SeriesContainer(points, color);
    list.add(seriesContainer);
    return seriesContainer;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  /** @return {{x_min, x_max}, {y_min, y_max}} */
  public Tensor getPlotRange() {
    return Transpose.of(Tensors.of( //
        list.stream().map(SeriesContainer::points).flatMap(Tensor::stream).reduce(Entrywise.min()).get(), //
        list.stream().map(SeriesContainer::points).flatMap(Tensor::stream).reduce(Entrywise.max()).get()));
  }

  public Stream<SeriesContainer> stream() {
    return list.stream();
  }

  @Override
  public Iterator<SeriesContainer> iterator() {
    return list.iterator();
  }

  public static void main(String[] args) {
    SeriesCollection pd = new SeriesCollection();
    pd.add(Tensors.vector(1, 2, 3, 4), Tensors.vector(10, -2, 9, -5));
    Tensor pr = pd.getPlotRange();
    System.out.println(pr);
  }
}
