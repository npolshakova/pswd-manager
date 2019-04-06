package datastruct;

public interface Storage {

    String insert(int id, String credential);

    String update(int id, String credential);

    String delete(int id);

    String search(int id);



}
