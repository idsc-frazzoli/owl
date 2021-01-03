// code by jph
package ch.ethz.idsc.sophus.app.clt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.sophus.clt.ClothoidContext;
import ch.ethz.idsc.sophus.clt.ClothoidEmit;
import ch.ethz.idsc.sophus.clt.ClothoidSolutions;
import ch.ethz.idsc.sophus.clt.ClothoidSolutions.Search;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class ClothoidDefectContainer implements RenderInterface {
  private static final Clip RANGE = Clips.absolute(15.0);
  private static final Scalar DENOM = RealScalar.of(5.0);
  private static final ClothoidSolutions CLOTHOID_SOLUTIONS = ClothoidSolutions.of(RANGE);
  // ---
  public final Search search;
  public final ClothoidContext clothoidContext;

  public ClothoidDefectContainer(ClothoidContext clothoidContext) {
    search = CLOTHOID_SOLUTIONS.new Search(clothoidContext.s1(), clothoidContext.s2());
    this.clothoidContext = clothoidContext;
  }

  public boolean encodes(ClothoidContext clothoidContext) {
    return this.clothoidContext.s1().equals(clothoidContext.s1()) //
        && this.clothoidContext.s2().equals(clothoidContext.s2());
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    PathRender pathRender = new PathRender(new Color(0, 0, 0, 128));
    Tensor tensor = Transpose.of(Tensors.of(CLOTHOID_SOLUTIONS.probes, search.defects_real));
    pathRender.setCurve(tensor, false);
    pathRender.render(geometricLayer, graphics);
    Tensor lambdas = search.lambdas();
    List<Clothoid> clothoids = ClothoidEmit.stream(clothoidContext, lambdas).collect(Collectors.toList());
    for (int index = 0; index < lambdas.length(); ++index) {
      Scalar lambda = lambdas.Get(index);
      Clothoid clothoid = clothoids.get(index);
      {
        Scalar length = clothoid.length().divide(DENOM);
        graphics.setColor(new Color(0, 128, 0));
        graphics.setStroke(new BasicStroke(2f));
        graphics.draw(geometricLayer.toLine2D(Tensors.of(lambda, length.zero()), Tensors.of(lambda, length)));
        graphics.setStroke(new BasicStroke(1f));
      }
      {
        Scalar length = clothoid.length();
        graphics.setColor(new Color(0, 128, 0));
        graphics.setStroke(new BasicStroke(2f));
        Scalar x = lambda.add(RealScalar.of(0.1));
        graphics.draw(geometricLayer.toLine2D(Tensors.of(x, length.zero()), Tensors.of(x, length)));
        graphics.setStroke(new BasicStroke(1f));
      }
    }
  }
}
