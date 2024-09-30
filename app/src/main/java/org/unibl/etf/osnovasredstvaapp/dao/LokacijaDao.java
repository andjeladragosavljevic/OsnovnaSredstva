package org.unibl.etf.osnovasredstvaapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import org.unibl.etf.osnovasredstvaapp.entity.Lokacija;

import java.util.List;

@Dao
public interface LokacijaDao {
    @Insert
    void insert(Lokacija lokacija);

    @Update
    void update(Lokacija lokacija);

    @Delete
    void delete(Lokacija lokacija);

    @Query("SELECT * FROM lokacija WHERE id = :id")
    Lokacija getById(int id);

    @Query("SELECT * FROM lokacija")
    List<Lokacija> getAll();
}
