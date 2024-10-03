package org.unibl.etf.osnovasredstvaapp.ui.popisnastavka;

import android.os.AsyncTask;
import android.util.Log;

import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;


import java.lang.ref.WeakReference;

public class OsGetTask extends AsyncTask<Void, Void, OsnovnoSredstvo> {
    private int id;

    private WeakReference<AddPopisnaStavkaFragment> fragmentReference;
    private OsnovnoSredstvoDao osnovnoSredstvoDao;


    // Konstruktor za READ operaciju
    public OsGetTask(AddPopisnaStavkaFragment fragment, OsnovnoSredstvoDao dao, int id) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.osnovnoSredstvoDao = dao;
        this.id = id;
    }

    @Override
    protected OsnovnoSredstvo doInBackground(Void... voids) {
        return osnovnoSredstvoDao.getById(id);
    }

    @Override
    protected void onPostExecute(OsnovnoSredstvo osnovnoSredstvo) {
        AddPopisnaStavkaFragment fragment = fragmentReference.get();
        if (fragment != null && osnovnoSredstvo != null) {
            fragment.updateList(osnovnoSredstvo); // Ažuriraj listu nakon dohvatanja podataka
        }
    }

}
