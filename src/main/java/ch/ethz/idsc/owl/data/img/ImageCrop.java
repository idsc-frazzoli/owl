// code by jph
package ch.ethz.idsc.owl.data.img;

import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.TensorMap;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Boole;
import ch.ethz.idsc.tensor.red.Total;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ImageCrop.html">ImageCrop</a> */
// TODO obsolete V062
public class ImageCrop implements TensorUnaryOperator {
  public static TensorUnaryOperator constant(Tensor value) {
    return new ImageCrop(entry -> entry.equals(value));
  }

  // ---
  private final Function<Tensor, Boolean> function;

  private ImageCrop(Function<Tensor, Boolean> function) {
    this.function = function;
  }

  @Override
  public Tensor apply(Tensor image) {
    int dim0 = image.length();
    int dim1 = Unprotect.dimension1(image);
    Tensor boole = TensorMap.of(entry -> Boole.of(function.apply(entry)), image, 2);
    Tensor vectorX = TensorMap.of(Total::of, boole, 0);
    Tensor vectorY = TensorMap.of(Total::of, boole, 1);
    Scalar dimS0 = RealScalar.of(dim0);
    Scalar dimS1 = RealScalar.of(dim1);
    OptionalInt xMin = IntStream.range(0, dim1) //
        .filter(index -> !vectorX.Get(index).equals(dimS0)).findFirst();
    OptionalInt xMax = IntStream.range(0, dim1) //
        .filter(index -> !vectorX.Get(dim1 - 1 - index).equals(dimS0)).findFirst();
    OptionalInt yMin = IntStream.range(0, dim0) //
        .filter(index -> !vectorY.Get(index).equals(dimS1)).findFirst();
    OptionalInt yMax = IntStream.range(0, dim0) //
        .filter(index -> !vectorY.Get(dim0 - 1 - index).equals(dimS1)).findFirst();
    int xmin = xMin.orElse(0);
    int xmax = dim1 - xMax.orElse(0);
    int skip = yMin.orElse(0);
    return Tensor.of(image.stream() //
        .skip(skip) //
        .limit(dim0 - yMax.orElse(0) - skip) //
        .map(row -> row.extract(xmin, xmax)));
  }
}
