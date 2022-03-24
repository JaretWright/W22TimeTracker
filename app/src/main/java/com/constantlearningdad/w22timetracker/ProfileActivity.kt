package com.constantlearningdad.w22timetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import com.constantlearningdad.w22timetracker.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    private val authDB = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //this allows the scrollbars to ... scroll
        binding.termsTextViw.movementMethod = ScrollingMovementMethod()

        //ensure we have an authenticated user
        if (authDB.currentUser == null)
            logout()
        else
        {
            authDB.currentUser?.let{
                binding.usersNameTextView.text = it.displayName
                binding.emailTextView.text = it.email
            }
        }
        //configure the toolbar to hold the main_menu
        setSupportActionBar(binding.mainToolBar.toolbar)
    }

    /**
     * Add the menu to the toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /**
     * This method will navigate to the appropriate activity when an icon is selected in the toolbar
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_log_time -> {
                startActivity(Intent(applicationContext, LogTimeActivity::class.java))
                return true
            }
            R.id.action_add_project -> {
                startActivity(Intent(applicationContext, CreateProjectActivity::class.java))
                return true
            }
            R.id.action_view_summary -> {
                startActivity(Intent(applicationContext, SummaryActivity::class.java))
                return true
            }
            R.id.action_edit_profile -> {
//                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout()
    {
        authDB.signOut()
        finish()
        startActivity(Intent(this, SigninActivity::class.java))
    }
}