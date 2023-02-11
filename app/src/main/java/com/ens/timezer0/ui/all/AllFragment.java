package com.ens.timezer0.ui.all;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.ens.timezer0.NavigActivity;
import com.ens.timezer0.R;
import com.ens.timezer0.basedonnes.BaseContract;
import com.ens.timezer0.dbtest;
import com.ens.timezer0.ui.AjouterTacheActivity;
import com.ens.timezer0.ui.TacheActivity;
import com.ens.timezer0.utils.TacheCursorAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AllFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{

    private AllViewModel allViewModel;

    private static final int TACHE_LOADER_ALL = 1;
    private static final int TACHE_LOADER_TODAY = 2;
    TacheCursorAdapter mCursorAdapter;

    View vieww;
    ListView listView;
    Boolean today;
    TextView textEmptySubtitle;
    MaterialButton delete_past_btn,btn_today,btn_all;

    String firstTime;

    private void deletePastTache() {

        String where = "notification = 2";
        int rowsDeleted = (int) getActivity().getApplicationContext().getContentResolver().delete(BaseContract.InfoBase.CONTENT_URI, where, null);
        if (rowsDeleted == 0) {
            Toast.makeText(getActivity().getApplicationContext(), "aucun a supprimer!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "la suppression de " + Integer.toString(rowsDeleted) + " a ete bien supprimer",
                    Toast.LENGTH_SHORT).show();
        }

        mCursorAdapter.swapCursor(null);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("tous les taches");
        today = false;
        textEmptySubtitle.setText("ajouter une tache");
        ((AppCompatActivity)getActivity()).getSupportLoaderManager().initLoader(TACHE_LOADER_ALL, null, this);

    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("tu es sure que vous voullez supprimer tous les taches?");
        builder.setCancelable(false);
        builder.setTitle("DELETE");
        builder.setIcon(android.R.drawable.ic_menu_delete);
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePastTache();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        allViewModel =
                ViewModelProviders.of(this).get(AllViewModel.class);
        View root = inflater.inflate(R.layout.fragment_all, container, false);
        final TextView textView = root.findViewById(R.id.empty_title_text);
        vieww = (View) root.findViewById(R.id.empty_view);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       startActivity(new Intent(getActivity(), AjouterTacheActivity.class));
                                   }
                               });

        listView = (ListView) root.findViewById(R.id.list);
        listView.setEmptyView(vieww);
        delete_past_btn = (MaterialButton) root.findViewById(R.id.btn_delete_past);
        btn_today=(MaterialButton) root.findViewById(R.id.btn_today);
        btn_all = (MaterialButton) root.findViewById(R.id.btn_all);
        textEmptySubtitle = (TextView) root.findViewById(R.id.empty_subtitle_text);

        mCursorAdapter = new TacheCursorAdapter(getActivity().getApplicationContext(), null);
        listView.setAdapter(mCursorAdapter);
        delete_past_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCursorAdapter.swapCursor(null);
                today = false;
                textEmptySubtitle.setText("ajouter une taache");
                getActivity().getSupportLoaderManager().initLoader(TACHE_LOADER_ALL, null,AllFragment.this);
                LoaderManager lm = LoaderManager.getInstance(getActivity());
                lm.initLoader(TACHE_LOADER_ALL,null,AllFragment.this);
            }
        });
        btn_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCursorAdapter.swapCursor(null);
                today = true;
                textEmptySubtitle.setText("ajouter une tache");
                getActivity().getSupportLoaderManager().initLoader(TACHE_LOADER_TODAY, null, AllFragment.this);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TacheActivity.class);
                Uri currentTacheUri = ContentUris.withAppendedId(BaseContract.InfoBase.CONTENT_URI, l);
                intent.setData(currentTacheUri);
                startActivity(intent);
            }
        });

        allViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        mCursorAdapter.swapCursor(null);
      //  ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(" All tache");
        today = false;
        textEmptySubtitle.setText("ajouter une tache");
        ((AppCompatActivity)getActivity()).getSupportLoaderManager().initLoader(TACHE_LOADER_ALL, null, this);
    }



    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                BaseContract.InfoBase._ID,
                BaseContract.InfoBase.COLUMN_HEADING,
                BaseContract.InfoBase.COLUMN_DATE,
                BaseContract.InfoBase.COLUMN_TIME,
                BaseContract.InfoBase.COLUMN_NOTIFICATION,
                BaseContract.InfoBase.COLUMN_IMPO};
        switch (id) {
            case TACHE_LOADER_ALL:
                return new CursorLoader(getActivity().getApplicationContext(), BaseContract.InfoBase.CONTENT_URI, projection, null, null, null);

            case TACHE_LOADER_TODAY:
                Date dateObject = new Date();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd / MM / YYYY");
                String current_date_string = dateFormatter.format(dateObject);
                String selection = BaseContract.InfoBase.COLUMN_DATE + " =? ";
                String[] selectionArgs = new String[]{current_date_string};
                Log.i("Current date", current_date_string);
                return new CursorLoader(getActivity().getApplicationContext(), BaseContract.InfoBase.CONTENT_URI, projection, selection, selectionArgs, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
