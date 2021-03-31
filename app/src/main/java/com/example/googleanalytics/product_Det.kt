package com.example.googleanalytics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product__det.*
import java.util.*

class product_Det : AppCompatActivity() {


    lateinit var db: FirebaseFirestore
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    var begin: Long = 0
    var end: Long = 0
    var total: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product__det)

        begin = Calendar.getInstance().timeInMillis
        db= Firebase.firestore

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Track_Screen("product_Det")

        val pname = intent.getStringExtra("pname")
        val pimage = intent.getStringExtra("pimage")
        val pprice = intent.getStringExtra("pprice")
        val pdescription = intent.getStringExtra("pdescription")

        Picasso.get().load(pimage).into(product_image)
        Product_Name.text = pname
        Product_Price.text = pprice.toString()
        product_Description.text = pdescription

    }

    private fun Track_Screen(screenName:String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "product_Det")
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun Time_Screen(time: String, userId:String, pageName:String){

        val time= hashMapOf("time" to time,"userId" to userId,"pageName" to pageName)
        db.collection("Time")
            .add(time)
            .addOnSuccessListener {
                Log.e("ola", "time added successfully")
            }
            .addOnFailureListener {
                Log.e("ola", "failuer")
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        end = Calendar.getInstance().timeInMillis
        total = end - begin

        val minutes: Long = total / 1000 / 60
        val seconds = (total / 1000 % 60)
        Time_Screen("$minutes m $seconds s","olaomar123456","product_Det")
    }
}
