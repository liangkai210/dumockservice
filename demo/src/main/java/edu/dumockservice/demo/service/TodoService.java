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
@RequestMapping("/todo")
public class TodoService{

    @Autowired
    BaseService baseService;

    @RequestMapping(value = "/getAllTodos", method = RequestMethod.GET, produces = "application/json")
    public List<Object> getAllTodos(){
        DBIterator iterator = DBs.TODOS.getInstance().iterator();
        return baseService.getList(iterator);
    }

    @RequestMapping(value = "/getTodo/{todoId}", method = RequestMethod.GET, produces = "application/json")
    public Object getTodo(@PathVariable String todoId){
        String target = asString(DBs.TODOS.getInstance().get(bytes(todoId)));
        return baseService.get(target);
    }

    @RequestMapping(value = "/addTodoToDB", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String addTodos(@RequestBody String str){
        try {
            if(str.startsWith("[")) {
                JSONArray jsonArray = (JSONArray) (new JSONParser().parse(str));
                for (Object o : jsonArray) {
                    String tempId = Utils.getUUId().toString();
                    JSONObject jsonObject = (JSONObject) o;
                    jsonObject.put("tempId", tempId);
                    DBs.TODOS.getInstance().put(bytes(tempId), bytes(jsonObject.toString()));
                }
            }else if(str.startsWith("{")){
                String tempId = Utils.getUUId().toString();
                JSONObject jsonObject = (JSONObject) (new JSONParser().parse(str));
                jsonObject.put("tempId", tempId);
                DBs.TODOS.getInstance().put(bytes(tempId), bytes(jsonObject.toString()));
            }else{
                return "format error";
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @RequestMapping(value = "/updateTodo",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateTodo(@RequestBody JSONObject obj){
        try {
            String value = obj.toString();
            JSONObject jsonObject = baseService.update(value);
            String tempId = jsonObject.get("tempId").toString();
            DBs.TODOS.getInstance().put(bytes(tempId), bytes(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/deleteTodo/{todoId}",method = RequestMethod.DELETE)
    public void deleteTodo(@PathVariable String todoId){
        try {
            DBs.TODOS.getInstance().delete(bytes(todoId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
