package realm.mine.com.realmdatabase;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by MyPC on 01/08/2017.
 */

public class Task extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
    public Task(){}
    public Task(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public String toString(){
        return this.name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
