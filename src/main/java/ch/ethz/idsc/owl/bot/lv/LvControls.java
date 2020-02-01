// code by jph
package ch.ethz.idsc.owl.bot.lv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ enum LvControls {
  ;
  public static Collection<Tensor> create(int num) {
    List<Tensor> list = new ArrayList<>();
    for (Tensor u : Subdivide.of(0.0, 1.0, num))
      list.add(Tensors.of(u));
    return list;
  }
}
