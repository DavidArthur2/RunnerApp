package com.festipay.runnerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.festipay.runnerapp.R
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.Functions.launchFragment
import com.festipay.runnerapp.utilities.Mode
import com.festipay.runnerapp.utilities.showError
import com.google.firebase.Timestamp


/**
 * OUT OF FUNCTION
 */
class CommentsAddFragment : Fragment() {
    private lateinit var commentText: EditText
    private lateinit var addButton: Button
    private lateinit var modeName: String
    private lateinit var context: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comments_add, container, false)

        initFragment()
        initViews(view)
        return view
    }

    private fun initFragment() {
        context = requireActivity()
        CurrentState.fragmentType = when (CurrentState.mode) {
            Mode.INSTALL -> FragmentType.INSTALL_COMPANY_COMMENTS_ADD
            Mode.DEMOLITION -> FragmentType.DEMOLITION_COMPANY_COMMENTS_ADD
            Mode.INVENTORY -> FragmentType.INVENTORY_ITEM_COMMENTS_ADD
            Mode.FINAL_INVENTORY -> FragmentType.FINAL_INVENTORY_ITEM_COMMENTS_ADD
            else -> FragmentType.INVENTORY_ITEM_COMMENTS_ADD

        }
        modeName = Database.mapCollectionModeName()
    }

    private fun initViews(view: View) {
        commentText = view.findViewById(R.id.commentInput)
        addButton = view.findViewById(R.id.commentsAddButton)
        addButton.setOnClickListener {
            addComment()
        }
    }

    private fun addComment() {
        Functions.showLoadingScreen(context)
        val data = hashMapOf(
            "Comment" to commentText.text.toString(),
            "Timestamp" to Timestamp.now().toDate()
        )
        Database.db.collection(modeName).document(CurrentState.companySiteID ?: "")
            .collection("Comments").add(data)
            .addOnSuccessListener {
                launchFragment(context, CommentsFragment())
                Functions.showInfoDialog(
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