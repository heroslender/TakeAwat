package net.netne.kitlfre.takeawat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruno Martins
 */

public class dataBaseLocal extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "takeAwatDB.db";
    private static final String TABLE_PRODUTO = "produtos";
    private static final String TABLE_PEDIDO = "pedidos";
    public static final String COLUMN_PEDIDO_QUANT = "quantidade";
    public static final String COLUMN_PRODUTO_COD = "cod_produto";
    public static final String COLUMN_PRODUTO_DESIGNACAO = "designacao";
    public static final String COLUMN_PRODUTO_PRECO = "preco";
    public static final String COLUMN_PRODUTO_DIA = "dia";

    public dataBaseLocal(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public dataBaseLocal(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEDIDO);
        onCreate(db);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_PRODUTO + "(" + COLUMN_PRODUTO_COD + " INTEGER PRIMARY KEY," + COLUMN_PRODUTO_DESIGNACAO
                + " TEXT," + COLUMN_PRODUTO_PRECO + " decimal(10,2)" + "," + COLUMN_PRODUTO_DIA + " date)";
        String CREATE_PEDIDO_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_PEDIDO + "(" + COLUMN_PRODUTO_COD + " INTEGER," + COLUMN_PEDIDO_QUANT
                + " decimal(10,2))";
        db.execSQL(CREATE_PRODUCTS_TABLE);
        db.execSQL(CREATE_PEDIDO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEDIDO);
        onCreate(db);
    }

    public void adicionaProduto(Produto produto) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUTO_COD, produto.getCod_produto() + "");
        values.put(COLUMN_PRODUTO_DESIGNACAO, produto.getDesignacao() + "");
        values.put(COLUMN_PRODUTO_PRECO, produto.getPreco() + "");
        values.put(COLUMN_PRODUTO_DIA, produto.getDia() + "");

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_PRODUTO, null, values);
        db.close();
    }

    public void adicionaPedido(pedido Pedido) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUTO_COD, Pedido.getP().getCod_produto() + "");
        values.put(COLUMN_PEDIDO_QUANT, Pedido.getQuant() + "");


        db.insert(TABLE_PEDIDO, null, values);
        db.close();
    }

    public Produto procuraProdutos(Integer cod_produto) {
        Produto produto = new Produto();
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUTO + " WHERE " + COLUMN_PRODUTO_COD + "='" + cod_produto + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            produto.setCod_produto(Integer.parseInt(cursor.getString(0)));
            produto.setDesignacao(cursor.getString(1));
            produto.setPreco(cursor.getString(2));
            produto.setDia(cursor.getString(3));
        }
        cursor.close();
        return produto;
    }

    public List<Produto> procuraTodosProdutos(String dia) {
        List<Produto> produtos = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUTO + " WHERE " + COLUMN_PRODUTO_DIA + "='" + dia + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Produto produto = new Produto();
                produto.setCod_produto(Integer.parseInt(cursor.getString(0)));
                produto.setDesignacao(cursor.getString(1));
                produto.setPreco(cursor.getString(2));
                produto.setDia(cursor.getString(3));
                produtos.add(produto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return produtos;
    }

    public List<String> procuraTodosProdutosDias() {
        List<String> dias = new ArrayList<>();
        String selectQuery = "SELECT  DISTINCT " + COLUMN_PRODUTO_DIA + " FROM " + TABLE_PRODUTO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                dias.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dias;
    }

    public List<pedido> procuraPedidos() {
        List<pedido> pedidos = new ArrayList<>();
        // Pesquisa SQL
        String selectQuery = "SELECT  * FROM " + TABLE_PEDIDO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // avanca pelas linhas, adicionando a lista
        if (cursor.moveToFirst()) {
            do {
                Integer cod_produto = Integer.parseInt(cursor.getString(0));
                Double quantidade = Double.parseDouble(cursor.getString(1));
                // Adiciona o produto a lista
                pedidos.add(new pedido(procuraProdutos(cod_produto), quantidade));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // Retorna a lista dos produtos
        return pedidos;
    }

    public boolean apagaTodosProdutos() {
        boolean result = false;
        String query = "Select * FROM " + TABLE_PRODUTO;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            // db.delete(String tableName, String whereClause, String[] whereArgs);
            // If whereClause is null, it will delete all rows.
            db.delete(TABLE_PRODUTO, null, null);
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public boolean apagaTodosPedidos() {
        boolean result = false;
        String query = "Select * FROM " + TABLE_PEDIDO;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            // db.delete(String tableName, String whereClause, String[] whereArgs);
            // If whereClause is null, it will delete all rows.
            db.delete(TABLE_PEDIDO, null, null);
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }
}

