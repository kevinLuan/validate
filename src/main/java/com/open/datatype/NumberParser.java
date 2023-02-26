package com.open.datatype;

import com.open.param.Primitive;

public class NumberParser {

	public boolean isDouble;
	public boolean isLong;
	public Number value;

	public boolean isNull() {
		return this.value == null;
	}

	public static NumberParser parse(String value, boolean require) {
		NumberParser numberParser = new NumberParser();
		if (value != null) {
			if (value.indexOf(".") != -1) {
				numberParser.value = Double.parseDouble(value);
				numberParser.isDouble = true;
			} else {
				numberParser.value = Long.parseLong(value);
				numberParser.isLong = true;
			}
		}
		return numberParser;
	}

	public void check(Primitive primitive) {
		if (primitive.isRequire()) {
			if (value == null) {
				throw new IllegalArgumentException(primitive.getName() + "参数不能为空");
			}
		} else {
			if (value == null) {
				return;
			}
		}
		if (isDouble) {
			if (primitive.getMin() != null) {
				if (primitive.getMin().doubleValue() > value.doubleValue()) {
					throw new IllegalArgumentException(primitive.getTipMsg());
				}
			}
			if (primitive.getMax() != null) {
				if (primitive.getMax().doubleValue() < value.doubleValue()) {
					throw new IllegalArgumentException(primitive.getTipMsg());
				}
			}
		} else {
			if (primitive.getMin() != null) {
				if (primitive.getMin().longValue() > value.longValue()) {
					throw new IllegalArgumentException(primitive.getTipMsg());
				}
			}
			if (primitive.getMax() != null) {
				if (primitive.getMax().longValue() < value.longValue()) {
					throw new IllegalArgumentException(primitive.getTipMsg());
				}
			}
		}
	}

}
