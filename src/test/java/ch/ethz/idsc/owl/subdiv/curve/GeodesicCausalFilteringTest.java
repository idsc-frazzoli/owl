package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.owl.math.group.LieGroupGeodesic;
import ch.ethz.idsc.owl.math.group.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.group.Se2Group;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicCausalFilteringTest extends TestCase {
  public void testSimple() {
    GeodesicInterface geodesicInterface = //
        new LieGroupGeodesic(Se2Group.INSTANCE::element, Se2CoveringExponential.INSTANCE);
    

    GeodesicCausalFiltering geodesicCausalFiltering = new GeodesicCausalFiltering(Tensors.of(//
        Tensors.vector(1,2,0.5), Tensors.vector(4,5,0.5), Tensors.vector(6.164525387368366, 8.648949142895502, 0.75)));
    assertTrue((RealScalar.ZERO.equals(geodesicCausalFiltering.log.get(geodesicCausalFiltering.log.length()-1).Get(1))));
    
    
    // The following test is maybe not useful because it might fail when we change the alpharange or filter
    assertTrue((RealScalar.of(0.98075).equals(geodesicCausalFiltering.log.get(0).Get(1))));

  }
  
}
