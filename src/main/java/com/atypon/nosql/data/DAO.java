package com.atypon.nosql.data;

import com.atypon.nosql.record.Attribute;
import com.atypon.nosql.record.Record;

import java.io.IOException;

public interface DAO {
  public Object getAll();

  public Boolean add(Record record) throws IOException;

  public Object getByAttribute(Attribute attribute);

  public void update(Record record) throws IOException;

  public Record delete(String recordID) throws IOException;
}
