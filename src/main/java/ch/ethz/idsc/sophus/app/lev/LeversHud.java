// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.sophus.krg.Biinvariant;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;

public enum LeversHud {
  ;
  public static void render(Biinvariant biinvariant, LeversRender leversRender) {
    render(biinvariant, leversRender, //
        ColorDataGradients.TEMPERATURE.deriveWithOpacity(RealScalar.of(0.5)));
  }

  public static void render( //
      Biinvariant biinvariant, LeversRender leversRender, ColorDataGradient colorDataGradient) {
    leversRender.renderSequence();
    leversRender.renderOrigin();
    leversRender.renderLevers();
    // ---
    switch (biinvariant) {
    case METRIC:
      leversRender.renderLeverLength();
      break;
    case LEVERAGE:
      leversRender.renderLeverages();
      break;
    case TARGET:
      leversRender.renderTangentsXtoP(false);
      leversRender.renderMahalanobisFormX(false, colorDataGradient);
      break;
    case HARBOR:
    case NORM2:
      leversRender.renderInfluenceX(colorDataGradient);
      leversRender.renderInfluenceP(colorDataGradient);
      break;
    case GARDEN:
      leversRender.renderTangentsPtoX(false);
      leversRender.renderMahalanobisFormsP(false, colorDataGradient);
      break;
    default:
      break;
    }
    leversRender.renderIndex();
  }
}
