package com.example.bgc

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

@SuppressLint("StaticFieldLeak")
@Suppress("DEPRECATION")
class UserStartDownloader(

    val activityApp: AppCompatActivity?,
    val button: Button,
    var layouts: ArrayList<LinearLayout>,
    var progressBars: ArrayList<ProgressBar>,
    var context: Context?,
    var username: String?,
    var findNavController: NavController,
    private val from: String,
): AsyncTask<String, Int, String>(){

    private var dbHandler: MyDBHandler = MyDBHandler(context!!, null, null, 1)

    override fun onPreExecute() {
        super.onPreExecute()
        layouts[0].visibility = View.VISIBLE
        layouts[1].visibility = View.VISIBLE
        layouts[2].visibility = View.VISIBLE
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if(result == "success") {
            val user: User? = dbHandler.findUser()
            user?.numberOfAddOns = dbHandler.getNumAddOns()
            user?.numberOfGames = dbHandler.getNumGames()
            if (user != null) {
                dbHandler.syncUser(user)
            }
            if(from == "synchronization") {
                confirmRemove()
            }
            if (from == "configuration") {
                findNavController.navigate(R.id.action_configuration_to_mainScreen)
            }
        }else{
            if(from == "configuration"){
                dbHandler.deleteAllUsers()
            }
            button.isEnabled = true
            activityApp?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            Toast.makeText(context, "$result", Toast.LENGTH_LONG).show()
        }
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        progressBars[values[1]!!].progress = values[0]!!
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
                progress = total*100 / 5851857
                publishProgress(progress, 0)
                fosGames.write(data, 0, count)
                count = isStreamGames.read(data)
            }
            isStreamGames.close()
            fosGames.close()

            count = isStreamAddOns.read(data)
            while(count != -1){
                total += count
                progress = total*100 / 5851857
                publishProgress(progress, 0)
                fosAddOns.write(data, 0, count)
                count = isStreamAddOns.read(data)
            }
            isStreamAddOns.close()
            fosAddOns.close()
            val check = checkFile()
            if(check!="Ok"){
                throw Exception(check)
            }
            loadDataGame()
            loadDataAddOn()
        }catch(e: MalformedURLException){
            return "Zły URL"
        } catch (e: FileNotFoundException){
            return "Brak pliku"
        } catch (e: IOException) {
            return "Wyjątek IO"
        } catch (e: Exception){
            return e.message.toString()
        }
        return "success"
    }

    private fun confirmRemove(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context!!)
        builder.setCancelable(true)
        builder.setTitle("Potwierdzenie")
        builder.setMessage("Czy chcesz usunąć gry, które zniknęły z listy")
        builder.setPositiveButton("Zatwierdz",
            DialogInterface.OnClickListener { dialog, which ->
                dbHandler.removeOldGames()
                findNavController.navigate(R.id.action_synchronization_to_mainScreen)
            })
        builder.setNegativeButton("Anuluj",DialogInterface.OnClickListener { dialog, which ->
            findNavController.navigate(R.id.action_synchronization_to_mainScreen)
        })

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun checkFile():String{
        var filename = "games.xml"
        val path = context?.filesDir
        var inDir = File(path, "XML")

        if (inDir.exists()) {
            val file = File(inDir, filename)
            if (file.exists()) {
                val xmlDoc: Document =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                xmlDoc.documentElement.normalize()
                val elems: NodeList? = xmlDoc.getElementsByTagName("errors")
                if (elems != null) {
                    for (i in 0 until elems.length){
                        val itemNode: Node = elems.item(i)
                        if(itemNode.nodeName == "errors"){
                            val elemsV2: NodeList = itemNode.childNodes
                            for (j in 0 until elemsV2.length){
                                val itemNodeV2: Node = elemsV2.item(j)
                                if(itemNodeV2.nodeName == "error"){
                                    val elemsV3: NodeList = itemNodeV2.childNodes
                                    for (k in 0 until elemsV2.length){
                                        val itemNodeV3: Node = elemsV3.item(k)
                                        if(itemNodeV3.nodeName == "message"){
                                            return itemNodeV3.textContent
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        filename = "addons.xml"
        inDir = File(path, "XML")
        if (inDir.exists()) {
            val file = File(inDir, filename)
            if (file.exists()) {
                val xmlDoc: Document =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                xmlDoc.documentElement.normalize()
                val elems: NodeList? = xmlDoc.getElementsByTagName("errors")
                if (elems != null) {
                    for (i in 0 until elems.length){
                        val itemNode: Node = elems.item(i)
                        if(itemNode.nodeName == "errors"){
                            val elemsV2: NodeList = itemNode.childNodes
                            for (j in 0 until elemsV2.length){
                                val itemNodeV2: Node = elemsV2.item(j)
                                if(itemNodeV2.nodeName == "error"){
                                    val elemsV3: NodeList = itemNodeV2.childNodes
                                    for (k in 0 until elemsV2.length){
                                        val itemNodeV3: Node = elemsV3.item(k)
                                        if(itemNodeV3.nodeName == "message"){
                                            return itemNodeV3.textContent
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return "Ok"
    }

    fun loadDataGame(){
        if(from == "synchronization"){
            dbHandler.setToRemove(1)
        }
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
                        publishProgress((i+1)*100/items.length, 1)
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
                        publishProgress((i+1)*100/items.length, 2)
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