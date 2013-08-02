package net.jirasystems.cheeporm;

import java.lang.reflect.Field;

import javax.persistence.Column;

public class ColumnAnnotationDefaults {

	@SuppressWarnings("unused")
	@Column
	private String field;

	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		Field field = ColumnAnnotationDefaults.class.getDeclaredField("field");
		Column column = field.getAnnotation(Column.class);
		System.out.println("columnDefinition: " + column.columnDefinition());
		System.out.println("insertable: " + column.insertable());
		System.out.println("length: " + column.length());
		System.out.println("name: " + column.name());
		System.out.println("nullable: " + column.nullable());
		System.out.println("precision: " + column.precision());
		System.out.println("scale: " + column.scale());
		System.out.println("table: " + column.table());
		System.out.println("unique: " + column.unique());
		System.out.println("updatable: " + column.updatable());
	}
}
