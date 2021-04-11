package com.example.cardmaker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.transform.Result;

import static android.app.Activity.RESULT_OK;


public class DetailFragment extends Fragment {

    ImageButton deleteButton , backButton;
    Button saveButton;
    SQLiteDatabase database;
    ImageView imageView;
    EditText editText;
    ImageButton imageButton;
    Bitmap selectedImage;
    Boolean fotosecildi=false;
    Boolean yeni_mi=true;
    String pos;
    TextView cardNameText;

    public DetailFragment() {
        // Required empty public constructor
    }


    public static DetailFragment newInstance() {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            yeni_mi = DetailFragmentArgs.fromBundle(getArguments()).getYeniMi();
            pos = DetailFragmentArgs.fromBundle(getArguments()).getPosition();
            System.out.println(pos);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = getActivity().openOrCreateDatabase("Cards" , Context.MODE_PRIVATE , null);

        backButton = view.findViewById(R.id.backImageButtonId);
        deleteButton = view.findViewById(R.id.deleteImageButtonİd);
        saveButton = view.findViewById(R.id.saveButtonId);
        imageView = view.findViewById(R.id.resimId);
        editText = view.findViewById(R.id.editTextTextPersonName);
        imageButton = view.findViewById(R.id.setId);
        cardNameText = view.findViewById(R.id.cardNameTextId);


        ///////////////////////////YENİ YA DA ESKİYE GÖRE İŞLEMLER/////////////////////////////////////


        if (yeni_mi){
            deleteButton.setVisibility(View.INVISIBLE);
            backButton.setVisibility(View.INVISIBLE);
            cardNameText.setVisibility(View.INVISIBLE);
        }
        else {

            try {
                Cursor cursor = database.rawQuery("SELECT * FROM card WHERE isim = ?" , new String[] {pos});

                int nameIndex = cursor.getColumnIndex("isim");
                int resimIndex = cursor.getColumnIndex("resim");
                
                while (cursor.moveToNext()){
                    byte[] bytes = cursor.getBlob(resimIndex);
                    Bitmap bitmapResim = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                    cardNameText.setText(cursor.getString(nameIndex).toString());
                    imageView.setImageBitmap(bitmapResim);
                cursor.close();
                }



            }catch (Exception e){
                System.out.println(e.getLocalizedMessage());
            }

            editText.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
            imageButton.setVisibility(View.INVISIBLE);
            editText.setEnabled(false);
        }


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPicF(v);
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveF(v);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteF(v);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backf(v);
            }
        });
    }

    /////////////////////////////// BACK İŞLEMİ ////////////////////////////////

    public void backf(View view){

        NavDirections action = DetailFragmentDirections.actionDetailFragmentToListFragment();
        Navigation.findNavController(view).navigate(action);

    }

    //////////////////////////////DELETE İŞLEMİ ///////////////////////////////

    public void deleteF(View view){


         System.out.println("alerttt");
        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
        alert.setTitle("Delete Card");
        alert.setMessage("Are you sure you want to delete the card?");

        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    database.execSQL("CREATE TABLE IF NOT EXISTS card (id INTEGER PRIMARY KEY , isim VARCHAR , resim BLOB) ");


                    String sqlString = "DELETE FROM card WHERE isim = ?";
                    SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                    sqLiteStatement.bindString(1, String.valueOf(pos));
                    sqLiteStatement.execute();

                    Toast.makeText(getActivity().getApplicationContext(), "The card deleted...", Toast.LENGTH_SHORT).show();
                    backf(view);


                    //Cursor cursor= database.rawQuery("SELECT * FROM card",null);

                    //  Integer a = 1;

                   // while (cursor.moveToNext()){
                  //  database.execSQL("UPDATE card SET id = id-1 WHERE id>pos");

                    //}


                }catch (Exception e){
                    System.out.println(e.getLocalizedMessage());
                }


            }
        });
        alert.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
   }


        /////////////////////////// SAVE İŞLEMİ ///////////////////////////////

    public void saveF(View view){

        if (fotosecildi == false){

        }
        else {
            String cardName = editText.getText().toString();

            //////RESİMİ SQLİTE YE KAYDETMEK İÇİN BİTMAP'DEN VERİYE DÖNÜŞTÜRMEMİZ GEREK ONU YAPIYORUZ .
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

            Bitmap smallImage = makeSmallerPhoto(selectedImage , 300); /// yazdığımız metodu kullanarak fotuyu küçültüp başka bir değişkene attık.

            smallImage.compress(Bitmap.CompressFormat.PNG , 50 , outputStream);
            byte[] byteList= outputStream.toByteArray();


            try {

                database.execSQL("CREATE TABLE IF NOT EXISTS card (id INTEGER PRIMARY KEY , isim VARCHAR , resim BLOB) ");

                String sqlString = "INSERT INTO card (isim , resim) VALUES (? , ?)";    ////// kullanıcıdan aldığımız veriyi kullanmak için böyle yapıyoruz.
                SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);   ///Normal Stringi Sqlite Strinigne çeviriyor.
                sqLiteStatement.bindString(1,cardName);    /// burada ve aşağıda sırayla girdiğimiz bilgileri soru işareti bıraktığımız yerlere ekliyor.
                sqLiteStatement.bindBlob(2,byteList);
                sqLiteStatement.execute();

                Toast.makeText(getActivity().getApplicationContext(), "Başarılı", Toast.LENGTH_SHORT).show();

                NavDirections action = DetailFragmentDirections.actionDetailFragmentToListFragment();
                Navigation.findNavController(view).navigate(action);

            }catch (Exception e){
                Toast.makeText(getActivity().getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

        }


    }


            ///////////////////    GALERİ İŞLEMLERİ /////////////////////////////////////////////////////////////////////

    public void setPicF(View view){

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext() , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { // EĞER İZİN ALINMADIYSA
            ActivityCompat.requestPermissions(getActivity() , new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);             //İZİN AL
        }
        else {  ///İZİN ZATEN VERİLİYSE GALERİYE GİDİYORUZ.
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {    //İZİN İÇİN CEVAP VERİNCE NOLCAK

        if (requestCode==1){     /// İZİNE EVET DERSE NOLCAK. GALERİYE YOLLADIK YİNE.
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null ){ // EĞER Bİ FOTO SEÇERSE
            Uri imageData = data.getData();
            fotosecildi = true;

            try {           ///// FOTOYU DÖNÜŞTÜRDÜK FALAN SONRA İMAGEVİEWE ATADIK.

                if (Build.VERSION.SDK_INT >= 28){

                    ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver() , imageData);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedImage);
                }
                else {

                    selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageData);

                    imageView.setImageBitmap(selectedImage);


                }



            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    public Bitmap makeSmallerPhoto(Bitmap image , int maximumSize){    /// fotonun boyutlarını ayarladık.

        int width = image.getWidth();
        int height = image.getHeight();

        float bolum = (float) width / (float) height;

        if (bolum > 1){
            width = maximumSize;
            height = (int) (width / bolum);
        }
        else {
            height = maximumSize;
            width = (int) (height * bolum);
        }

        return Bitmap.createScaledBitmap(image , width , height , true);

    }

    }
