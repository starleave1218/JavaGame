package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class Fight : AppCompatActivity() , View.OnClickListener{
    private val playerInfoDatabaseCollectionName = "PlayerInfo"
    // 宣告一個 CoroutineScope
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var loadingAnimation: LoadingAnimation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fight)

        //loading動畫
        loadingAnimation = LoadingAnimation(this)
        loadingAnimation.start()

        val btSection1 = findViewById<Button>(R.id.buttonSection1)
        val btSection2 = findViewById<Button>(R.id.buttonSection2)
        val btSection3 = findViewById<Button>(R.id.buttonSection3)
        val btAddQuestion: ImageButton = findViewById(R.id.btAddQuestion)

        btSection1.setOnClickListener(this)
        btSection2.setOnClickListener(this)
        btSection3.setOnClickListener(this)

        btAddQuestion.setOnClickListener{
            val intent = Intent(this, FightAddQuestion::class.java)
            startActivity(intent)
        }
        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonSection1 -> {
                val buttonText = (view as Button).text.toString() // 獲取按鈕上的文字
                navigateToFightSelect(buttonText)
            }
            R.id.buttonSection2 -> {
                val buttonText = (view as Button).text.toString() // 獲取按鈕上的文字
                navigateToFightSelect(buttonText)
            }
            R.id.buttonSection3 -> {
                val buttonText = (view as Button).text.toString() // 獲取按鈕上的文字
                navigateToFightSelect(buttonText)
            }
        }
    }

    private fun navigateToFightSelect(buttonText: String) {
        val intent = Intent(this, FightSelect::class.java)
        intent.putExtra("buttonText", buttonText)
        intent.putExtra("questionTitle", buttonText)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        //實作文本(名稱)
        val playerName = findViewById<TextView>(R.id.playerId)
        val playerMoney = findViewById<TextView>(R.id.gold)
        val playerLevel = findViewById<TextView>(R.id.level)
        //讀取本地資料庫User
        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)
        Log.d("ERR",sharedPreferences.getString("ID", "-1").toString())
        //取得名稱
        val db = FirebaseFirestore.getInstance()

        val serialNumber = sharedPreferences.getString("ID", "-1").toString()

        db.collection(playerInfoDatabaseCollectionName).document(serialNumber).get()
            .addOnSuccessListener { documents ->
                playerName.text = documents.getString("PlayerId").toString()
                playerMoney.text = String.format("%s G",documents.getLong("Gold").toString())
                playerLevel.text = String.format("Lv: %s",documents.getLong("Level").toString())
                readTitle()
            }

        simulateLoadingComplete()
    }


    //讀稱號
    private fun readTitle() {
        val playerInfoDatabaseCollectionName = "PlayerInfo"
        val titleDatabaseCollectionName = "Title"

        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(playerInfoDatabaseCollectionName)
            .document(sharedPreferences.getString("ID", "-1").toString())
        val titleRef = db.collection(titleDatabaseCollectionName)

        docRef.get().addOnSuccessListener { doc ->
            val titleNumber = doc.getLong("TitleNumber")
            titleRef.document(titleNumber.toString()).get().addOnSuccessListener { docs ->
                val playerTitle = findViewById<TextView>(R.id.userTitle)
                playerTitle.text = docs.getString("TitleName").toString()
            }
        }
    }

    private fun simulateLoadingComplete() {
        handler.postDelayed({
            // 加載完成後停止
            loadingAnimation.stop()
        }, 800)
    }
}