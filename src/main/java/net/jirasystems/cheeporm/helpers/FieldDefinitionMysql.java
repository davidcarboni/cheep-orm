// package net.jirasystems.cheeporm.helpers;
//
// import java.lang.reflect.Field;
//
// import javax.persistence.Column;
//
// import net.jirasystems.cheeporm.Reflection;
//
// import org.apache.commons.lang.StringUtils;
//
// public class FieldDefinitionMysql extends FieldDefinition {
//
// private Reflection reflection = new Reflection();
//
// private Field field;
//
// private String name;
// private String definition;
// private int length;
// private boolean nullable;
// private int precision;
// private int scale;
//
// public FieldDefinitionMysql(Field field, boolean isKey, Object... instance) {
// super(field, isKey, instance);
// }
//
// /**
// * Determines the SQL type of the column.
// *
// * @return An SQL type for the given field.
// */
// @Override
// protected void setSqlType() {
//
// // Set the default type:
// super.setSqlType();
//
// // This special case prevents MySQL from creating a TIMESTAMP column, which will auto-update on
// every update of a row:
// if (StringUtils.equals(getSqlTypeName(), "TIMESTAMP")) {
// setSqlTypeName("DATETIME");
// }
// }
//
// /**
// * Updates the name of the field if the {@link Column} annotation does not specify an explicit
// * name by converting the field name to a database identifier using
// * {@link Reflection#camelCaseToDatabase(String)}.
// *
// */
// private void updateColumnName() {
//
// if (StringUtils.isBlank(name)) {
// name = reflection.camelCaseToDatabase(field.getName());
// }
// }
//}
