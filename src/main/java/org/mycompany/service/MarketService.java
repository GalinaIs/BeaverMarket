package org.mycompany.service;

import org.mycompany.service.exception.MarkerServiceException;

public interface MarketService {
    String trySell(int count, int price) throws MarkerServiceException;


    String tryBuy(int count, int price) throws MarkerServiceException;
}
