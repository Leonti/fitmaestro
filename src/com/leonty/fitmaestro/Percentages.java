package com.leonty.fitmaestro;

import java.math.BigDecimal;

public class Percentages {
	
	private Double mPrecision;
	public Percentages(Double precision){
		mPrecision = precision;
	}
	public Percentages(){
		mPrecision = 0.5;
	}

	public Double getValue(Double percentage, Double value){
		
		Double calculatedValue = value != 0 ? percentage*value/100 : 0;
		return calculatedValue;
	}
	
	public Double getValueWithPrecision(Double percentage, Double value){
		
		Double calculatedValue = this.getValue(percentage, Double.valueOf(value));
		Double steppedNumber = mPrecision != 0 ? Math.round(calculatedValue / mPrecision) * mPrecision 
				: Math.round(calculatedValue);
		BigDecimal bd = new BigDecimal(steppedNumber);
	    bd = bd.setScale(2,BigDecimal.ROUND_HALF_DOWN);
	    
		return bd.doubleValue();
	}
	
	public long getIntValue(Double percentage, Long value){
		
		Double calculatedValue = this.getValue(percentage, Double.valueOf(value));
		
		return Math.round(calculatedValue);
	}
}
