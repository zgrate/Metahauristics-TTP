package org.dzing.base;

public interface ItemChoiceAlgorithm {

    TTP.ItemsResponse selectItemsAndScore(TTP ttp, int[] citiesInOrder);

}
