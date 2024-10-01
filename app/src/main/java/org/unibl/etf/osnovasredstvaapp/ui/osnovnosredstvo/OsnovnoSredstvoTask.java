package org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo;

import android.os.AsyncTask;
import android.util.Log;

import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;

import java.lang.ref.WeakReference;
import java.util.List;

public class OsnovnoSredstvoTask extends AsyncTask<Void, Void, List<OsnovnoSredstvo>> {
    private int lokacijaId;

    private String nazivFilter;
    private String barkodFilter;

    public enum OperationType {
        INSERT, UPDATE, DELETE, READ, GETBYLOCATIONID, FILTER
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

    public OsnovnoSredstvoTask(OsnovnoSredstvoFragment fragment, OsnovnoSredstvoDao osnovnoSredstvoDao,  OperationType operationType, int lokacijaId) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.osnovnoSredstvoDao = osnovnoSredstvoDao;
        this.operationType = operationType;
        this.lokacijaId = lokacijaId;
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
            case INSERT: {
                try {
                    osnovnoSredstvoDao.insert(osnovnoSredstvo);
                    Log.d("OsnovnoSredstvoTask", "Insert success");
                } catch (Exception e) {
                    Log.e("OsnovnoSredstvoTask", "Insert error: " + e.getMessage());
                }

            }
            case GETBYLOCATIONID:
                return osnovnoSredstvoDao.getOsnovnaSredstvaByLokacijaId(lokacijaId);

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
