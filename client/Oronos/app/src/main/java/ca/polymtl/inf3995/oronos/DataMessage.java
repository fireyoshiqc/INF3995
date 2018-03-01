package ca.polymtl.inf3995.oronos;

import java.util.List;

/**
 * Created by prst on 2018-03-01.
 */

public class DataMessage {

    private String id;
    private List<Object> data;

    public DataMessage(String id, List<Object> data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public List<Object> getData() {
        return data;
    }

}
