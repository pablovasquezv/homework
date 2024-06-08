package com.everis.homework.helper;

import com.everis.homework.mapper.ServiceFields;
import com.everis.homework.mapper.StaffFields;
import com.everis.homework.mapper.TechnicalFields;
import com.everis.homework.mapper.TraditionalFields;
import com.github.dozermapper.core.CustomConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HistoryToVisual implements CustomConverter {
	private Gson gsonBuilder;

	public HistoryToVisual() {
		gsonBuilder = new GsonBuilder().create();
	}

	@Override
	public Object convert(Object dest, Object source, Class<?> arg2, Class<?> arg3) {
		if (source == null)
			return null;

		if(arg2==TraditionalFields.class) {
			return gsonBuilder.fromJson((String) source, TraditionalFields.class);
		}
		else if(arg2==ServiceFields.class) {
			return gsonBuilder.fromJson((String) source, ServiceFields.class);
		}
		else if(arg2==StaffFields.class) {
			return gsonBuilder.fromJson((String) source, StaffFields.class);
		}
		else if(arg2==TechnicalFields.class) {
			return gsonBuilder.fromJson((String) source, TechnicalFields.class);
		}
		else if(arg2==String.class) {
			return gsonBuilder.toJson(source);
		}
		else
			return null;
	}
}
