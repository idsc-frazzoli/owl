// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.util.Objects;

import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.Biinvariants;

public enum Bitype {
  METRIC1, //
  METRIC2, //
  LEVERAGES1, //
  LEVERAGES2, //
  GARDEN, //
  HARBOR, //
  CUPOLA, //
  ;

  public Biinvariant from(ManifoldDisplay geodesicDisplay) {
    switch (this) {
    case METRIC1:
    case METRIC2:
      Biinvariant biinvariant = geodesicDisplay.metricBiinvariant();
      return Objects.nonNull(biinvariant) //
          ? biinvariant
          : Biinvariants.LEVERAGES;
    case LEVERAGES1:
    case LEVERAGES2:
      return Biinvariants.LEVERAGES;
    case GARDEN:
      return Biinvariants.GARDEN;
    case HARBOR:
      return Biinvariants.HARBOR;
    case CUPOLA:
      return Biinvariants.CUPOLA;
    }
    throw new IllegalArgumentException();
  }
}
