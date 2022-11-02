package org.dzing.tabu;

import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.TTP;

public interface TabuInitializer {

    TTP.ItemsResponse initialize(TTP ttp, ItemChoiceAlgorithm algo);

}
