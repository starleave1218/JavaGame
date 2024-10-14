package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class FightAddQuestion : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fight_add_question)
        val database = "GiveQuestion"
        val question: EditText = findViewById(R.id.questionInput)
        val selectA: EditText = findViewById(R.id.selectAInput)
        val selectB: EditText = findViewById(R.id.selectBInput)
        val selectC: EditText = findViewById(R.id.selectCInput)
        val selectD: EditText = findViewById(R.id.selectDInput)
        val answer: Spinner = findViewById(R.id.answerInput)
        val send: Button = findViewById(R.id.send)
        val back: Button = findViewById(R.id.back)

        send.setOnClickListener {
            val data: MutableMap<String, Any> = HashMap()
            data["Info"] = question.text.toString()
            data["SelectA"] = selectA.text.toString()
            data["SelectB"] = selectB.text.toString()
            data["SelectC"] = selectC.text.toString()
            data["SelectD"] = selectD.text.toString()
            data["Answer"] = "Select" + answer.selectedItem.toString()

            if ("" == data["Info"] || "" == data["SelectA"] || "" == data["SelectB"] ||
                "" == data["SelectC"] || "" == data["SelectD"] || "" == database.trim()
            ) {
                Toast.makeText(this, "請避免空值", Toast.LENGTH_SHORT).show()
            } else {
                // 存取資料庫
                val dbCollection =
                    FirebaseFirestore.getInstance().collection(database)
                dbCollection.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val querySnapshot = task.result
                        if (querySnapshot.isEmpty) {
                            // 集合不存在
                            Toast.makeText(this, "集合不存在!", Toast.LENGTH_SHORT).show()
                        } else {
                            // 集合存在
                            dbCollection.add(data)
                                .addOnSuccessListener { documentReference -> // 新增成功
                                    Log.d(
                                        TAG,
                                        "DocumentSnapshot added with ID: " + documentReference.id
                                    )
                                    question.setText("")
                                    selectA.setText("")
                                    selectB.setText("")
                                    selectC.setText("")
                                    selectD.setText("")
                                    Toast.makeText(this, "新增題目成功!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e -> // 新增失敗
                                    Log.w(TAG, "Error adding document", e)
                                }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                    }
                }
            }
        }
        back.setOnClickListener {
            finish()
        }
    }
}