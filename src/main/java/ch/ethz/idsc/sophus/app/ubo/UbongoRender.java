// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.util.List;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.CyclicColorDataIndexed;
import ch.ethz.idsc.tensor.img.StrictColorDataIndexed;

/* package */ enum UbongoRender {
  ;
  private static final ColorDataIndexed INSTANCE = StrictColorDataIndexed.of(Tensor.of(Stream.of(Ubongo.values()).map(Ubongo::colorVector)));
  private static final ColorDataIndexed MONOCHROME = CyclicColorDataIndexed.of(Tensors.of(Tensors.vector(160, 160, 160, 255)));

  /** @param list
   * @param solution
   * @return */
  public static Tensor matrix(List<Integer> list, List<UbongoEntry> solution) {
    Tensor image = ConstantArray.of(DoubleScalar.INDETERMINATE, list).copy();
    for (UbongoEntry ubongoEntry : solution) {
      List<Integer> size = Dimensions.of(ubongoEntry.stamp);
      for (int si = 0; si < size.get(0); ++si)
        for (int sj = 0; sj < size.get(1); ++sj)
          if (Scalars.nonZero(ubongoEntry.stamp.Get(si, sj)))
            image.set(RealScalar.of(ubongoEntry.ubongo.ordinal()), ubongoEntry.i + si, ubongoEntry.j + sj);
    }
    return image;
  }

  /** @param list
   * @param solution
   * @return */
  public static Tensor of(List<Integer> list, List<UbongoEntry> solution) {
    return matrix(list, solution).map(INSTANCE);
  }

  /** @param list
   * @param solution
   * @return */
  public static Tensor gray(List<Integer> list, List<UbongoEntry> solution) {
    return matrix(list, solution).map(MONOCHROME);
  }
}
