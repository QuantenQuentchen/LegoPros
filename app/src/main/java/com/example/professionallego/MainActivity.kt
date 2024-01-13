package com.example.professionallego

import android.app.AlertDialog
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
import androidx.lifecycle.ViewModelProvider
import com.example.professionallego.databinding.ActivityMainBinding
import com.example.professionallego.data.AppSharedViewModel
import com.example.professionallego.data.sqlliteDBHelper

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: AppSharedViewModel
    private lateinit var DBHelper: sqlliteDBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener {
            showAlertWithEditText()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        DBHelper = sqlliteDBHelper(this)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        model = ViewModelProvider(this)[AppSharedViewModel::class.java]
        //Wonky Lifecycle shit, but fuck you I don't get paid enough for a room
        model.loadLegoBoxes()
        model.loadHistories()
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
                model.addBox(model.currentLegoBoxBoxId, inputNumber)
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
                model.addBox(model.currentLegoBoxBoxId, inputNumber)
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

    override fun onDestroy() {
        super.onDestroy()
        model.saveHistoryData()
        model.saveLegoBoxData()
    }

}