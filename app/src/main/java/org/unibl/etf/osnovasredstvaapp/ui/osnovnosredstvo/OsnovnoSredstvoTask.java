package org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo;

import android.os.AsyncTask;
import android.util.Log;

import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;

import java.lang.ref.WeakReference;
import java.util.List;

public class OsnovnoSredstvoTask extends AsyncTask<Void, Void, List<OsnovnoSredstvo>> {

    public enum OperationType {
        INSERT, UPDATE, DELETE, READ
    }

    private WeakReference<OsnovnoSredstvoFragment> fragmentReference;
    private OsnovnoSredstvoDao osnovnoSredstvoDao;
    private OperationType operationType;
    private OsnovnoSredstvo osnovnoSredstvo;

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
        Log.d("OsnovnoSredstvoTask", "Operation: " + operationType);

        switch (operationType) {
            case INSERT: {
                try {
                    osnovnoSredstvoDao.insert(osnovnoSredstvo);
                    Log.d("OsnovnoSredstvoTask", "Insert success");
                } catch (Exception e) {
                    Log.e("OsnovnoSredstvoTask", "Insert error: " + e.getMessage());
                }

            }
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
