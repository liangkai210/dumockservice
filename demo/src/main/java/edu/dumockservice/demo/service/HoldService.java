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
@RequestMapping("/hold")
public class HoldService{

    @Autowired
    BaseService baseService;

    @RequestMapping(value = "/getAllHolds", method = RequestMethod.GET, produces = "application/json")
    public List<Object> getAllHolds(){
        DBIterator iterator = DBs.HOLDS.getInstance().iterator();
        return baseService.getList(iterator);
    }

    @RequestMapping(value = "/getHold/{holdId}", method = RequestMethod.GET, produces = "application/json")
    public Object getHold(@PathVariable String holdId){
        String target = asString(DBs.HOLDS.getInstance().get(bytes(holdId)));
        return baseService.get(target);
    }

    @RequestMapping(value = "/addHoldToDB", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String addHolds(@RequestBody String str){
        try {
            if(str.startsWith("[")) {
                JSONArray jsonArray = (JSONArray) (new JSONParser().parse(str));
                for (Object o : jsonArray) {
                    String tempId = Utils.getUUId().toString();
                    JSONObject jsonObject = (JSONObject) o;
                    jsonObject.put("tempId", tempId);
                    DBs.HOLDS.getInstance().put(bytes(tempId), bytes(jsonObject.toString()));
                }
            }else if(str.startsWith("{")){
                String tempId = Utils.getUUId().toString();
                JSONObject jsonObject = (JSONObject) (new JSONParser().parse(str));
                jsonObject.put("tempId", tempId);
                DBs.HOLDS.getInstance().put(bytes(tempId), bytes(jsonObject.toString()));
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @RequestMapping(value = "/updateHold",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateHold(@RequestBody JSONObject obj){
        try {
            String value = obj.toString();
            JSONObject jsonObject = baseService.update(value);
            String tempId = jsonObject.get("tempId").toString();
            DBs.HOLDS.getInstance().put(bytes(tempId), bytes(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/deleteHold/{holdId}",method = RequestMethod.DELETE)
    public void deleteHold(@PathVariable String holdId){
        try {
            DBs.HOLDS.getInstance().delete(bytes(holdId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
