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

    @Query("SELECT * FROM osnovna_sredstva WHERE zaduzenaLokacijaId = :lokacijaId")
    List<OsnovnoSredstvo> getOsnovnaSredstvaByLokacijaId(int lokacijaId);


    // Filtriranje po imenu
    @Query("SELECT * FROM osnovna_sredstva WHERE naziv LIKE '%' || :name || '%'")
    List<OsnovnoSredstvo> filterByName(String name);

    // Filtriranje po lokaciji i imenu
    @Query("SELECT * FROM osnovna_sredstva WHERE zaduzenaLokacijaId = :lokacijaId AND naziv LIKE '%' || :name || '%'")
    List<OsnovnoSredstvo> filterByLocationAndName(int lokacijaId, String name);
}
