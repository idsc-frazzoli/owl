// code by jph
package ch.ethz.idsc.sophus.app.clt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransition;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.ClothoidContext;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;

/* package */ class ClothoidDefectContainer extends ClothoidSolutions implements RenderInterface {
  private static final Scalar DENOM = RealScalar.of(5.0);
  // ---
  public final ClothoidContext clothoidContext;

  public ClothoidDefectContainer(ClothoidContext clothoidContext) {
    super(clothoidContext.s1(), clothoidContext.s2());
    this.clothoidContext = clothoidContext;
  }

  public boolean encodes(ClothoidContext clothoidContext) {
    return this.clothoidContext.s1().equals(clothoidContext.s1()) //
        && this.clothoidContext.s2().equals(clothoidContext.s2());
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    PathRender pathRender = new PathRender(new Color(0, 0, 0, 128));
    Tensor tensor = Transpose.of(Tensors.of(LAMBDAS, defects_real));
    pathRender.setCurve(tensor, false);
    pathRender.render(geometricLayer, graphics);
    int index = 0;
    for (Tensor _lambda : lambdas()) {
      Scalar lambda = (Scalar) _lambda;
      ClothoidBuilder clothoidBuilder = CustomClothoidBuilder.of(lambda);
      ClothoidTransition clothoidTransition = ClothoidTransition.of(clothoidBuilder, clothoidContext.p(), clothoidContext.q());
      Clothoid clothoid = clothoidTransition.clothoid();
      {
        Scalar length = clothoid.length().divide(DENOM);
        graphics.setColor(new Color(0, 128, 0));
        graphics.setStroke(new BasicStroke(2f));
        graphics.draw(geometricLayer.toLine2D(Tensors.of(lambda, length.zero()), Tensors.of(lambda, length)));
        graphics.setStroke(new BasicStroke(1f));
      }
      {
        Scalar length = lengths().Get(index);
        graphics.setColor(new Color(0, 128, 0));
        graphics.setStroke(new BasicStroke(2f));
        Scalar x = lambda.add(RealScalar.of(0.1));
        graphics.draw(geometricLayer.toLine2D(Tensors.of(x, length.zero()), Tensors.of(x, length)));
        graphics.setStroke(new BasicStroke(1f));
      }
      ++index;
    }
  }
}
