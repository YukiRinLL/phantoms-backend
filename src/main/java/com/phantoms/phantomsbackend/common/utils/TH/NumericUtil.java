package com.phantoms.phantomsbackend.common.utils.TH;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;


public class NumericUtil {
	protected static Log logger = LogFactory.getLog(NumericUtil.class);
	private static String CURRENCY_OR_SCIENTIFIC = "[$]?[-+]{0,1}[\\d.,]*[eE]?[-]?[\\d]*";


	public static double parseCurrency(String value){
		double widgetValue;
		try{
			NumberFormat nf = NumberFormat.getInstance();
			widgetValue = nf.parse(value.toString()).doubleValue();
		}catch(ParseException e){
			logger.debug("!@# Exception Caught, trying to parse as Currency");
			NumberFormat nfc = NumberFormat.getCurrencyInstance();
			try{
				widgetValue = nfc.parse(value.toString()).doubleValue();
			}catch(ParseException e1){
				return Double.NaN;
			}
		}catch(NumberFormatException e){
			return Double.NaN;
		}

		if(!(value.toString().matches(CURRENCY_OR_SCIENTIFIC))){
			return Double.NaN;
		}
		return widgetValue;
	}

	public static double parseNumber(String value){
		double widgetValue;
		try{
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(40);
			nf.setMaximumIntegerDigits(40);
			widgetValue = nf.parse(value.toString()).doubleValue();
		}catch(ParseException e){
			return Double.NaN;
		}catch(NumberFormatException e){
			return Double.NaN;
		}catch (Exception e) {
			return Double.NaN;
		}

		if(!(value.toString().matches(CURRENCY_OR_SCIENTIFIC))){
			return Double.NaN;
		}
		return widgetValue;
	}

	public static boolean isNumber(String value){
		//Calls parseNumber
		if(Double.isNaN(NumericUtil.parseNumber(value))){
			return false;
		}
		return true;
	}
	public static boolean isInteger(String value){
		//Calls parseNumber
		try{
			Integer.parseInt(value);
		}catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isCurrency(String value){
		//Calls parseCurrency
		double parseCurrency = NumericUtil.parseCurrency(value);
		if(Double.isNaN(parseCurrency)){
			return false;
		}
		return true;
	}

	public static double getDoubleValue(String value){
		//Calls parseNumber
		if(StringUtils.isEmpty(value)){
			return 0;
		}
		return parseNumber(value.trim());
	}

	public static float getFloatValue(String value){
		//Calls parseNumber
		if(StringUtils.isEmpty(value)){
			return 0;
		}
		return Float.parseFloat(value);
	}
	public static double getDoubleValue(Number value, float defaultValue){
		//Calls parseNumber
		if(value==null){
			return defaultValue;
		}
		return value.doubleValue();
	}

	public static  double getDouble(Object data)
	{
		double value = 0;
		try
		{
			value = Double.parseDouble(data.toString());
		}catch (Exception e)
		{}
		return value;
	}

	public static float getFloatValue(Number value, float defaultValue){
		//Calls parseNumber
		if(value==null){
			return defaultValue;
		}
		return value.floatValue();
	}

	public static int getIntValue(Object obj)
    {
    	return obj==null ? 0 : ((Integer)obj).intValue();
    }

	public static int getInt(Object data)
	{
		int value = 0;
		try
		{
			value = Integer.parseInt(data.toString());
		}catch (Exception e)
		{}
		return value;
	}

	public static int getLongValue(Object obj)
    {
    	return obj==null ? 0 : ((Long)obj).intValue();
    }

	public static int getIntValue(String obj)
    {
		if(StringUtils.isEmpty(obj)){
			return 0;
		}
		return getInt(obj);
    }

	public static String numToStrPrefix(int n, char prefix , int length) {
		String s = String.valueOf(n);
		int l = length - s.length();
		if(l > 0) {
			for (int i = 0; i < l; i++) {
				s = prefix + s;
			}
		}
		return s;
	}

	public static String numToStrSuffix(int n, char suffix , int length) {
		String s = String.valueOf(n);
		int l = length - s.length();
		if(l > 0) {
			for (int i = 0; i < l; i++) {
				s = s + suffix;
			}
		}
		return s;
	}
	public static  DecimalFormat df = new DecimalFormat("###0.#####");
	public static  DecimalFormat intdf = new DecimalFormat("###0");
	public static double formatNumber(double d){
		return Double.parseDouble(df.format(d));
	}
	public static String formatNumberToStr(double d){
		return df.format(d);
	}
	public static double doubleAdd(double d1,double... d2){
		BigDecimal b1 = new BigDecimal(formatNumberToStr(d1));
		BigDecimal result = b1;
		if(d2!=null){
			for(double d:d2){
				BigDecimal b2 = new BigDecimal(formatNumberToStr(d));
				result = result.add(b2);
			}
		}

		return result.doubleValue();
	}
	public static double subtract(double d1,double... d2){
		BigDecimal b1 = new BigDecimal(formatNumberToStr(d1));
		BigDecimal result = b1;
		if(d2!=null){
			for(double d:d2){
				BigDecimal b2 = new BigDecimal(formatNumberToStr(d));
				result = result.subtract(b2);
			}
		}

		return result.doubleValue();
	}
	public static double doubleMinus(double d1,double... d2){
		BigDecimal b1 = new BigDecimal(formatNumberToStr(d1));
		BigDecimal result = b1;
		if(d2!=null){
			for(double d:d2){
				BigDecimal b2 = new BigDecimal(formatNumberToStr(d));
				result = result.add(b2.negate());
			}
		}
		return result.doubleValue();
	}
	public static double doubleMultiply(double d1,double... d2){
		BigDecimal b1 = new BigDecimal(formatNumberToStr(d1));
		BigDecimal result = b1;
		if(d2!=null){
			for(double d:d2){
				BigDecimal b2 = new BigDecimal(formatNumberToStr(d));
				result = result.multiply(b2);
			}
		}
		return result.doubleValue();
	}
	public static double doubleDivide(double d1,double d2){
		BigDecimal b1 = new BigDecimal(formatNumberToStr(d1));
		BigDecimal b2 = new BigDecimal(formatNumberToStr(d2));
		BigDecimal result = b1.divide(b2, 5, BigDecimal.ROUND_HALF_UP);
		return result.doubleValue();
	}

	public static double doubleRemainder(double d1,double d2){
		BigDecimal b1 = new BigDecimal(formatNumberToStr(d1));
		BigDecimal b2 = new BigDecimal(formatNumberToStr(d2));
		BigDecimal result = b1.remainder(b2);
		return result.doubleValue();
	}

	public static long doubleHalfUp(double d){
		BigDecimal b = new BigDecimal(formatNumberToStr(d));
		BigDecimal result = b.divide(new BigDecimal(1), 0, BigDecimal.ROUND_HALF_UP);
		return result.longValue();
	}

	public static int getInt(double d){
		BigDecimal b = new BigDecimal(d);
		return b.intValue();
	}

	public static double calculateUpPackUom(Double qty, Double packnum){
		double packuom=1;
		BigDecimal qtyb = new BigDecimal(formatNumber(qty.doubleValue()));
		BigDecimal packnumb = new BigDecimal(formatNumber(packnum.doubleValue()));

		packuom = Math.ceil(qtyb.divide(packnumb, BigDecimal.ROUND_CEILING).doubleValue());

		return packuom;
	}
	public static double calculateDownPackUom(Double qty, Double packnum){
		double packuom=1;
		BigDecimal qtyb = new BigDecimal(formatNumber(qty.doubleValue()));
		BigDecimal packnumb = new BigDecimal(formatNumber(packnum.doubleValue()));

		packuom = Math.floor(qtyb.divide(packnumb,5, BigDecimal.ROUND_HALF_UP).doubleValue());

		return packuom;
	}
}
