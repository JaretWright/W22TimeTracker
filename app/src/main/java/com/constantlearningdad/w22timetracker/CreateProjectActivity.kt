package com.constantlearningdad.w22timetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.constantlearningdad.w22timetracker.databinding.ActivityCreateProjectBinding
import com.google.firebase.firestore.FirebaseFirestore

class CreateProjectActivity : AppCompatActivity() {
    //private ActivityCreateProjectBinding binding;  Java version of declaring the variable
    private lateinit var binding : ActivityCreateProjectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createProjectButton.setOnClickListener {
            var projectName = binding.projectNameEditText.text.toString().trim()
            var description = binding.descriptionEditText.text.toString().trim()

            if (projectName.isNotEmpty() && description.isNotEmpty())
            {
                //create a Project object
                var project = Project(projectName, description)

                //connect to the Firestore DB
                val db = FirebaseFirestore.getInstance().collection("projects")

                //get a unique id from Firestore
                val id = db.document().getId()
                project.id = id

                //save our new Project object to the db using the unique id
                db.document(id).set(project)
                    .addOnSuccessListener { Toast.makeText(this,"DB Updated",Toast.LENGTH_LONG).show() }
                    .addOnFailureListener { exception -> Log.w("DB_Issue",exception.localizedMessage) }
            }
            else
                Toast.makeText(this, "name and description must be filled in", Toast.LENGTH_LONG).show()
        }
    }
}