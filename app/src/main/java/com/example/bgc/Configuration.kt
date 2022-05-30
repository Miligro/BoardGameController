package com.example.bgc

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.bgc.databinding.FragmentConfigurationBinding
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

class Configuration : Fragment() {
    private var _binding: FragmentConfigurationBinding? = null
    private lateinit var dbHandler: MyDBHandler

    var gamesList: MutableList<Game>? = null
    var addOnsList: MutableList<Game>? = null
    var progres_temp: Int = -1
    private var username: String = ""
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        _binding = FragmentConfigurationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHandler = MyDBHandler(requireActivity(), null, null, 1)
        dbHandler.deleteAllGamesAddOns()
        binding.progressBar.progress = 0
        binding.saveUserBtn.setOnClickListener{
            username = binding.usernameText.text.toString()
            val user: User = User(username, 0, 0, "123")
            dbHandler.addUser(user)
            downloadData()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private inner class UserStartDownloader(var progressBar: ProgressBar): AsyncTask<String, Int, String>(){
        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            loadDataGame()
            loadDataAddOn()
            findNavController().navigate(R.id.action_configuration_to_mainScreen)
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
    }

    fun downloadData() {
        val cd = UserStartDownloader(binding.progressBar)
        cd.execute()
    }

    fun loadDataGame(){
        gamesList = mutableListOf()
        addOnsList = mutableListOf()

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
//                                                                    if (curR == "Not Ranked"){
//                                                                        currentRanking = 0
//                                                                    }else{
//                                                                        currentRanking = curR.toInt()
//                                                                    }
                                                                    if (curR != "Not Ranked"){
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
                        if (currentRanking == null) continue
                        if (currentReleaseYear == null) currentReleaseYear = 0

                        val game=Game(currentId, currentTitle, currentImg, currentReleaseYear, currentRanking)
                        dbHandler.addGame(game)
                    }
                }
            }
        }
        val user: User? = dbHandler.findUser()
        user?.numberOfGames = dbHandler.getNumGames()
        if (user != null) {
            dbHandler.syncUser(user)
        }
    }

    fun loadDataAddOn(){
        gamesList = mutableListOf()
        addOnsList = mutableListOf()

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
                        if (currentReleaseYear == null) currentReleaseYear = 0

                        val addOn=AddOn(currentId, currentTitle, currentImg, currentReleaseYear)
                        dbHandler.addAddOn(addOn)
                    }
                }
            }
        }
        val user: User? = dbHandler.findUser()
        user?.numberOfAddOns = dbHandler.getNumAddOns()
        if (user != null) {
            dbHandler.syncUser(user)
        }
    }
}