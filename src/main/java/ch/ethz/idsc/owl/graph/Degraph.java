// code by jph
package ch.ethz.idsc.owl.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.mat.SquareMatrixQ;

/** T is the type of vertex label */
// TODO JPH/ASTOLL class name is not final
/* package */ class Degraph<T> {
  private final Collection<Vert<T>> set = new HashSet<>();

  public Vert<T> createSingletonVert(T type) {
    Vert<T> vert = new Vert<>(type);
    set.add(vert);
    Tensor matrixDouble = Tensors.matrixDouble(new double[][] { { 1, 2, 3 }, { 1, 2, 3 }, { 1, 2, 3 } });
    SquareMatrixQ.of(matrixDouble);
    Tensor plot = ArrayPlot.of(matrixDouble, ColorDataGradients.CLASSIC);
    Tensor nearest = ImageResize.nearest(plot, 10);
    // Export.of(HomeDirectory.Pictures("degraph.png"), nearest);
    return vert;
  }

  public Collection<Vert<T>> verts() {
    return Collections.unmodifiableCollection(set);
  }
}
