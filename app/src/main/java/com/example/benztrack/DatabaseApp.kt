package com.example.benztrack
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DatabaseApp(val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "DBExample"
        private const val DATABASE_VERSION = 1
        private const val TABLE_TYPES = "TypesTable"
        private const val TABLE_MAKES = "MakesTable"
        private const val TABLE_YEARS = "YearsTable"
        private const val TABLE_LISTOFCARS = "CarsTable"

        private const val COLUMN_TYPES = "types"
        private const val COLUMN_MAKES = "makes"
        private const val COLUMN_YEARS = "years"

        private const val COLUMN_MODEL = "model"
        private const val COLUMN_MAINTENANCE = "manutenzione"
        private const val COLUMN_BOLLO = "bollo"
        private const val COLUMN_ASSICURAZIONE = "assicurazione"
        private const val COLUMN_SPEND_ON_FUEL = "benzina"
        private const val COLUMN_FUEL_COST="CostoBenzina"
        private const val COLUMN_LAT = "latitudine"
        private const val COLUMN_LON = "longitudine"
        private const val COLUMN_KM = "KM"
        private const val COLUMN_DATE = "data"
        private const val COLUMN_CONSUME = "consumoCO2"
        private const val COLUMN_CO2 ="CO2"
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_TYPES($COLUMN_TYPES TEXT PRIMARY KEY)")
            db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_MAKES($COLUMN_MAKES TEXT PRIMARY KEY)")
            db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_YEARS($COLUMN_YEARS TEXT PRIMARY KEY)")


            db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_LISTOFCARS ($COLUMN_MODEL TEXT PRIMARY KEY, $COLUMN_CO2 REAL)")




            insertInitialValues(COLUMN_TYPES,TABLE_TYPES,"types", db)
            insertInitialValues(COLUMN_MAKES,TABLE_MAKES,"makes", db)
            insertInitialValues(COLUMN_YEARS,TABLE_YEARS,"years", db)

        } catch (e: Exception) {
            Log.e("DatabaseApp", "Error during database initialization: ${e.message}")
        }
    }

    private fun insertInitialValues(colonna:String ,tabella:String, item: String, db: SQLiteDatabase) {
        scope.launch {
            insertValue(colonna, tabella, item, db)
        }
    }

    private suspend fun insertValue(colonna:String ,tabella:String,item: String, db: SQLiteDatabase) {
        val requestUrl = "https://car-data.p.rapidapi.com/cars/$item"

        try {
            // Apri il database
            db.beginTransaction()

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(requestUrl)
                .get()
                .addHeader("X-RapidAPI-Key", "0dc8f0efbdmsha7a5bc2f50d8e7dp1a3ad3jsn088f99490027")
                .build()
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            if (response.isSuccessful) {
                val responseBody = response.body?.string()

                val responseArray = responseBody
                    ?.removeSurrounding("[", "]")
                    ?.replace("\"", "")
                    ?.split(",")
                    ?.toTypedArray()

                responseArray?.let {
                    for (data in responseArray) {
                        Log.e("DatabaseApp", "Data prima di inserire: $data")
                        val values = ContentValues().apply {
                            put(colonna, data) // Utilizza il nome dell'elemento come nome della colonna
                        }
                        Log.e("DatabaseApp", "Data inserted : $data")
                        db.insertWithOnConflict(tabella, null, values, SQLiteDatabase.CONFLICT_IGNORE)
                    }
                }
                // Imposta la transazione come completata
                db.setTransactionSuccessful()
            } else {
                throw IOException("Error in request: ${response.code}")
            }
        } catch (e: IOException) {
            Log.e("DatabaseApp", "Connection error: ${e.message}")
        } finally {
            // Chiudi la transazione e il database
            db.endTransaction()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}


    fun insertCar(model:String,CO2:Double){
        val db= this.writableDatabase
        val data= ContentValues()
        data.put(COLUMN_MODEL, model)
        data.put(COLUMN_CO2,CO2)
        db.insert(TABLE_LISTOFCARS, null, data)
        db.close()

    }

    fun insertValueforCar(column: String, table: String, value: Double) {
        val db = this.writableDatabase
        val data = ContentValues()
        data.put(column, value)
        data.put(COLUMN_DATE, getCurrentDateTime()) // Inserisci la data formattata come stringa
        val tableName = "\"$table\""  // Aggiungi virgolette al nome della tabella
        db.insert(tableName, null, data)
        db.close()
    }

    fun insertLocationForVehicle(tableName: String, latitude: Double, longitude: Double) {
        val db = this.writableDatabase
        val data = ContentValues()
        data.put(COLUMN_LAT, latitude)
        data.put(COLUMN_LON, longitude)
        data.put(COLUMN_DATE, getCurrentDateTime()) // Inserisci la data formattata come stringa
        db.insert("\"$tableName\"", null, data)
        db.close()
    }

    fun getLocationsForVehicle(tableName: String): List<Pair<Double, Double>> {
        val locations = mutableListOf<Pair<Double, Double>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_LAT, $COLUMN_LON FROM \"$tableName\" WHERE $COLUMN_LAT IS NOT NULL AND $COLUMN_LON IS NOT NULL", null)

        cursor.use {
            while (it.moveToNext()) {
                val latitude = it.getDouble(it.getColumnIndexOrThrow(COLUMN_LAT))
                val longitude = it.getDouble(it.getColumnIndexOrThrow(COLUMN_LON))
                locations.add(Pair(latitude, longitude))
            }
        }
        return locations
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) // Formato della data e dell'ora
        val date = Date()
        return dateFormat.format(date)
    }

    fun createTableInfoVehicle(tablename:String ){
        val db = this.writableDatabase

        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS \"$tablename\" (" +
                    "$COLUMN_BOLLO REAL, " +
                    "$COLUMN_ASSICURAZIONE REAL, " +
                    "$COLUMN_SPEND_ON_FUEL REAL, " +
                    "$COLUMN_FUEL_COST REAL, " +
                    "$COLUMN_MAINTENANCE REAL, " +
                    "$COLUMN_CONSUME REAL, " +
                    "$COLUMN_KM REAL, " +
                    "$COLUMN_LAT TEXT, " +
                    "$COLUMN_LON TEXT, " +
                    "$COLUMN_DATE TEXT)")

        } catch (e: Exception) {
            Log.e("DatabaseApp", "Error creating table $tablename: ${e.message}")
        } finally {
            db.close()
        }
    }

    fun getAllData(tableName: String): ArrayList<String> {
        val dataList = ArrayList<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM \"$tableName\"", null)

        cursor.use {
            while (it.moveToNext()) {
                val rowData = StringBuilder()
                for (i in 0 until it.columnCount) {
                    //val columnName = it.getColumnName(i)
                    val columnValue = it.getString(i)
                    rowData.append(columnValue)
                }
                dataList.add(rowData.toString())
            }
        }

        Log.d("DatabaseApp", "Data from $tableName: $dataList")

        return dataList
    }
    fun getDataColumnDouble(columnName: String, tableName: String): ArrayList<Double> {
        val dataList = ArrayList<Double>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT \"$columnName\" FROM \"$tableName\"", null)

        cursor.use {
            while (it.moveToNext()) {
                for (i in 0 until it.columnCount) {
                    if (!it.isNull(i)) { // Verifica se il valore non è nullo
                        val columnValue = it.getDouble(i)
                        dataList.add(columnValue)
                    }
                }
            }
        }

        Log.d("DatabaseApp", "Data from $tableName: $dataList")

        return dataList
    }

    fun getDataColumnString(columnName: String, tableName: String): ArrayList<String> {
        val dataList = ArrayList<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT \"$columnName\" FROM \"$tableName\"", null)

        cursor.use {
            while (it.moveToNext()) {
                for (i in 0 until it.columnCount) {
                    if (!it.isNull(i)) { // Verifica se il valore non è nullo
                        val columnValue = it.getString(i)
                        dataList.add(columnValue)
                    }
                }
            }
        }

        Log.d("DatabaseApp", "Data from $tableName: $dataList")

        return dataList
    }


    fun getCO2ofVeichle (veichle:String):Double{
        val db = readableDatabase

        val query= "SELECT $COLUMN_CO2 FROM $TABLE_LISTOFCARS WHERE $COLUMN_MODEL= ? "

        val cursor = db.rawQuery(query, arrayOf(veichle))
        var co2 = 0.0 // Default value in case no CO2 value is found

        cursor.use { // Use `use` to close the cursor automatically after use
            if (it.moveToFirst()) {
                co2 = it.getDouble(it.getColumnIndexOrThrow(COLUMN_CO2))

            }
        }

        db.close()
        return co2
    }




    fun getLastKmValue(tableName: String): Double? {
        val query = "SELECT $COLUMN_KM FROM $tableName "
        var lastKm: Double? = null

        val db = readableDatabase
        val cursor = db.rawQuery(query, null)

        cursor.use { // Usa il blocco use per assicurare la chiusura automatica del cursore
            if (cursor.moveToLast()) {
                lastKm = cursor.getDouble(cursor.getColumnIndexOrThrow("KM"))
            }
        } // Il cursore verrà chiuso automaticamente qui

        db.close()

        return lastKm
    }



    fun getSumColumn(columnName:String, tableName: String): Double {
        val dati= getDataColumnDouble(columnName,tableName)
        var sum = 0.0
        for (data in dati) {
            if (data != null) {
                sum += data
            }
        }
        Log.d("DatabaseApp", " $sum")

        return sum
    }


    fun getLastConsumption(vehicleName: String): Double? {
        val db = readableDatabase
        val query = "SELECT $COLUMN_CONSUME FROM $vehicleName ORDER BY $COLUMN_DATE DESC LIMIT 1"
        var lastConsumption: Double? = null

        val cursor = db.rawQuery(query, null)
        cursor.use {
            if (it.moveToFirst()) {
                lastConsumption = it.getDouble(it.getColumnIndexOrThrow(COLUMN_CONSUME))
            }
        }

        db.close()
        return lastConsumption
    }




}
