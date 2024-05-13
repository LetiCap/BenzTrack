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
        private const val COLUMN_ID = "id"
        private const val COLUMN_BOLLO = "bollo"
        private const val COLUMN_ASSICURAZIONE = "assicurazione"
        private const val COLUMN_BENZINA = "benzina"
        private const val COLUMN_LAT = "latitudine"
        private const val COLUMN_LON = "longitudine"
        private const val COLUMN_KM = "KM"
        private const val COLUMN_DATE = "data"
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_TYPES($COLUMN_TYPES TEXT PRIMARY KEY)")
            db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_MAKES($COLUMN_MAKES TEXT PRIMARY KEY)")
            db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_YEARS($COLUMN_YEARS TEXT PRIMARY KEY)")
            db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_LISTOFCARS($COLUMN_ID INTEGER PRIMARY KEY)")



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


    fun insertCar(id:Int){
        val db= this.writableDatabase
        val data= ContentValues()
        data.put(COLUMN_ID, id)
        db.insert(TABLE_LISTOFCARS, null, data)
        db.close()

    }

    fun insertValueforCar(id:Int, column:String, table:String , value:Int){
        val db = this.writableDatabase
        val data = ContentValues()
        data.put(column, value)
        data.put("dateColumn", getCurrentDateTime()) // Aggiungi la data corrente
        val tableName = "\"$table\""  // Aggiungi virgolette al nome della tabella
        db.insert(tableName, null, data)
        db.close()
    }

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy ss:mm:HH", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    fun createTableInfoVehicle(id:String ){
        val db = this.writableDatabase

        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS \"$id\" (" +
                    "$COLUMN_BOLLO INTEGER " +
                    "$COLUMN_ASSICURAZIONE INTEGER " +
                    "$COLUMN_BENZINA INTEGER " +
                    "$COLUMN_KM INTEGER " +
                    "$COLUMN_LAT TEXT " +
                    "$COLUMN_LON TEXT" +
                    "$COLUMN_DATE TEXT)")

        } catch (e: Exception) {
            Log.e("DatabaseApp", "Error creating table $id: ${e.message}")
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
    fun getDataColumn(columnName: String, tableName: String): ArrayList<Int> {
        val dataList = ArrayList<Int>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT \"$columnName\" FROM \"$tableName\"", null)

        cursor.use {
            while (it.moveToNext()) {
                for (i in 0 until it.columnCount) {
                    if (!it.isNull(i)) { // Verifica se il valore non è nullo
                        val columnValue = it.getInt(i)
                        dataList.add(columnValue)
                    }
                }
            }
        }

        Log.d("DatabaseApp", "Data from $tableName: $dataList")

        return dataList
    }

    fun getDataWithDateColumn(columnName: String, dateColumnName: String, tableName: String): ArrayList<Pair<Int, String>> {
        val dataList = ArrayList<Pair<Int, String>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT \"$columnName\", \"$dateColumnName\" FROM \"$tableName\"", null)

        cursor.use {
            while (it.moveToNext()) {
                val columnValue = it.getInt(0)
                val dateValue = it.getString(1)
                dataList.add(Pair(columnValue, dateValue))
            }
        }

        Log.d("DatabaseApp", "Data with date from $tableName: $dataList")

        return dataList
    }


    fun getSumColumn(columnName:String, tableName: String): Int {
        val dati= getDataColumn(columnName,tableName)
        var sum = 0
        for (data in dati) {
            if (data != null) {
                sum += data
            }
        }
        Log.d("DatabaseApp", " $sum")

        return sum
    }


}
