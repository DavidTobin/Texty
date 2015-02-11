package tobin.texty.collections;

import java.util.Collection;
import java.util.HashMap;

public interface CollectionInterface {
    public Collection<HashMap<String, String>> getAll();
    public Collection<HashMap<String, String>> getAll(String order);

    public HashMap<String, String> getOne(int id);

    public Collection<HashMap<String, String>> getWhere(String condition);
    public Collection<HashMap<String, String>> getWhere(String condition, String order);

    public void groupBy(String columnName);
}
