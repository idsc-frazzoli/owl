// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.util.List;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.img.ColorDataLists;

/* package */ enum UbongoRender {
  ;
  public static Tensor of(Tensor mask, List<UbongoEntry> solution) {
    List<Integer> list = Dimensions.of(mask);
    Tensor image = ConstantArray.of(DoubleScalar.INDETERMINATE, list).copy();
    for (UbongoEntry ubongoEntry : solution) {
      List<Integer> size = Dimensions.of(ubongoEntry.stamp);
      for (int si = 0; si < size.get(0); ++si)
        for (int sj = 0; sj < size.get(1); ++sj)
          if (Scalars.nonZero(ubongoEntry.stamp.Get(si, sj)))
            image.set(RealScalar.of(ubongoEntry.ubongo.ordinal()), ubongoEntry.i + si, ubongoEntry.j + sj);
    }
    return image.map(ColorDataLists._251.strict());
  }
}
