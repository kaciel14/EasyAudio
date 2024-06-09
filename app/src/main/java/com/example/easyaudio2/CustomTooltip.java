package com.example.easyaudio2;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class CustomTooltip {


    private final Dialog dialog;

    public CustomTooltip(Context context, View anchorView, String message) {
        dialog = new Dialog(context);

        // Configura el diálogo para que no tenga título ni fondo
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Crea un TextView para mostrar el mensaje del tooltip
        TextView textView = new TextView(context);
        textView.setText(message);
        textView.setTextSize(24);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.BLACK);
        textView.setPadding(10, 10, 10, 10);

        dialog.setContentView(textView);

        // Configura la posición del diálogo
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        //params.x = location[0];
        //params.y = location[1] + anchorView.getHeight();

        dialog.getWindow().setAttributes(params);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}