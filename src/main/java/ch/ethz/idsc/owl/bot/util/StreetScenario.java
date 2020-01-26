// code by jph
package ch.ethz.idsc.owl.bot.util;

public enum StreetScenario {
  S1, //
  S3, //
  S4, //
  S5, //
  S6, //
  S7, //
  S8;

  public StreetScenarioData load() {
    return StreetScenarioData.load(name().toLowerCase());
  }
}
