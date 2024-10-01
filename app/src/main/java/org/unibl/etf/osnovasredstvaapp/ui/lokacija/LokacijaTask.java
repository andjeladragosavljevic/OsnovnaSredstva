package org.unibl.etf.osnovasredstvaapp.ui.lokacija;

import android.os.AsyncTask;

import org.unibl.etf.osnovasredstvaapp.dao.LokacijaDao;
import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.entity.Lokacija;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.OsnovnoSredstvoFragment;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.OsnovnoSredstvoTask;
import org.unibl.etf.osnovasredstvaapp.ui.zaposleni.ZaposleniFragment;
import org.unibl.etf.osnovasredstvaapp.ui.zaposleni.ZaposleniTask;

import java.lang.ref.WeakReference;
import java.util.List;

public class LokacijaTask extends AsyncTask<Void, Void, List<Lokacija>> {
    private String adresaFilter;
    private String gradFilter;

    public enum OperationType {
        INSERT, UPDATE, DELETE, READ, FILTER
    }

    private WeakReference<LokacijaFragment> fragmentReference;
    private LokacijaDao lokacijaDao;
    private LokacijaTask.OperationType operationType;
    private Lokacija lokacija;

    // Konstruktor za filtriranje
    public LokacijaTask(LokacijaFragment fragment, LokacijaDao dao, String gradFilter, String adresaFilter) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.lokacijaDao = dao;
        this.gradFilter = gradFilter;
        this.adresaFilter = adresaFilter;
        this.operationType = LokacijaTask.OperationType.FILTER;
    }

    // Konstruktor za INSERT, UPDATE i DELETE operacije
    public LokacijaTask(LokacijaFragment fragment, LokacijaDao dao, LokacijaTask.OperationType operationType, Lokacija lokacija) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.lokacijaDao = dao;
        this.operationType = operationType;
        this.lokacija = lokacija;
    }

    // Konstruktor za READ operaciju
    public LokacijaTask(LokacijaFragment fragment, LokacijaDao dao) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.lokacijaDao = dao;
        this.operationType = LokacijaTask.OperationType.READ;
    }

    @Override
    protected List<Lokacija> doInBackground(Void... voids) {
        switch (operationType) {
            case FILTER:
                if (!gradFilter.isEmpty() && !adresaFilter.isEmpty()) {
                    return lokacijaDao.filterByGradAndAdresa(gradFilter, adresaFilter);
                } else if (!gradFilter.isEmpty()) {
                    return lokacijaDao.filterByGrad(gradFilter);
                } else if (!adresaFilter.isEmpty()) {
                    return lokacijaDao.filterByAdresa(adresaFilter);
                } else {
                    return lokacijaDao.getAll();
                }
            case INSERT:
                lokacijaDao.insert(lokacija);
                break;
            case UPDATE:
                lokacijaDao.update(lokacija);
                break;
            case DELETE:
                lokacijaDao.delete(lokacija);
                break;
            case READ:
                return lokacijaDao.getAll();
        }
        return null; // Vraća se null osim u slučaju READ operacije
    }

    @Override
    protected void onPostExecute(List<Lokacija> lokacije) {
        LokacijaFragment fragment = fragmentReference.get();
        if (fragment != null && lokacije != null) {
            fragment.updateList(lokacije); // Ažuriraj listu nakon dohvatanja podataka
        }
    }

}
