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


    @Query("SELECT * FROM osnovna_sredstva WHERE naziv LIKE '%' || :naziv || '%'")
    List<OsnovnoSredstvo> filterByName(String naziv);

    @Query("SELECT * FROM osnovna_sredstva WHERE barkod LIKE '%' || :barkod || '%'")
    List<OsnovnoSredstvo> filterByBarkod(String barkod);

    @Query("SELECT * FROM osnovna_sredstva WHERE naziv LIKE '%' || :naziv || '%' AND barkod LIKE '%' || :barkod || '%'")
    List<OsnovnoSredstvo> filterByNazivAndBarkod(String naziv, String barkod);

}
