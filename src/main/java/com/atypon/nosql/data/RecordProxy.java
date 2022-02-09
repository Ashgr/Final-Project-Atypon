package com.atypon.nosql.data;

import com.atypon.nosql.record.Attribute;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RecordProxy {
    public static String validateUUID(List<Attribute> attributeList) {
        for (Attribute attribute : attributeList) {
            if (attribute.getKey().equals("_id")) return (String) attribute.getValue();
        }
        return UUID.randomUUID().toString();
    }

    public static String validateTime(List<Attribute> attributeList) {
        for (Attribute attribute : attributeList) {
            if (attribute.getKey().equals("createdAt")) return (String) attribute.getValue();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
