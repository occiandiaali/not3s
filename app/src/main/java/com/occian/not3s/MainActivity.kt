package com.occian.not3s

import android.app.SearchManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row.view.*
import android.view.View as View1

class MainActivity : AppCompatActivity() {

    var listNotes = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Load from DB
        LoadQuery("%")
        if (listNotes.isEmpty()) {
            val inflater = layoutInflater
            val container: ViewGroup? = findViewById(R.id.custom_toast_container)
            val layout: ViewGroup = inflater.inflate(R.layout.custom_toast, container) as ViewGroup
            val text: TextView = layout.findViewById(R.id.text)
            text.text = "This place looks so empty!"
            text.textSize = 27F
            text.gravity = Gravity.CENTER
            with (Toast(this@MainActivity)) {
                setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
                duration = Toast.LENGTH_LONG
                view = layout
                show()
            }

        } // graphic shows when no note has been created
    } // on create

    override fun onResume() {
        super.onResume()
        LoadQuery("%")
    }

    private fun LoadQuery(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections, "Title like ?", selectionArgs, "Title")
        listNotes.clear()
        if (cursor.moveToFirst()) {

            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(ID, Title, Description))

            } while (cursor.moveToNext())
        }

        //adapter
        var myNotesAdapter = MyNotesAdapter(this, listNotes)
        //set adapter
        not3sLv.adapter = myNotesAdapter

        //get total number of tasks from ListView
        val total = not3sLv.count
        //actionbar
        val mActionBar = supportActionBar
        if (mActionBar != null) {
            //set to actionbar as subtitle of actionbar
            mActionBar.subtitle = "$total note(s)"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        //searchView
        val sv: SearchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView

        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                LoadQuery("%" + query + "%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                LoadQuery("%" + newText + "%")
                return false
            }
        });

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.addNote -> {
                    val addNoteIntent = Intent(this@MainActivity, AddNoteActivity::class.java)
                    addNoteIntent.putExtra("note", "New Note")
                    startActivity(addNoteIntent)
                    //startActivity(Intent(this, AddNoteActivity::class.java))
                }
                R.id.action_settings -> {
                    val settingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)
                    settingsIntent.putExtra("settings", "Settings")
                    startActivity(settingsIntent)
                }
                R.id.app_info -> {
                    val infoIntent = Intent(this@MainActivity, InfoActivity::class.java)
                    infoIntent.putExtra("info", "Info")
                    startActivity(infoIntent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class MyNotesAdapter : BaseAdapter {
        var listNotesAdapter = ArrayList<Note>()
        var context: Context? = null

        constructor(context: Context, listNotesAdapter: ArrayList<Note>) : super() {
            this.listNotesAdapter = listNotesAdapter
            this.context = context
        }



        override fun getView(position: Int, convertView: View1?, parent: ViewGroup?): View1 {
            //inflate layout row.xml
            var myView = layoutInflater.inflate(R.layout.row, null)
            val myNote = listNotesAdapter[position]
            myView.titleTv.text = myNote.nodeName
            myView.descTv.text = myNote.nodeDesc

           fun confirmDelete() {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Delete Note?")
                builder.setMessage("Are you sure? This action cannot be reversed!")

                // builder buttons
                builder.setPositiveButton("Yes") { _, which ->
                    var dbManager = DbManager(this.context!!)
                    val selectionArgs = arrayOf(myNote.nodeID.toString())
                    dbManager.delete("ID=?", selectionArgs)
                    LoadQuery("%")
                    Toast.makeText(this@MainActivity, "This Note is gone forever", Toast.LENGTH_SHORT).show()
                } // positive button
               builder.setNegativeButton("Cancel") { _, which ->
                   Toast.makeText(this@MainActivity, "Note not deleted", Toast.LENGTH_SHORT).show()
               }
               builder.show()
            } // confirm delete

            //delete button click
            myView.deleteBtn.setOnClickListener {
                confirmDelete()
//                var dbManager = DbManager(this.context!!)
//                val selectionArgs = arrayOf(myNote.nodeID.toString())
//                dbManager.delete("ID=?", selectionArgs)
//                LoadQuery("%")
            }

            myView.setOnClickListener { GoToUpdateFun(myNote) }
            //edit//update button click
//            myView.editBtn.setOnClickListener {
//                GoToUpdateFun(myNote)
//            }
            //copy btn click
            myView.copyBtn.setOnClickListener {
                //get title
                val title = myView.titleTv.text.toString()
                //get description
                val desc = myView.descTv.text.toString()
                //concatinate
                val s = title + "\n" + desc
                val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cb.text = s // add to clipboard
                Toast.makeText(this@MainActivity, "Copied...", Toast.LENGTH_SHORT).show()
            }
            //share btn click
            myView.shareBtn.setOnClickListener {
                //get title
                val title = myView.titleTv.text.toString()
                //get description
                val desc = myView.descTv.text.toString()
                //concatenate
                // val s = title + "\n" + desc
                //share intent
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
                shareIntent.putExtra(Intent.EXTRA_TEXT, desc)
                startActivity(Intent.createChooser(shareIntent, "Send via"))
            }

            return myView
        }

        override fun getItem(position: Int): Any {
            return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }

    }

    private fun GoToUpdateFun(myNote: Note) {
        var intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("edit", "Edit Nota") // edit
        intent.putExtra("ID", myNote.nodeID) //put id
        intent.putExtra("name", myNote.nodeName) //ut name
        intent.putExtra("des", myNote.nodeDesc) //put description
        startActivity(intent) //start activity
    }
} // class
