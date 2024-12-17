package com.iot.iot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.util.ArrayList;
import java.util.List;

public class RiwayatFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<SensorData> itemList;
    private SharedViewModel sharedViewModel;
    private LinearLayout btn_download;

    public RiwayatFragment() {
        // Required empty public constructor
    }

    public static RiwayatFragment newInstance() {
        return new RiwayatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_riwayat, container, false);

        // Setup RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        // Setup Adapter
        itemList = new ArrayList<>();
        myAdapter = new MyAdapter(itemList);
        recyclerView.setAdapter(myAdapter);

        // Setup ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Setup download button
        btn_download = rootView.findViewById(R.id.btn_download);
        btn_download.setOnClickListener(view -> showBottomSheetDialog());

        // Observe data from ViewModel
        sharedViewModel.getSensorDataList().observe(getViewLifecycleOwner(), sensorData -> {
            if (sensorData != null && !sensorData.equals(itemList)) {
                itemList.clear();
                itemList.addAll(sensorData);
                myAdapter.notifyDataSetChanged();
            }
        });

        // Fetch data from InfluxDB
        sharedViewModel.fetchDataFromInfluxDB();

        return rootView;
    }

    private void showBottomSheetDialog() {
        // Create BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());

        // Inflate layout for BottomSheet
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_download, (ViewGroup) getView(), false);

        // Find buttons in BottomSheet
        Button btnPdf = bottomSheetView.findViewById(R.id.btn_pdf);


        // Set click listeners for each button
        btnPdf.setOnClickListener(v -> {
            generatePdf();
            Toast.makeText(getContext(), "Download PDF", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });


        // Set layout to BottomSheetDialog
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void generatePdf() {
        try {
            // Define the file path where the PDF will be saved
            String filePath = getContext().getExternalFilesDir(null) + "/riwayat_data.pdf";

            // Initialize PdfWriter and PdfDocument
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDoc = new PdfDocument(writer);

            // Create a Document object to add content
            Document document = new Document(pdfDoc);

            // Add content to the PDF (e.g., data from RecyclerView)
            for (SensorData data : itemList) {
                document.add(new Paragraph("pH: " + data.getPh()));
                document.add(new Paragraph("Salinity: " + data.getSalinity()));
                document.add(new Paragraph("Temperature: " + data.getTemp()));
                document.add(new Paragraph("Date: " + data.getFormattedDate()));
                document.add(new Paragraph("Time: " + data.getFormattedTime()));


                document.add(new Paragraph("\n"));
            }

            // Close the document to finalize the PDF
            document.close();

            // Notify user that the PDF was saved successfully
            Toast.makeText(getContext(), "PDF saved at: " + filePath, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to generate PDF", Toast.LENGTH_SHORT).show();
        }
    }


}
