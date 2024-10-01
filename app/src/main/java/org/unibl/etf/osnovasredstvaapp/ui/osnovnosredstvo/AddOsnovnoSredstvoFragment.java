package org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddOsnovnoSredstvoFragment extends Fragment {
    // Polja vezana za osnovno sredstvo
    private OsnovnoSredstvo osnovnoSredstvo;
    private EditText nazivOsnovnogSredstva;
    private EditText opisOsnovnogSredstva;
    private EditText barcodeEditText;
    private EditText cijenaOsnovnogSredstva;
    private DatePicker datumKreiranja;
    private Zaposleni selectedZaposleni;
    private Lokacija selectedLokacija;
    private ImageView imageView;
    private Button uploadImageButton, takePictureButton, saveButton;

    // Polja vezana za slike
    private String currentPhotoPath;
    private Uri imageUri;

    private OsnovnoSredstvoDao osnovnoSredstvoDao;
    private OsnovnoSredstvo osnovnoSredstvoZaAzuriranje;
    private static final String ARG_OS = "osnovnoSredstvo";
    AppDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            osnovnoSredstvoZaAzuriranje = (OsnovnoSredstvo) getArguments().getSerializable(ARG_OS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_osnovno_sredstvo, container, false);

        // Inicijalizacija UI elemenata
        nazivOsnovnogSredstva = view.findViewById(R.id.naziv_osnovno_sredtsvo);
        opisOsnovnogSredstva = view.findViewById(R.id.opis_osnovno_sredstvo);
        barcodeEditText = view.findViewById(R.id.barkod_osnovno_sredstvo);
        cijenaOsnovnogSredstva = view.findViewById(R.id.cijena_osnovno_sredstvo);
        datumKreiranja = view.findViewById(R.id.datum_kreiranja_osnovno_sredstvo);
        imageView = view.findViewById(R.id.imageView);
        uploadImageButton = view.findViewById(R.id.buttonUploadSlika);
        takePictureButton = view.findViewById(R.id.buttonTakePicture);
        saveButton = view.findViewById(R.id.save_osnovno_sredstvo);

        // Inicijalizacija baze
        db = AppDatabase.getInstance(requireContext());
        OsnovnoSredstvoDao osnovnoSredstvoDao = db.osnovnoSredstvoDao();
        ZaposleniDao zaposleniDao = db.zaposleniDao();
        LokacijaDao lokacijaDao = db.lokacijaDao();


            // Provera i inicijalizacija osnovnog sredstva
            if (osnovnoSredstvoZaAzuriranje != null) {
                osnovnoSredstvo = osnovnoSredstvoZaAzuriranje;  // Ažuriramo postojeće osnovno sredstvo

                // Popunite polja sa postojećim podacima
                nazivOsnovnogSredstva.setText(osnovnoSredstvo.getNaziv());
                opisOsnovnogSredstva.setText(osnovnoSredstvo.getOpis());
                barcodeEditText.setText(osnovnoSredstvo.getBarkod());
                cijenaOsnovnogSredstva.setText(String.valueOf(osnovnoSredstvo.getCijena()));

                currentPhotoPath = osnovnoSredstvo.getSlikaPath();

                // Prikaži sliku ako postoji putanja
                if (osnovnoSredstvoZaAzuriranje != null && osnovnoSredstvoZaAzuriranje.getSlikaPath() != null) {
                    currentPhotoPath = osnovnoSredstvoZaAzuriranje.getSlikaPath();
                    File imgFile = new File(currentPhotoPath);
                    if (imgFile.exists()) {
                        imageView.setImageURI(Uri.fromFile(imgFile)); // Prikaz slike
                    }
                }


            } else {
                osnovnoSredstvo = new OsnovnoSredstvo();  // Kreiramo novo osnovno sredstvo
            }
        // Dohvatanje svih zaposlenih i lokacija iz baze
        List<Zaposleni> zaposleniList = zaposleniDao.getAll();
        List<Lokacija> lokacijaList = lokacijaDao.getAll();

        // Popunjavanje AutoCompleteTextView za zaposlene
        List<String> zaposleniImena = new ArrayList<>();
        for (Zaposleni zaposleni : zaposleniList) {
            zaposleniImena.add(zaposleni.getIme() + " " + zaposleni.getPrezime());
        }
        ArrayAdapter<String> zaposleniAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, zaposleniImena);
        AutoCompleteTextView autoCompleteTextViewOsoba = view.findViewById(R.id.autoCompleteTextViewOsoba);
        autoCompleteTextViewOsoba.setAdapter(zaposleniAdapter);

        // Popunjavanje AutoCompleteTextView za lokacije
        List<String> lokacijeImena = new ArrayList<>();
        for (Lokacija lokacija : lokacijaList) {
            lokacijeImena.add(lokacija.getGrad() + ", " + lokacija.getAdresa());
        }
        ArrayAdapter<String> lokacijaAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, lokacijeImena);
        AutoCompleteTextView autoCompleteTextViewLokacija = view.findViewById(R.id.autoCompleteTextViewLokacija);
        autoCompleteTextViewLokacija.setAdapter(lokacijaAdapter);

        // Odabir zaposlenog
        autoCompleteTextViewOsoba.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedZaposleniName = (String) parent.getItemAtPosition(position);
            for (Zaposleni zaposleni : zaposleniList) {
                if ((zaposleni.getIme() + " " + zaposleni.getPrezime()).equals(selectedZaposleniName)) {
                    selectedZaposleni = zaposleni;
                    break;
                }
            }
        });

        // Odabir lokacije
        autoCompleteTextViewLokacija.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedLokacijaName = (String) parent.getItemAtPosition(position);
            for (Lokacija lokacija : lokacijaList) {
                if ((lokacija.getGrad() + ", " + lokacija.getAdresa()).equals(selectedLokacijaName)) {
                    selectedLokacija = lokacija;
                    break;
                }
            }
        });

//        // Učitavanje osnovnog sredstva za ažuriranje (ako postoji)
//        if (osnovnoSredstvoZaAzuriranje != null) {
//            nazivOsnovnogSredstva.setText(osnovnoSredstvoZaAzuriranje.getNaziv());
//            opisOsnovnogSredstva.setText(osnovnoSredstvoZaAzuriranje.getOpis());
//            barcodeEditText.setText(osnovnoSredstvoZaAzuriranje.getBarkod());
//            cijenaOsnovnogSredstva.setText(String.valueOf(osnovnoSredstvoZaAzuriranje.getCijena()));
//            Log.d("STO", osnovnoSredstvoZaAzuriranje.getSlikaPath());
//            if (osnovnoSredstvoZaAzuriranje.getSlikaPath() != null) {
//
//                loadImageFromPath(osnovnoSredstvoZaAzuriranje.getSlikaPath());
//            }
//        }

        // Listener za upload slike iz galerije
        uploadImageButton.setOnClickListener(v -> openGallery());

        // Listener za slikanje pomoću kamere
        takePictureButton.setOnClickListener(v -> checkCameraPermission());

        // Listener za skeniranje barkoda
        barcodeEditText = view.findViewById(R.id.barkod_osnovno_sredstvo);
        Button scanBarcodeButton = view.findViewById(R.id.scanBarcodeButton);
        scanBarcodeButton.setOnClickListener(v -> checkCameraPermissionAndScan());

        // Čuvanje osnovnog sredstva
        saveButton.setOnClickListener(v -> saveOsnovnoSredstvo());

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }


    private void launchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        // Sačuvaj sliku u lokalni direktorijum aplikacije
                        saveImageToAppStorage(imageUri);

                        // Prikaz slike nakon što je sačuvana u lokalnom direktorijumu
                        imageView.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
                    }
                }
            });

    // Metoda koja proverava da li URI dolazi iz MediaStore
    private boolean isMediaStoreUri(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    // Metoda za proveru dozvole za kameru i otvaranje kamere
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    // Otvaranje kamere i snimanje slike
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = createImageFile();
        if (photoFile != null) {
            imageUri = FileProvider.getUriForFile(requireContext(), "org.unibl.etf.osnovasredstvaapp.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraLauncher.launch(intent);
        }
    }

    // Kreiranje fajla za skladištenje slike
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath(); // Postavljanje putanje slike
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    // Pokretanje kamere
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Prikaz slike koja je uslikana kamerom
                    imageView.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
                }
            });


    // Proveravanje dozvole za kameru
    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(requireContext(), "Dozvola za kameru je potrebna.", Toast.LENGTH_SHORT).show();
                }
            });



    private void checkCameraPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Ako je dozvola odobrena, pokreni skeniranje
            scanCode();
        } else {
            // Ako dozvola nije odobrena, zatraži dozvolu
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

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
                    // Ako je dozvola odbijena, obavesti korisnika
                    Toast.makeText(getContext(), "Barkod se ne može skenirati bez kamere.", Toast.LENGTH_SHORT).show();
                }
            });

    // Čuvanje osnovnog sredstva
    private void saveOsnovnoSredstvo() {
        String naziv = nazivOsnovnogSredstva.getText().toString();
        String opis = opisOsnovnogSredstva.getText().toString();
        String barkod = barcodeEditText.getText().toString();
        double cijena = Double.parseDouble(cijenaOsnovnogSredstva.getText().toString());
        int day = datumKreiranja.getDayOfMonth();
        int month = datumKreiranja.getMonth();
        int year = datumKreiranja.getYear();
        String datum = day + "/" + (month + 1) + "/" + year;

        osnovnoSredstvo.setNaziv(naziv);
        osnovnoSredstvo.setOpis(opis);
        osnovnoSredstvo.setBarkod(barkod);
        osnovnoSredstvo.setCijena(cijena);
        osnovnoSredstvo.setDatumKreiranja(datum);
        if (selectedZaposleni != null) {
            osnovnoSredstvo.setZaduzenaOsobaId(selectedZaposleni.getId()); // Strani ključ za zaposlenog
        }
        if (selectedLokacija != null) {
            osnovnoSredstvo.setZaduzenaLokacijaId(selectedLokacija.getId()); // Strani ključ za lokaciju
        }

        // Postavljanje putanje slike samo ako postoji putanja
        if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
            osnovnoSredstvo.setSlikaPath(currentPhotoPath);
        }

        if (osnovnoSredstvoZaAzuriranje != null) {
            // Ažuriranje
            new OsnovnoSredstvoTask(null, db.osnovnoSredstvoDao(), OsnovnoSredstvoTask.OperationType.UPDATE, osnovnoSredstvoZaAzuriranje).execute();
        } else {
            // Kreiranje novog
            new OsnovnoSredstvoTask(null, db.osnovnoSredstvoDao(), OsnovnoSredstvoTask.OperationType.INSERT, osnovnoSredstvo).execute();
        }
    }

    private void loadImageFromPath(String imagePath) {
        Log.d("IMAGEPATH", imagePath);
        if (imagePath != null && !imagePath.isEmpty()) {
            Uri uri = Uri.parse(imagePath);

            if (uri != null && uri.getScheme() != null) {
                if (uri.getScheme().equals("content")) {
                    try {
                        // Proverite da li URI i dalje postoji pre nego što ga pokušate otvoriti
                        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                        if (inputStream != null) {
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imageView.setImageBitmap(bitmap); // Prikaz slike
                        } else {
                            Log.e("IMAGE_ERROR", "Nije moguće otvoriti URI: " + uri.toString());
                        }
                    } catch (FileNotFoundException e) {
                        Log.e("IMAGE_ERROR", "Slika nije pronađena: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        imageView.setImageBitmap(bitmap);
                    } else {
                        Log.e("FILE_ERROR", "Fajl ne postoji na putanji: " + imagePath);
                    }
                }
            }
        } else {
            Log.e("IMAGEPATH_ERROR", "Putanja slike je null ili prazna.");
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ako je dozvola odobrena, otvori galeriju ili učitaj sliku
                openGallery();
            } else {
                Toast.makeText(getContext(), "Dozvola za čitanje memorije nije odobrena.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImageToAppStorage(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            File file = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "slika_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            // Sačuvajte apsolutnu putanju do slike u bazi
            currentPhotoPath = file.getAbsolutePath();
            Log.d("SAVE_IMAGE", "Slika je sačuvana u: " + currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

