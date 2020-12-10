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
      for (int i1 = 1; i1 < array[i0].length; ++i1) {
        Tensor po = array[i0][i1];
        Tensor p0 = array[i0 - 1][i1];
        Tensor p1 = array[i0][i1 - 1];
        Tensor pd = array[i0 - 1][i1 - 1];
        {
          Scalar shading = QuadShading.ANGLE.map(po, p0, p1, pd);
          graphics.setColor(ColorFormat.toColor(colorDataGradient.apply(shading)));
          graphics.fill(geometricLayer.toPath2D(Unprotect.byRef(po, p0, pd, p1)));
        }
        graphics.draw(geometricLayer.toPath2D(Tensors.of(p0, po)));
        graphics.draw(geometricLayer.toPath2D(Tensors.of(p1, po)));
      }
  }
}
