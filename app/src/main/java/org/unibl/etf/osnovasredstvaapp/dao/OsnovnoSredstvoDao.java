package org.unibl.etf.osnovasredstvaapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;

import java.util.List;

@Dao
public interface OsnovnoSredstvoDao {
    @Insert
    void insert(OsnovnoSredstvo osnovnoSredstvo);

    @Update
    void update(OsnovnoSredstvo osnovnoSredstvo);

    @Delete
    void delete(OsnovnoSredstvo osnovnoSredstvo);

    @Query("SELECT * FROM osnovna_sredstva where id = :id")
    OsnovnoSredstvo getById(int id);

    @Query("SELECT * FROM osnovna_sredstva")
    List<OsnovnoSredstvo> getAll();

}
