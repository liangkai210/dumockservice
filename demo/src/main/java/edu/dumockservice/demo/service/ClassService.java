package edu.dumockservice.demo.service;

import edu.dumockservice.demo.util.DBs;
import edu.dumockservice.demo.util.Utils;
import org.iq80.leveldb.DBIterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;

/**
 * Created by liang on 2017/6/4.
 */
@RestController
@RequestMapping("/class")
public class ClassService{

    @Autowired
    BaseService baseService;

    @RequestMapping(value = "/getAllClasses", method = RequestMethod.GET, produces = "application/json")
    public List<Object> getAllClasses(){
        DBIterator iterator = DBs.ClASSES.getInstance().iterator();
        return baseService.getList(iterator);
    }

    @RequestMapping(value = "/getClass/{classId}", method = RequestMethod.GET, produces = "application/json")
    public Object getClass(@PathVariable String classId){
        String target = asString(DBs.ClASSES.getInstance().get(bytes(classId)));
        return baseService.get(target);
    }

    @RequestMapping(value = "/addClassesToDB", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String addClassesToDB(@RequestBody String str){
        try {
            if(str.startsWith("[")) {
                JSONArray jsonArray = (JSONArray) (new JSONParser().parse(str));
                for (Object o : jsonArray) {
                    String tempId = Utils.getUUId().toString();
                    JSONObject jsonObject = (JSONObject) o;
                    jsonObject.put("tempId", tempId);
                    DBs.ClASSES.getInstance().put(bytes(tempId), bytes(jsonObject.toString()));
                }
            }else if(str.startsWith("{")){
                String tempId = Utils.getUUId().toString();
                JSONObject jsonObject = (JSONObject) (new JSONParser().parse(str));
                jsonObject.put("tempId", tempId);
                DBs.ClASSES.getInstance().put(bytes(tempId), bytes(jsonObject.toString()));
            }else{
                return "format error";
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @RequestMapping(value = "/updateClass",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateClass(@RequestBody JSONObject obj){
        try {
            String value = obj.toString();
            JSONObject jsonObject = baseService.update(value);
            String tempId = jsonObject.get("tempId").toString();
            DBs.ClASSES.getInstance().put(bytes(tempId), bytes(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/deleteClass/{classId}",method = RequestMethod.DELETE)
    public void deleteClass(@PathVariable String classId){
        try {
            DBs.ClASSES.getInstance().delete(bytes(classId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
