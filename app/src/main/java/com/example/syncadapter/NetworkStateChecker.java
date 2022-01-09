package com.example.syncadapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkStateChecker extends BroadcastReceiver {
    private Context context;
    private SQLiteDataHelper db;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        db= new SQLiteDataHelper(context);

        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo actionNetwork = cm.getActiveNetworkInfo();

        //Si existe la coneción.
        if (actionNetwork != null)
        {
            //Si esta conectada a wifi o datos.
            if (actionNetwork.getType() == ConnectivityManager.TYPE_WIFI || actionNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                //Obtiene todos los daatos no sincronizados
                Cursor cursor= db.getUnsyncedNames();
                if(cursor.moveToFirst())
                {
                    do {
                        //Guardar los datos no sincronizados
                        saveName(cursor.getInt(cursor.getColumnIndex(SQLiteDataHelper.column_id)),
                                cursor.getString(cursor.getColumnIndex(SQLiteDataHelper.column_name)),
                                cursor.getString(cursor.getColumnIndex(SQLiteDataHelper.column_phone))
                        );
                    } while (cursor.moveToNext());

                }
            }
        }
    }
    private void saveName(final int id, final String name,String telefono)
    {
        StringRequest stringRequest= new StringRequest(Request.Method.POST, MainActivity.URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                db.updateNameStatus(id, MainActivity.name_synced_with_server);
                                context.sendBroadcast(new Intent(MainActivity.data_saved_broadcast));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String > params= new HashMap<>();
                params.put("name", name);
                params.put("telefono",telefono);
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
}
