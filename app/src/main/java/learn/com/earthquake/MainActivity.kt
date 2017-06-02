package learn.com.earthquake

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var earthquakeAdapter:EarthquakeAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val earthquakeAsyncTask=EarthQuakeAsync()
        earthquakeAsyncTask.execute()

    }
    fun updateUI(earthquakeList:ArrayList<Event>){
        earthquakeAdapter=EarthquakeAdapter(this,earthquakeList)
        rvQuake.layoutManager=LinearLayoutManager(this)
        rvQuake.adapter=earthquakeAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search,menu)
        val menuItem=menu?.findItem(R.id.app_bar_search)
        val searchView:SearchView= menuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object :android.widget.SearchView.OnQueryTextListener, SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                earthquakeAdapter?.filter?.filter(newText)
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }
    inner class EarthQuakeAsync : AsyncTask<URL, Unit, ArrayList<Event>>() {
        val USGS_EARTHQUAKE_JSON="https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2014-01-01&endtime=2014-01-02"
        override fun doInBackground(vararg params: URL?): ArrayList<Event> {
            val url=createURL(USGS_EARTHQUAKE_JSON)
            var jsonString=""
            jsonString=makeHTTPRequest(url)
            return extractAtribute(jsonString)
        }

        private fun extractAtribute(jsonString: String): ArrayList<Event> {
            var quakeList=ArrayList<Event>()
            try {
                val baseJSONObject=JSONObject(jsonString)
                val featureArray=baseJSONObject.getJSONArray("features")
                for (i:Int in 0..featureArray.length()-1){
                    var earthquakeObject=featureArray.getJSONObject(i)
                    var earthquakeProperties=earthquakeObject.getJSONObject("properties")

                    //Extract feature
                    var place=earthquakeProperties.getString("place")
                    var time=earthquakeProperties.getLong("time")
                    var mag=earthquakeProperties.getDouble("mag")
                    quakeList.add(Event(place,time,mag))
                }
            }catch (ex:Exception){

            }
            return quakeList
        }

        private fun  makeHTTPRequest(url: URL): String {
            var jsonResponse=String()
            try {
                val httpRequest = url.openConnection() as HttpURLConnection
                httpRequest.requestMethod="GET"
                httpRequest.readTimeout=10000
                httpRequest.connectTimeout=20000
                httpRequest.connect()
                val inputStream=httpRequest.inputStream as InputStream
                jsonResponse=readFromStream(inputStream)

            }catch (ex:Exception){

            }
            return jsonResponse
        }

        private fun  readFromStream(inputStream: InputStream?): String {
            var output=StringBuilder()
            val inputStreamReader=InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val bufferedReader=BufferedReader(inputStreamReader)
            var line=bufferedReader.readLine()

            while (line!=null){
                output.append(line)
                line=bufferedReader.readLine()
            }
            return output.toString()
        }

        private fun  createURL(urlString: String): URL {
            return URL(urlString)
        }

        override fun onPostExecute(result: ArrayList<Event>?) {
            result?.let { updateUI(it) }
            super.onPostExecute(result)
        }
    }
}