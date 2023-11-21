package com.example.professionallego

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.professionallego.databinding.ActivityMainBinding
import com.example.professionallego.ui.AppSharedViewModel
import com.example.professionallego.ui.LegoBoxBox.LegoBoxBoxData
import com.example.professionallego.ui.calculator.CalculatorOutputData
import com.example.professionallego.ui.history.HistoryData
import com.example.professionallego.ui.legoBox.LegoItemData

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: AppSharedViewModel;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            showAlertWithEditText();
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        model = ViewModelProvider(this)[AppSharedViewModel::class.java]
        //Wonky Lifecycle shit, but fuck you I don't get paid enough for a room
        model.LegoBoxBox = MutableLiveData(getBoxBoxData())
        model.History = MutableLiveData(getHistoryData())
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_calculator, R.id.nav_lego_box_box, R.id.nav_history
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener() { _, destination, _ ->
            if (destination.id == R.id.nav_lego_box) {
                binding.appBarMain.fab.show()
            } else {
                binding.appBarMain.fab.hide()
            }
        }
    }

    private fun showAlertWithEditText() {
        val editText = EditText(this)
        editText.inputType = InputType.TYPE_CLASS_NUMBER

        val dialog = AlertDialog.Builder(this)
            .setTitle(this.getString(R.string.add_lego_box_alert_title))
            .setMessage(this.getString(R.string.add_lego_box_alert_message))
            .setView(editText)
            .setPositiveButton(this.getString(R.string.add_lego_box_alert_pos_btn)) { dialog, which ->
                // Handle positive button click
                val inputString = editText.text.toString()
                val inputNumber = inputString.toIntOrNull() ?: 0
                model.addLegoBox(model.currentLegoBoxBoxId, inputNumber)
                dialog.dismiss()
                // Do something with the input
            }
            .setNegativeButton(this.getString(R.string.add_lego_box_alert_neg_btn)) { dialog, which ->
                dialog.cancel()
            }
            .show()
        editText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                val inputString = editText.text.toString()
                val inputNumber = inputString.toIntOrNull() ?: 0
                model.addLegoBox(model.currentLegoBoxBoxId, inputNumber)
                dialog.dismiss()
                true
            } else {
                false
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun saveBoxBoxData(legoBoxBoxData: LegoBoxBoxData){
        val db = sqlliteDBHelper(this).writableDatabase
        val parentValues = ContentValues().apply{
            put("name", legoBoxBoxData.name)
            put("created_at", legoBoxBoxData.createdAt)
            put("id", legoBoxBoxData.id)
        }
        db.execSQL("DELETE FROM legoboxbox")
        db.insertWithOnConflict("legoboxbox", null, parentValues, SQLiteDatabase.CONFLICT_REPLACE)
        db.execSQL("DELETE FROM legobox")
        for (child in legoBoxBoxData.LegoBox!!){
            val childValues = ContentValues().apply{
                put("name", child.name)
                put("size", child.size)
                put("id", child.id)
                put("parent_id", legoBoxBoxData.id)
            }
            db.insertWithOnConflict("legobox", null, childValues, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }
    private fun saveHistoryData(hisoryData: HistoryData){
        val db = sqlliteDBHelper(this).writableDatabase
        val parentValues = ContentValues().apply{
            put("input", hisoryData.input)
            put("timestamp", hisoryData.timestamp)
            put("id", hisoryData.id)
            put("lego_box_box_id", hisoryData.legoBoxId)
        }
        db.execSQL("DELETE FROM history")
        db.insertWithOnConflict("history", null, parentValues, SQLiteDatabase.CONFLICT_REPLACE)
        db.execSQL("DELETE FROM outputData")
        for(child in hisoryData.output){
            val childValues = ContentValues().apply{
                put("name", child.name)
                put("size", child.size)
                put("id", child.id)
                put("parent_id", hisoryData.id)
            }
            db.insertWithOnConflict("outputData", null, childValues, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }
    private fun getBoxBoxData():Map<Int, LegoBoxBoxData>{
        var legbbdata: MutableMap<Int, LegoBoxBoxData> = mutableMapOf()
        val db = sqlliteDBHelper(this).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM legoboxbox", null)
        if(cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id")?: return legbbdata)
                val name = cursor.getString(cursor.getColumnIndex("name")?: return legbbdata)
                val createdAt = cursor.getLong(cursor.getColumnIndex("created_at")?: return legbbdata)
                val legoBox = getLegoBoxData(id)
                var legb: LegoBoxBoxData = LegoBoxBoxData(name, createdAt, id)
                legb.LegoBox = legoBox
                legbbdata[id] = legb
            } while (cursor.moveToNext())
        }
        return legbbdata as Map<Int, LegoBoxBoxData>
    }
    private fun getLegoBoxData(id: Int): ArrayList<LegoItemData>{
        val db = sqlliteDBHelper(this).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM legobox WHERE parent_id = $id", null)
        var legoBox: ArrayList<LegoItemData> = ArrayList()
        if(cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id")?: return legoBox)
                val name = cursor.getString(cursor.getColumnIndex("name")?: return legoBox)
                val size = cursor.getInt(cursor.getColumnIndex("size")?: return legoBox)
                legoBox.add(LegoItemData(id, name, size))
            } while (cursor.moveToNext())
        }
        return legoBox
    }
    private fun getHistoryData(): ArrayList<HistoryData>{
        val db = sqlliteDBHelper(this).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM history", null)
        var history: ArrayList<HistoryData> = ArrayList()
        if(cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id")?: return history)
                val input = cursor.getInt(cursor.getColumnIndex("input")?: return history)
                val timestamp = cursor.getLong(cursor.getColumnIndex("timestamp")?: return history)
                val legoBoxId = cursor.getInt(cursor.getColumnIndex("lego_box_box_id")?: return history)
                val output = getOutputData(id)
                history.add(HistoryData(id, timestamp, legoBoxId, input, output))
            } while (cursor.moveToNext())
        }
        return history
    }
    private fun getOutputData(id:Int): ArrayList<CalculatorOutputData>{
        val db = sqlliteDBHelper(this).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM outputData WHERE parent_id = $id", null)
        var output: ArrayList<CalculatorOutputData> = ArrayList()
        if(cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id")?: return output)
                val name = cursor.getString(cursor.getColumnIndex("name")?: return output)
                val size = cursor.getInt(cursor.getColumnIndex("size")?: return output)
                output.add(CalculatorOutputData(id, name, size, 1))
            } while (cursor.moveToNext())
        }
        return output
    }

    override fun onDestroy() {
        super.onDestroy()
        for(i in model.LegoBoxBox.value?.values?: return){
            saveBoxBoxData(i)
        }
        for(i in model.History.value?: return){
            saveHistoryData(i)
        }
    }

}