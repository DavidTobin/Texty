package tobin.texty.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Base model from which all models will extend from
 */
public class Model {
    public HashMap<String, String> data;

    public Model(HashMap<String, String> data) {
        this.data = data;
    }

    public String getValue(String index) {
        return data.get(index);
    }

    public void putValue(String index, String value) {
        data.put(index, value);
    }

    public HashMap<String, String> getAll() {
        return data;
    }

    public String toShortDate(String index) {
        Date date = new Date();
        date.setTime(Long.parseLong(this.getValue(index)));

        return new SimpleDateFormat("MMM dd hh:mma").format(date);
    }
}
