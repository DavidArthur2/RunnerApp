package com.festipay.runnerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festipay.runnerapp.R
import com.festipay.runnerapp.adapters.CommentsAdapter
import com.festipay.runnerapp.data.Comment
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.DateFormatter
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Functions.launchFragment
import com.festipay.runnerapp.utilities.Functions.showInfoDialog
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.utilities.OperationType
import com.festipay.runnerapp.utilities.showError
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class CommentsFragment : Fragment(), IFragment<Comment> {
    override lateinit var recyclerView: RecyclerView
    override lateinit var itemList: ArrayList<Comment>
    private lateinit var modeName: String
    private lateinit var context: FragmentActivity

    private lateinit var commentText: EditText
    private lateinit var addButton: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comments, container, false)
        initFragment()
        initViews(view)
        loadComments(view)
        return view
    }

    private fun initFragment() {
        context = requireActivity()
        CurrentState.operation = OperationType.COMMENTS
        modeName = Database.mapCollectionModeName()
        CurrentState.fragmentType = when (CurrentState.mode) {
            Mode.INSTALL -> FragmentType.INSTALL_COMPANY_COMMENTS
            Mode.DEMOLITION -> FragmentType.DEMOLITION_COMPANY_COMMENTS
            Mode.INVENTORY -> FragmentType.INVENTORY_ITEM_COMMENTS
            Mode.FINAL_INVENTORY -> FragmentType.FINAL_INVENTORY_ITEM_COMMENTS
            else -> FragmentType.INVENTORY_ITEM_COMMENTS

        }

        val appBar: androidx.appcompat.widget.Toolbar = context.findViewById(R.id.toolbar)
        appBar.title = "${CurrentState.companySite} - Megjegyzések"
    }

    private fun initViews(view: View) {
        commentText = view.findViewById(R.id.commentInput)
        addButton = view.findViewById(R.id.commentsAddButton)
        addButton.setOnClickListener {
            addComment()
        }
    }

    override fun setupView(view: View) {
        recyclerView = view.findViewById(R.id.commentsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        val adapt = CommentsAdapter(itemList)
        recyclerView.adapter = adapt

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onViewLoaded()
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        adapt.setOnItemClickListener(object : CommentsAdapter.OnItemDeleteListener {
            override fun onItemDelete(position: Int, comment: Comment) {
                Functions.showLoadingScreen(context)
                Database.db.collection(modeName).document(CurrentState.companySiteID ?: "")
                    .collection("Comments").document(comment.docID).delete().addOnSuccessListener {
                        launchFragment(context, CommentsFragment())
                    showInfoDialog(
                        context,
                        "Törlés",
                        "Sikeresen törölted a megjegyzést!",
                        "Vissza",
                        false
                    )
                }.addOnFailureListener {
                    showError(
                        context,
                        "Megjegyzést törlése sikertelen!",
                        "companydocid: ${CurrentState.companySiteID} commentdocid: ${comment.docID} error: $it"
                    )
                }
            }
        })
    }

    override fun loadComments(view: View) {
        itemList = arrayListOf()
        Database.db.collection(modeName).document(CurrentState.companySiteID ?: "")
            .collection("Comments").orderBy("Timestamp", Query.Direction.ASCENDING)
            .get().addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    for (doc in result) {
                        itemList.add(
                            Comment(
                                doc.data["Comment"] as String,
                                DateFormatter.TimestampToLocalDateTime(doc.data["Timestamp"] as Timestamp),
                                doc.id
                            )
                        )
                    }
                }
                setupView(view)
            }.addOnFailureListener {
                showError(
                    context,
                    "Sikertelen megjegyzés beolvasás",
                    "companydocid: ${CurrentState.companySiteID} error: $it"
                )
            }
    }

    override fun onViewLoaded() {
        Functions.hideLoadingScreen()
    }

    override fun loadList(view: View) {

    }
    private fun addComment() {
        Functions.showLoadingScreen(context)
        val data = hashMapOf(
            "Comment" to commentText.text.toString(),
            "Timestamp" to Timestamp.now().toDate()
        )
        Database.db.collection(modeName).document(CurrentState.companySiteID ?: "").collection("Comments").add(data)
            .addOnSuccessListener {
                launchFragment(context, CommentsFragment())
                showInfoDialog(
                    context,
                    "Hozzáadás",
                    "Megjegyzés sikeresen hozzáadva!",
                    "Rendben",
                    false
                )
            }.addOnFailureListener { ex ->
                showError(
                    context,
                    "Sikertelen hozzáadás\nNézd a logot",
                    "Error at adding comment in Comments: $data, $ex"
                )
            }
    }
}