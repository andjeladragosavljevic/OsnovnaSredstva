package org.unibl.etf.osnovasredstvaapp.ui.zaposleni;

import android.os.AsyncTask;

import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.OsnovnoSredstvoFragment;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.OsnovnoSredstvoTask;

import java.lang.ref.WeakReference;
import java.util.List;

public class ZaposleniTask extends AsyncTask<Void, Void, List<Zaposleni>> {
    public enum OperationType {
        INSERT, UPDATE, DELETE, READ
    }

    private WeakReference<ZaposleniFragment> fragmentReference;
    private ZaposleniDao zaposleniDao;
    private OperationType operationType;
    private Zaposleni zaposleni;

    // Konstruktor za INSERT, UPDATE i DELETE operacije
    public ZaposleniTask(ZaposleniFragment fragment, ZaposleniDao dao, ZaposleniTask.OperationType operationType, Zaposleni zaposleni) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.zaposleniDao = dao;
        this.operationType = operationType;
        this.zaposleni = zaposleni;
    }

    // Konstruktor za READ operaciju
    public ZaposleniTask(ZaposleniFragment fragment, ZaposleniDao dao) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.zaposleniDao = dao;
        this.operationType = ZaposleniTask.OperationType.READ;
    }

    @Override
    protected List<Zaposleni> doInBackground(Void... voids) {
        switch (operationType) {
            case INSERT:
                zaposleniDao.insert(zaposleni);
                break;
            case UPDATE:
                zaposleniDao.update(zaposleni);
                break;
            case DELETE:
                zaposleniDao.delete(zaposleni);
                break;
            case READ:
                return zaposleniDao.getAll();
        }
        return null; // Vraća se null osim u slučaju READ operacije
    }

    @Override
    protected void onPostExecute(List<Zaposleni> zaposleni) {
        ZaposleniFragment fragment = fragmentReference.get();
        if (fragment != null && zaposleni != null) {
            fragment.updateList(zaposleni); // Ažuriraj listu nakon dohvatanja podataka
        }
    }

}
