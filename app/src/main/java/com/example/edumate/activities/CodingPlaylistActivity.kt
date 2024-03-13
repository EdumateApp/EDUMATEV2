package com.example.edumate.activities

import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edumate.R
import com.example.edumate.adapters.StudyPlaylistAdapter
import com.example.edumate.models.StudyPlaylist
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class CodingPlaylistActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private var codingPlaylistList = mutableListOf<StudyPlaylist>()
    private lateinit var adapter: StudyPlaylistAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coding_playlist)
        firestore = FirebaseFirestore.getInstance()

        setUpViews()
    }

    private fun setUpViews() {
        setUpToolbar()
        setUpStudyPlayList()
        setUpRecyclerView()
        setUpSearchView()
    }

    private fun setUpSearchView() {
        val searchView: androidx.appcompat.widget.SearchView = findViewById(R.id.playlistSearchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    // Filtering the list using search View
    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = mutableListOf<StudyPlaylist>()
            for (i in codingPlaylistList) {
                if (i.title.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }
            if (filteredList.isEmpty()) {
                Toast.makeText(this@CodingPlaylistActivity, "No Data found", Toast.LENGTH_SHORT)
                    .show()
            } else {
                adapter.setFilteredList(filteredList)
            }
        }
    }

    private fun setUpStudyPlayList() {
        val collectionReference = firestore.collection("Coding Playlist")
        collectionReference.addSnapshotListener { value, error ->
            if (value == null || error != null) {
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            codingPlaylistList.clear()
            codingPlaylistList.addAll(value.toObjects(StudyPlaylist::class.java))
            adapter.notifyDataSetChanged()
        }
    }

    private fun setUpRecyclerView() {
        val studyPlaylistRecyclerView = findViewById<RecyclerView>(R.id.codingPlaylistRecyclerView)
        adapter = StudyPlaylistAdapter(this, codingPlaylistList)
        studyPlaylistRecyclerView.layoutManager = LinearLayoutManager(this)
        studyPlaylistRecyclerView.adapter = adapter
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.codingPlaylistToolbar)
        toolbar?.title = "Study Playlist"
        setSupportActionBar(toolbar)

        toolbar?.setNavigationOnClickListener {
            finish()
        }
    }
}