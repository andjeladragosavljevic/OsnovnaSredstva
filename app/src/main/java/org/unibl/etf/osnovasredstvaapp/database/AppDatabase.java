package org.unibl.etf.osnovasredstvaapp.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.unibl.etf.osnovasredstvaapp.dao.LokacijaDao;
import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.dao.PopisnaListaDao;
import org.unibl.etf.osnovasredstvaapp.dao.PopisnaStavkaDao;
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.entity.Lokacija;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaLista;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaStavka;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;


@Database(entities = {OsnovnoSredstvo.class, Zaposleni.class, Lokacija.class, PopisnaStavka.class, PopisnaLista.class}, version = 8)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract OsnovnoSredstvoDao osnovnoSredstvoDao();
    public abstract ZaposleniDao zaposleniDao();
    public abstract LokacijaDao lokacijaDao();
    public abstract PopisnaListaDao popisnaListaDao();
    public abstract PopisnaStavkaDao popisnaStavkaDao();

    public static synchronized AppDatabase getInstance(Context context) {
       Log.d("DATABASE", "Database");
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "osnovna_sredstva_db")
                  //  .addCallback(roomCallback)
                    // Možete koristiti ovaj metod tokom razvoja, ne u produkciji
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public  void cleanUp(){
        instance = null;
    }



}
