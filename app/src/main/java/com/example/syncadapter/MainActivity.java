package com.example.syncadapter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.os.Bundle;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //Direccion del servidor web.
    public static final String URL_SAVE_NAME= "http://192.168.1.70/saveName.php";

    SQLiteDataHelper db;

    //Controladores
    Button btnSend;
    EditText etName,etNumber;
    ListView lvContactos;
    //Lista para almacenar todos los nombres
    private List<Name> names= new ArrayList<>();
    //1 significa que los datos estan sincronizados y 0 que no.
    public static final int name_synced_with_server=1;
    public static final int name_not_synced_with_server=0;
    //un receptor para saber si los datos estan sincronizados o no
    public static final String data_saved_broadcast= "com.example.datasaved";
    //broadcast receiver para saber el status de la sincronización.
    private BroadcastReceiver broadcastReceiver;
    //adapterobject para el listView
    private NameAdapter nameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=new SQLiteDataHelper(this);

        //linkeo de controladores
        btnSend=(Button)findViewById(R.id.btnSend);
        etName=(EditText) findViewById(R.id.etName);
        etNumber=(EditText)findViewById(R.id.etNumber);
        lvContactos=(ListView) findViewById(R.id.lvContactos);

        //Oyente del boton send
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNameToServer();
            }
        });
        //llamamos al método para cargar todos los almacenamiento.
        loadNames();
        //Actualización del Broadcast para sincronizar el status
        broadcastReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadNames();
            }
        };
        //Se registran los broadcast para actualizar el status de la sincronización.
        registerReceiver(broadcastReceiver, new IntentFilter(data_saved_broadcast));
    }

    private void loadNames()
    {
        names.clear();
        Cursor cursor= db.getNames();
        if (cursor.moveToFirst())
        {
            do {
                Name name= new Name(
                        cursor.getString(cursor.getColumnIndex(SQLiteDataHelper.column_name)),
                        cursor.getString(cursor.getColumnIndex(SQLiteDataHelper.column_phone)),
                        cursor.getInt(cursor.getColumnIndex(SQLiteDataHelper.column_status))
                );
                names.add(name);

            }while (cursor.moveToNext());
        }
        nameAdapter = new NameAdapter(this,R.layout.names,names);
        lvContactos.setAdapter(nameAdapter);
    }
    //Actualiza la lista
    private void refreshList()
    {
        nameAdapter.notifyDataSetChanged();
    }
    private void saveNameToServer()
    {
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Saving me..");
        progressDialog.show();

        final String name= etName.getText().toString().trim();
        final String telefono= etNumber.getText().toString().trim();
        Toast.makeText(getApplicationContext(),name,Toast.LENGTH_LONG).show();

        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL_SAVE_NAME,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        saveNameToLocalStorage(name,telefono, name_synced_with_server);
                    } else {
                        saveNameToLocalStorage(name,telefono, name_not_synced_with_server);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                saveNameToLocalStorage(name,telefono,name_not_synced_with_server);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=  new HashMap<>();
                params.put("name",name);
                params.put("telefono",telefono);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
    //Para guardar el nombre del almacenamiento local
    private void saveNameToLocalStorage(String name,String telefono, int status)
    {
        etName.setText("");
        etNumber.setText("");
        db.addName(name,telefono,status);
        Name n = new Name(name,telefono,status);
        names.add(n);
        refreshList();
    }
}