package org.dzing.genetic.base;

import org.dzing.base.City;

public interface Cross {

    City[][] cross(City[] firstParent, City[] secondParent);
}
