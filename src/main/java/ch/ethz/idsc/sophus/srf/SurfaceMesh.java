// code by jph
package ch.ethz.idsc.sophus.srf;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Primitives;

public class SurfaceMesh {
  public final Tensor ind = Tensors.empty();
  public Tensor vrt = Tensors.empty();

  public int addVert(Tensor vector) {
    int index = vrt.length();
    vrt.append(vector);
    return index;
  }

  public void addFace(int... values) {
    ind.append(Tensors.vectorInt(values));
  }

  public Tensor polygons() {
    return Tensor.of(ind.stream() //
        .map(face -> Tensor.of(IntStream.of(Primitives.toIntArray(face)).mapToObj(vrt::get))));
  }

  /** @return vert to face index */
  public List<List<Integer>> vertToFace() {
    @SuppressWarnings("unused")
    List<List<Integer>> list = IntStream.range(0, vrt.length()) //
        .mapToObj(i -> new ArrayList<Integer>()) //
        .collect(Collectors.toList());
    // ---
    int index = 0;
    for (Tensor face : ind) {
      for (int value : Primitives.toIntArray(face))
        list.get(value).add(index);
      ++index;
    }
    return list;
  }
}
