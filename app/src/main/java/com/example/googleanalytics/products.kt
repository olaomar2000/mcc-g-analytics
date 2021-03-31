package com.example.googleanalytics

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.googleanalytics.Adapter.CategoriesAdapter
import com.example.googleanalytics.Adapter.ProductAdapter
import com.example.googleanalytics.Modle.Products
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_products.*
import java.util.*

class products : AppCompatActivity(), ProductAdapter.onProductsItemClickListener {
    lateinit var db: FirebaseFirestore
    private var progressDialog: ProgressDialog?=null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    var begin: Long = 0
    var end: Long = 0
    var total: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        begin = Calendar.getInstance().timeInMillis

        db = Firebase.firestore
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Track_Screen("Product")
        Show_Dialog()

        var catName=intent.getStringExtra("catName")
        Log.e("ola", catName.toString())
        getProducts("$catName")


    }

    override fun onItemClick(data: Products, position: Int) {
        SELECT_CONTENT(data.id!!,data.name!!,data.image!!)

        end = Calendar.getInstance().timeInMillis
        total = end - begin

        val minutes: Long = total / 1000 / 60
        val seconds = (total / 1000 % 60)
        Time_Screen("$minutes m $seconds s","olaomar123456","products")

        var i = Intent(this,product_Det::class.java)
        i.putExtra("pid",data.id)
        i.putExtra("pname",data.name)
        i.putExtra("pimage",data.image)
        i.putExtra("pprice",data.price)
        i.putExtra("pdescription",data.description)
        i.putExtra("pcategory",data.categoryName)
        startActivity(i)

    }

    private fun getProducts(catName:String){
        val dataProduct = mutableListOf<Products>()

        db.collection("product").whereEqualTo("categoryName",catName)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.e("product", "${document.id} -> ${document.get("name")}")
                        val id = document.id
                        val data = document.data
                        val name = data["name"] as String?
                        val image = data["image"] as String?
                        val price = data["price"] as String?
                        val description = data["description"] as String?
                        val categoryName = data["categoryName"] as String?
                        dataProduct.add(
                            Products(id,name,price,description,categoryName,image)
                        )
                        Log.e("ola","Add data product Suc")
                    }

                    recViewProduct?.layoutManager =
                        LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                    recViewProduct.setHasFixedSize(true)
                    val productAdapter = ProductAdapter(this, dataProduct, this)
                    recViewProduct.adapter = productAdapter

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
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "products")
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