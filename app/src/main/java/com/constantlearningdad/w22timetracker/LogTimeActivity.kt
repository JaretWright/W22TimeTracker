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
import android.widget.Toast
import com.constantlearningdad.w22timetracker.databinding.ActivityLogTimeBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LogTimeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLogTimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get the course information and update the header
        val projectID = intent.getStringExtra("projectID")

        val db = FirebaseFirestore.getInstance().collection("projects")

        //query the firestore database to get all the projects for the active user
        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        if (userID == null)
        {
            finish()
            startActivity(Intent(this, SigninActivity::class.java))
        }

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
                //if we have a projectID from the intent, set the spinner with it
                projectID?.let {
                    var projectSelected = Project()
                    for (project in projects)
                    {
                        if (project!!.id.equals(projectID))
                            projectSelected =project
                    }
                    if (projectSelected.id.equals(projectID))
                        binding.projectSelectSpinner.setSelection(projects.indexOf(projectSelected))
                }
                adapter.notifyDataSetChanged()
            }

        var project = Project()
        //create a "listener" for the spinner
        binding.projectSelectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i(
                    "Spinner",
                    "position = $position, which is project: ${projects.get(position)}"
                )

                project = projects.get(position)!!

                }

            override fun onNothingSelected(p0: AdapterView<*>?) {
//                TODO("Not yet implemented")
            }
        }

        //variables to store the category, start & stop times
        var startTime: Timestamp? = null
        var finishTime: Timestamp? = null
        var category: String? = null

        //if the start time hasn't been clicked already, set the start time
        binding.startButton.setOnClickListener {
            if (binding.startTextView.text.toString().isNullOrBlank()) {
                startTime = Timestamp.now()
                binding.startTextView.text = startTime!!.toDate().toString()
            }
        }

        //if a category is selected and the start time has been pushed, establish the end time
        //and store in the DB
        binding.finishButton.setOnClickListener {
            if (startTime != null && binding.spinner.selectedItemPosition > 0) {
                category = binding.spinner.selectedItem.toString()
                finishTime = Timestamp.now()
                binding.finishTextView.text = finishTime!!.toDate().toString()

                //create a TimeRecord object and display the duration
                val timeRecord = TimeRecord(category, startTime, finishTime)
                binding.totalTimeTextView.text =
                    String.format("Total Time: %d minutes", timeRecord.getDuration())

                //update the project with the new time record and save it to the database
                project.addTimeRecord(timeRecord)
                project?.let {
                    db.document(project.id!!).set(project)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "DB updated",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this,
                                "DB write failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            } else {
                Toast.makeText(this, "Start Time or Category not selected", Toast.LENGTH_LONG)
                    .show()
            }
        }

        //setup a click listener for the floating action button to navigate to the maps activity
        binding.mapsButton.setOnClickListener {
            var intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("projectID", projectID)
            startActivity(intent)
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
//                startActivity(Intent(applicationContext, LogTimeActivity::class.java))
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
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
