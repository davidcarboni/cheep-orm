package net.jirasystems.cheeporm.test;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Generically generates values for primitives, primitive wrappers, String and enum types, suitable
 * for use in reflection calls.
 * 
 * @author David Carboni
 * 
 */
public final class ValueGenerator {

	/**
	 * No need to instantiate this class.
	 */
	private ValueGenerator() {
		// No need to instantiate.
	}

	/**
	 * 
	 * @param type
	 *            The type of the example value to generate.
	 * @param ordinal
	 *            A number, >=0, used to generate different example values.
	 * @return A suitable instance containing the example value.
	 * @throws Exception .
	 */
	public static Object valueExample(Class<?> type, int ordinal) throws Exception {

		// NB for some reason, Checkstyle has issues with e.g.:
		// if ((Double.class.isAssignableFrom(type)) || (double.class.isAssignableFrom(type))) {
		//
		// Therefore these have been split up, because Checkstyle marks it as an error, so it makes
		// it difficult to see real errors in the project.

		if (int.class.isAssignableFrom(type)) {
			return Integer.valueOf(ordinal - 1);

		} else if (Integer.class.isAssignableFrom(type)) {
			return Integer.valueOf(ordinal - 1);

		} else if (Long.class.isAssignableFrom(type)) {
			return Long.valueOf(ordinal - 1);

		} else if (long.class.isAssignableFrom(type)) {
			return Long.valueOf((short) (ordinal - 1));

		} else if (Short.class.isAssignableFrom(type)) {
			return Short.valueOf((short) (ordinal - 1));

		} else if (short.class.isAssignableFrom(type)) {
			return Short.valueOf((short) (ordinal - 1));

		} else if (Byte.class.isAssignableFrom(type)) {
			return Byte.valueOf((byte) (ordinal - 1));

		} else if (byte.class.isAssignableFrom(type)) {
			return Byte.valueOf((byte) (ordinal - 1));

		} else if (Double.class.isAssignableFrom(type)) {
			return Double.valueOf((byte) (ordinal - 1));

		} else if (double.class.isAssignableFrom(type)) {
			return Double.valueOf((byte) (ordinal - 1));

		} else if (Float.class.isAssignableFrom(type)) {
			return Float.valueOf((byte) (ordinal - 1));

		} else if (float.class.isAssignableFrom(type)) {
			return Float.valueOf((byte) (ordinal - 1));

		} else if (Boolean.class.isAssignableFrom(type)) {
			return Boolean.valueOf(ordinal % 2 == 0);

		} else if (boolean.class.isAssignableFrom(type)) {
			return Boolean.valueOf(ordinal % 2 == 0);

		} else if (Character.class.isAssignableFrom(type)) {
			return Character.valueOf((char) ordinal);

		} else if (char.class.isAssignableFrom(type)) {
			return Character.valueOf((char) ordinal);

		} else if (String.class.isAssignableFrom(type)) {
			return String.valueOf(ordinal);

		} else if (Timestamp.class.isAssignableFrom(type)) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, ordinal);
			calendar.add(Calendar.HOUR, -ordinal);
			calendar.add(Calendar.MINUTE, ordinal * 2);
			calendar.add(Calendar.SECOND, -ordinal * 2);
			Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
			return timestamp;

		} else if (Enum.class.isAssignableFrom(type)) {

			// Get the value list from the specific enum type
			@SuppressWarnings("unchecked")
			Enum<? extends Enum<?>>[] values = (Enum<? extends Enum<?>>[]) type.getMethod("values").invoke(type);

			int index = ordinal % values.length;
			return values[index];
		}

		// Not a recognised type
		throw new RuntimeException("Type " + type.getName() + " not supported.");
	}
}
