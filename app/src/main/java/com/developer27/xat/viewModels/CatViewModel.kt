package com.developer27.xat.viewModels

import com.developer27.xat.fragments.CatFragment
import android.R
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import android.widget.ImageView
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonArrayRequest

class CatViewModel : ViewModel(){
    // Create an instance of FirstFragament
    private var mainFragment = CatFragment()

    // Cat related data
    private var catName = ""
    private var catOrigin = ""
    private var catDescription = ""

    // Method to download cat image
    fun loadCatImage(context: Context, breedId: String, imageView: ImageView) {
        val apiUrl = "https://api.thecatapi.com/v1/images/search?breed_ids=$breedId"

        val requestQueue = Volley.newRequestQueue(context)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, apiUrl, null,
            Response.Listener<JSONArray> { response ->
                try {
                    if (response.length() > 0) {
                        val catObject = response.getJSONObject(0)
                        val imageUrl = catObject.getString("url")
                        loadImage(context, imageUrl, imageView)
                    } else {
                        Log.e("Load Image", "No image found for $breedId cat")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                mainFragment.showMessageBox("Load Image", "Error fetching image: ${error.message}")
            })

        requestQueue.add(jsonArrayRequest)
    }

    // Method to load image
    fun loadImage(context: Context, imageUrl: String, catImage: ImageView) {
        val imageRequest = ImageRequest(
            imageUrl,
            Response.Listener { response ->
                catImage.setImageBitmap(response)
            },
            0,
            0,
            ImageView.ScaleType.CENTER_INSIDE,
            null,
            Response.ErrorListener { error ->
                mainFragment.showMessageBox("Load Image", "Error loading image: ${error.message}")
            })

        Volley.newRequestQueue(context).add(imageRequest)
    }

    // Method to get name, origin and description
    public fun getrestofData(context: Context, breedId: String) {
        val apiUrl = "https://api.thecatapi.com/v1/breeds/$breedId"
        val requestQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, apiUrl, null,
            Response.Listener<JSONObject> { response ->
                // Parse the JSON response and populate the TextViews
                populateCatData(response)
            },
            Response.ErrorListener { error ->
                mainFragment.showMessageBox("Fetch Cat Data", "Error fetching data: ${error.message}")
            })

        Volley.newRequestQueue(context).add(jsonObjectRequest)
    }

    // Method to initiliaze cat name, origin and description
    private fun populateCatData(data: JSONObject) {
            catName = data.getString("name")
            catOrigin = "${data.getString("origin")}"
            catDescription = data.getString("description")
    }

    // Method to return cat name
    fun getCatName(): String{
        return catName
    }

    // Method to return cat origin
    fun getCatOrigin(): String{
        return catOrigin
    }

    // Method to return cat description
    fun getCatDescription(): String{
        return catDescription
    }

}