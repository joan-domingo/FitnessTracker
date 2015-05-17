package cat.xojan.fittracker.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Joan on 17/05/2015.
 */
public class Serializer {
    public static byte[] serialize(Object obj)  {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = null;
        try {
            o = new ObjectOutputStream(b);
            o.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return b.toByteArray();
    }

    public static Object deserialize(byte[] bytes) {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = null;
        try {
            o = new ObjectInputStream(b);
            return o.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
