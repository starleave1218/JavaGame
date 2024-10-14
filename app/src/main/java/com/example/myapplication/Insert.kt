package com.example.myapplication

import android.content.ContentValues
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class Insert : AppCompatActivity() {

    private lateinit var layoutContainer: LinearLayout
    private var editTextCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)

        layoutContainer = findViewById(R.id.parent_view_group)
        val radioGroup = findViewById<RadioGroup>(R.id.database)
        val sure = findViewById<Button>(R.id.sure)



        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.users -> {
                    // 执行选中 RadioButton 1 的操作
                    Toast.makeText(this, "你選中了 users", Toast.LENGTH_SHORT).show()
                    deleteEditText()
                    addEditText("account")
                    addEditText("password")
                    addEditText("name")
                    addEditText("lv")
                    addEditText("money")
                    addEditText("history")

                    sure.setOnClickListener {
                        val con = checkField()

                        if(con){
                            writeUsers()
                        }


                    }
                }

                R.id.questionsType -> {
                    // 执行选中 RadioButton 3 的操作
                    Toast.makeText(this, "你選中了 questionsType", Toast.LENGTH_SHORT).show()
                    deleteEditText()
                    addEditText("Question")
                    addEditText("a")
                    addEditText("b")
                    addEditText("c")
                    addEditText("d")
                    addEditText("ans")

                    sure.setOnClickListener {
                        val con = checkField()


                        if(con){
                            writeQuestion()
                            clearText()
                        }


                    }

                }
            }
        }

    }
    private fun addEditText(str:String) {
        val editText = EditText(this)
        editText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        editTextCount++
        editText.id=editTextCount
        editText.hint = str
        layoutContainer.addView(editText)

    }
    private fun deleteEditText() {
        while (editTextCount > 0) {
            val lastEditText = findViewById<EditText>(editTextCount)
            layoutContainer.removeView(lastEditText)
            editTextCount--
        }
    }

    private fun checkField() :Boolean {
        var id: Any
        for(i in editTextCount downTo 1){
            id=i
            val checkEditText = findViewById<EditText>(id)
            if(checkEditText.text.toString()==""){
                Toast.makeText(this,"所有欄位不可為空!!!",Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun clearText(){
        var id :Any
        for(i in editTextCount downTo 1){
            id = i
            val editText = findViewById<EditText>(id)
            editText.text.clear()
        }
    }

    private  fun writeUsers(){
        val usersDatabaseCollectionName = "users"
        val db = FirebaseFirestore.getInstance()

        var id =1
        val account = findViewById<EditText>(id)
        id ++
        val password = findViewById<EditText>(id)
        id ++
        val name = findViewById<EditText>(id)
        id ++
        val lv = findViewById<EditText>(id)
        id ++
        val money = findViewById<EditText>(id)
        id ++
        val history = findViewById<EditText>(id)

        db.collection(usersDatabaseCollectionName).whereEqualTo("account",account.text.toString()).get()
            .addOnSuccessListener { documents ->
                if (documents.size() == 0) {

                    //查是否重複名稱
                    db.collection("properties").whereEqualTo("name",name.text.toString()).get()
                        .addOnSuccessListener{doc ->
                            if(doc.size()==0){
                                // 將資料存放在data
                                val data = hashMapOf(
                                    "account" to account.text.toString(),
                                    "password" to password.text.toString(),
                                )


                                db.collection(usersDatabaseCollectionName)
                                    .get().addOnSuccessListener {documents ->
                                        val totalDocuments = documents.size()
                                        Log.e("Test",totalDocuments.toString())

                                        val writeUser = db.collection("users").document(totalDocuments.toString())
                                        val writeData = db.collection("properties").document(totalDocuments.toString())

                                        //將 data 寫入資料庫
                                        writeUser.set(data)

                                        val data2 = hashMapOf(
                                            "name" to name.text.toString(),
                                            "lv" to Integer.parseInt(lv.text.toString()),
                                            "history" to Integer.parseInt(history.text.toString()),
                                            "money" to Integer.parseInt(money.text.toString()),

                                            )
                                        //將資料寫入資料庫
                                        Log.d("test", "success!")
                                        writeData.set(data2)

                                        //顯示寫入成功的彈窗
                                        Toast.makeText(this, "寫入成功!", Toast.LENGTH_SHORT).show()
                                        Log.d(ContentValues.TAG, "Input success!")
                                        clearText()
                                }

                            }
                            else{
                                Toast.makeText(this, "此名稱已存在!", Toast.LENGTH_SHORT).show()
                            }
                        }

                } else {
                    //顯示註冊失敗的彈窗
                    Toast.makeText(this, "帳號已存在!", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private  fun writeQuestion(){
        val questionsTypeDatabaseCollectionName = "questionsType"
        val db = FirebaseFirestore.getInstance()

        var id =1
        val question = findViewById<EditText>(id)
        id ++
        val a = findViewById<EditText>(id)
        id ++
        val b = findViewById<EditText>(id)
        id ++
        val c = findViewById<EditText>(id)
        id ++
        val d = findViewById<EditText>(id)
        id ++
        val ans = findViewById<EditText>(id)

        val data = hashMapOf(
            "Question" to question.text.toString(),
            "a" to a.text.toString(),
            "b" to b.text.toString(),
            "c" to c.text.toString(),
            "d" to d.text.toString(),
            "ans" to ans.text.toString()
        )

        val writeData = db.collection(questionsTypeDatabaseCollectionName).document()
        writeData.set(data)
        Log.d("test", "success!")

        //顯示寫入成功的彈窗
        Toast.makeText(this, "寫入成功!", Toast.LENGTH_SHORT).show()
        Log.d(ContentValues.TAG, "Input success!")

    }


}


