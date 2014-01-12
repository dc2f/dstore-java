package com.dc2f.utils;

import javax.annotation.Nonnull;

public class NullUtils {
	public static @Nonnull <T extends Object> T assertNotNull(T obj) {
		if (obj == null) {
			throw new AssertionError("Must not be null.");
		}
		return obj;
	}
}
