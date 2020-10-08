// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.util.Arrays;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class ImageGradientInterpolationTest extends TestCase {
  public void testLinear() {
    Tensor range = Tensors.vector(9, 6.5);
    Tensor res;
    Scalar max;
    final Tensor image = ResourceData.of("/io/delta_uxy.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    {
      ImageGradientInterpolation imageGradientInterpolation = //
          ImageGradientInterpolation.linear(image, range, RealScalar.of(0.5));
      res = imageGradientInterpolation.get(Tensors.vector(2, 3));
      max = imageGradientInterpolation.maxNormGradient();
    }
    {
      ImageGradientInterpolation imageGradientInterpolation = //
          ImageGradientInterpolation.linear(image, range, RealScalar.ONE);
      Tensor cmp = imageGradientInterpolation.get(Tensors.vector(2, 3));
      assertEquals(cmp, res.multiply(RealScalar.of(2)));
      assertEquals(imageGradientInterpolation.maxNormGradient(), max.multiply(RealScalar.of(2)));
    }
  }

  public void testNearest() {
    Tensor range = Tensors.vector(9, 6.5);
    Tensor res;
    Scalar max;
    final Tensor image = ResourceData.of("/io/delta_uxy.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    {
      ImageGradientInterpolation imageGradientInterpolation = //
          ImageGradientInterpolation.nearest(image, range, RealScalar.of(0.5));
      res = imageGradientInterpolation.get(Tensors.vector(2, 3));
      max = imageGradientInterpolation.maxNormGradient();
    }
    {
      ImageGradientInterpolation imageGradientInterpolation = //
          ImageGradientInterpolation.nearest(image, range, RealScalar.ONE);
      Tensor cmp = imageGradientInterpolation.get(Tensors.vector(2, 3));
      assertEquals(cmp, res.multiply(RealScalar.of(2)));
      assertEquals(imageGradientInterpolation.maxNormGradient(), max.multiply(RealScalar.of(2)));
      assertEquals(imageGradientInterpolation.get(Tensors.vector(22, -3)), Array.zeros(2));
    }
  }

  public void testSerialize() throws Exception {
    Tensor range = Tensors.vector(9, 6.5);
    final Tensor image = ResourceData.of("/io/delta_uxy.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    ImageGradientInterpolation imageGradientInterpolation = //
        ImageGradientInterpolation.linear(image, range, RealScalar.of(0.5));
    Serialization.copy(imageGradientInterpolation);
  }
}
