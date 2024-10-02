package org.unibl.etf.osnovasredstvaapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import org.unibl.etf.osnovasredstvaapp.entity.PopisnaLista;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;

import java.util.List;

@Dao
public interface PopisnaListaDao {
    @Insert
    long insert(PopisnaLista popisnaLista);

    @Update
    void update(PopisnaLista popisnaLista);

    @Delete
    void delete(PopisnaLista popisnaLista);

    @Query("SELECT * FROM popisna_lista WHERE id = :id")
    PopisnaLista getById(int id);

    @Query("SELECT * FROM popisna_lista")
    List<PopisnaLista> getAll();

    @Query("SELECT * FROM popisna_lista WHERE naziv LIKE '%' || :naziv || '%'")
    List<PopisnaLista> filterByName(String naziv);

    @Query("SELECT * FROM popisna_lista WHERE datumKreiranja = :date")
    List<PopisnaLista> filterByDate(String date);

    @Query("SELECT * FROM popisna_lista WHERE naziv LIKE '%' || :name || '%' AND datumKreiranja = :date")
    List<PopisnaLista> filterByNameAndDate(String name, String date);


}
