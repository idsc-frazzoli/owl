// code by jph
package ch.ethz.idsc.sophus.surf.subdiv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.io.Primitives;

public class SurfaceMesh {
  public Tensor ind = Tensors.empty();
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
    Tensor tensor = Unprotect.empty(ind.length());
    for (Tensor face : ind)
      tensor.append(Tensor.of(IntStream.of(Primitives.toIntArray(face)).mapToObj(vrt::get)));
    return tensor;
  }

  public Tensor vertRings() {
    Map<Integer, List<Integer>> map = new HashMap<>();
    int index = 0;
    for (Tensor face : ind) {
      int[] values = Primitives.toIntArray(face);
      for (int value : values) {
        if (!map.containsKey(value))
          map.put(value, new ArrayList<>());
        map.get(value).add(index);
      }
      ++index;
    }
    return null;
  }
}
