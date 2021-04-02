package org.mycompany.service;

import org.mycompany.service.exception.MarkerServiceException;

public interface MarketService {
    String trySell(int count, int price, String userName) throws MarkerServiceException;


    String tryBuy(int count, int price, String userName) throws MarkerServiceException;
}
