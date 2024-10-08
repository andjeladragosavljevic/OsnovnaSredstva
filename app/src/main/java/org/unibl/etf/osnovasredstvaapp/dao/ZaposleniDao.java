package org.unibl.etf.osnovasredstvaapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import org.unibl.etf.osnovasredstvaapp.entity.Lokacija;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;

import java.util.List;

@Dao
public interface ZaposleniDao {
    @Insert
    void insert(Zaposleni zaposleni);

    @Update
    void update(Zaposleni zaposleni);

    @Delete
    void delete(Zaposleni zaposleni);

    @Query("SELECT * FROM zaposleni WHERE id = :id")
    Zaposleni getById(int id);

    @Query("SELECT * FROM zaposleni")
    List<Zaposleni> getAll();

    @Query("SELECT * FROM zaposleni WHERE ime LIKE '%' || :ime || '%'")
    List<Zaposleni> filterByIme(String ime);

    @Query("SELECT * FROM zaposleni WHERE prezime LIKE '%' || :prezime || '%'")
    List<Zaposleni> filterByPrezime(String prezime);

    @Query("SELECT * FROM zaposleni WHERE ime LIKE '%' || :ime || '%' AND prezime LIKE '%' || :prezime || '%'")
    List<Zaposleni> filterByImeAndPrezime(String ime, String prezime);

}
