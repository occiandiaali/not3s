package com.occian.not3s

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_note.*

class AddNoteActivity : AppCompatActivity() {

    val dbTable = "Not3sTable"
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        try {
            val bundle:Bundle? = intent.extras
            val actionBar = supportActionBar

            if (bundle != null) {
                if (bundle.containsKey("edit")) { actionBar?.title = "Edit Note" }
                if (bundle.containsKey("note")) { actionBar?.title = "New Note" }
            }

            if (bundle != null) {
                id = bundle.getInt("ID", 0)
            }
            if (id!=0){
                if (bundle != null) {
                    titleEt.setText(bundle.getString("name"))
                }
                if (bundle != null) {
                    descEt.setText(bundle.getString("des"))
                }
            }
        }catch (ex:Exception){}

    } // on create

    fun addFunc(view: View){
        var dbManager = DbManager(this)

        var values = ContentValues()
        values.put("Title", titleEt.text.toString())
        values.put("Description", descEt.text.toString())

        if (id ==0){
            val ID = dbManager.insert(values)
            if (ID>0){
                Toast.makeText(this, "Things went well..", Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(this, "Error adding Note...", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            var selectionArgs = arrayOf(id.toString())
            val ID = dbManager.update(values, "ID=?", selectionArgs)
            if (ID>0){
                Toast.makeText(this, "Looking good..", Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(this, "Error adding Not3...", Toast.LENGTH_SHORT).show()
            }
        }
    }
} // class
