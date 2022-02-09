package com.atypon.nosql.data;

import com.atypon.nosql.cache.LFUCache;
import com.atypon.nosql.record.AbstractRecord;
import com.atypon.nosql.record.Attribute;
import com.atypon.nosql.record.NullRecord;
import com.atypon.nosql.record.Record;
import com.atypon.nosql.schema.Schema;
import com.atypon.nosql.utils.FileIO;
import com.atypon.nosql.utils.Hash;
import org.json.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class SchemaDAO implements DAO {
    private ConcurrentHashMap<String, Record> data;
    private LFUCache cache = LFUCache.getInstance();
    private String schemaName;
    private Schema schema;

    private SchemaDAO(String schemaName) throws IOException, ParseException {
        FileIO.createFile(schemaName);
        this.schemaName = schemaName;
        this.schema = Schema.getInstance(schemaName);
        this.data = parseFile();
    }

    public static DAO getInstance(String schemaName) throws IOException, ParseException {
        return new SchemaDAO(schemaName);
    }

    private ConcurrentHashMap parseFile() {
        ConcurrentHashMap<String, Record> data = new ConcurrentHashMap<>();
        try {
            ArrayList<Record> records = FileIO.fileToRecords(schemaName);
            System.out.println(records.size());
            if (records != null) {
                for (AbstractRecord record : records) {
                    if (!record.isNull()) {
                        Record record1 = (Record) record;
                        data.put(record1.getRecordID(), record1);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public Object getAll() {
        String hash = Hash.hashAll(schemaName);
        Object cacheResult = cache.get(hash);
        if (cacheResult == null) {
            JSONArray jsonArray = new JSONArray();
            for (Object record : data.values()) {
                Record tempRecord = (Record) record;
                jsonArray.put(tempRecord.toJson());
            }
            cache.put(hash, jsonArray);
            return jsonArray;

        } else return cacheResult;
    }

    @Override
    public Boolean add(Record record) throws IOException {
        if (!schema.isValidRecord(record)) {
            return false;
        }
        String hash = Hash.hashRecord(schemaName, record.getRecordID());
        data.put(record.getRecordID(), record);
        cache.put(hash, record);
        cache.remove(Hash.hashAll(schemaName));
        FileIO.writeToJson(schemaName, data.values()); // synchronized
        return true;
    }

    @Override
    public Object getByAttribute(Attribute attribute) {
        String hash = Hash.hashRecord(schemaName, attribute.getKey());
        Object cacheResult = cache.get(hash);
        if (cacheResult == null) {
            ArrayList<Record> result = new ArrayList<>();
            for (Record record : data.values()) {
                for (Attribute currentAttribute : record.getAttributes()) {
                    if (attribute.equals(currentAttribute)) {
                        result.add(record);
                    }
                }
            }
            if (result.size() == 0) {
                return NullRecord.getInstance();
            }
            if (result.size() > 0) cache.put(hash, result);
            return result;
        } else return cacheResult;
    }

    @Override
    public AbstractRecord update(Record record) throws IOException {
        if (record == null) throw new NullPointerException("record cannot be null");
        Record oldRecord = data.get(record.getRecordID());
        synchronized (this) {
            for (Attribute attribute : record.getAttributes()) {
                for (Attribute oldAttribute : oldRecord.getAttributes()) {
                    if (attribute.getKey().equals(oldAttribute.getKey())) {
                        oldAttribute.setValue(attribute.getValue());
                    }
                }
            }
            add(oldRecord);
            return oldRecord;
        }
    }

    @Override
    public AbstractRecord delete(String recordID) throws IOException {
        if (recordID == null) throw new NullPointerException("ID cannot be null");
        if (data.get(recordID) == null) return NullRecord.getInstance();
        String hash = Hash.hashRecord(schemaName, recordID);
        String schemaHash = Hash.hashAll(schemaName);
        Record record = data.get(recordID);
        data.remove(recordID);
        cache.remove(hash);
        cache.remove(schemaHash);
        FileIO.writeToJson(schemaName, data.values());
        return record;
    }
}
