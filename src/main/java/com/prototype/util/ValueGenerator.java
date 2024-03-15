package com.prototype.util;

import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

public class ValueGenerator {

	public static String getRandomAlphaNumeric(int codeLength) {
		
		long seed = 123L; 
        Random randomWithSeed = new Random(seed);
        
		return RandomStringUtils.random(codeLength, 0, 0, true, true, null, randomWithSeed);
	}
}
