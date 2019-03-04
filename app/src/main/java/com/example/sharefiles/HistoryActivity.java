package com.example.sharefiles;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.FileListItem;
import com.github.angads25.filepicker.model.MarkedItemList;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.tml.sharethem.sender.SHAREthemActivity;
import com.tml.sharethem.sender.SHAREthemService;

import java.util.ArrayList;
import java.util.List;

import static com.tml.sharethem.utils.Utils.DEFAULT_PORT_OREO;

public class HistoryActivity extends AppCompatActivity implements View.OnLongClickListener {
    HistoryAdapter myadapter;
    private DialogSelectionListener callbacks;
    List<HistoryModel> historyModels = new ArrayList<>();
    RecyclerView recyclerView;
    TextView notextView;
    public boolean is_Long_click_mode = false;
    Toolbar m_toolbar;
    AdView LH_top,LH_bottom;
    private ProgressDialog pDialog;
    DatabaseHelper db;
    public boolean selectAllFlag;
    ArrayList<HistoryModel> tempArray = new ArrayList<>();
    int counter = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_list);
        MobileAds.initialize(this,getString(R.string.ApAdId));
        AdRequest adRequest=new AdRequest.Builder().build();
        LH_top=findViewById(R.id.LH_topAd);
        LH_bottom=findViewById(R.id.LH_bottomAd);
        LH_top.loadAd(adRequest);
        LH_bottom.loadAd(adRequest);
        recyclerView = findViewById(R.id.recycler_view);
        notextView = findViewById(R.id.empty_notes_view);
        m_toolbar = (Toolbar) findViewById(R.id.toolbar);
        m_toolbar.setTitle("HISTORY");
        setSupportActionBar(m_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        selectAllFlag = false;
        db = new DatabaseHelper(this);

        historyModels.addAll(db.getAllNotes());

        myadapter = new HistoryAdapter(this, historyModels);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(myadapter);
        toggleEmptyNotes();
/*
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));*/
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (historyModels.size() > 0) {
            notextView.setVisibility(View.GONE);
        } else {
            notextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:

                is_Long_click_mode = false;
                updateArray(tempArray);
                myadapter.booleanArray.clear();
                m_toolbar.getMenu().clear();
                m_toolbar.inflateMenu(R.menu.history_menu);
                m_toolbar.setTitle("History");
                //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                counter = 0;
                tempArray.clear();
                //       db.deleteAll();
                //     historyModels.clear();
                //   myadapter.notifyDataSetChanged();
                // toggleEmptyNotes();
                Toast.makeText(this, "History Clear", Toast.LENGTH_SHORT).show();
                break;
            case R.id.send:
                is_Long_click_mode = false;
                Sendfiles(tempArray);

                break;
            case R.id.edit:
                is_Long_click_mode = true;
                selectAllFlag = false;
                HistoryAdapter.DisSelectAll();
                Log.d("press", "working");
                m_toolbar.getMenu().clear();
                m_toolbar.inflateMenu(R.menu.history_edit_menu);
                m_toolbar.setTitle("0 item selected");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                myadapter.notifyDataSetChanged();
                break;
            case R.id.selectAll:
                if (!selectAllFlag) {
                    SelectAll();
                } else {
                    disSelect();
                }

                break;
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void disSelect() {
        counter = 0;
        tempArray.clear();
        HistoryAdapter.DisSelectAll();
        myadapter.notifyDataSetChanged();
        selectAllFlag = false;
        changeTitleText();
    }

    private void SelectAll() {
        counter = 0;
        tempArray.clear();
        HistoryAdapter.selectAll();
        if (historyModels != null) {
            tempArray.addAll(historyModels);
        } else {
            Toast.makeText(this, "na kr yar", Toast.LENGTH_SHORT).show();
        }
        counter = historyModels.size();
        Log.d("History", "counter" + counter);
        myadapter.notifyDataSetChanged();
        selectAllFlag = true;
        changeTitleText();
        Log.d("historyActivity", "SelectAll");
    }


    private void deleteNote(int position) {
        // deleting the note from db
        Log.d("position is", "check this" + historyModels.get(position));
        db.deleteNote(historyModels.get(position));

        // removing the note from the list
        historyModels.remove(position);
        myadapter.notifyItemRemoved(position);
        toggleEmptyNotes();
    }

    public void updateArray(ArrayList<HistoryModel> arrayList) {

        for (HistoryModel history : arrayList) {
            //movieArrayList.remove(movie);
            db.deleteNote(history);

            // removing the note from the list
            historyModels.remove(history);

        }
        myadapter.notifyDataSetChanged();
        toggleEmptyNotes();
    }

    public void Sendfiles(ArrayList<HistoryModel> arrayList) {
        MarkedItemList.clearSelectionList();
        for (HistoryModel history : arrayList) {

            FileListItem parent = new FileListItem();
            parent.setLocation(history.getUrlpath());
            MarkedItemList.addSelectedItem(parent);
        }

        String file[] = MarkedItemList.getSelectedPaths();

        //Toast.makeText(this, builder.toString(), Toast.LENGTH_SHORT).show();
        this.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (null == files || files.length == 0) {
                    Toast.makeText(HistoryActivity.this, "Select at least one file to start Share Mode", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
                intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, files);

                intent.putExtra(SHAREthemService.EXTRA_PORT, DEFAULT_PORT_OREO);

                intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
                startActivity(intent);
                finish();
            }
        });


        if (callbacks != null) {
            callbacks.onSelectedFilePaths(file);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onLongClick(View view) {
        is_Long_click_mode = true;
        Log.d("press", "working");
        m_toolbar.getMenu().clear();
        m_toolbar.inflateMenu(R.menu.history_edit_menu);
        m_toolbar.setTitle("0 item selected");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myadapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (is_Long_click_mode) {
            is_Long_click_mode = false;
            myadapter.booleanArray.clear();
            counter = 0;
            tempArray.clear();
            selectAllFlag = false;
            m_toolbar.getMenu().clear();
            m_toolbar.inflateMenu(R.menu.history_menu);
            m_toolbar.setTitle("History");
            //toolbar_textview.setText("Long Click Listener");
            myadapter.notifyDataSetChanged();

            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            selectAllFlag = false;
            super.onBackPressed();
        }
    }

    public void updateCheckedData(View view, int position) {
        if (((CheckBox) view).isChecked()) {
            tempArray.add(historyModels.get(position));
            //String url=historyModels.get(position).getUrlpath();
            //Log.d("url","thisisurl"+url);
            FileListItem parent = new FileListItem();
            parent.setLocation(historyModels.get(position).getUrlpath());
            counter++;
            changeTitleText();
        } else {
            tempArray.remove(historyModels.get(position));
            counter--;
            changeTitleText();
        }
    }


    void changeTitleText() {
        if (counter == 0) {
            //toolbar_textview.setText("0 item selected");
            m_toolbar.setTitle("0 item selected");
        } else {
            m_toolbar.setTitle(counter + " item selected");
            //toolbar_textview.setText(counter+" item selected");
        }
    }


    public void setDialogSelectionListener(DialogSelectionListener callbacks) {
        this.callbacks = callbacks;
    }
}
