package com.example.bgc

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.view.View
import android.widget.ProgressBar
import androidx.navigation.NavController
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

@SuppressLint("StaticFieldLeak")
@Suppress("DEPRECATION")
class UserStartDownloader(
    var progressBar: ProgressBar,
    var context: Context?,
    var username: String,
    var findNavController: NavController
): AsyncTask<String, Int, String>(){

    private var dbHandler: MyDBHandler = MyDBHandler(context!!, null, null, 1)

    override fun onPreExecute() {
        super.onPreExecute()
        progressBar.visibility = View.VISIBLE
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        loadDataGame()
        loadDataAddOn()
        val user: User? = dbHandler.findUser()
        user?.numberOfAddOns = dbHandler.getNumAddOns()
        user?.numberOfGames = dbHandler.getNumGames()
        if (user != null) {
            dbHandler.syncUser(user)
        }
        findNavController.navigate(R.id.action_configuration_to_mainScreen)
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        progressBar.progress = values[0]!!
    }

    override fun doInBackground(vararg p0: String?): String {
        try{
            val urlGames = URL("https://boardgamegeek.com/xmlapi2/collection?username=$username&stats=1&subtype=boardgame")
            val connectionGames = urlGames.openConnection()
            connectionGames.connect()
            val isStreamGames = urlGames.openStream()

            val urlAddOns = URL("https://boardgamegeek.com/xmlapi2/collection?username=$username&stats=1&subtype=boardgameexpansion")
            val connectionAddOns = urlAddOns.openConnection()
            connectionAddOns.connect()
            val isStreamAddOns = urlAddOns.openStream()

//                val lengthOfFile = connectionGames.contentLengthLong
            val path = context?.filesDir
            val testDirectory = File("$path/XML")
            if (!testDirectory.exists()) testDirectory.mkdir()

            val fosGames = FileOutputStream("$testDirectory/games.xml")
            val fosAddOns = FileOutputStream("$testDirectory/addons.xml")

            val data = ByteArray(1024)
            var count = 0
            var total:Int = 0
            var progress= 0

            count = isStreamGames.read(data)
            while(count != -1){
                total += count
                progress = total*100 / 3851857
                publishProgress(progress)
                fosGames.write(data, 0, count)
                count = isStreamGames.read(data)
            }
            isStreamGames.close()
            fosGames.close()

            count = isStreamAddOns.read(data)
            while(count != -1){
                total += count
                progress = total*100 / 3851857
                publishProgress(progress)
                fosAddOns.write(data, 0, count)
                count = isStreamAddOns.read(data)
            }
            isStreamAddOns.close()
            fosAddOns.close()

        }catch(e: MalformedURLException){
            return "Zły URL"
        } catch (e: FileNotFoundException){
            return "Brak pliku"
        } catch (e: IOException) {
            return "Wyjątek IO"
        }
        return "success"
    }

    fun loadDataGame(){
        val filename = "games.xml"
        val path = context?.filesDir
        val inDir = File(path, "XML")

        if (inDir.exists()) {
            val file = File(inDir, filename)
            if (file.exists()) {
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                xmlDoc.documentElement.normalize()

                val items: NodeList = xmlDoc.getElementsByTagName("item")
                for (i in 0 until items.length){
                    val itemNode: Node = items.item(i)
                    if(itemNode.nodeType == Node.ELEMENT_NODE) {
                        val elem = itemNode as Element
                        val children = elem.childNodes
//                        binding.progressBar.progress = (i+1)*100/items.length
                        var currentId:Long? = null
                        var currentTitle:String? = null
                        var currentImg:String? = null
                        var currentReleaseYear:Int? = null
                        var currentRanking:Int? = null

                        if (elem.attributes != null){
                            currentId = elem.attributes.getNamedItem("objectid").nodeValue.toLong()

                        }
                        for (j in 0 until children.length){
                            val node = children.item(j)
                            if (node is Element){
                                when(node.nodeName){
                                    "name" -> {
                                        currentTitle = node.textContent
                                    }
                                    "thumbnail" -> {
                                        currentImg = node.textContent
                                    }
                                    "yearpublished" -> {
                                        currentReleaseYear = node.textContent.toInt()
                                    }
                                    "stats" -> {
                                        val childrenV2 = node.childNodes
                                        for (k in 0 until childrenV2.length){
                                            val nodeV2 = childrenV2.item(k)
                                            if (nodeV2.nodeName == "rating"){
                                                val childrenV3 = nodeV2.childNodes
                                                for (l in 0 until childrenV3.length){
                                                    val nodeV3 = childrenV3.item(l)
                                                    if(nodeV3.nodeName == "ranks"){
                                                        val childrenV4 = nodeV3.childNodes
                                                        for (m in 0 until childrenV4.length){
                                                            val nodeV4 = childrenV4.item(m)
                                                            if (nodeV4.attributes != null){
                                                                if (nodeV4.attributes.getNamedItem("name").nodeValue == "boardgame" && nodeV4.attributes.getNamedItem("type").nodeValue == "subtype"){
                                                                    val curR: String = nodeV4.attributes.getNamedItem("value").nodeValue
                                                                    if (curR == "Not Ranked"){
                                                                        currentRanking = 0
                                                                    }else{
                                                                        currentRanking = curR.toInt()
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (currentId == null) currentId = -1
                        if (currentTitle == null) currentTitle = "No title"
                        if (currentImg == null) currentImg = "https://www.freeiconspng.com/uploads/no-image-icon-6.png"
                        if (currentRanking == null) currentRanking = -1
                        if (currentReleaseYear == null) currentReleaseYear = -1

                        val game=GameAddOn(currentId, currentTitle, currentImg, currentReleaseYear, currentRanking)
                        dbHandler.addGame(game)
                    }
                }
            }
        }
    }

    fun loadDataAddOn(){
        val filename = "addons.xml"
        val path = context?.filesDir
        val inDir = File(path, "XML")

        if (inDir.exists()) {
            val file = File(inDir, filename)
            if (file.exists()) {
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                xmlDoc.documentElement.normalize()

                val items: NodeList = xmlDoc.getElementsByTagName("item")
                for (i in 0 until items.length){
                    val itemNode: Node = items.item(i)
                    if(itemNode.nodeType == Node.ELEMENT_NODE) {
                        val elem = itemNode as Element
                        val children = elem.childNodes
//                        binding.progressBar.progress = (i+1)*100/items.length
                        var currentId:Long? = null
                        var currentTitle:String? = null
                        var currentImg:String? = null
                        var currentReleaseYear:Int? = null

                        if (elem.attributes != null){
                            currentId = elem.attributes.getNamedItem("objectid").nodeValue.toLong()

                        }
                        for (j in 0 until children.length){
                            val node = children.item(j)
                            if (node is Element){
                                when(node.nodeName){
                                    "name" -> {
                                        currentTitle = node.textContent
                                    }
                                    "thumbnail" -> {
                                        currentImg = node.textContent
                                    }
                                    "yearpublished" -> {
                                        currentReleaseYear = node.textContent.toInt()
                                    }
                                }
                            }
                        }
                        if (currentId == null) currentId = -1
                        if (currentTitle == null) currentTitle = "No title"
                        if (currentImg == null) currentImg = "https://www.freeiconspng.com/uploads/no-image-icon-6.png"
                        if (currentReleaseYear == null) currentReleaseYear = -1

                        val addOn=GameAddOn(currentId, currentTitle, currentImg, currentReleaseYear)
                        dbHandler.addAddOn(addOn)
                    }
                }
            }
        }
    }
}