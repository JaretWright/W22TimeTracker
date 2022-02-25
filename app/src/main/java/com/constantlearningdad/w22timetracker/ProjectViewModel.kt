package com.constantlearningdad.w22timetracker

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

//Java equivalent public class ProjectViewModel extends ViewModel;
class ProjectViewModel : ViewModel() {
    private val projects = MutableLiveData<List<Project>>()
    private var auth : FirebaseAuth

    /**
     * This is called after the constructor runs and can be used to setup our live data
     */
    init{
        auth=Firebase.auth
        val userID = auth.currentUser?.uid

        //query the DB to get all the Projects for a specific user
        val db = FirebaseFirestore.getInstance().collection("projects")
            .whereEqualTo("uid", userID)
            .orderBy("projectName")
            .addSnapshotListener{ documents, exception ->
                if (exception != null)
                {
                    Log.w("DB_Response","Listen Failed ${exception.code}")
                    return@addSnapshotListener
                }

                Log.i("DB_Response", "# of documents = ${documents!!.size()}")
                //loop over the documents and create Project objects
                documents?.let{
                    val projectList = ArrayList<Project>()
                    for (document in documents)
                    {
                        Log.i("DB_Response", "${document.data}")

                        //convert the JSON document into a Project object
                        val project = document.toObject(Project::class.java)
                        projectList.add(project)
                    }
                    projects.value = projectList
                }
            }
    }

    fun getProjects() : LiveData<List<Project>>
    {
        return projects
    }

}