package com.omurgun.additionandwrite.Game;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.omurgun.additionandwrite.R;

public class ExampleDialog extends AppCompatDialogFragment {

    private EditText resultAnswer;
    private ExampleDialogListener listener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog,null);
        final AlertDialog  builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setCancelable(false)
                .setTitle("sum")
                .setPositiveButton("Ok", null)
                .show();
        Button positiveButton =builder.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String resultText = resultAnswer.getText().toString();
                if(!resultText.isEmpty())
                {
                    listener.applyTexts(resultText);
                    builder.dismiss();
                }
                else
                {
                    Toast.makeText(getActivity(),"Result Cannot Be Empty!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        resultAnswer = view.findViewById(R.id.editText);

        return  builder;

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ExampleDialogListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString());

        }

    }

}

interface ExampleDialogListener{
   void applyTexts(String result);
}

