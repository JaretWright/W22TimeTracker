package com.constantlearningdad.w22timetracker

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.constantlearningdad.w22timetracker.databinding.ActivityCreateProjectBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CreateProjectActivity : AppCompatActivity() {
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

            if (projectName.isNotEmpty() && description.isNotEmpty())
            {
                //connect to the Firestore DB
                val db = FirebaseFirestore.getInstance().collection("projects")

                //get a unique id from Firestore
                val id = db.document().getId()

                //create a Project object
                var uID = auth.currentUser!!.uid
                var project = Project(projectName, description, id, uID, ArrayList<TimeRecord>())

                //save our new Project object to the db using the unique id
                db.document(id).set(project)
                    .addOnSuccessListener { Toast.makeText(this,"DB Updated",Toast.LENGTH_LONG).show() }
                    .addOnFailureListener { exception -> Log.w("DB_Issue", exception!!.localizedMessage) }
            }
            else
                Toast.makeText(this, "name and description must be filled in", Toast.LENGTH_LONG).show()
        }

        //connect RecyclerView with FirestoreDB via the ViewModel
        val viewModel : ProjectViewModel by viewModels()
        viewModel.getProjects().observe(this, {
            for (project in it)
                Log.i("DB_Response","inside CreateProjectActivity, project: $project")
        })
    }
}