package com.festipay.runnerapp.fragments

import com.festipay.runnerapp.utilities.LocationGetter.getLocation
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.festipay.runnerapp.R
import com.festipay.runnerapp.activities.MapsActivity
import com.festipay.runnerapp.data.References.Companion.company_ref
import com.festipay.runnerapp.data.References.Companion.coord_ref
import com.festipay.runnerapp.database.Database
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Functions.hideLoadingScreen
import com.festipay.runnerapp.utilities.Functions.launchFragment
import com.festipay.runnerapp.utilities.Functions.showInfoDialog
import com.festipay.runnerapp.utilities.Functions.showLoadingScreen
import com.festipay.runnerapp.utilities.OperationType
import com.festipay.runnerapp.utilities.showError
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint


class GPSFragment : Fragment() {

    private lateinit var refreshButton: Button
    private lateinit var goButton: Button
    private lateinit var longText: TextView
    private lateinit var latText: TextView
    private lateinit var context: FragmentActivity

    private var coord: LatLng? = null
    private var docID: String? = null
    private lateinit var modeName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_coords, container, false)

        initFragment()
        initView(view)
        loadCoords()
        return view
    }
    private fun initFragment(){
        context = requireActivity()
        CurrentState.operation = OperationType.GPS
        CurrentState.fragmentType = FragmentType.INSTALL_COMPANY_GPS
        modeName = Database.mapCollectionModeName()
        val appBar: androidx.appcompat.widget.Toolbar = context.findViewById(R.id.toolbar)
        appBar.title = "${CurrentState.companySite} - GPS koord."

    }
    private fun initView(view: View){
        longText = view.findViewById(R.id.longText)
        latText = view.findViewById(R.id.latText)
        refreshButton = view.findViewById(R.id.refreshButton)
        goButton = view.findViewById(R.id.goButton)

        refreshButton.setOnClickListener {
            refreshCoords()
        }
        goButton.setOnClickListener {
            startMaps()
        }
    }

    private fun loadCoords(){
        val companyRef = Database.db.collection(modeName).document(CurrentState.companySiteID ?: "")

        coord_ref().whereEqualTo("ref", companyRef).get().addOnSuccessListener {
            if(!it.isEmpty){
                val geoPoint = it.first().data["coord"] as GeoPoint
                coord = LatLng(geoPoint.latitude, geoPoint.longitude)
                longText.text = geoPoint.longitude.toString()
                latText.text = geoPoint.latitude.toString()
                docID = it.first().id

                if(arguments?.getString("go") == "go") // Ha gyorsgombbol inditjuk el
                    startMaps()
                else if(arguments?.getString("save") == "save")
                    refreshCoords()
                else
                    onViewLoaded()
            }
            else{
                longText.text = "-"
                latText.text = "-"
                onViewLoaded()
            }
        }.addOnFailureListener {ex ->
            showError(context, "Sikertelen koordináta lekérdezés", ex.toString())
        }
    }

    private fun refreshCoords() {
        showLoadingScreen(context)
        getLocation(context,
            onSuccess = { location ->
                val geoPoint = GeoPoint(location.latitude, location.longitude)
                var data = hashMapOf<String, Any>("coord" to geoPoint)
                if(docID != null)coord_ref().document(docID!!).update(data).addOnSuccessListener {
                    launchFragment(context, OperationSelectorFragment())
                    showInfoDialog(context, "Rögzítés", "Koordináták sikeresen rögzítve!")
                }.addOnFailureListener {
                    showError(context, "Sikertelen koordináta rögzítés", it.toString())
                }
                if(docID == null) {
                    data = hashMapOf("coord" to geoPoint, "ref" to company_ref())

                    coord_ref().add(data).addOnSuccessListener {
                        launchFragment(context, OperationSelectorFragment())
                        showInfoDialog(context, "Rögzítés", "Koordináták sikeresen rögzítve!")
                    }.addOnFailureListener {
                        showError(context, "Sikertelen koordináta rögzítés", it.toString())
                    }
                }
            },
            onError = { errorMessage ->
                showError(context, "Sikertelen pozíció lekérés!\n$errorMessage", errorMessage)
            }
        )
    }

    private fun startMaps() {
        if(coord != null) {
            val intent = Intent(context, MapsActivity::class.java).apply {
                putExtra("lat", coord!!.latitude.toString())
                putExtra("long", coord!!.longitude.toString())
            }
            startActivity(intent)
            context.finish()
        }
        else showError(context, "Koordináták nincsenek rögzítve")
    }

    private fun onViewLoaded(){
        hideLoadingScreen()
    }
}