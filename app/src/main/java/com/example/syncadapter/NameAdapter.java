package com.example.syncadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.List;

public class NameAdapter  extends ArrayAdapter<Name> {


    private  List<Name> names;
    private Context context;

    public NameAdapter(Context context, int resource, List<Name> names) {
        super(context, resource,names);
        this.context= context;
        this.names=names;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Para obtener los items
        View listViewItem= inflater.inflate(R.layout.names,null, true);
        TextView textViewName=(TextView) listViewItem.findViewById(R.id.tvName);
        ImageView imageView=(ImageView)listViewItem.findViewById(R.id.imgStatus);

        //Obteniendo el nombre actual
        Name name= names.get(position);
        //Colocando el nombre en el textView
        textViewName.setText(name.getName()+", "+ name.getTelefono());
        //Para asignar el icono del registro de acuerdo a si está sincronizado o no.
        if (name.getStatus()==0)
        {
            imageView.setBackgroundResource(R.drawable.stopwatch);
        }else
        {
            imageView.setBackgroundResource(R.drawable.success);
        }
        return listViewItem;

    }
}
