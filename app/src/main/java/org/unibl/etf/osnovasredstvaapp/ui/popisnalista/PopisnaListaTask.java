package org.unibl.etf.osnovasredstvaapp.ui.popisnalista;

import android.os.AsyncTask;
import android.util.Log;

import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.dao.PopisnaListaDao;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaLista;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.OsnovnoSredstvoFragment;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.OsnovnoSredstvoTask;

import java.lang.ref.WeakReference;
import java.util.List;

public class PopisnaListaTask  extends AsyncTask<Void, Void, List<PopisnaLista>> {

    private String nazivFilter;
    private String datumFilter;

    public enum OperationType {
        INSERT, UPDATE, DELETE, READ,  FILTER
    }

    private WeakReference<PopisnaListaFragment> fragmentReference;
    private PopisnaListaDao popisnaListaDao;
    private PopisnaListaTask.OperationType operationType;
    private PopisnaLista popisnaLista;

    // Konstruktor za filtriranje
    public PopisnaListaTask(PopisnaListaFragment fragment, PopisnaListaDao dao, String nazivFilter, String datumFilter) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.popisnaListaDao = dao;
        this.nazivFilter = nazivFilter;
        this.datumFilter = datumFilter;
        this.operationType = PopisnaListaTask.OperationType.FILTER;
    }


    // Konstruktor za INSERT, UPDATE i DELETE operacije
    public PopisnaListaTask(PopisnaListaFragment fragment, PopisnaListaDao dao, PopisnaListaTask.OperationType operationType, PopisnaLista popisnaLista) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.popisnaListaDao = dao;
        this.operationType = operationType;
        this.popisnaLista = popisnaLista;
    }

    // Konstruktor za READ operaciju
    public PopisnaListaTask(PopisnaListaFragment fragment, PopisnaListaDao dao) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.popisnaListaDao = dao;
        this.operationType = PopisnaListaTask.OperationType.READ;
    }

    @Override
    protected List<PopisnaLista> doInBackground(Void... voids) {

        switch (operationType) {
            case FILTER:
                if (!nazivFilter.isEmpty() && !datumFilter.isEmpty()) {
                    return popisnaListaDao.filterByNameAndDate(nazivFilter, datumFilter);
                } else if (!nazivFilter.isEmpty()) {
                    return popisnaListaDao.filterByName(nazivFilter);
                } else if (!datumFilter.isEmpty()) {
                    return popisnaListaDao.filterByDate(datumFilter);
                } else {
                    return popisnaListaDao.getAll();
                }
            case INSERT: {
                    popisnaListaDao.insert(popisnaLista);
            }
            case UPDATE:
                popisnaListaDao.update(popisnaLista);
                break;
            case DELETE:
                popisnaListaDao.delete(popisnaLista);
                break;
            case READ:
                return popisnaListaDao.getAll();
        }
        return null; // Vraća se null osim u slučaju READ operacije
    }

    @Override
    protected void onPostExecute(List<PopisnaLista> popisneListe) {

        PopisnaListaFragment fragment = fragmentReference.get();
        if (fragment != null && popisneListe != null) {
            fragment.updateList(popisneListe); // Ažuriraj listu nakon dohvatanja podataka
        }
    }
}
