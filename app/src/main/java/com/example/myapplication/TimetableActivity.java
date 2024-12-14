package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.apache.poi.ss.usermodel.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimetableActivity extends AppCompatActivity {
    private static final String TAG = "TimetableActivity";
    private Uri fileUri;
    private TextView tvFileName, tvExtractedText, tvFirestoreData;
    private Button btnSelectPdf, btnExtractText, btnSavePdfData, btnFetchTimetables, btnSelectExcel;
    private FirebaseFirestore firestore;
    private ActivityResultLauncher<Intent> selectFileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

      
        tvFileName = findViewById(R.id.tvFileName);
        tvExtractedText = findViewById(R.id.tvExtractedText);
        tvFirestoreData = findViewById(R.id.tvFirestoreData);
        btnSelectPdf = findViewById(R.id.btnSelectPdf);
        btnSelectExcel = findViewById(R.id.btnSelectExcel);
        btnExtractText = findViewById(R.id.btnExtractText);
        btnSavePdfData = findViewById(R.id.btnSavePdfData);
        btnFetchTimetables = findViewById(R.id.btnFetchTimetables);

        firestore = FirebaseFirestore.getInstance();

      
        selectFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        fileUri = result.getData().getData();
                        if (fileUri != null) {
                            tvFileName.setText("Fichier : " + getFileName(fileUri));
                            btnExtractText.setEnabled(true);
                        }
                    }
                });


        btnSelectPdf.setOnClickListener(v -> selectFile("application/pdf"));
        btnSelectExcel.setOnClickListener(v -> selectFile("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        btnExtractText.setOnClickListener(v -> extractTextFromFile());
        btnSavePdfData.setOnClickListener(v -> saveDataToFirestore());
        btnFetchTimetables.setOnClickListener(v -> fetchTimetablesFromFirestore());
    }

    private void selectFile(String type) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        selectFileLauncher.launch(intent);
    }

    private void extractTextFromFile() {
        if (fileUri != null) {
            String fileType = getContentResolver().getType(fileUri);
            if ("application/pdf".equals(fileType)) {
                extractTextFromPdf();
            } else if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(fileType)) {
                extractDataFromExcel(fileUri);
            } else {
                Toast.makeText(this, "Type de fichier non supporté", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void extractTextFromPdf() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            PdfReader reader = new PdfReader(inputStream);
            PdfDocument pdfDoc = new PdfDocument(reader);
            StringBuilder extractedText = new StringBuilder();
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                extractedText.append(PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i))).append("\n");
            }
            btnSavePdfData.setEnabled(true);
            tvExtractedText.setText(extractedText.toString());
            parseAndFillFields(extractedText.toString());
            pdfDoc.close();
        } catch (IOException e) {
            Log.e(TAG, "Erreur d'extraction PDF", e);
        }
    }

    private void extractDataFromExcel(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Workbook workbook = WorkbookFactory.create(inputStream);
            StringBuilder extractedText = new StringBuilder();
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        extractedText.append(cell.toString()).append(" ");
                    }
                    extractedText.append("\n");
                }
            }
            tvExtractedText.setText(extractedText.toString());
            parseAndFillFields(extractedText.toString());
            workbook.close();
        } catch (IOException e) {
            Log.e(TAG, "Erreur d'extraction Excel", e);
        }
    }

    private void parseAndFillFields(String text) {
       
        Map<String, String> fields = new HashMap<>();
        fields.put("teacherId", extractField(text, "\"teacherId\":\\s*(\\d+)"));
        fields.put("date", extractField(text, "\"date\":\\s*\"(\\d{2}/\\d{2}/\\d{4})"));
        fields.put("class", extractField(text, "\"class\":\\s*([\\d,\\s]+)"));
        fields.put("salle", extractField(text, "\"salle\":\\s*([\\d,\\s]+)"));

   
        String result = "Professeur : " + fields.get("teacherId") + "\nDate : " + fields.get("date") + "\nClass : " + fields.get("class") + "\nSalle : " + fields.get("salle");
        tvExtractedText.setText(result);
    }

    private String extractField(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        return matcher.find() ? matcher.group(1) : "Non trouvé";
    }

    private void saveDataToFirestore() {
        String extractedText = tvExtractedText.getText().toString().trim();
        if (!extractedText.isEmpty()) {
            String teacherId = extractField(extractedText, "Professeur : (\\d+)");
            String date = extractField(extractedText, "Date : (\\d{2}/\\d{2}/\\d{4})");
            String classroom = extractField(extractedText, "Class : ([\\d,\\s]+)"); 
            String salle = extractField(extractedText, "Salle : ([\\d,\\s]+)");


            Map<String, Object> timetable = new HashMap<>();
            timetable.put("teacherId", teacherId);
            timetable.put("date", date);
            timetable.put("class", classroom);
            timetable.put("salle", salle);

            firestore.collection("timetables")
                    .add(timetable)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Données enregistrées dans Firestore", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Aucun texte à enregistrer. Veuillez extraire le texte d'abord.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchTimetablesFromFirestore() {
        firestore.collection("timetables").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StringBuilder data = new StringBuilder();
                for (QueryDocumentSnapshot document : task.getResult()) {
                   
                    String teacherId = document.getString("teacherId");
                    String date = document.getString("date");
                    String classroom = document.getString("class");
                    String salle = document.getString("salle");

                    data.append("Professeur: ").append(teacherId)
                            .append("\nDate: ").append(date)
                            .append("\nClasse: ").append(classroom)
                            .append("\nSalle: ").append(salle)
                            .append("\n\n");
                }
                tvFirestoreData.setText(data.toString());
            } else {
                Log.e(TAG, "Erreur de récupération Firestore");
            }
        });
    }

    private String getFileName(Uri uri) {
        return uri.getLastPathSegment();
    }
}
