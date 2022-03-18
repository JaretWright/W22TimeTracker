package com.constantlearningdad.w22timetracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.constantlearningdad.w22timetracker.databinding.ActivityCreateProjectBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CreateProjectActivity : AppCompatActivity(), ProjectAdapter.ProjectItemListener {
    //private ActivityCreateProjectBinding binding;  Java version of declaring the variable
    private lateinit var binding : ActivityCreateProjectBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.createProjectButton.setOnClickListener {
            var projectName = binding.projectNameEditText.text.toString().trim()
            var description = binding.descriptionEditText.text.toString().trim()

            if (projectName.isNotEmpty() && description.isNotEmpty()) {
                //connect to the Firestore DB
                val db = FirebaseFirestore.getInstance().collection("projects")

                //get a unique id from Firestore
                val id = db.document().getId()

                //create a Project object
                var uID = auth.currentUser!!.uid
                var project = Project(projectName, description, id, uID, ArrayList<TimeRecord>())

                //save our new Project object to the db using the unique id
                db.document(id).set(project)
                    .addOnSuccessListener {
                        Toast.makeText(this, "DB Updated", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { exception ->
                        Log.w(
                            "DB_Issue",
                            exception!!.localizedMessage
                        )
                    }
            } else
                Toast.makeText(this, "name and description must be filled in", Toast.LENGTH_LONG)
                    .show()
        }

        //connect RecyclerView with FirestoreDB via the ViewModel
        val viewModel: ProjectViewModel by viewModels()
        viewModel.getProjects().observe(this, { projects ->
            binding.recyclerView.adapter = ProjectAdapter(this, projects, this)
//            binding.linearLayout.removeAllViews()
//            for (project in projects)
//            {
//                var newProjectTextView = TextView(this)
//                newProjectTextView.text = project.toString()
//                binding.linearLayout.addView(newProjectTextView)
//            }
        })

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
//                startActivity(Intent(applicationContext, CreateProjectActivity::class.java))
                return true
            }
            R.id.action_view_summary -> {
                //page to be created
                return true
            }
            R.id.action_edit_profile -> {
                //page to be created
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun projectSelected(project: Project) {
        var intent = Intent(this, LogTimeActivity::class.java)
        intent.putExtra("projectID", project.id)
        startActivity(intent)
    }

}

