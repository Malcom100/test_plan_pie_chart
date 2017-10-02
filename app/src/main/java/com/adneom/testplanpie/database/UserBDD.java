package com.adneom.testplanpie.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.adneom.testplanpie.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gtshilombowanticale on 04-08-16.
 */
public class UserBDD {

    private static final int VERSION_BDD = 4;
    private static final String NOM_BDD = "usersTest.db";

    private static final String TABLE_USERS = "table_users";
    private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;
    private static final String COL_EMAIL = "EMAIL";
    private static final int NUM_COL_EMAIL = 1;
    private static final String COL_DATE = "DATE";
    private static final int NUM_COL_DATE = 2;

    private SQLiteDatabase bdd;

    private MyBaseSQLite myBaseSQLite;

    public UserBDD(Context context){
        myBaseSQLite =  new MyBaseSQLite(context, NOM_BDD,null,VERSION_BDD);
    }

    public void open(){
        //on ouvre la BDD en écriture
        bdd = myBaseSQLite.getWritableDatabase();
    }

    public void close(){
        //on ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }

    public long insertLivre(User user){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues contentValues = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        contentValues.put(COL_EMAIL, user.getEmail());
        contentValues.put(COL_DATE, user.getDate().toString());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_USERS, null, contentValues);
    }

    public int updateLivre(int id, User user){
        //La mise à jour d'un livre dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simplement préciser quel livre on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, user.getEmail());
        values.put(COL_DATE, user.getDate().toString());
        return bdd.update(TABLE_USERS, values, COL_ID + " = " + id, null);
    }

    public int removeLivreWithID(int id){
        //Suppression d'un livre de la BDD grâce à l'ID
        return bdd.delete(TABLE_USERS, COL_ID + " = " + id, null);
    }

    public User getLivreWitheMAIL(String email){
        //Récupère dans un Cursor les valeurs correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)

        Cursor c = bdd.query(TABLE_USERS, new String[] {COL_ID, COL_EMAIL, COL_DATE}, COL_EMAIL + " LIKE \"" + email +"\"", null, null, null, null);
        return cursorToUser(c);
    }

    private User cursorToUser(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un livre
        User u = new User();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        u.setId(c.getInt(NUM_COL_ID));
        Log.i("Adneom"," id is"+c.getInt(NUM_COL_ID));
        u.setEmail(c.getString(NUM_COL_EMAIL));
        //u.setDate(c.getString(NUM_COL_DATE));
        u.setDate(null);
        //On ferme le cursor
        c.close();

        //On retourne le user
        return u;
    }

    public List<User> getAllUsers(){
        List<User> users = new ArrayList<User>();
        JSONArray jsonArrayUsers = null;

        Cursor cursor = bdd.query(TABLE_USERS, new String[]{COL_ID, COL_EMAIL, COL_DATE},null,null,null,null,null);

        if(cursor.moveToFirst()){
            jsonArrayUsers = new JSONArray();
            do{
                User us = new User();
                us.setId(Integer.parseInt(cursor.getString(NUM_COL_ID)));
                us.setEmail(cursor.getString(NUM_COL_EMAIL));
                JSONObject jsonObjectUser = new JSONObject();
                try {
                    jsonObjectUser.put("ID",us.getId());
                    jsonObjectUser.put("EMAIL",us.getEmail());
                    jsonObjectUser.put("DATE",new Date());
                    //jsonArrayUsers.put(jsonObjectUser);

                    JSONObject obj = new JSONObject();
                    obj.put("ID",us.getId());
                    obj.put("USER",jsonObjectUser);
                    jsonArrayUsers.put(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                users.add(us);
            }while (cursor.moveToNext());
        }

        if(jsonArrayUsers != null)
            Log.i("Adneom"," my json array : "+jsonArrayUsers.toString());

        cursor.close();
        return users;
    }

    public int getUsersCount() {
        String countQuery = "SELECT * FROM " + TABLE_USERS;
        Cursor cursor = bdd.rawQuery(countQuery,null);
        int size = cursor.getCount();
        cursor.close();

        return size;
    }
}
