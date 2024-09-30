package org.unibl.etf.osnovasredstvaapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import org.unibl.etf.osnovasredstvaapp.entity.PopisnaLista;

import java.util.List;

@Dao
public interface PopisnaListaDao {
    @Insert
    void insert(PopisnaLista popisnaLista);

    @Update
    void update(PopisnaLista popisnaLista);

    @Delete
    void delete(PopisnaLista popisnaLista);

    @Query("SELECT * FROM popisna_lista WHERE id = :id")
    PopisnaLista getById(int id);

    @Query("SELECT * FROM popisna_lista")
    List<PopisnaLista> getAll();

}
