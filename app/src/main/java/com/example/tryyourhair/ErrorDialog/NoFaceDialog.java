package com.example.tryyourhair.ErrorDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.tryyourhair.OpenCamera;

public class NoFaceDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("No face detected")
                .setMessage("Oops! No face detected")
                .setPositiveButton("Retake another photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(NoFaceDialog.this.getActivity(), OpenCamera.class);
                        startActivity(intent);
                    }
                })
                .create();
        setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
