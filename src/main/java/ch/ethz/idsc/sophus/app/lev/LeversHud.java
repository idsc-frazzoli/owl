// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.sophus.krg.Biinvariant;
import ch.ethz.idsc.sophus.krg.Biinvariants;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;

public enum LeversHud {
  ;
  public static final ColorDataGradient COLOR_DATA_GRADIENT = //
      ColorDataGradients.TEMPERATURE.deriveWithOpacity(RealScalar.of(0.5));

  public static void render(Biinvariant biinvariant, LeversRender leversRender) {
    render(biinvariant, leversRender, COLOR_DATA_GRADIENT);
  }

  public static void render( //
      Biinvariant biinvariant, LeversRender leversRender, ColorDataGradient colorDataGradient) {
    leversRender.renderSequence();
    leversRender.renderOrigin();
    leversRender.renderLevers();
    // ---
    Biinvariants biinvariants = (Biinvariants) biinvariant;
    switch (biinvariants) {
    case METRIC:
      leversRender.renderLeverLength();
      break;
    case TARGET:
      leversRender.renderTangentsXtoP(false); // boolean: no tangent plane
      if (leversRender.getSequence().length() <= 2)
        leversRender.renderMahalanobisFormXEV(colorDataGradient);
      else
        leversRender.renderMahalanobisEllipse();
      leversRender.renderLeverages();
      break;
    case ANCHOR:
      leversRender.renderInfluenceX(colorDataGradient);
      leversRender.renderLeverages();
      break;
    case GARDEN:
      leversRender.renderTangentsPtoX(false); // boolean: no tangent planes
      leversRender.renderMahalanobisEllipseP(); // no evs
      break;
    case HARBOR:
      leversRender.renderInfluenceX(colorDataGradient);
      leversRender.renderInfluenceP(colorDataGradient);
      break;
    default:
      break;
    }
    leversRender.renderIndexX();
    leversRender.renderIndexP();
  }
}
