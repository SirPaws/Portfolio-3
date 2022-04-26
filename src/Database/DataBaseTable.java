package Database;

import Database.Annotations.PrimaryKey;
import Database.Annotations.Reference;

import java.io.InvalidClassException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DataBaseTable<T> {
    private final Class<T> class_;

    public DataBaseTable(Class<T> c) {
        class_ = c;
    }

    public boolean isTypeSupported(Class<?> t) {
        // if (t.isArray()) return isTypeSupported(t.arrayType());
        if (t.isArray()) return false;

        if (t == Integer.class) return true;
        if (t == int.class) return true;
        return t == String.class;
    }

    public <ExpectedType>
    boolean is(Class<ExpectedType> c) {
        return c.equals(class_);
    }

    public String createTable() throws InvalidClassException {
        if (class_ == null) return "";

        String header = "create table if not exists ";
        StringBuilder statement = new StringBuilder(header + class_.getSimpleName() + "(\n");
        List<Field> fields = List.of(class_.getDeclaredFields());

        ArrayList<FieldAnnotation> all_annotations = new ArrayList<>();
        for (Field f : fields) {
            List<FieldAnnotation> annotations = getFieldAnnotations(f);
            String field = createField(f, annotations);
            statement.append("    ").append(field);
            if (fields.indexOf(f) != fields.size() - 1)
                statement.append(",\n");

            all_annotations.addAll(annotations);
        }

        StringBuilder db_late  = new StringBuilder();
        int old_length = 0;
        for (FieldAnnotation annotation: all_annotations) {
            annotation.handler.lateAnnotate(db_late, annotation.annotation);
            if (db_late.length() != old_length) {
                db_late.insert(old_length, ",\n    ");
                old_length = db_late.length();
            }
        }

        String late = db_late.toString();
        if (late.length() != 0) {
            statement.append(late);
        }
        statement.append('\n');
        statement.append(')');
        return statement.toString();
    }

    public String dropTable() {
        if (class_ == null) return "";
        return "drop table if exists " + class_.getSimpleName();
    }

    public String insert(T object) {
        return insert(object, null);
    }

    public String insert(T object, Map<Class<?>, Integer> references) {
        if (class_ == null) return "";

        // insert into #ClassName (#ColumNames...) values ( ... values );
        StringBuilder builder = new StringBuilder("insert into ");
        builder.append(class_.getSimpleName());
        builder.append('(');

        List<Field> fields = List.of(class_.getDeclaredFields());

        for (Field f : fields) {
            if (isReference(f))
                 builder.append(getFieldName(f)).append("_key");
            else builder.append(getFieldName(f));
            if (fields.indexOf(f) != fields.size() - 1)
                builder.append(", ");
        }



        builder.append(") values (");
        for (Field f : fields) {
            if (isReference(f)) {
                Integer id = references.get(f.getType());
                assert id != null;

                builder.append(id);
                if (fields.indexOf(f) != fields.size() - 1)
                    builder.append(", ");
                continue;
            }


            try {
                f.setAccessible(true);
                Object value = f.get(object);
                if (value instanceof String s)
                    builder .append('\'')
                            .append(s)
                            .append('\'');
                else if (value == null)
                    builder.append("null");
                else builder.append(value);
            } catch (IllegalAccessException ignored) {}
            if (fields.indexOf(f) != fields.size() - 1)
                builder.append(", ");
        }
        builder.append(")");

        return builder.toString();
    }

    public String getIDString(Class<?> class_) {
        Field f = getID(class_);
        return f == null ? null : getFieldName(class_, f);
    }

    Object getFieldValue(ResultSet rs, Class<?> field_type, String name) throws SQLException {
        if (field_type == Integer.class || field_type == int.class)
            return rs.getInt(name);
        else if (field_type == String.class)
            return rs.getString(name);
        throw new Error("unsupported type!");
    }

    public ArrayList<T> select(DataBase db) {
        ArrayList<T> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder("select * from ");

        boolean has_reference = false;
        int offset = builder.length();
        List<Field> fields = List.of(class_.getDeclaredFields());
        for (Field f : fields) {
            FieldAnnotation annotation;
            if ((annotation = getReferenceAnnotation(f)) != null) {
                builder.insert(offset, '(');
                if (!has_reference)
                    builder.append(class_.getSimpleName());
                builder.append(queryAppendReference(annotation, f));
                builder.append(')');
                has_reference = true;
            }
        }
        if (!has_reference)
            builder.append(class_.getSimpleName());
        ResultSet rs = db.query(builder.toString());

        try {
            Constructor<T> constructor = class_.getConstructor();
            constructor.setAccessible(true);
            while (rs.next()) {
                boolean is_set = false;
                T value = constructor.newInstance();

                for (Field f : fields) {
                    handleFieldValue(db, rs, value, f);
                    is_set = true;
                }

                if (is_set) list.add(value);
            }
        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        if (list.size() == 0) return null;
        return list;
    }


    public ArrayList<T> selectAllWithID(DataBase db, Integer id) {
        StringBuilder builder = new StringBuilder();
        Field id_field = getID(class_);
        builder.append("select")
                .append("*")
                .append(" from ");


               /*
                .append(class_.getSimpleName())
                .append(" where ")
                .append(getFieldName(id_field))
                .append("=")
                .append(id)
                .append(';');
                */

        int offset = builder.length() - 1;
        List<Field> fields = List.of(class_.getDeclaredFields());
        for (Field f : fields) {
            FieldAnnotation annotation;
            if ((annotation = getReferenceAnnotation(f)) != null) {
                builder.insert(offset, '(');
                builder.append(queryAppendReference(annotation, f));
                builder.append(')');
            }
        }

        ArrayList<T> list = new ArrayList<>();
        ResultSet rs = db.query(builder.toString());
        try {
            while (rs.next()) {
                Constructor<T> constructor = class_.getConstructor();
                constructor.setAccessible(true);
                T value = constructor.newInstance();

                for (Field f : fields) {
                    handleFieldValue(db, rs, value, f);
                }

                list.add(value);
            }
        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public T selectWithID(DataBase db, Integer id) {
        StringBuilder builder = new StringBuilder();
        Field id_field = getID(class_);
        builder.append("select")
                .append("*")
                .append(" from ")
                .append(class_.getSimpleName())
                .append(" where ")
                .append(getFieldName(id_field))
                .append("=")
                .append(id)
                .append(';');

        ResultSet rs = db.query(builder.toString());
        try {
            Constructor<T> constructor = class_.getConstructor();
            constructor.setAccessible(true);
            T value = constructor.newInstance();

            List<Field> fields = List.of(class_.getDeclaredFields());
            for (Field f : fields) {
                handleFieldValue(db, rs, value, f);
            }

            return value;
        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    String queryAppendReference(FieldAnnotation reference, Field f) {
        if (!(reference.annotation instanceof Reference ref)) throw new Error("wrong value passed into function");
        Class<?> ref_class = ref.value();
        StringBuilder builder = new StringBuilder(" inner join ");
        builder.append(ref_class.getSimpleName());
        builder.append(" on ");
        builder.append(class_.getSimpleName());
        builder.append('.');
        builder.append(getFieldName(f));
        builder.append("_key == ");
        builder.append(ref_class.getSimpleName());
        builder.append('.');
        builder.append(getIDString(ref.value()));
        return builder.toString();
    }

    private void handleFieldValue(DataBase db, ResultSet rs, Object value, Field f) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        handleFieldValue(db, rs, value, f, class_);
    }
    private void handleFieldValue(DataBase db, ResultSet rs, Object value, Field f, Class<?> class_) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        FieldAnnotation annotation = null;
        if ((annotation = getReferenceAnnotation(f)) != null) {
            Reference ref = (Reference) annotation.annotation;
            Class<?> ref_class = ref.value();

            Constructor<?> constructor = ref_class.getConstructor();
            constructor.setAccessible(true);

            Object o = constructor.newInstance();
            for (Field ref_field : ref_class.getDeclaredFields())
                handleFieldValue(db, rs, o, ref_field, ref_class);

            f.setAccessible(true);
            f.set(value, o);
            return;
        }
        /*
        else if (isArray(f)) {
            Integer key = rs.getInt(getFieldName(f) + "_key");

            ArrayList<Object> o = db.selectAllWithID(f.getArrayCollectionType(), key);
            fieldAppend(f, o);
            f.setAccessible(true);
            f.set(value, o);
            return;
        }
         */
        Object o = getFieldValue(rs, f.getType(), getFieldName(class_, f));
        f.setAccessible(true);
        f.set(value, o);
    }

    Field getID(Class<?> class_) {
        List<Field> fields = List.of(class_.getDeclaredFields());

        for (Field f : fields) {
            if (!isID(f)) continue;

            return f;
        }
        return null;
    }

    String javaTypeToSQLType(Class<?> type) throws InvalidClassException {
        if (type.isArray()) throw new InvalidClassException("arrays are not supported yet");

        if (type == Integer.class) return "integer";
        if (type == int.class) return "int";
        if (type == String.class) return "text";
        throw new InvalidClassException("");
    }

    boolean typeHandled(List<FieldAnnotation> list) {
        for (FieldAnnotation annotation: list)
            if(annotation.handler.isTypeHandled())
                return true;
        return false;
    }

    FieldAnnotation getReferenceAnnotation(Field f) {
        List<FieldAnnotation> annotations = getFieldAnnotations(f);
        for (FieldAnnotation annotation: annotations) {
            if (annotation.annotation instanceof Reference)
                return annotation;
        }
        return null;
    }
    boolean isReference(Field f) {
        List<FieldAnnotation> annotations = getFieldAnnotations(f);
        for (FieldAnnotation annotation: annotations) {
            if (annotation.annotation instanceof Reference)
                return true;
        }
        return false;
    }

    boolean isID(Field f) {
        List<FieldAnnotation> annotations = getFieldAnnotations(f);
        for (FieldAnnotation annotation: annotations) {
            if (annotation.annotation instanceof PrimaryKey)
                return true;
        }
        return false;
    }

    List<FieldAnnotation> getFieldAnnotations(Field f) {
        Annotation[] field_annotations = f.getDeclaredAnnotations();
        ArrayList<FieldAnnotation> unsorted_annotations = new ArrayList<>();

        for (Annotation field_annotation : field_annotations) {
            FieldAnnotation annotation = AnnotationRegistry.getKey(field_annotation.annotationType());
            unsorted_annotations.add(new FieldAnnotation(annotation, field_annotation));
        }

        return unsorted_annotations.stream().sorted().toList();
    }

    String getFieldName(Field f) {
        return getFieldName(class_, f);
    }
    String getFieldName(Class<?> class_, Field f) {
        final String class_name = class_.getName();

        String str = f.toString();
        int begin = str.indexOf(class_name);
        begin += class_name.length() + 1;
        String name = str.substring(begin);

        return (class_name + '_' + name).toLowerCase();
    }

    String createField(Field f, List<FieldAnnotation> annotations) throws InvalidClassException {
        String field_name = getFieldName(f);
        boolean type_handled = typeHandled(annotations);

        StringBuilder db_field = new StringBuilder(field_name);

        if (!type_handled) {
            Class<?> type = f.getType();
            if (!isTypeSupported(type))
                throw new InvalidClassException("");

            db_field.append(" ").append(javaTypeToSQLType(type));
        }

        for (FieldAnnotation annotation: annotations) {
            db_field.append(' ');
            annotation.handler.annotate(db_field, annotation.annotation);
        }

        return db_field.toString();
    }
}
