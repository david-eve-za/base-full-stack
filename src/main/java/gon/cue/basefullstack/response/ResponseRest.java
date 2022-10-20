package gon.cue.basefullstack.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseRest<T> {
    private List<Map<String,String>> metadata= new ArrayList<>();
    private List<T> data = new ArrayList<>();

    public List<Map<String, String>> getMetadata() {
        return metadata;
    }

    public void setMetadata(String type, String code,String message) {
        Map<String,String> map = new HashMap<>();

        map.put("type",type);
        map.put("code",code);
        map.put("message",message);

        this.metadata.add(map);
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
