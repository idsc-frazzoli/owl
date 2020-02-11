// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.red.VectorAngle;

public class ArrayRender implements RenderInterface {
  private final Tensor[][] array;
  private final ColorDataGradient colorDataGradient;

  public ArrayRender(Tensor[][] array, ColorDataGradient colorDataGradient) {
    this.array = Objects.requireNonNull(array);
    this.colorDataGradient = colorDataGradient;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    for (int i0 = 1; i0 < array.length; ++i0)
      for (int i1 = 1; i1 < array.length; ++i1) {
        Tensor c = array[i0][i1];
        Tensor p0 = array[i0 - 1][i1];
        Tensor p1 = array[i0][i1 - 1];
        Tensor pc = array[i0 - 1][i1 - 1];
        {
          Scalar scalar = VectorAngle.of(p0.subtract(c), p1.subtract(c)).get();
          Tensor rgba = colorDataGradient.apply(scalar.divide(Pi.VALUE));
          graphics.setColor(ColorFormat.toColor(rgba));
          graphics.fill(geometricLayer.toPath2D(Unprotect.byRef(c, p0, pc, p1)));
        }
        graphics.draw(geometricLayer.toPath2D(Tensors.of(p0, c)));
        graphics.draw(geometricLayer.toPath2D(Tensors.of(p1, c)));
      }
  }
}
