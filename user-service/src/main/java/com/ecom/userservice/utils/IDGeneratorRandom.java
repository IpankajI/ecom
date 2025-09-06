package com.ecom.userservice.utils;

import java.util.Random;

public class IDGeneratorRandom implements IDGenerator{
    private Random random;
    public IDGeneratorRandom(){
        random=new Random(System.currentTimeMillis());
    }
    public long next(){
        System.out.println("......... dd");
        return random.nextLong(0, Long.MAX_VALUE);
    }
}
