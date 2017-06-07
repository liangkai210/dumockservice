package edu.dumockservice.demo.util;


import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;
import java.io.File;
import java.io.IOException;

public enum DBs {
    TODOS("todos"),
    HOLDS("holds"),
    ACCOUNTBALANCE("accBalance"),
    ClASSES("classes");

    private String name = null;
    private DB dbInstance = null;

    private DBs(String name){
        String path = Utils.getAbsPath() + "/duDataBase";
        System.out.println(path);
        this.name = name;
        Options options = new Options().createIfMissing(true);
        try {
            dbInstance = factory.open(new File(path,name), options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DB getInstance() {
        return this.dbInstance;
    }
}
