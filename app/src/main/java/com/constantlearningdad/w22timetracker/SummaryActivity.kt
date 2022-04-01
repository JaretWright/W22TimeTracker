package com.constantlearningdad.w22timetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.constantlearningdad.w22timetracker.databinding.ActivitySummaryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SummaryActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //query the firestore database to get all the projects for the active user
        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        if (userID == null)
            finish()

        //Connect to Firestore collection called projects
        val db = FirebaseFirestore.getInstance().collection("projects")

        //create a mutable (changeable) list of Project objects
        val projects : MutableList<Project?> = ArrayList()

        //create an adapter for the spinner so that we can change the ArrayList in the spinner
        val adapter = ArrayAdapter(applicationContext, R.layout.spinner_item, projects)
        binding.projectSelectSpinner.adapter = adapter

        db.whereEqualTo("uid", userID)
            .orderBy("projectName")
            .get()
            .addOnSuccessListener { querySnapshot ->
                projects.add(Project(projectName="Choose a Project"))
            for (document in querySnapshot)
            {
                val project = document.toObject(Project::class.java)
                projects.add(project)
            }
            adapter.notifyDataSetChanged()
        }

        //create a "listener" for the spinner
        binding.projectSelectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i("Spinner", "position = $position, which is project: ${projects.get(position)}")

                val projectSelected = projects.get(position)

                projectSelected!!.timeRecords?.let{
                    val research = it.filter{timeRecord -> timeRecord.activity.equals("Research")}
                                    .map{timeRecord -> timeRecord.getDuration()}
                                    .sum()
                    binding.researchMinTextView.text = research.toString()

                    val design = it.filter{timeRecord -> timeRecord.activity.equals("Design")}
                        .map{timeRecord -> timeRecord.getDuration()}
                        .sum()
                    binding.designMinTextView.text = design.toString()

                    val development = it.filter{timeRecord -> timeRecord.activity.equals("Development")}
                        .map{timeRecord -> timeRecord.getDuration()}
                        .sum()
                    binding.developmentMinTextView.text = development.toString()

                    val testing = it.filter{timeRecord -> timeRecord.activity.equals("Testing")}
                        .map{timeRecord -> timeRecord.getDuration()}
                        .sum()
                    binding.testingMinTextView.text = testing.toString()

                    val other = it.filter{timeRecord -> timeRecord.activity.equals("Other")}
                        .map{timeRecord -> timeRecord.getDuration()}
                        .sum()
                    binding.otherMinTextView.text = other.toString()

                    val totalInMin = it.map { timeRecord -> timeRecord.getDuration()  }
                                        .sum()
                    binding.totalMinTextView.text = totalInMin.toString()
                    binding.totalInHours.text = ("Total in Hours: "+totalInMin/60)
                }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
//                TODO("Not yet implemented")
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
//                startActivity(Intent(applicationContext, SummaryActivity::class.java))
                return true
            }
            R.id.action_edit_profile -> {
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}