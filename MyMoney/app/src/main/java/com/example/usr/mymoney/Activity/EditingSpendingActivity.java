package com.example.usr.mymoney.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.usr.mymoney.DataBase.DbHelper;
import com.example.usr.mymoney.R;
import com.example.usr.mymoney.Adapter.RVAdapterSection;
import com.example.usr.mymoney.Adapter.RecyclerItemClickListener;
import com.example.usr.mymoney.Entity.Section;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class EditingSpendingActivity extends AppCompatActivity implements View.OnClickListener {

    protected List<Section> sections;
    ImageButton btn_add_spend_section;
    DateFormat df;

    RVAdapterSection adapter;

    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_spending);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");

        dbHelper = new DbHelper(this);

        sections = dbHelper.getAllSectionSpending();

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_editing_spending);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RVAdapterSection(sections);

        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {

                        LayoutInflater li = LayoutInflater.from(getApplicationContext());
                        View promptsView = li.inflate(R.layout.name_section, null);

                        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(EditingSpendingActivity.this);

                        mDialogBuilder.setView(promptsView);

                        final EditText edit_name_section = (EditText) promptsView.findViewById(R.id.edit_new_name);
                        final String oldName = sections.get(position).nameSection.toString();
                        edit_name_section.setText(sections.get(position).getNameSection());
                        edit_name_section.setTextColor(Integer.valueOf(R.color.colorPrimaryDark));

                        mDialogBuilder
                                .setCancelable(true)
                                .setPositiveButton("Ок",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                String newName = edit_name_section.getText().toString();
                                                if (!newName.equals("") && newName != oldName) {
                                                    Section updateSection = sections.get(position);
                                                    updateSection.setNameSection(newName);
                                                    adapter.updateItem(updateSection, position);
                                                    dbHelper.updateSpendingSection(oldName, newName);
                                                }
                                            }
                                        })
                                .setNegativeButton("Удалить",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                adapter.removeItem(position);
                                                dbHelper.deleteSpendingSection(oldName);
                                                dialog.cancel();
                                            }
                                        });

                        AlertDialog alertDialog = mDialogBuilder.create();

                        alertDialog.show();

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        btn_add_spend_section = (ImageButton) findViewById(R.id.btn_add_spend_section);
        btn_add_spend_section.setOnClickListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_spend_section:
                LayoutInflater li = LayoutInflater.from(this);
                View promptsView = li.inflate(R.layout.name_section, null);

                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

                mDialogBuilder.setView(promptsView);

                final EditText edit_name_section = (EditText) promptsView.findViewById(R.id.edit_new_name);

                mDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Добавить",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        final String nameSection = edit_name_section.getText().toString();
                                        if (!nameSection.equals("")) {

                                            int newPosition = sections.size();
                                            Section newSection = new Section(newPosition, nameSection, R.drawable.star, "", "");
                                            adapter.addItem(newSection, newPosition);
                                            dbHelper.addSpendingSection(newSection);
                                        }
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
                break;
        }
    }

}
