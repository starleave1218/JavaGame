package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

@Suppress("NAME_SHADOWING", "DEPRECATION")
class Shop : AppCompatActivity(), View.OnClickListener {
    // 宣告一個 CoroutineScope
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var loadingAnimation: LoadingAnimation

    private val playerInfoDatabaseCollectionName = "PlayerInfo"
    private var itemCase: String = ""
    private lateinit var descriptionTextView: TextView
    // 每個商品的初始可購買數量
    private val remainingPurchaseCounts = mutableMapOf(
        "M1" to 5,
        "M2" to 5,
        "M3" to 5,
        "M4" to 5,
        "M5" to 5,
        "M6" to 5
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        loadingAnimation = LoadingAnimation(this)
        loadingAnimation.start()

        // 返回按鈕
        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        // 初始化商品圖片
        val commodity1 = findViewById<ImageView>(R.id.commodity1)
        val commodity2 = findViewById<ImageView>(R.id.commodity2)
        val commodity3 = findViewById<ImageView>(R.id.commodity3)
        val commodity4 = findViewById<ImageView>(R.id.commodity4)
        val commodity5 = findViewById<ImageView>(R.id.commodity5)
        val commodity6 = findViewById<ImageView>(R.id.commodity6)
        // 刷新按鈕
        val refresh = findViewById<Button>(R.id.refresh)
        refresh.setOnClickListener {
            // 顯示所有商品
            commodity1.visibility = View.VISIBLE
            commodity2.visibility = View.VISIBLE
            commodity3.visibility = View.VISIBLE
            commodity4.visibility = View.VISIBLE
            commodity5.visibility = View.VISIBLE
            commodity6.visibility = View.VISIBLE
        }

        // 設置按鈕監聽
        commodity1.setOnClickListener(this)
        commodity2.setOnClickListener(this)
        commodity3.setOnClickListener(this)
        commodity4.setOnClickListener(this)
        commodity5.setOnClickListener(this)
        commodity6.setOnClickListener(this)
    }
    

    //施行按鈕方法
    @SuppressLint("MissingInflatedId", "CutPasteId")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onClick(view: View?) {
        // 獲取使用者資訊
        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)
        // 造訪 Firebase fireStore
        val db = FirebaseFirestore.getInstance()
        // 使用產生的 ID 建立新文檔
        val information = db.collection(playerInfoDatabaseCollectionName)
            .document(sharedPreferences.getString("ID", "-1").toString())
        val writeData = db.collection(playerInfoDatabaseCollectionName)
            .document(sharedPreferences.getString("ID", "-1").toString())
        // 購買數量
        var counter = 1

        // 設置購買數量的視窗
        val myPurchaseView = layoutInflater.inflate(
            R.layout.purchase_quantity,
            findViewById(android.R.id.content),
            false
        )
        myPurchaseView.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )

        descriptionTextView = myPurchaseView.findViewById(R.id.descriptionTextView)
        // 根據點選的商品設定itemCase
        when (view?.id) {
            R.id.commodity1 -> {
                itemCase = "M1"
            }
            R.id.commodity2 -> {
                itemCase = "M2"
            }
            R.id.commodity3 -> {
                itemCase = "M3"
            }
            R.id.commodity4 -> {
                itemCase = "M4"
            }
            R.id.commodity5 -> {
                itemCase = "M5"
            }
            R.id.commodity6 -> {
                itemCase = "M6"
            }
        }


        // 設定彈出視窗的屬性
        val popupWindow = PopupWindow(this).apply {
            contentView = myPurchaseView
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            isFocusable = true
            isTouchable = true
            isClippingEnabled = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        }
        val ref = db.collection("Item").document(itemCase)
        ref.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val itemDescription: String? = document.getString("Description")
                descriptionTextView.text = itemDescription
            } else {
                Log.d("ShopActivity", "文檔不存在")
            }
        }
        // 設定購買數量的按鈕監聽
        val counterTextView = myPurchaseView.findViewById<TextView>(R.id.counterTextView)
        myPurchaseView.findViewById<ImageButton>(R.id.addNumber).setOnClickListener {
            if (counter < 5) {
                counter++
                counterTextView.text = "$counter"
            } else {
                counter = 5
                Toast.makeText(this, "已超過購買數量", Toast.LENGTH_SHORT).show()
            }
        }

        myPurchaseView.findViewById<ImageButton>(R.id.minusNumber).setOnClickListener {
            if (counter > 0) {
                counter--
                counterTextView.text = "$counter"
            } else {
                counter = 0
                Toast.makeText(this, "已超過購買數量", Toast.LENGTH_SHORT).show()
            }
        }

        // 確認購買按鈕

        myPurchaseView.findViewById<TextView>(R.id.descriptionTextView)
        myPurchaseView.findViewById<ImageButton>(R.id.yes).setOnClickListener {
            popupWindow.dismiss()
            information.get().addOnSuccessListener { documents ->
                var userMoney: Int =
                    Integer.parseInt(documents.getLong("Gold").toString())

                val ref = db.collection("Item").document(itemCase)
                ref.get().addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val itemMoney: Int = document.getLong("Money")?.toInt() ?: 0
                        val purchaseMoney = itemMoney * counter

                        if (userMoney >= purchaseMoney) {
                            userMoney -= purchaseMoney
                            writeData.update("Gold", userMoney)
                            Toast.makeText(
                                this,
                                "購買成功!!總共花費 $purchaseMoney G",
                                Toast.LENGTH_SHORT
                            ).show()
                            changeMoney()

                            // 從文件中獲取 "itemCase" 欄位的值
                            val backpackItemName: String? = document.getString("backpackItemName")
                            val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)
                            val userId =sharedPreferences.getString("ID", "-1").toString()
                            // 獲取資料庫中 "BackpackTest" 集合的文檔，ID 為 "1"
                            val backpackRef = db.collection("BackPage").document(userId)

                            // 獲取異步操作的成功監聽器
                            backpackRef.get().addOnSuccessListener { backpackDocument ->
                                // 檢查 "itemCase" 是否等於當前的 "itemCase"
                                if (backpackItemName == null) {
                                    // 如果相等，獲取現有的數量，並加上 counter
                                    val existingCounter = backpackDocument.getLong(itemCase)?.toInt() ?: 0
                                    val updatedCounter = existingCounter + counter

                                    // 建立要更新到資料庫的資料
                                    val updateData = hashMapOf(
                                        itemCase to updatedCounter
                                    )
                                    // 將更新的資料設定到資料庫中，並使用 SetOptions.merge() 來合併資料
                                    backpackRef.set(updateData, SetOptions.merge())
                                } else {
                                    // 如果 "itemCase" 不相等，則新增一個新的 "itemCase" 到資料庫中
                                    val newData = hashMapOf(
                                        itemCase to 5
                                    )

                                    // 將新的資料設定到資料庫中，並使用 SetOptions.merge() 來合併資料
                                    backpackRef.set(newData, SetOptions.merge())
                                }
                            }


                            val remainingCount = remainingPurchaseCounts[itemCase] ?: 0
                            if (remainingCount >= counter) {
                                remainingPurchaseCounts[itemCase] = remainingCount - counter

                                if (remainingPurchaseCounts[itemCase] == 0) {
                                    // 如果商品購買數量為0，隱藏該商品
                                    val commodity1 = findViewById<ImageView>(R.id.commodity1)
                                    val commodity2 = findViewById<ImageView>(R.id.commodity2)
                                    val commodity3 = findViewById<ImageView>(R.id.commodity3)
                                    val commodity4 = findViewById<ImageView>(R.id.commodity4)
                                    val commodity5 = findViewById<ImageView>(R.id.commodity5)
                                    val commodity6 = findViewById<ImageView>(R.id.commodity6)
                                    when (itemCase) {
                                        "M1" -> commodity1.visibility = View.INVISIBLE
                                        "M2" -> commodity2.visibility = View.INVISIBLE
                                        "M3" -> commodity3.visibility = View.INVISIBLE
                                        "M4" -> commodity4.visibility = View.INVISIBLE
                                        "M5" -> commodity5.visibility = View.INVISIBLE
                                        "M6" -> commodity6.visibility = View.INVISIBLE
                                    }
                                }
                            } else {
                                Toast.makeText(this, "購買數量已超過庫存!", Toast.LENGTH_SHORT).show()
                                // 如果商品購買數量超過庫存，隱藏該商品
                                val commodity1 = findViewById<ImageView>(R.id.commodity1)
                                val commodity2 = findViewById<ImageView>(R.id.commodity2)
                                val commodity3 = findViewById<ImageView>(R.id.commodity3)
                                val commodity4 = findViewById<ImageView>(R.id.commodity4)
                                val commodity5 = findViewById<ImageView>(R.id.commodity5)
                                val commodity6 = findViewById<ImageView>(R.id.commodity6)
                                when (itemCase) {
                                    "M1" -> commodity1.visibility = View.INVISIBLE
                                    "M2" -> commodity2.visibility = View.INVISIBLE
                                    "M3" -> commodity3.visibility = View.INVISIBLE
                                    "M4" -> commodity4.visibility = View.INVISIBLE
                                    "M5" -> commodity5.visibility = View.INVISIBLE
                                    "M6" -> commodity6.visibility = View.INVISIBLE
                                }
                            }
                        }else {
                            Toast.makeText(this, "餘額不足!!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        // 取消購買按鈕
        myPurchaseView.findViewById<ImageButton>(R.id.no).setOnClickListener {
            popupWindow.dismiss()
            Toast.makeText(this, "已取消購買", Toast.LENGTH_SHORT).show()
        }

        // 顯示彈出視窗
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
    }



    // 更新使用者金幣數量
    private fun changeMoney() {
        val playerMoney = findViewById<TextView>(R.id.gold)
        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)
        val db = FirebaseFirestore.getInstance()
        val serialNumber = sharedPreferences.getString("ID", "-1").toString()

        db.collection(playerInfoDatabaseCollectionName).document(serialNumber).get()
            .addOnSuccessListener { documents ->
                playerMoney.text =
                    String.format("%s G", documents.getLong("Gold").toString())
            }
    }

    // 進入頁面時刷新
    override fun onResume() {
        super.onResume()
        changeMoney()
        // 顯示使用者名稱和等級
        val playerName = findViewById<TextView>(R.id.playerId)
        val playerLevel = findViewById<TextView>(R.id.level)
        val db = FirebaseFirestore.getInstance()
        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)
        val serialNumber = sharedPreferences.getString("ID", "-1").toString()

        // 讀取使用者資訊
        db.collection(playerInfoDatabaseCollectionName).document(serialNumber).get()
            .addOnSuccessListener { documents ->
                playerName.text = documents.getString("PlayerId").toString()
                playerLevel.text =
                    String.format("Lv: %s", documents.getLong("Level").toString())
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
