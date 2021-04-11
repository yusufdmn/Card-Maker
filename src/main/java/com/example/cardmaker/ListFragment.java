package com.example.cardmaker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class ListFragment extends Fragment implements RecyclerAdaptor.OnNoteListener {

    RecyclerAdaptor recyclerAdaptor;
    SQLiteDatabase database;
    ArrayList<String> nameCards;
    ArrayList<Bitmap> picCards;
    public RecyclerView recyclerView;
    ImageButton addCardButton;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        picCards = new ArrayList<>();
        nameCards = new ArrayList<>();

        database = getActivity().openOrCreateDatabase("Cards" , MODE_PRIVATE , null);


        addCardButton = view.findViewById(R.id.addCardButtonId);
        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCardF(v);
            }
        });

        ////////////////////       RECYCLER

        getData();

        recyclerView = view.findViewById(R.id.recyclerViewId);
        recyclerAdaptor = new RecyclerAdaptor(nameCards , picCards , this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(recyclerAdaptor);



        super.onViewCreated(view, savedInstanceState);
    }


    /////////////////////////// RECYLER VİEW İTEMLERİNE BASINCA NOLACAK :

    @Override
    public void onNoteClick(int position, View view) {
        ListFragmentDirections.ActionListFragmentToDetailFragment action = ListFragmentDirections.actionListFragmentToDetailFragment(nameCards.get(position));
        action.setPosition(nameCards.get(position));
        action.setYeniMi(false);
        Navigation.findNavController(view).navigate(action);

    }
    ///////////////////////////////////////////////

    public void getData(){

        try {

            Cursor cursor = database.rawQuery("SELECT * FROM card", null);

            int nameIndex = cursor.getColumnIndex("isim");
            int idIndex = cursor.getColumnIndex("id");
            int resimIndex = cursor.getColumnIndex("resim");

            while (cursor.moveToNext()){
                nameCards.add(cursor.getString(nameIndex));

                byte[] bytes = cursor.getBlob(resimIndex);
                Bitmap bitmapResim = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                picCards.add(bitmapResim);

                System.out.println("id: " + cursor.getString(idIndex) + "isim: " + cursor.getString(nameIndex));


            }
            System.out.println("data : " + picCards.size());
            recyclerAdaptor.notifyDataSetChanged();   /// DEĞİŞİKLİKLERİ GÖSTER.
            cursor.close();
        }
        catch (Exception e){

        }

    }

    public void addCardF(View view){

        ListFragmentDirections.ActionListFragmentToDetailFragment action = ListFragmentDirections.actionListFragmentToDetailFragment("a");
        action.setYeniMi(true);
        Navigation.findNavController(view).navigate(action);

    }
}