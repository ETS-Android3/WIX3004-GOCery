package com.example.goceryreceiptocr;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

// References:
// https://developers.google.com/ml-kit/vision/text-recognition/android#java
// https://www.youtube.com/watch?v=E76TO5aImN8
// https://www.geeksforgeeks.org/android-how-to-request-permissions-in-android-application/
// https://developer.android.com/training/basics/intents/result
// https://www.geeksforgeeks.org/text-detector-in-android-using-firebase-ml-kit/
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyTag";
    private Button btnCopyText;
    private FloatingActionButton btnChooseImageFromGallery, btnTakeImageFromCamera;
    private ExtendedFloatingActionButton btnGetImage;

    // To check whether sub FABs are visible or not
    private Boolean isAllFabsVisible;

    // These TextViews are taken to make visible and
    // invisible along with FABs except parent FAB's action
    // name
    private TextView TVChooseImageFromGallery, TVTakeImageFromCamera, TVOCRResult;
    private ImageView IVSelectedImage;

    // Defining Permission codes.
    // We can give any value
    // but unique for each permission.
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int STORAGE_PERMISSION_CODE = 102;
    private static final int REQUEST_IMAGE_CAPTURE = 103;
    private static final int SELECT_IMAGE = 104;

    private ActivityResultLauncher<Intent> loadImageFromGallery;
    private ActivityResultLauncher<Intent> takeImageFromCamera;
    private ActivityResultLauncher<String> cropImage;

    private InputImage inputImage;
    private TextRecognizer textRecognizer;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View/viewgroups initialisation
        TVOCRResult = findViewById(R.id.TVOCRResult);
        btnChooseImageFromGallery = findViewById(R.id.btnChooseImageFromGallery);
        btnTakeImageFromCamera = findViewById(R.id.btnTakeImageFromCamera);
        btnGetImage = findViewById(R.id.btnGetImage);
        btnCopyText = findViewById(R.id.btnCopyText);

        TVChooseImageFromGallery = findViewById(R.id.TVChooseImageFromGallery);
        TVTakeImageFromCamera = findViewById(R.id.TVTakeImageFromCamera);
        IVSelectedImage = findViewById(R.id.IVSelectedImage);

        // Check permission(s)
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

        // Manage FABS
        manageImageFabs();

        // Initialise ML Kit's TextRecognizer
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

//        // Activity Result for loading image from Gallery/File
//        loadImageFromGallery = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        cropImage.launch("image/*");
//                    }
//                }
//        );

        // Activity Result for taking image from camera
        takeImageFromCamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // Handle images
                        Intent data = result.getData();

                        // Try-catch block to capture if user cancels camera image confirmation
                        try {
                            // Get bitmap data
                            Bitmap selectedImage = (Bitmap) data.getExtras().get("data");

                            // Set ImageView with selected image
                            IVSelectedImage.setImageBitmap(selectedImage);
                            convertCameraImageToText(selectedImage);
                        } catch (Exception e) {
                            Log.d(TAG, "takeImageFromCamera: Error: " + e.getMessage());
                        }
                    }
                }
        );

        // Activity Result for image cropping
        // References:
        // https://www.youtube.com/watch?v=DM8vorNKIFg
        // https://github.com/Yalantis/uCrop
        cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent = new Intent(MainActivity.this, CropperActivity.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 101);
            }
        });

        // Event listener for copying images
        btnCopyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scanned_text = TVOCRResult.getText().toString();
                copyToClipboard(scanned_text);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 101) {
            String result = data.getStringExtra("RESULT");
            Uri resultUri = null;

            if (result != null) {
                resultUri = Uri.parse(result);
                // Set ImageView with selected image
                // IVSelectedImage.setImageURI(imageUri);
                Picasso.get().load(resultUri).into(IVSelectedImage);
                convertImageToText(resultUri);
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.d(TAG, "Error: " + cropError);
        }
    }

    // Function to handle copy of OCR text
    // References:
    // https://github.com/evanemran/OCR_App_CWE/blob/master/app/src/main/java/com/example/ocrapp/MainActivity.java
    private void copyToClipboard(String text) {
        ClipboardManager clipBoard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied data", text);
        clipBoard.setPrimaryClip(clip);
        Toast.makeText(MainActivity.this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

    // References:
    // https://www.geeksforgeeks.org/extended-floating-action-button-in-android-with-example/
    // https://material.io/components/buttons-floating-action-button/android#extended-fabs
    private void manageImageFabs() {
        // Now set all the FABs and all the action name
        // texts as GONE
        btnChooseImageFromGallery.setVisibility(View.GONE);
        btnTakeImageFromCamera.setVisibility(View.GONE);
        TVChooseImageFromGallery.setVisibility(View.GONE);
        TVTakeImageFromCamera.setVisibility(View.GONE);

        // Make the boolean variable as false, as all the
        // action name texts and all the sub FABs are
        // invisible
        isAllFabsVisible = false;

        // Set the Extended floating action button to
        // shrinked state initially
        btnGetImage.shrink();

        // We will make all the FABs and action name texts
        // visible only when Parent FAB button is clicked So
        // we have to handle the Parent FAB button first, by
        // using setOnClickListener you can see below
        btnGetImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {

                            // when isAllFabsVisible becomes
                            // true make all the action name
                            // texts and FABs VISIBLE.
                            btnChooseImageFromGallery.show();
                            btnTakeImageFromCamera.show();
                            TVChooseImageFromGallery.setVisibility(View.VISIBLE);
                            TVTakeImageFromCamera.setVisibility(View.VISIBLE);

                            // Now extend the parent FAB, as
                            // user clicks on the shrinked
                            // parent FAB
                            btnGetImage.extend();

                            // make the boolean variable true as
                            // we have set the sub FABs
                            // visibility to GONE
                            isAllFabsVisible = true;
                        } else {

                            // when isAllFabsVisible becomes
                            // true make all the action name
                            // texts and FABs GONE.
                            btnChooseImageFromGallery.hide();
                            btnTakeImageFromCamera.hide();
                            TVChooseImageFromGallery.setVisibility(View.GONE);
                            TVTakeImageFromCamera.setVisibility(View.GONE);

                            // Set the FAB to shrink after user
                            // closes all the sub FABs
                            btnGetImage.shrink();

                            // make the boolean variable false
                            // as we have set the sub FABs
                            // visibility to GONE
                            isAllFabsVisible = false;
                        }
                    }
                });

        btnChooseImageFromGallery.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, "Choose Image from Gallery clicked", Toast.LENGTH_SHORT).show();

                        // when isAllFabsVisible becomes
                        // true make all the action name
                        // texts and FABs GONE.
                        btnChooseImageFromGallery.hide();
                        btnTakeImageFromCamera.hide();
                        TVChooseImageFromGallery.setVisibility(View.GONE);
                        TVTakeImageFromCamera.setVisibility(View.GONE);

                        // Set the FAB to shrink after user
                        // closes all the sub FABs
                        btnGetImage.shrink();

                        // make the boolean variable false
                        // as we have set the sub FABs
                        // visibility to GONE
                        isAllFabsVisible = false;

                        // Create an instance of the
                        // intent of the type image
//                        Intent intent = new Intent();
//                        intent.setType("image/*");
//                        intent.setAction(Intent.ACTION_GET_CONTENT);
//                        // startActivityForResult(); // DEPRECATED
//                        loadImageFromGallery.launch(intent);
                        cropImage.launch("image/*");
                    }
                });

        btnTakeImageFromCamera.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // when isAllFabsVisible becomes
                        // true make all the action name
                        // texts and FABs GONE.
                        btnChooseImageFromGallery.hide();
                        btnTakeImageFromCamera.hide();
                        TVChooseImageFromGallery.setVisibility(View.GONE);
                        TVTakeImageFromCamera.setVisibility(View.GONE);

                        // Set the FAB to shrink after user
                        // closes all the sub FABs
                        btnGetImage.shrink();

                        // make the boolean variable false
                        // as we have set the sub FABs
                        // visibility to GONE
                        isAllFabsVisible = false;

                        Toast.makeText(MainActivity.this, "Take Image from Camera clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        takeImageFromCamera.launch(intent);
                    }
                });
    }

    private void convertImageToText(Uri imageUri) {
        try {
            inputImage = InputImage.fromFilePath(getApplicationContext(), imageUri);

            // Get Text from Input Image
            Task<Text> result = textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            // Task completed successfully
                            TVOCRResult.setText(text.getText());
                            btnCopyText.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    TVOCRResult.setText("Error: " + e.getMessage());
                                    Log.d(TAG, "Error: " + e.getMessage());
                                }
                            });
        } catch (Exception e) {
            Log.d(TAG, "convertImageToText: Error: " + e.getMessage());
        }
    }

    // References:
    // https://medium.com/@hasangi/capture-image-or-choose-from-gallery-photos-implementation-for-android-a5ca59bc6883
    // https://github.com/ayushgemini/android-choose-photo/blob/master/app/src/main/java/com/geminisoftservices/choosephoto/MainActivity.java
    private void convertCameraImageToText(Bitmap imageBitmap) {
        try {
            int rotationDegree = 0;
            inputImage = InputImage.fromBitmap(imageBitmap, rotationDegree);

            // Get Text from Input Image
            Task<Text> result = textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            // Task completed successfully
                            TVOCRResult.setText(text.getText());
                            Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    TVOCRResult.setText("Error: " + e.getMessage());
                                    Log.d(TAG, "Error: " + e.getMessage());
                                }
                            });
        } catch (Exception e) {
            Log.d(TAG, "convertImageToText: Error: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // Function to check and request permission
    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }

    // This function is called when user accept or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when user is prompt for permission.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Showing the toast message
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}