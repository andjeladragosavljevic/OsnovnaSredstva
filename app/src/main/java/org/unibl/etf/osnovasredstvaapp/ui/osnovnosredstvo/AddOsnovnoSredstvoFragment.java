package org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.journeyapps.barcodescanner.ScanOptions;
import com.journeyapps.barcodescanner.ScanContract;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.LokacijaDao;
import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.Lokacija;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddOsnovnoSredstvoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddOsnovnoSredstvoFragment extends Fragment {
    private OsnovnoSredstvo osnovnoSredstvo;
    private EditText nazivOsnovnogSredstva;
    private EditText opisOsnovnogSredstva;
    private Button saveButton;
    private EditText barcodeEditText;
    private EditText cijenaOsnovnogSredstva;
    private DatePicker datumKreiranja;

    private Zaposleni selectedZaposleni;
    private Lokacija selectedLokacija;

    private OsnovnoSredstvoDao osnovnoSredstvoDao;
    private OsnovnoSredstvo osnovnoSredstvoZaAzuriranje;

    private static final String ARG_OS = "osnovnoSredstvo";

    private ImageView imageView;
    private Button uploadImageButton, takePictureButton;
    private Uri imageUri;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddOsnovnoSredstvoFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AddOsnovnoSredstvoFragment newInstance(OsnovnoSredstvo osnovnoSredstvo) {
        AddOsnovnoSredstvoFragment fragment = new AddOsnovnoSredstvoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OS, osnovnoSredstvo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            osnovnoSredstvoZaAzuriranje = (OsnovnoSredstvo) getArguments().getSerializable(ARG_OS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_osnovno_sredstvo, container, false);
        osnovnoSredstvo = new OsnovnoSredstvo();

        // Inicijalizacija svih polja
        nazivOsnovnogSredstva = view.findViewById(R.id.naziv_osnovno_sredtsvo);
        opisOsnovnogSredstva = view.findViewById(R.id.opis_osnovno_sredstvo);
        barcodeEditText = view.findViewById(R.id.barkod_osnovno_sredstvo);
        cijenaOsnovnogSredstva = view.findViewById(R.id.cijena_osnovno_sredstvo);
        datumKreiranja = view.findViewById(R.id.datum_kreiranja_osnovno_sredstvo);
        imageView = view.findViewById(R.id.imageView);

        // Inicijalizacija dugmadi
        uploadImageButton = view.findViewById(R.id.buttonUploadSlika);
        takePictureButton = view.findViewById(R.id.buttonTakePicture);
        saveButton = view.findViewById(R.id.save_osnovno_sredstvo);

        AutoCompleteTextView autoCompleteTextViewOsoba = view.findViewById(R.id.autoCompleteTextViewOsoba);
        AutoCompleteTextView autoCompleteTextViewLokacija = view.findViewById(R.id.autoCompleteTextViewLokacija);

        // Dohvatite podatke iz baze za zaposlene i lokacije
        AppDatabase db = AppDatabase.getInstance(getContext());
        ZaposleniDao zaposleniDao = db.zaposleniDao();
        LokacijaDao lokacijaDao = db.lokacijaDao();

        List<Zaposleni> zaposleniList = zaposleniDao.getAll(); // Dohvati sve zaposlene
        List<Lokacija> lokacijaList = lokacijaDao.getAll(); // Dohvati sve lokacije

        // Adapter za zaposlene
        List<String> zaposleniImena = new ArrayList<>();
        for (Zaposleni zaposleni : zaposleniList) {
            zaposleniImena.add(zaposleni.getIme() + " " + zaposleni.getPrezime());
        }
        ArrayAdapter<String> zaposleniAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, zaposleniImena);
        autoCompleteTextViewOsoba.setAdapter(zaposleniAdapter);

        // Adapter za lokacije
        List<String> lokacijeImena = new ArrayList<>();
        for (Lokacija lokacija : lokacijaList) {
            lokacijeImena.add(lokacija.getGrad() + ", " + lokacija.getAdresa());
        }
        ArrayAdapter<String> lokacijaAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, lokacijeImena);
        autoCompleteTextViewLokacija.setAdapter(lokacijaAdapter);


        autoCompleteTextViewOsoba.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedZaposleniName = (String) parent.getItemAtPosition(position); // Dobijte ime zaposlenog iz prikazane liste

            for (Zaposleni zaposleni : zaposleniList) {
                if ((zaposleni.getIme() + " " + zaposleni.getPrezime()).equals(selectedZaposleniName)) {
                    selectedZaposleni = zaposleni; // Pronađen zaposleni
                    Log.d("ZAPOSLENI", "Izabran zaposleni ID: " + selectedZaposleni.getId() + ", Ime: " + selectedZaposleni.getIme() + " " + selectedZaposleni.getPrezime());
                    break;
                }
            }
        });

        autoCompleteTextViewLokacija.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedLokacijaName = (String) parent.getItemAtPosition(position); // Dobijte ime lokacije iz prikazane liste

            // Pronađite odgovarajuću lokaciju u originalnoj listi na osnovu imena
            for (Lokacija lokacija : lokacijaList) {
                if ((lokacija.getGrad() + ", " + lokacija.getAdresa()).equals(selectedLokacijaName)) {
                    selectedLokacija = lokacija; // Pronađena lokacija
                    Log.d("LOKACIJA", "Izabrana lokacija ID: " + selectedLokacija.getId() + ", Naziv: " + selectedLokacija.getGrad());
                    break;
                }
            }
        });


        barcodeEditText = view.findViewById(R.id.barkod_osnovno_sredstvo);
        Button scanBarcodeButton = view.findViewById(R.id.scanBarcodeButton);

        // Postavi klik listener na dugme za skeniranje barkoda
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermissionAndScan();
            }
        });

        if (osnovnoSredstvoZaAzuriranje != null) {
            nazivOsnovnogSredstva.setText(osnovnoSredstvoZaAzuriranje.getNaziv());
            opisOsnovnogSredstva.setText(osnovnoSredstvoZaAzuriranje.getOpis());
            barcodeEditText.setText(osnovnoSredstvoZaAzuriranje.getBarkod());
            cijenaOsnovnogSredstva.setText(String.valueOf(osnovnoSredstvoZaAzuriranje.getCijena()));

            // Podešavanje datuma u DatePicker
            String[] datumDijelovi = osnovnoSredstvoZaAzuriranje.getDatumKreiranja().split("/");
            datumKreiranja.updateDate(
                    Integer.parseInt(datumDijelovi[2]),
                    Integer.parseInt(datumDijelovi[1]) - 1,
                    Integer.parseInt(datumDijelovi[0]) // Dan
            );
            Log.d("SLIKA", osnovnoSredstvoZaAzuriranje.getSlika());
            if (osnovnoSredstvoZaAzuriranje.getSlika() != null) {
               prikaziSliku(osnovnoSredstvoZaAzuriranje.getSlika());
            }
        }


        // Listener za upload slike iz galerije
        uploadImageButton.setOnClickListener(v -> openGallery());

        // Listener za slikanje pomoću kamere
        takePictureButton.setOnClickListener(v -> checkCameraPermission());

        saveButton.setOnClickListener(v -> {
            String naziv = nazivOsnovnogSredstva.getText().toString();
            String opis = opisOsnovnogSredstva.getText().toString();
            String barkod = barcodeEditText.getText().toString();
            String cijena = cijenaOsnovnogSredstva.getText().toString();
            Log.d("CIJENA ", cijena);

            // Extracting the date from DatePicker
            int day = datumKreiranja.getDayOfMonth();
            int month = datumKreiranja.getMonth();
            int year = datumKreiranja.getYear();
            String datum = day + "/" + (month + 1) + "/" + year; // Datum format


            if (osnovnoSredstvoZaAzuriranje != null) {
                // Ažuriranje postojećeg osnovnog sredstva
                osnovnoSredstvoZaAzuriranje.setNaziv(naziv);
                osnovnoSredstvoZaAzuriranje.setOpis(opis);
                osnovnoSredstvoZaAzuriranje.setBarkod(barkod);
                osnovnoSredstvoZaAzuriranje.setCijena(Double.parseDouble(cijena));
                osnovnoSredstvoZaAzuriranje.setDatumKreiranja(datum);

                if (selectedZaposleni != null) {
                    osnovnoSredstvoZaAzuriranje.setZaduzenaOsobaId(selectedZaposleni.getId());
                }
                if (selectedLokacija != null) {
                    osnovnoSredstvoZaAzuriranje.setZaduzenaLokacijaId(selectedLokacija.getId());
                }

                new OsnovnoSredstvoTask(null, db.osnovnoSredstvoDao(), OsnovnoSredstvoTask.OperationType.UPDATE, osnovnoSredstvoZaAzuriranje).execute();
            } else {
                // Kreiranje novog osnovnog sredstva
                osnovnoSredstvo.setNaziv(naziv);
                osnovnoSredstvo.setOpis(opis);
                osnovnoSredstvo.setBarkod(barkod);
                osnovnoSredstvo.setCijena(Double.parseDouble(cijena));
                osnovnoSredstvo.setDatumKreiranja(datum);
                if (selectedZaposleni != null) {
                    osnovnoSredstvo.setZaduzenaOsobaId(selectedZaposleni.getId());
                }
                if (selectedLokacija != null) {
                    osnovnoSredstvo.setZaduzenaLokacijaId(selectedLokacija.getId());
                }

                new OsnovnoSredstvoTask(null, db.osnovnoSredstvoDao(), OsnovnoSredstvoTask.OperationType.INSERT, osnovnoSredstvo).execute();
            }

        });

        return view;

    }

    public void prikaziSliku(String putanjaSlike) {
        Uri imageUri = null;

        if (putanjaSlike.contains("/DCIM/Camera/")) {
            // Slikano kamerom
            File slikaFile = new File(putanjaSlike);
            try {
                imageUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", slikaFile);
            } catch (IllegalArgumentException e) {
                Log.e("SLIKA", "Greška pri generisanju URI-ja: " + e.getMessage());
            }
        } else if (putanjaSlike.contains("/external_files/")) {
            // Uploadovana slika
            imageUri = Uri.parse(putanjaSlike);
        } else {
            Log.e("SLIKA", "Nepoznata putanja: " + putanjaSlike);
        }

        if (imageUri != null) {
            imageView.setImageURI(imageUri);
        } else {
            Log.e("SLIKA", "URI nije pronađen za putanju: " + putanjaSlike);
        }
    }




    // Metoda za provjeru i zahtjev za dozvolu kamere
    private void checkCameraPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Ako je dozvola odobrena, pokreni skeniranje
            scanCode();
        } else {
            // Ako dozvola nije odobrena, zahtjeva je
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }
    private final ActivityResultLauncher<ScanOptions> scanLauncher = registerForActivityResult(
            new ScanContract(), result -> {
                if (result.getContents() != null) {
                    barcodeEditText.setText(result.getContents());
                }
            });

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Ako je dozvola odobrena, pokreni skeniranje
                    scanCode();
                } else {
                    // Ako je dozvola odbijena, obavjesti korisnika
                    Toast.makeText(getContext(), "Barkod se ne može skenirati bez kamer.", Toast.LENGTH_SHORT).show();
                }
            });

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES); // Za 1D barkodove
        options.setPrompt("Skeniraj barkod");
        options.setCameraId(0);  // Koristi zadnju kameru
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);

        // Pokreni skeniranje
        scanLauncher.launch(options);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        galleryLauncher.launch(intent);
    }

    // Provera dozvole za kameru
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    // Otvaranje kamere
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = createImageFile(); // Kreiraj fajl za sliku
        if (photoFile != null) {
            // Use FileProvider to get a content URI
            imageUri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraLauncher.launch(intent);
        }
    }



    private File createImageFile() {
        // Kreiraj fajl gdje će slika biti snimljena
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, "osnovnosredstvo_" + System.currentTimeMillis() + ".jpg");
    }

    // Launcher za upload iz galerije
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        imageView.setImageURI(selectedImageUri);
                        osnovnoSredstvo.setSlika(selectedImageUri.getPath());
                    }
                }
            });


    // Launcher za slikanje kamerom
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    imageView.setImageURI(imageUri); // Postavi sliku u ImageView
                    osnovnoSredstvo.setSlika(imageUri.getPath());
                }
            });

    // Launcher za dozvole za kameru
    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(getContext(), "Dozvola za kameru je potrebna.", Toast.LENGTH_SHORT).show();
                }
            });

}