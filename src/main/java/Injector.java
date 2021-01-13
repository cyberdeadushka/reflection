import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Properties;


class Injector<T> {
    private Properties prop;

    Injector() throws IOException {
        prop = new Properties();
        prop.load(new FileInputStream(new File("src/main/resources/prop.properties")));
    }

    T inject(T obj) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class dependencyClass;
        Class className = obj.getClass();
        Field[] fields = className.getDeclaredFields();
        for (Field field: fields) {
            Annotation annotation = field.getAnnotation(AutoInjectable.class);
            if (annotation != null) {
                String[] type = ((field.getType()).toString()).split(" ");
                String implementationClassName = prop.getProperty(type[1], null);
                if (implementationClassName != null) {
                    dependencyClass = Class.forName(implementationClassName);
                    field.setAccessible(true);
                    field.set(obj, dependencyClass.newInstance());
                }
            }
        }
        return obj;
    }
}