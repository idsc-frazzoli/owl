// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.util.Arrays;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class ImageGradientTest extends TestCase {
  public void testSimple() throws Exception {
    Tensor range = Tensors.vector(9, 6.5);
    Tensor res;
    Scalar max;
    final Tensor image = ResourceData.of("/io/delta_uxy.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    {
      ImageGradient ig = ImageGradient.linear(image, range, RealScalar.of(.5));
      res = ig.rotate(Tensors.vector(2, 3));
      max = ig.maxNormGradient();
    }
    {
      ImageGradient ig = ImageGradient.linear(image, range, RealScalar.ONE);
      Tensor cmp = ig.rotate(Tensors.vector(2, 3));
      assertEquals(cmp, res.multiply(RealScalar.of(2)));
      assertEquals(ig.maxNormGradient(), max.multiply(RealScalar.of(2)));
    }
  }

  public void testNearest() throws Exception {
    Tensor range = Tensors.vector(9, 6.5);
    Tensor res;
    Scalar max;
    final Tensor image = ResourceData.of("/io/delta_uxy.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    {
      ImageGradient ig = ImageGradient.nearest(image, range, RealScalar.of(.5));
      res = ig.rotate(Tensors.vector(2, 3));
      max = ig.maxNormGradient();
    }
    {
      ImageGradient ig = ImageGradient.nearest(image, range, RealScalar.ONE);
      Tensor cmp = ig.rotate(Tensors.vector(2, 3));
      assertEquals(cmp, res.multiply(RealScalar.of(2)));
      assertEquals(ig.maxNormGradient(), max.multiply(RealScalar.of(2)));
      assertEquals(ig.rotate(Tensors.vector(22, -3)), Array.zeros(2));
    }
  }

  public void testSerialize() throws Exception {
    Tensor range = Tensors.vector(9, 6.5);
    final Tensor image = ResourceData.of("/io/delta_uxy.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    ImageGradient ig = ImageGradient.linear(image, range, RealScalar.of(.5));
    @SuppressWarnings("unused")
    ImageGradient ig2 = Serialization.copy(ig);
  }
}
