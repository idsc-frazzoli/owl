// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.io.IOException;

import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class KlotskiFlowsTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Serialization.copy(new KlotskiFlows(Huarong.AMBUSH.create()));
  }
}
