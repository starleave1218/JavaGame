package com.example.myapplication

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Handler
import android.os.Looper


class Start : AppCompatActivity(), View.OnClickListener {
    private val playerInfoDatabaseCollectionName = "PlayerInfo"
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var btDatabase: Button
    //private lateinit var btGPT: Button

    // 宣告一個 CoroutineScope
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var loadingAnimation: LoadingAnimation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //實作按鈕
        val fight: ImageButton = findViewById(R.id.fight)
        val history: ImageButton = findViewById(R.id.history)
        val shop: ImageButton = findViewById(R.id.shop)
        val backPack: ImageButton = findViewById(R.id.backPack)

        //loading動畫
        loadingAnimation = LoadingAnimation(this)


        btDatabase = findViewById(R.id.insert)
        //btGPT = findViewById(R.id.gpt)


        //設置按鈕監聽
        fight.setOnClickListener(this)
        history.setOnClickListener(this)
        shop.setOnClickListener(this)
        backPack.setOnClickListener(this)
        btDatabase.setOnClickListener {
            val intent = Intent(this, Insert::class.java)
            startActivity(intent)
        }
//        btGPT.setOnClickListener {
//            val intent = Intent(this, ChatGPT::class.java)
//            startActivity(intent)
//        }

    }

    //施行按鈕方法
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fight -> {
                // 啟動目標
                val intent = Intent(this, Fight::class.java)
                startActivity(intent)
            }
            R.id.history -> {
                val intent = Intent(this, Record::class.java)
                startActivity(intent)
            }
            R.id.shop -> {
                val intent = Intent(this, Shop::class.java)
                startActivity(intent)
                // 關閉頁面
                // finish()
                Log.d("test", "This is Debug.")
            }
            R.id.backPack -> {
                val intent = Intent(this, BackPack::class.java)
                startActivity(intent)
            }

        }
    }

    private fun simulateLoadingComplete() {
        handler.postDelayed({
            // 加載完成後停止
            loadingAnimation.stop()
        }, 800)
    }


    //刷新頁面
    override fun onResume() {
        super.onResume()
        loadingAnimation.start()
        //實作文本(名稱)
        val playerName = findViewById<TextView>(R.id.playerId)
        val playerMoney = findViewById<TextView>(R.id.gold)
        val playerLevel = findViewById<TextView>(R.id.level)

        //讀取本地資料庫User
        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)
        Log.d("ERR", sharedPreferences.getString("ID", "-1").toString())

        //取得名稱
        val db = FirebaseFirestore.getInstance()

        val serialNumber = sharedPreferences.getString("ID", "-1").toString()

        db.collection(playerInfoDatabaseCollectionName).document(serialNumber).get()
            .addOnSuccessListener { documents ->
                playerName.text = documents.getString("PlayerId").toString()
                Log.d("name",documents.getString("PlayerId").toString())
                playerMoney.text = String.format("%s G",documents.getLong("Gold").toString())
                playerLevel.text = String.format("Lv: %s",documents.getLong("Level").toString())
                readTitle()
                if (playerName.text == "a"){
                    Log.d("game","是測試者")

                }else{
                    if (btDatabase.visibility == View.VISIBLE ){
                        btDatabase.visibility = View.INVISIBLE
//                        btGPT.visibility = View.INVISIBLE
                    }

                }

    }
        //音樂
        mediaPlayer = MediaPlayer.create(this, R.raw.start)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        //停止動畫
        simulateLoadingComplete()

    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.release()
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