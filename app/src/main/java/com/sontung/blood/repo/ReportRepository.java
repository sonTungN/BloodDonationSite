package com.sontung.blood.repo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sontung.blood.callback.FirebaseCallback;
import com.sontung.blood.model.Report;
import com.sontung.blood.model.Site;
import com.sontung.blood.shared.Paths;

public class ReportRepository {
    private final Context context;
    
    private final FirebaseFirestore db;
    private final CollectionReference siteCollection;
    private final CollectionReference reportCollection;
    
    private final MutableLiveData<Report> reportData = new MutableLiveData<>();
    
    public ReportRepository(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.reportCollection = db.collection(Paths.REPORT_COLLECTION_PATH);
        this.siteCollection = db.collection(Paths.SITE_COLLECTION_PATH);
    }
    
    public void createReport(Report report, FirebaseCallback<Report> callback) {
        reportCollection
                .add(report)
                .addOnSuccessListener(documentReference -> {
                    String reportId = documentReference.getId();
                    report.setReportId(reportId);
                    callback.onSuccess(report);
                    
                    siteCollection
                            .document(report.getSiteId())
                            .update("listOfReports", FieldValue.arrayUnion(reportId))
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(context, "Can not update reportId into site listOfReports", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Can not update reportId into site listOfReports", Toast.LENGTH_SHORT).show();
                                Log.d("REPORT: Cant update reportId into listOfReports, Error: " + e.getMessage(), e.getMessage() != null ? e.getMessage() : "Error");
                            });
                })
                .addOnFailureListener(e -> {
                    Log.d("CREATE", "Create Report failed!");
                    Toast.makeText(context, "Failed to create new report", Toast.LENGTH_SHORT).show();
                });
    }
}
