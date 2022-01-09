package com.example.syncadapter;

public class Name {
    private String name;
    private int status;
    private String telefono;

    public Name(String name,String telefono, int status){
        this.name=name;
        this.status=status;
        this.telefono=telefono;
    }

    public String getName() {
        return name;
    }
    public String getTelefono(){return telefono;}

    public int getStatus() {
        return status;
    }


}
