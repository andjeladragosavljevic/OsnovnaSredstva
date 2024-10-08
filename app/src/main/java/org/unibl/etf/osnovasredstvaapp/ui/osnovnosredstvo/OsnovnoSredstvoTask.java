package org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo;

import android.os.AsyncTask;


import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class OsnovnoSredstvoTask extends AsyncTask<Void, Void, List<OsnovnoSredstvo>> {
    private int id;
    private String nazivFilter;
    private String barkodFilter;

    public enum OperationType {
        INSERT, UPDATE, DELETE, READ, GETBYLOCATIONID, FILTER, GETBY
    }

    private WeakReference<OsnovnoSredstvoFragment> fragmentReference;
    private OsnovnoSredstvoDao osnovnoSredstvoDao;
    private OperationType operationType;
    private OsnovnoSredstvo osnovnoSredstvo;

    // Konstruktor za filtriranje
    public OsnovnoSredstvoTask(OsnovnoSredstvoFragment fragment, OsnovnoSredstvoDao dao, String nazivFilter, String barkodFilter) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.osnovnoSredstvoDao = dao;
        this.nazivFilter = nazivFilter;
        this.barkodFilter = barkodFilter;
        this.operationType = OperationType.FILTER;
    }

    public OsnovnoSredstvoTask(OsnovnoSredstvoFragment fragment, OsnovnoSredstvoDao osnovnoSredstvoDao, OperationType operationType, int id) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.osnovnoSredstvoDao = osnovnoSredstvoDao;
        this.operationType = operationType;
        this.id = id;
    }


    // Konstruktor za INSERT, UPDATE i DELETE operacije
    public OsnovnoSredstvoTask(OsnovnoSredstvoFragment fragment, OsnovnoSredstvoDao dao, OperationType operationType, OsnovnoSredstvo osnovnoSredstvo) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.osnovnoSredstvoDao = dao;
        this.operationType = operationType;
        this.osnovnoSredstvo = osnovnoSredstvo;
    }

    // Konstruktor za READ operaciju
    public OsnovnoSredstvoTask(OsnovnoSredstvoFragment fragment, OsnovnoSredstvoDao dao) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.osnovnoSredstvoDao = dao;
        this.operationType = OperationType.READ;
    }

    @Override
    protected List<OsnovnoSredstvo> doInBackground(Void... voids) {
        switch (operationType) {
            case FILTER:
                if (!nazivFilter.isEmpty() && !barkodFilter.isEmpty()) {
                    return osnovnoSredstvoDao.filterByNazivAndBarkod(nazivFilter, barkodFilter);
                } else if (!nazivFilter.isEmpty()) {
                    return osnovnoSredstvoDao.filterByName(nazivFilter);
                } else if (!barkodFilter.isEmpty()) {
                    return osnovnoSredstvoDao.filterByBarkod(barkodFilter);
                } else {
                    return osnovnoSredstvoDao.getAll();
                }
            case INSERT:
                osnovnoSredstvoDao.insert(osnovnoSredstvo);
            case GETBY: {
                List<OsnovnoSredstvo> os = new ArrayList<>();
                os.add(osnovnoSredstvoDao.getById(id));
                return os;
            }
            case GETBYLOCATIONID:
                return osnovnoSredstvoDao.getOsnovnaSredstvaByLokacijaId(id);

            case UPDATE:
                osnovnoSredstvoDao.update(osnovnoSredstvo);
                break;
            case DELETE:
                osnovnoSredstvoDao.delete(osnovnoSredstvo);
                break;
            case READ:
                return osnovnoSredstvoDao.getAll();
        }
        return null; // Vraća se null osim u slučaju READ operacije
    }

    @Override
    protected void onPostExecute(List<OsnovnoSredstvo> osnovnaSredstva) {
        OsnovnoSredstvoFragment fragment = fragmentReference.get();

        if (fragment != null && osnovnaSredstva != null) {
            fragment.updateList(osnovnaSredstva); // Ažuriraj listu nakon dohvatanja podataka
        }
    }

}
