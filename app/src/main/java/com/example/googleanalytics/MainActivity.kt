package com.example.googleanalytics

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.googleanalytics.Adapter.CategoriesAdapter
import com.example.googleanalytics.Modle.Category
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_product__det.*
import java.util.*


class MainActivity : AppCompatActivity() , CategoriesAdapter.onCategoryItemClickListener {

    lateinit var db: FirebaseFirestore
    private var progressDialog: ProgressDialog?=null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    var begin: Long = 0
    var end: Long = 0
    var total: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        begin = Calendar.getInstance().timeInMillis
        db = Firebase.firestore
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Track_Screen("Category")
        Show_Dialog()
        getCategories()

    }

    override fun onItemClick(data: Category, position: Int) {
        SELECT_CONTENT(data.id!!,data.nameCategory!!,data.imageCategory!!)
        end = Calendar.getInstance().timeInMillis
        total = end - begin

        val minutes: Long = total / 1000 / 60
        val seconds = (total / 1000 % 60)
        Time_Screen("$minutes m $seconds s","olaomar123456","MainActivity")

        var i = Intent(this, products::class.java)
        i.putExtra("id",data.id)
        i.putExtra("catImage",data.imageCategory)
        i.putExtra("catName",data.nameCategory)

        startActivity(i)
    }

    private fun getCategories(){
        val categoryList= mutableListOf<Category>()
        db.collection("Category")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.e("ola", "${document.id} -> ${document.get("name")} -> ${document.get("image")}")
                        val id = document.id
                        val data = document.data
                        val categoryName = data["name"] as String?
                        val categoryImage = data["image"] as String?
                        categoryList.add(Category(id,categoryName,categoryImage))
                    }
                    recViewCategory?.layoutManager =
                        LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                    recViewCategory.setHasFixedSize(true)
                    val categoriesAdapter = CategoriesAdapter(this, categoryList, this)
                    recViewCategory.adapter = categoriesAdapter
                }
                Hide_Dialog()
            }
    }

    private fun Show_Dialog() {
        Log.e("ola","Show ... ")
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Loading ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun Hide_Dialog(){
        if(progressDialog!!.isShowing){
            Log.e("ola","Hide ...")
            progressDialog!!.dismiss()
        }
    }

    private fun Track_Screen(screenName:String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    private fun SELECT_CONTENT(id:String, name:String, contentType:String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun Time_Screen(time: String, userId:String, pageName:String){

        val time= hashMapOf("time" to time,"userId" to userId,"pageName" to pageName)
        db.collection("Time")
            .add(time)
            .addOnSuccessListener {documentReference ->
                Log.e("ola","successfully")
            }
            .addOnFailureListener {exception ->
                Log.e("ola", exception.message.toString())
            }
    }
}



