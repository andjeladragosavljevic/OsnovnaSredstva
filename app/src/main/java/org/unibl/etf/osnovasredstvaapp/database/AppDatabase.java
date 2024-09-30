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


@Database(entities = {OsnovnoSredstvo.class, Zaposleni.class, Lokacija.class, PopisnaStavka.class, PopisnaLista.class}, version = 1)
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
                    //.fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public  void cleanUp(){
        instance = null;
    }


    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private OsnovnoSredstvoDao osnovnoSredstvoDao;
        private ZaposleniDao zaposleniDao;
        private LokacijaDao lokacijaDao;

        private PopulateDbAsyncTask(AppDatabase db) {
            osnovnoSredstvoDao = db.osnovnoSredstvoDao();
            zaposleniDao = db.zaposleniDao();
            lokacijaDao = db.lokacijaDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("test", "test");
            // Dodaj zaposlenog
            Zaposleni zaposleni1 = new Zaposleni();
            zaposleni1.setIme("Marko");
            zaposleni1.setPrezime("Marković");
            zaposleni1.setPozicija("pozicija");
            zaposleniDao.insert(zaposleni1);

            System.out.println("ZAPOSELNI " + zaposleni1.getIme());

            // Dodaj lokaciju
            Lokacija lokacija1 = new Lokacija();
            lokacija1.setAdresa("Kancelarija 1");
            lokacijaDao.insert(lokacija1);

            // Dodaj osnovna sredstva
            OsnovnoSredstvo osnovnoSredstvo1 = new OsnovnoSredstvo();
            osnovnoSredstvo1.setNaziv("Laptop");
            osnovnoSredstvo1.setOpis("Prenosni računar");
            osnovnoSredstvo1.setBarkod(123456);
            osnovnoSredstvo1.setCijena(1500.0);
            osnovnoSredstvo1.setDatumKreiranja("2024-09-28");
            osnovnoSredstvo1.setZaduzenaOsobaId(1);  // ID zaposlenog
            osnovnoSredstvo1.setZaduzenaLokacijaId(1);  // ID lokacije
            osnovnoSredstvo1.setSlika("path/to/image.jpg");

            OsnovnoSredstvo osnovnoSredstvo2 = new OsnovnoSredstvo();
            osnovnoSredstvo2.setNaziv("Printer");
            osnovnoSredstvo2.setOpis("Oprema za štampanje");
            osnovnoSredstvo2.setBarkod(789012);
            osnovnoSredstvo2.setCijena(500.0);
            osnovnoSredstvo2.setDatumKreiranja("2024-09-27");
            osnovnoSredstvo2.setZaduzenaOsobaId(2);  // ID zaposlenog
            osnovnoSredstvo2.setZaduzenaLokacijaId(2);  // ID lokacije
            osnovnoSredstvo2.setSlika("path/to/printer.jpg");


            Log.d("DB_INSERT", "Osnovno sredstvo inserted: " + osnovnoSredstvo1.getNaziv());
            // Dodaj osnovna sredstva
            osnovnoSredstvoDao.insert(osnovnoSredstvo1);
            osnovnoSredstvoDao.insert(osnovnoSredstvo2);
            return null;

        }
    }

}
