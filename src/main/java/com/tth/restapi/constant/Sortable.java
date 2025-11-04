package com.tth.restapi.constant;

import java.util.Arrays;
import java.util.List;

public class Sortable {

	public static final String CREATED_AT = "createdAt";
	public static final String UPDATED_AT = "updatedAt";
	public static final String ID = "id";
	public static final String STATUS = "status";
	public static final String NAME = "name";

	public static final List<String> USER = Arrays.asList(
			CREATED_AT, UPDATED_AT, ID, STATUS, NAME);

}
