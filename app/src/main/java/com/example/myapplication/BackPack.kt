package com.example.myapplication

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class BackPack : AppCompatActivity(), View.OnClickListener {
    // 宣告一個 CoroutineScope
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var loadingAnimation: LoadingAnimation

    private val map: Map<String, Int> =
        mapOf("M1" to R.drawable.healing_potion, "M2" to R.drawable.powerup1,"M3" to R.drawable.bag_sword) //物品圖片位置
    private var equipmentNum = ArrayList<String>(5) //裝備中的物品名稱
    private var haveTitle: ArrayList<String> = ArrayList()

    private var wear: String = "" //未來連接資料庫

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_pack)

        readBackPageData()
        //loading動畫
        loadingAnimation = LoadingAnimation(this)
        loadingAnimation.start()

        getEquipment()
        readTitleData()
        //稱號
        haveTitle()

        val equipment1: ImageButton = findViewById(R.id.equipment1)
        val equipment2: ImageButton = findViewById(R.id.equipment2)
        val equipment3: ImageButton = findViewById(R.id.equipment3)
        val equipment4: ImageButton = findViewById(R.id.equipment4)
        val equipment5: ImageButton = findViewById(R.id.equipment5)
        val equipmentButton: Button = findViewById(R.id.equipmentButton)
        val titleButton: Button = findViewById(R.id.titleButton)

        equipment1.setOnClickListener(this)
        equipment2.setOnClickListener(this)
        equipment3.setOnClickListener(this)
        equipment4.setOnClickListener(this)
        equipment5.setOnClickListener(this)
        equipmentButton.setOnClickListener(this)
        titleButton.setOnClickListener(this)

        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        showWearEquipment()
        addTitle()
        simulateLoadingComplete()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.equipment1 -> {
                //移除裝備物品
                val equipment = findViewById<ImageButton>(R.id.equipment1)
                equipmentNum.remove(equipment.tag)
            }
            R.id.equipment2 -> {
                //移除裝備物品
                val equipment = findViewById<ImageButton>(R.id.equipment2)
                equipmentNum.remove(equipment.tag)
            }
            R.id.equipment3 -> {
                //移除裝備物品
                val equipment = findViewById<ImageButton>(R.id.equipment3)
                equipmentNum.remove(equipment.tag)
            }
            R.id.equipment4 -> {
                //移除裝備物品
                val equipment = findViewById<ImageButton>(R.id.equipment4)
                equipmentNum.remove(equipment.tag)
            }
            R.id.equipment5 -> {
                //移除裝備物品
                val equipment = findViewById<ImageButton>(R.id.equipment5)
                equipmentNum.remove(equipment.tag)
            }
            R.id.equipmentButton -> {
                //切換畫面
                change(R.id.equipment, R.id.title)

                val equipmentButton: Button = findViewById(R.id.equipmentButton)
                val titleButton: Button = findViewById(R.id.titleButton)

                equipmentButton.setTextColor(Color.RED)
                titleButton.setTextColor(Color.WHITE)
            }
            R.id.titleButton -> {
                //切換畫面
                change(R.id.title, R.id.equipment)

                val equipmentButton: Button = findViewById(R.id.equipmentButton)
                val titleButton: Button = findViewById(R.id.titleButton)

                equipmentButton.setTextColor(Color.WHITE)
                titleButton.setTextColor(Color.RED)
            }


        }
        //更新畫面
        onResume()

    }

    //城市結束執行
    override fun onDestroy() {
        super.onDestroy()

        Log.e("a","stop")
        //讀取本地資料庫User
        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)
        //寫入資料庫
        val playerInfoDatabaseCollectionName = "PlayerInfo"
        val titleDatabaseCollectionName = "Title"

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(playerInfoDatabaseCollectionName)
            .document(sharedPreferences.getString("ID", "-1").toString())
        val titleRef = db.collection(titleDatabaseCollectionName)

        var equipment=""
        //稱號寫入格式
        for(i in equipmentNum){
            equipment +="$i,"
        }
        Log.e("test",equipment)

        docRef.get().addOnSuccessListener {
            titleRef.whereEqualTo("TitleName",wear).get().addOnSuccessListener { docs->
                val updates = hashMapOf(
                    "Equipment" to equipment,
                )
                docRef.update(updates as Map<String, Any>)

            }
        }
    }

    //讀取使用者背包所擁有的物品及物品資訊
    private fun readBackPageData() {
        val backPageDatabaseCollectionName = "BackPage"
        val itemDatabaseCollectionName = "Item"

        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(backPageDatabaseCollectionName)
            .document(sharedPreferences.getString("ID", "-1").toString())

        //抓背包持有物
        docRef.get()
            .addOnSuccessListener { doc ->
                doc.data?.let { data ->
                    //排序欄位資料
                    val sortedData = data.keys.sorted()

                    var count = 1
                    for (entry in sortedData) {
                        val itemRef = db.collection(itemDatabaseCollectionName).document(entry)
                        itemRef.get()
                            .addOnSuccessListener {
                                val imageId = map[entry]
                                //當有這物品且數量不為0，新增欄位
                                if (imageId != null &&
                                    Integer.parseInt(doc.getLong(entry).toString()) != 0) {
                                    when (count) {
                                        1 -> {
                                            addItem(R.id.ItemList, imageId, entry)
                                            count += 1
                                        }
                                        2 -> {
                                            addItem(R.id.ItemList1, imageId, entry)
                                            count += 1
                                        }
                                        3 -> {
                                            addItem(R.id.ItemList2, imageId, entry)
                                            count = 1
                                        }
                                    }

                                }

                            }

                    }
                }
            }

    }

    //抓裝備中的稱號
    private fun readTitleData(){
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
                wear= docs.getString("TitleName").toString()
                val title = findViewById<TextView>(R.id.titleNames)
                title.text = wear
            }

        }

    }

    //添加物品欄位
    private fun addItem(viewId: Int, imgId: Int, tag: String) {
        val scrollViewLayout = findViewById<LinearLayout>(viewId)

        val customView = BackpackItems(this, null)
        customView.setImageResource(imgId)
        customView.tag = tag

        //View布局
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER
        layoutParams.bottomMargin = 20

        // 添加 CustomImageViewTextView 到 ScrollView 的子視圖中
        customView.layoutParams = layoutParams

        //設置每個動作
        customView.setOnClickListener { view ->
            val tagInfo = view.tag

            val infoView = InfoView(this, null)
            val icon = map[tagInfo]
            if (icon != null) {
                val db = FirebaseFirestore.getInstance()
                val docRef = db.collection("Item")
                    .document(tagInfo.toString())
                //抓物品資訊
                docRef.get().addOnSuccessListener { doc ->
                    val info = doc.getString("Description")
                    //是否裝備中
                    if (equipmentNum.contains(tagInfo)) {
                        infoView.setView(icon, info.toString(), "已裝備")
                        infoView.setClick(click = false, focus = false)
                    } else {
                        infoView.setView(icon, info.toString(), "裝備")
                        infoView.setClick(click = true, focus = true)
                    }
                }

            }

            //dp轉換(設寬度用)
            val marginInDp = 20 // 20dp
            val marginInPixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                marginInDp.toFloat(),
                resources.displayMetrics
            ).toInt()

            //彈窗設定
            val popupWindow = PopupWindow(this).apply {
                contentView = infoView
                width = resources.displayMetrics.widthPixels - 2 * marginInPixels
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                //沒添加會一直創建新的
                isFocusable = true
                //全屏背景
                isClippingEnabled = true
                setBackgroundDrawable(ColorDrawable(Color.BLACK))
            }


            popupWindow.isOutsideTouchable = false // true 表示外部可碰觸關閉，false 表示外部不可碰觸關閉

            infoView.findViewById<Button>(R.id.sure).setOnClickListener {
                equipmentNum.add(tagInfo as String)
                onResume()
                popupWindow.dismiss()
            }


            //出現位置
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        }

        scrollViewLayout.addView(customView)


    }

    //裝備後顯示
    private fun showEquipment(id: Int, viewId: Int, tag: String) {
        val equipment = findViewById<ImageButton>(viewId)
        equipment.tag = tag
        equipment.setImageResource(id)
        equipment.visibility = View.VISIBLE
    }

    private fun showWearEquipment(){
        //重置裝備顯示
        clearEquipment()

        //裝備顯示
        var count = 1
        for (i in equipmentNum) {
            when (count) {
                1 -> {
                    val equipmentId = map[i]
                    if (equipmentId != null) {
                        showEquipment(equipmentId, R.id.equipment1, i)
                    }
                    count++
                }
                2 -> {
                    val equipmentId = map[i]
                    if (equipmentId != null) {
                        showEquipment(equipmentId, R.id.equipment2, i)
                    }
                    count++
                }
                3 -> {
                    val equipmentId = map[i]
                    if (equipmentId != null) {
                        showEquipment(equipmentId, R.id.equipment3, i)
                    }
                    count++
                }
                4 -> {
                    val equipmentId = map[i]
                    if (equipmentId != null) {
                        showEquipment(equipmentId, R.id.equipment4, i)
                    }
                    count++
                }
                5 -> {
                    val equipmentId = map[i]
                    if (equipmentId != null) {
                        showEquipment(equipmentId, R.id.equipment5, i)
                    }
                    count++
                }

            }
        }
    }

    //添加稱號欄位
    private fun addTitle() {
        val scrollViewLayout = findViewById<LinearLayout>(R.id.showTitle)

        //清除稱號欄位
        scrollViewLayout.removeAllViews()


        for(title in haveTitle.reversed()) {
            val customView = TitleView(this, null)
            customView.setting(title)
            customView.tag = title

            //View布局
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.gravity = Gravity.CENTER
            layoutParams.bottomMargin = 20

            //背景顏色
            customView.setBackgroundColor(Color.parseColor("#CCFFFFFF"))

            // 添加 CustomImageViewTextView 到 ScrollView 的子視圖中
            customView.layoutParams = layoutParams

            //如果是穿戴中的打勾
            if (title == wear) {
                customView.visible(View.VISIBLE)
                customView.setTextColor(Color.BLACK)
            } else {
                customView.visible(View.INVISIBLE)
            }

            //設置每個動作
            customView.setOnClickListener { view ->
                wear = view.tag.toString()
                changeTitle(view.tag.toString())

                val playerInfoDatabaseCollectionName = "PlayerInfo"
                val titleDatabaseCollectionName = "Title"

                val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)

                val db = FirebaseFirestore.getInstance()
                val docRef = db.collection(playerInfoDatabaseCollectionName)
                    .document(sharedPreferences.getString("ID", "-1").toString())
                val titleRef = db.collection(titleDatabaseCollectionName)
                docRef.get().addOnSuccessListener {
                    titleRef.whereEqualTo("TitleName",wear).get().addOnSuccessListener { docs->
                        for (doa in docs){
                            val updates = hashMapOf(
                                "TitleNumber" to Integer.parseInt(doa.id)
                            )
                            docRef.update(updates as Map<String, Any>)
                        }
                    }
                }
                onResume()
            }

            scrollViewLayout.addView(customView)
        }

    }

    private fun clearEquipment(){
        val wear = findViewById<LinearLayout>(R.id.wear)
        for (i in 0 until wear.childCount){
            val child: View = wear.getChildAt(i)
            child.visibility=View.INVISIBLE
        }
    }

    //裝備及稱號頁面切換
    private fun change(open: Int, close: Int) {
        val closeView = findViewById<LinearLayout>(close)
        val openView = findViewById<LinearLayout>(open)

        closeView.visibility = View.INVISIBLE
        openView.visibility = View.VISIBLE
    }

    //從資料庫取得裝備中的物品
    private fun getEquipment() {
        val playerInfoDatabaseCollectionName = "PlayerInfo"


        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(playerInfoDatabaseCollectionName)
            .document(sharedPreferences.getString("ID", "-1").toString())

        docRef.get().addOnSuccessListener {doc ->
            val text = doc.getString("Equipment")
            if (text != null) {
                for(title in text.split(Regex(","))){
                    if(title!=""){
                        equipmentNum.add(title)
                    }

                }
                showWearEquipment()
            }

        }

    }

    //改變稱號
    private fun changeTitle(title :String){
        wear= title
        val titleView = findViewById<TextView>(R.id.titleNames)
        titleView.text = wear

    }

    //擁有的稱號
    private fun haveTitle(){
        val playerInfoDatabaseCollectionName = "PlayerInfo"
        val titleDatabaseCollectionName = "Title"


        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(playerInfoDatabaseCollectionName)
            .document(sharedPreferences.getString("ID", "-1").toString())
        val titleRef = db.collection(titleDatabaseCollectionName)

        docRef.get().addOnSuccessListener {doc ->
            val text = doc.getString("TitlesOwned")
            if (text != null) {
                for(title in text.split(Regex(","))){
                    titleRef.document(title).get().addOnSuccessListener {docs ->
                        haveTitle.add(docs.getString("TitleName").toString())
                    }
                }
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