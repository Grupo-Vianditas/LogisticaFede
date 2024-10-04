package ar.edu.utn.dds.k3003.repositories.auxiliar;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class PersistenceUtils {

    public static EntityManagerFactory createEntityManagerFactory() {
        // https://stackoverflow.com/questions/8836834/read-environment-variables-in-persistence-xml-file
        Map<String, String> env = System.getenv();
        Map<String, Object> configOverrides = new HashMap<>();
        String[] keys = new String[]{
                "javax.persistence.jdbc.url",
                "javax.persistence.jdbc.user",
                "javax.persistence.jdbc.password",
                "javax.persistence.jdbc.driver",
                "hibernate.hbm2ddl.auto",
                "hibernate.connection.pool_size",
                "hibernate.show_sql"
        };

        for (String key : keys) {
            if (env.containsKey(key)) {
                String value = env.get(key);
                configOverrides.put(key, value);
            }
        }

        return Persistence.createEntityManagerFactory("db", configOverrides);
    }

}