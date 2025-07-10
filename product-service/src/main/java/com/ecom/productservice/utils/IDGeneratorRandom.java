package com.ecom.productservice.utils;

import java.util.Random;

public class IDGeneratorRandom implements IDGenerator{
    private Random random;
    public IDGeneratorRandom(){
        random=new Random(System.currentTimeMillis());
    }
    public long next(){
        return random.nextLong(0, Long.MAX_VALUE);
    }
}
