package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore


class FightSelect : AppCompatActivity(), View.OnClickListener {
    private val playerInfoDatabaseCollectionName = "PlayerInfo"
    private var dataSet = ""
    private var bossLevel = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = intent
        dataSet = intent.getStringExtra("questionTitle").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fight_select)

        val back: ImageButton = findViewById(R.id.back)
        val btq1 = findViewById<Button>(R.id.buttonQ1)
        val btq2 = findViewById<Button>(R.id.buttonQ2)
        val btq3 = findViewById<Button>(R.id.buttonQ3)
        val btAddQuestion: ImageButton = findViewById(R.id.btAddQuestion)

        back.setOnClickListener {
            finish()
        }
        btAddQuestion.setOnClickListener{
            val intents = Intent(this, FightAddQuestion::class.java)
            startActivity(intents)
        }

        val titleName = findViewById<TextView>(R.id.title)
        val buttonText = intent.getStringExtra("buttonText")
        titleName.text = buttonText

        btq1.setOnClickListener(this)
        btq2.setOnClickListener(this)
        btq3.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonQ1 -> {
                val btq1 = findViewById<Button>(R.id.buttonQ1)
                val intent = Intent(this, FightMain::class.java)
                intent.putExtra("questionTitle", dataSet+btq1.text.toString())
                bossLevel = "1"
                intent.putExtra("bossLevel", bossLevel)
                startActivity(intent)         }
            R.id.buttonQ2 -> {
                val btq2 = findViewById<Button>(R.id.buttonQ2)
                val intent = Intent(this, FightMain::class.java)
                bossLevel = "2"
                intent.putExtra("bossLevel", bossLevel)
                intent.putExtra("questionTitle", dataSet+btq2.text.toString())
                startActivity(intent)
            }
            R.id.buttonQ3 -> {
                val btq3 = findViewById<Button>(R.id.buttonQ3)
                val intent = Intent(this, FightMain::class.java)
                bossLevel = "3"
                intent.putExtra("bossLevel", bossLevel)
                intent.putExtra("questionTitle", dataSet+btq3.text.toString())
                startActivity(intent)
            }
            R.id.btAddQuestion -> {
                val intent = Intent(this, FightAddQuestion::class.java)
                startActivity(intent)
            }

        }
    }
    override fun onResume() {
        super.onResume()
        //實作文本(名稱)
        val playerName = findViewById<TextView>(R.id.playerId)
        val playerMoney = findViewById<TextView>(R.id.gold)
        val playerLevel = findViewById<TextView>(R.id.level)
        val playerTitle = findViewById<TextView>(R.id.userTitle)
        //讀取本地資料庫User
        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)
        //取得名稱
        val db = FirebaseFirestore.getInstance()

        //設置自定義文字格式
        playerName.setTextAppearance(R.style.AppTheme)
        playerMoney.setTextAppearance(R.style.AppTheme)
        playerLevel.setTextAppearance(R.style.AppTheme)
        playerTitle.setTextAppearance(R.style.AppTheme)
        Log.d("ERR", sharedPreferences.getString("ID", "-1").toString())

        val serialNumber = sharedPreferences.getString("ID", "-1").toString()
        db.collection(playerInfoDatabaseCollectionName).document(serialNumber).get()
            .addOnSuccessListener { documents ->
                playerName.text = documents.getString("PlayerId").toString()
                playerMoney.text =
                    String.format("%s G", documents.getLong("Gold").toString())
                playerLevel.text =
                    String.format("Lv: %s", documents.getLong("Level").toString())
                readTitle()
            }
    }


    //讀稱號
    private fun readTitle(){
        val playerInfoDatabaseCollectionName = "PlayerInfo"
        val titleDatabaseCollectionName = "Title"

        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(playerInfoDatabaseCollectionName)
            .document(sharedPreferences.getString("ID", "-1").toString())
        val titleRef = db.collection(titleDatabaseCollectionName)

        docRef.get().addOnSuccessListener {doc ->
            val titleNumber = doc.getLong("TitleNumber")
            titleRef.document(titleNumber.toString()).get().addOnSuccessListener {docs ->
                val playerTitle = findViewById<TextView>(R.id.userTitle)
                playerTitle.text = docs.getString("TitleName").toString()
            }
        }

    }
}