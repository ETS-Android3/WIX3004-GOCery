package com.example.gocery.receipt_ocr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gocery.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

// References:
// https://www.youtube.com/watch?v=DM8vorNKIFg
// https://github.com/Yalantis/uCrop
public class CropperActivity extends AppCompatActivity {

    String result;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);

        readIntent();
        String dest_uri = UUID.randomUUID().toString() + ".jpg";

        UCrop.of(fileUri, Uri.fromFile(new File(getCacheDir(), dest_uri)))
                .withOptions(new UCrop.Options())
                .withAspectRatio(0, 0)
                .useSourceImageAspectRatio()
                .withMaxResultSize(2000, 2000)
                .start(CropperActivity.this);
    }

    private void readIntent() {
        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            result = intent.getStringExtra("DATA");
            fileUri = Uri.parse(result);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if codes match
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULT", resultUri + "");
            setResult(-1, returnIntent);
            finish();

        }
        // Capture UCrop error = throw Throwable
        else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
        // Capture if user taps 'X' button (cancels cropping) = return to previous fragment
        else {
            super.onBackPressed();
        }
    }
}