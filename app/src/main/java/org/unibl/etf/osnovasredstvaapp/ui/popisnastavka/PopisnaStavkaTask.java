package org.unibl.etf.osnovasredstvaapp.ui.popisnastavka;

import android.os.AsyncTask;

import org.unibl.etf.osnovasredstvaapp.dao.PopisnaStavkaDao;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaStavka;


import java.lang.ref.WeakReference;
import java.util.List;

public class PopisnaStavkaTask extends AsyncTask<Void, Void, List<PopisnaStavka>> {

    public enum OperationType {
        INSERT, UPDATE, DELETE, READ
    }

    private WeakReference<PopisnaStavkaFragment> fragmentReference;
    private PopisnaStavkaDao popisnaStavkaDao;
    private PopisnaStavkaTask.OperationType operationType;
    private PopisnaStavka popisnaStavka;

    // Konstruktor za INSERT, UPDATE i DELETE operacije
    public PopisnaStavkaTask(PopisnaStavkaFragment fragment, PopisnaStavkaDao dao, PopisnaStavkaTask.OperationType operationType, PopisnaStavka popisnaStavka) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.popisnaStavkaDao = dao;
        this.operationType = operationType;
        this.popisnaStavka = popisnaStavka;
    }

    // Konstruktor za READ operaciju
    public PopisnaStavkaTask(PopisnaStavkaFragment fragment, PopisnaStavkaDao dao) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.popisnaStavkaDao = dao;
        this.operationType = PopisnaStavkaTask.OperationType.READ;
    }



    @Override
    protected List<PopisnaStavka> doInBackground(Void... voids) {
        switch (operationType) {
            case INSERT:
                popisnaStavkaDao.insert(popisnaStavka);
            case UPDATE:
                popisnaStavkaDao.update(popisnaStavka);
                break;
            case DELETE:
                popisnaStavkaDao.delete(popisnaStavka);
                break;
            case READ:
                return popisnaStavkaDao.getAll();
        }
        return null; // Vraća se null osim u slučaju READ operacije
    }

    @Override
    protected void onPostExecute(List<PopisnaStavka> popisneStavke) {
        PopisnaStavkaFragment fragment = fragmentReference.get();
        if (fragment != null && popisneStavke != null) {
            fragment.updateList(popisneStavke);
        }
    }

}
