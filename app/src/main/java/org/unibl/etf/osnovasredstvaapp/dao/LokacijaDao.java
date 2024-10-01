package org.unibl.etf.osnovasredstvaapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import org.unibl.etf.osnovasredstvaapp.entity.Lokacija;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;

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

    @Query("SELECT * FROM lokacija WHERE grad LIKE '%' || :grad || '%'")
    List<Lokacija> filterByGrad(String grad);

    @Query("SELECT * FROM lokacija WHERE adresa LIKE '%' || :adresa || '%'")
    List<Lokacija> filterByAdresa(String adresa);

    @Query("SELECT * FROM lokacija WHERE grad LIKE '%' || :grad || '%' AND adresa LIKE '%' || :adresa || '%'")
    List<Lokacija> filterByGradAndAdresa(String grad, String adresa);

}
