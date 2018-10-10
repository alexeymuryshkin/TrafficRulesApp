package muryshkin.alexey.pdd.Data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.text.Charsets.UTF_8

/**
 * Created by 123 on 7/7/2016.
 */
class DataHolder {

    private var trafficRulesData: MutableMap<String, List<JSONObject>> = HashMap()

    private var answers: List<Int> = ArrayList()
    private var correctAnswers: List<Int> = ArrayList()
    private var result: Int = 0
    private var answered: Int = 0

    private var bTests: List<Test> = ArrayList()
    private var maxCorrect: List<Int> = ArrayList()
    private var test: JSONObject? = null

    private var chosenArticle: JSONObject? = null

    init {
        storeData(
                TRAFFIC_RULES,
                retrieveAssetsData(TRAFFIC_RULES, FILE_EXTENSION_JSON))
        storeData(
                TRAFFIC_SIGNS,
                retrieveAssetsData(TRAFFIC_SIGNS, FILE_EXTENSION_JSON))
    }

    private fun retrieveAssetsData(fileName: String, fileExtension: String): String? {

        val context: Context
        val assetManager = context.assets

        try {
            val inputStream = assetManager.open(
                    "$fileName.$fileExtension", MODE_PRIVATE)
            val size = inputStream.available()
            val buffer = ByteArray(size)

            inputStream.read(buffer)
            inputStream.close()

            return String(buffer, UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun storeData(key: String, data: String?): Boolean {
        if (data == null) {
            return false
        }

        try {
            val jsonArray = JSONArray(data)
            val dataArray = ArrayList<JSONObject>()

            for (i in 0 until jsonArray.length()) {
                dataArray.add(jsonArray.getJSONObject(i))
            }

            trafficRulesData[key] = dataArray
            return true
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return false
    }
}

private var dataHolder: DataHolder? = null

fun getDataHolder(): DataHolder {
    if (dataHolder == null)
        dataHolder = DataHolder()
    return dataHolder!!
}
