package org.unibl.etf.osnovasredstvaapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import org.unibl.etf.osnovasredstvaapp.entity.PopisnaStavka;

import java.util.List;

@Dao
public interface PopisnaStavkaDao {
    @Insert
    void insert(PopisnaStavka popisnaStavka);

    @Update
    void update(PopisnaStavka popisnaStavka);

    @Delete
    void delete(PopisnaStavka popisnaStavka);

    @Query("SELECT * FROM popisna_stavka WHERE id = :id")
    PopisnaStavka getById(int id);

    @Query("SELECT * FROM popisna_stavka")
    List<PopisnaStavka> getAll();

    @Query("SELECT * FROM popisna_stavka WHERE popisnaListaId = :listaId")
    List<PopisnaStavka> getStavkeListe(int listaId);

    @Query("SELECT * FROM popisna_stavka WHERE popisnaListaId = :popisnaListaId")
    List<PopisnaStavka> getByPopisnaListaId(int popisnaListaId);

}
