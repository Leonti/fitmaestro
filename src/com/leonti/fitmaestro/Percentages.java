package com.leonti.fitmaestro;

public class Percentages {
	
	private Double mPrecision;
	public Percentages(Double precision){
		mPrecision = precision;
	}
	public Percentages(){
		
	}

	public Double getValue(Double percentage, Double value){
		
		Double calculatedValue = value != 0 ?percentage*value/100 : 0;
		return calculatedValue;
	}
	
	public long getIntValue(Double percentage, Long value){
		
		Double calculatedValue = this.getValue(percentage, Double.valueOf(value));
		
		return Math.round(calculatedValue);
	}
}
